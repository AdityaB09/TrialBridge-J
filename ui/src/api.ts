export interface TrialIngestRequest {
  trialId?: string;
  title: string;
  condition: string;
  description: string;
  inclusionCriteria: string[];
  exclusionCriteria: string[];
}

export interface PatientIngestRequest {
  patientId?: string;
  note: string;
  age?: number;
  sex?: string;
  labs?: Record<string, number>;
}

export interface MatchResult {
  trialId: string;
  trialTitle: string;
  similarityScore: number;
  eligibilityScore: number;
  overallScore: number;
  eligibility: string;
  reasons: string[];
}

const API_BASE = "/api";

export async function ingestTrial(payload: TrialIngestRequest) {
  const res = await fetch(`${API_BASE}/trials/ingest`, {
    method: "POST",
    headers: { "content-type": "application/json" },
    body: JSON.stringify(payload)
  });
  if (!res.ok) throw new Error("Failed to ingest trial");
  return res.json();
}

export async function ingestPatient(payload: PatientIngestRequest) {
  const res = await fetch(`${API_BASE}/patients/ingest`, {
    method: "POST",
    headers: { "content-type": "application/json" },
    body: JSON.stringify(payload)
  });
  if (!res.ok) throw new Error("Failed to ingest patient");
  return res.json();
}

export async function fetchMatches(patientId: string): Promise<MatchResult[]> {
  const res = await fetch(`${API_BASE}/match/${patientId}`);
  if (!res.ok) throw new Error("Failed to fetch matches");
  return res.json();
}
