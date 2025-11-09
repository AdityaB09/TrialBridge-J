package com.trialbridgej.model;

import java.util.ArrayList;
import java.util.List;

public class Trial {
    public String id;
    public String title;
    public String condition;
    public String description;
    public List<Criterion> inclusion = new ArrayList<>();
    public List<Criterion> exclusion = new ArrayList<>();
    public double[] embedding; // simple numeric vector

    public Trial() {}
}
