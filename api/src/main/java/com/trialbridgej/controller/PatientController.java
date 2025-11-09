package com.trialbridgej.controller;

import com.trialbridgej.dto.PatientIngestRequest;
import com.trialbridgej.model.Patient;
import com.trialbridgej.service.PatientService;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @PostMapping("/ingest")
    public Patient ingest(@RequestBody PatientIngestRequest req) {
        return patientService.ingest(req);
    }

    @GetMapping
    public Collection<Patient> list() {
        return patientService.getAll();
    }

    @GetMapping("/{id}")
    public Patient get(@PathVariable("id") String id) {
        return patientService.getById(id)
                .orElseThrow(() -> new NoSuchElementException("Patient not found: " + id));
    }
}
