package com.trialbridgej.controller;

import com.trialbridgej.dto.MatchResultDto;
import com.trialbridgej.model.MatchResult;
import com.trialbridgej.service.MatchingService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/match")
public class MatchController {

    private final MatchingService matchingService;

    public MatchController(MatchingService matchingService) {
        this.matchingService = matchingService;
    }

    @GetMapping("/{patientId}")
    public List<MatchResultDto> match(@PathVariable("patientId") String patientId) {
        List<MatchResult> results = matchingService.match(patientId);
        return results.stream().map(this::toDto).collect(Collectors.toList());
    }

    @GetMapping("/{patientId}/{trialId}")
    public MatchResultDto explain(@PathVariable("patientId") String patientId,
                                  @PathVariable("trialId") String trialId) {
        MatchResult res = matchingService.explain(patientId, trialId)
                .orElseThrow(() -> new NoSuchElementException("Pair not found"));
        return toDto(res);
    }

    private MatchResultDto toDto(MatchResult r) {
        MatchResultDto dto = new MatchResultDto();
        dto.trialId = r.trialId;
        dto.trialTitle = r.trialTitle;
        dto.similarityScore = r.similarityScore;
        dto.eligibilityScore = r.eligibilityScore;
        dto.overallScore = r.overallScore;
        dto.eligibility = r.eligibilityLabel;
        dto.reasons = r.reasons;
        return dto;
    }
}
