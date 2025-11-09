package com.trialbridgej.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Patient {
    public String id;
    public String note;
    public Integer age;
    public String sex;

    public List<String> conditions = new ArrayList<>();
    public Map<String, Double> labs = new HashMap<>();
    public List<String> forbiddenHistory = new ArrayList<>();

    public double[] embedding;

    public Patient() {}
}
