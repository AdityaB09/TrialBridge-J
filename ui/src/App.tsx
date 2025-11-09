import React, { useEffect, useState } from "react";
import { Header } from "./components/Header";
import { PatientForm } from "./components/PatientForm";
import { TrialForm } from "./components/TrialForm";
import { MatchTable } from "./components/MatchTable";
import { fetchMatches, MatchResult } from "./api";

export const App: React.FC = () => {
  const [currentPatientId, setCurrentPatientId] = useState<string | null>(null);
  const [matches, setMatches] = useState<MatchResult[]>([]);
  const [dark, setDark] = useState<boolean>(false);
  const [running, setRunning] = useState(false);

  useEffect(() => {
    document.body.className = dark ? "dark" : "light";
  }, [dark]);

  const runMatcher = async () => {
    if (!currentPatientId) {
      alert("Please save a patient first.");
      return;
    }
    setRunning(true);
    try {
      const data = await fetchMatches(currentPatientId);
      setMatches(data);
    } catch (err) {
      console.error(err);
      alert("Failed to run matcher");
    } finally {
      setRunning(false);
    }
  };

  return (
    <div className="app-root">
      <div className="container">
        <div className="card" style={{ marginBottom: 12 }}>
          <Header dark={dark} onToggleTheme={() => setDark((d) => !d)} />
          <div style={{ fontSize: 13, color: "var(--muted)" }}>
            AI engine that reads patient notes and clinical trial criteria, then
            ranks trials by eligibility with explainable reasons.
          </div>
        </div>

        <div className="grid-2">
          <PatientForm
            onPatientSaved={(id) => {
              setCurrentPatientId(id);
              setMatches([]);
            }}
          />
          <div style={{ display: "flex", flexDirection: "column", gap: 12 }}>
            <TrialForm onTrialIngested={() => {}} />
            <div className="card">
              <div className="section-title">Matcher</div>
              <div style={{ display: "flex", alignItems: "center", gap: 8 }}>
                <button className="btn" onClick={runMatcher} disabled={running}>
                  {running ? "Matching..." : "Run matcher"}
                </button>
                <button
                  className="btn btn-secondary"
                  type="button"
                  onClick={() => setMatches([])}
                >
                  Clear results
                </button>
                <div style={{ fontSize: 11, color: "var(--muted)" }}>
                  PatientId:{" "}
                  {currentPatientId ? (
                    <strong>{currentPatientId}</strong>
                  ) : (
                    <span>none</span>
                  )}
                </div>
              </div>
            </div>
          </div>
        </div>

        <div style={{ marginTop: 14 }}>
          <MatchTable matches={matches} />
        </div>
      </div>
    </div>
  );
};
