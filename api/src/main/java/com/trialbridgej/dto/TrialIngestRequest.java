package com.trialbridgej.dto;

import java.util.List;

public class TrialIngestRequest {
    public String trialId;
    public String title;
    public String condition;
    public String description;
    public List<String> inclusionCriteria;
    public List<String> exclusionCriteria;
}
