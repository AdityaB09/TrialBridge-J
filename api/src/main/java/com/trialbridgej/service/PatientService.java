package com.trialbridgej.service;

import com.trialbridgej.dto.PatientIngestRequest;
import com.trialbridgej.model.Patient;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PatientService {

    private final Map<String, Patient> patients = new ConcurrentHashMap<>();
    private final NlpService nlpService;

    public PatientService(NlpService nlpService) {
        this.nlpService = nlpService;
    }

    public Patient ingest(PatientIngestRequest req) {
        Patient p = new Patient();
        p.id = req.patientId != null ? req.patientId : UUID.randomUUID().toString();
        p.note = req.note;
        p.sex = req.sex;

        p.age = nlpService.extractAge(req.note, req.age);
        p.labs = nlpService.extractLabs(req.note, req.labs);
        p.conditions = new ArrayList<>(nlpService.extractConditions(req.note));
        p.forbiddenHistory = new ArrayList<>(nlpService.extractForbiddenHistory(req.note));
        p.embedding = nlpService.embed(req.note);

        patients.put(p.id, p);
        return p;
    }

    public Optional<Patient> getById(String id) {
        return Optional.ofNullable(patients.get(id));
    }

    public Collection<Patient> getAll() {
        return patients.values();
    }
}
