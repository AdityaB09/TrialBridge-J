package com.trialbridgej.service;

import com.trialbridgej.dto.TrialIngestRequest;
import com.trialbridgej.model.Criterion;
import com.trialbridgej.model.Trial;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TrialService {

    private final Map<String, Trial> trials = new ConcurrentHashMap<>();
    private final NlpService nlpService;

    public TrialService(NlpService nlpService) {
        this.nlpService = nlpService;
    }

    public Trial ingest(TrialIngestRequest req) {
        Trial t = new Trial();
        t.id = req.trialId != null ? req.trialId : UUID.randomUUID().toString();
        t.title = req.title;
        t.condition = req.condition;
        t.description = req.description;

        if (req.inclusionCriteria != null) {
            for (String s : req.inclusionCriteria) {
                t.inclusion.add(new Criterion(s, List.of(), true));
            }
        }
        if (req.exclusionCriteria != null) {
            for (String s : req.exclusionCriteria) {
                t.exclusion.add(new Criterion(s, List.of(), true));
            }
        }

        String fullText = (t.title == null ? "" : t.title + " ") +
                (t.condition == null ? "" : t.condition + " ") +
                (t.description == null ? "" : t.description + " ");

        t.embedding = nlpService.embed(fullText);
        trials.put(t.id, t);
        return t;
    }

    public Collection<Trial> getAll() {
        return trials.values();
    }

    public Optional<Trial> getById(String id) {
        return Optional.ofNullable(trials.get(id));
    }
}
