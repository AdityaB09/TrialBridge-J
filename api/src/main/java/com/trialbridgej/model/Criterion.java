package com.trialbridgej.model;

import java.util.List;

public class Criterion {
    public String text;
    public List<String> tags; // e.g. ["AGE", "CONDITION", "LAB"]
    public boolean required;

    public Criterion() {}

    public Criterion(String text, List<String> tags, boolean required) {
        this.text = text;
        this.tags = tags;
        this.required = required;
    }
}
