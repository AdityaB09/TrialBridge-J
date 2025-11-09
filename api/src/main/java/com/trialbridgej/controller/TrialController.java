package com.trialbridgej.controller;

import com.trialbridgej.dto.TrialIngestRequest;
import com.trialbridgej.model.Trial;
import com.trialbridgej.service.TrialService;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/api/trials")
public class TrialController {

    private final TrialService trialService;

    public TrialController(TrialService trialService) {
        this.trialService = trialService;
    }

    @PostMapping("/ingest")
    public Trial ingest(@RequestBody TrialIngestRequest req) {
        return trialService.ingest(req);
    }

    @GetMapping
    public Collection<Trial> list() {
        return trialService.getAll();
    }
}
