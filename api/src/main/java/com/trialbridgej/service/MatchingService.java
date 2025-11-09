package com.trialbridgej.service;

import com.trialbridgej.model.MatchResult;
import com.trialbridgej.model.Patient;
import com.trialbridgej.model.Trial;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class MatchingService {

    private final NlpService nlpService;
    private final TrialService trialService;
    private final PatientService patientService;

    private static final Pattern AGE_RANGE_PATTERN =
            Pattern.compile("(\\d{1,3})\\s*[-–]\\s*(\\d{1,3})");

    public MatchingService(NlpService nlpService,
                           TrialService trialService,
                           PatientService patientService) {
        this.nlpService = nlpService;
        this.trialService = trialService;
        this.patientService = patientService;
    }

    public List<MatchResult> match(String patientId) {
        Patient p = patientService.getById(patientId)
                .orElseThrow(() -> new NoSuchElementException("Patient not found: " + patientId));

        List<MatchResult> results = new ArrayList<>();
        for (Trial t : trialService.getAll()) {
            MatchResult r = scorePair(p, t);
            if (!Double.isNaN(r.overallScore)) {
                results.add(r);
            }
        }
        Collections.sort(results);
        return results;
    }

    public Optional<MatchResult> explain(String patientId, String trialId) {
        Patient p = patientService.getById(patientId).orElse(null);
        Trial t = trialService.getById(trialId).orElse(null);
        if (p == null || t == null) return Optional.empty();
        return Optional.of(scorePair(p, t));
    }

    private MatchResult scorePair(Patient p, Trial t) {
        MatchResult r = new MatchResult();
        r.trialId = t.id;
        r.trialTitle = t.title;

        // 1) Similarity
        r.similarityScore = nlpService.cosine(p.embedding, t.embedding);

        // 2) Eligibility rule checks
        double eligibilityScore = 0;
        boolean hardExcluded = false;

        // Age constraints from inclusion criteria
        boolean ageMatched = false;
        if (p.age != null && p.age > 0) {
            for (var c : t.inclusion) {
                Matcher m = AGE_RANGE_PATTERN.matcher(c.text);
                if (m.find()) {
                    int min = Integer.parseInt(m.group(1));
                    int max = Integer.parseInt(m.group(2));
                    if (p.age >= min && p.age <= max) {
                        eligibilityScore += 0.25;
                        ageMatched = true;
                        r.reasons.add("Age " + p.age + " within " + min + "–" + max);
                    } else {
                        r.reasons.add("Age " + p.age + " not in " + min + "–" + max);
                    }
                }
            }
        }

        // Condition match
        boolean conditionMatched = false;
        String patientConditionsJoined = String.join(";", p.conditions).toLowerCase(Locale.ROOT);
        String trialCond = t.condition != null ? t.condition.toLowerCase(Locale.ROOT) : "";
        if (!trialCond.isBlank() && !patientConditionsJoined.isBlank()) {
            if (patientConditionsJoined.contains(trialCond)
                    || trialCond.contains("diabetes") && patientConditionsJoined.contains("diabetes")) {
                eligibilityScore += 0.25;
                conditionMatched = true;
                r.reasons.add("Has condition: " + t.condition);
            }
        }

        // Lab threshold example: "HbA1c ≥ 7.0"
        if (p.labs.containsKey("hba1c")) {
            double val = p.labs.get("hba1c");
            for (var c : t.inclusion) {
                if (c.text.toLowerCase(Locale.ROOT).contains("hba1c")) {
                    if (c.text.contains("≥") || c.text.contains(">=")) {
                        double threshold = parseNumber(c.text);
                        if (!Double.isNaN(threshold) && val >= threshold) {
                            eligibilityScore += 0.25;
                            r.reasons.add("HbA1c " + val + " ≥ " + threshold);
                        }
                    }
                }
            }
        }

        // Exclusion: simple stroke check
        for (var c : t.exclusion) {
            String lower = c.text.toLowerCase(Locale.ROOT);
            if (lower.contains("stroke")) {
                boolean hasStroke = p.conditions.stream()
                        .map(String::toLowerCase)
                        .anyMatch(s -> s.contains("stroke"));
                if (hasStroke) {
                    hardExcluded = true;
                    r.reasons.add("Excluded: history of stroke");
                } else {
                    r.reasons.add("No history of stroke (required)");
                }
            }
        }

        if (hardExcluded) {
            r.eligibilityScore = 0;
            r.overallScore = Double.NaN; // filtered out by caller
            r.eligibilityLabel = "Excluded";
            return r;
        }

        r.eligibilityScore = eligibilityScore;

        double alpha = 0.6;
        double beta = 0.4;
        r.overallScore = alpha * r.similarityScore + beta * r.eligibilityScore;

        if (eligibilityScore >= 0.6 && r.similarityScore > 0.3) {
            r.eligibilityLabel = "Likely eligible";
        } else if (eligibilityScore > 0.2) {
            r.eligibilityLabel = "Possibly eligible";
        } else {
            r.eligibilityLabel = "Weak match";
        }

        if (!ageMatched) {
            r.reasons.add("Age criteria uncertain or not specified.");
        }
        if (!conditionMatched) {
            r.reasons.add("Condition match uncertain.");
        }

        return r;
    }

    private double parseNumber(String text) {
        Matcher m = Pattern.compile("([0-9]+\\.?[0-9]*)").matcher(text);
        if (m.find()) {
            try {
                return Double.parseDouble(m.group(1));
            } catch (NumberFormatException ignored) {}
        }
        return Double.NaN;
    }
}
