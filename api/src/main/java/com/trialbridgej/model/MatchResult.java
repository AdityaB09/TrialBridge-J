package com.trialbridgej.model;

import java.util.ArrayList;
import java.util.List;

public class MatchResult implements Comparable<MatchResult> {
    public String trialId;
    public String trialTitle;
    public double similarityScore;
    public double eligibilityScore;
    public double overallScore;
    public String eligibilityLabel;
    public List<String> reasons = new ArrayList<>();

    @Override
    public int compareTo(MatchResult o) {
        return Double.compare(o.overallScore, this.overallScore);
    }
}
