package com.trialbridgej.service;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class NlpService {

    private static final int EMBEDDING_DIM = 32;

    private static final Pattern AGE_PATTERN =
            Pattern.compile("(\\d{1,3})\\s*-?year-old|age\\s*(\\d{1,3})", Pattern.CASE_INSENSITIVE);

    public int extractAge(String text, Integer fallback) {
        if (text == null) return fallback != null ? fallback : -1;
        Matcher m = AGE_PATTERN.matcher(text);
        if (m.find()) {
            for (int i = 1; i <= m.groupCount(); i++) {
                if (m.group(i) != null) {
                    try {
                        return Integer.parseInt(m.group(i));
                    } catch (NumberFormatException ignored) {}
                }
            }
        }
        return fallback != null ? fallback : -1;
    }

    public Map<String, Double> extractLabs(String text, Map<String, Double> given) {
        Map<String, Double> labs = new HashMap<>();
        if (given != null) labs.putAll(given);

        if (text == null) return labs;

        // Very naive: look for "HbA1c 8.3" or "HbA1c 8.3%"
        Pattern hba1c = Pattern.compile("HbA1c[^0-9]*([0-9]+\\.?[0-9]*)", Pattern.CASE_INSENSITIVE);
        Matcher m = hba1c.matcher(text);
        if (m.find()) {
            try {
                labs.put("hba1c", Double.parseDouble(m.group(1)));
            } catch (NumberFormatException ignored) {}
        }

        return labs;
    }

    public List<String> extractConditions(String text) {
        if (text == null) return List.of();
        String lower = text.toLowerCase(Locale.ROOT);
        List<String> result = new ArrayList<>();

        if (lower.contains("type 2 diabetes")) result.add("type 2 diabetes");
        if (lower.contains("diabetes") && !result.contains("type 2 diabetes")) result.add("diabetes");
        if (lower.contains("hypertension")) result.add("hypertension");

        // Stroke: only mark as condition if not explicitly negated
        boolean mentionsStroke = lower.contains("stroke");
        boolean negatedStroke =
                lower.contains("no history of stroke") ||
                lower.contains("no prior stroke") ||
                lower.contains("no stroke history");

        if (mentionsStroke && !negatedStroke) {
            result.add("stroke");
        }

        return result;
    }

    public List<String> extractForbiddenHistory(String text) {
        if (text == null) return List.of();
        String lower = text.toLowerCase(Locale.ROOT);
        List<String> result = new ArrayList<>();

        if (lower.contains("no history of stroke") ||
            lower.contains("no prior stroke") ||
            lower.contains("no stroke history")) {
            result.add("no stroke history");
        }

        if (lower.contains("history of stroke") && !lower.contains("no history of stroke")) {
            result.add("stroke");
        }

        return result;
    }

    public double[] embed(String text) {
        // Stub: deterministic hash-based embedding.
        double[] vec = new double[EMBEDDING_DIM];
        if (text == null || text.isBlank()) return vec;
        String[] tokens = text.toLowerCase(Locale.ROOT).split("\\W+");
        for (String t : tokens) {
            int h = Math.abs(t.hashCode());
            int idx = h % EMBEDDING_DIM;
            vec[idx] += 1.0;
        }
        // L2 normalize
        double norm = 0.0;
        for (double v : vec) norm += v * v;
        norm = Math.sqrt(norm);
        if (norm > 0) {
            for (int i = 0; i < vec.length; i++) vec[i] /= norm;
        }
        return vec;
    }

    public double cosine(double[] a, double[] b) {
        if (a == null || b == null || a.length != b.length) return 0;
        double dot = 0, na = 0, nb = 0;
        for (int i = 0; i < a.length; i++) {
            dot += a[i] * b[i];
            na += a[i] * a[i];
            nb += b[i] * b[i];
        }
        if (na == 0 || nb == 0) return 0;
        return dot / (Math.sqrt(na) * Math.sqrt(nb));
    }
}
