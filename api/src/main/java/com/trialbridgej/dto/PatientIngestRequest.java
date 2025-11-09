package com.trialbridgej.dto;

import java.util.Map;

public class PatientIngestRequest {
    public String patientId;
    public String note;
    public Integer age;
    public String sex;
    public Map<String, Double> labs; // optional structured labs
}
