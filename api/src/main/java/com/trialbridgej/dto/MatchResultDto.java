package com.trialbridgej.dto;

import java.util.List;

public class MatchResultDto {
    public String trialId;
    public String trialTitle;
    public double similarityScore;
    public double eligibilityScore;
    public double overallScore;
    public String eligibility;
    public List<String> reasons;
}
