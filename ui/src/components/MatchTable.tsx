import React, { useState } from "react";
import { MatchResult } from "../api";

interface MatchTableProps {
  matches: MatchResult[];
}

export const MatchTable: React.FC<MatchTableProps> = ({ matches }) => {
  const [expandedId, setExpandedId] = useState<string | null>(null);

  const toggleExpanded = (trialId: string) => {
    setExpandedId((current) => (current === trialId ? null : trialId));
  };

  return (
    <div className="card">
      <div className="section-title">Trial Matches</div>
      {matches.length === 0 ? (
        <div style={{ fontSize: 13, color: "var(--muted)" }}>
          No matches yet. Save a patient and a trial, then click &quot;Run
          matcher&quot;.
        </div>
      ) : (
        <table className="table">
          <thead>
            <tr>
              <th>Trial</th>
              <th>Eligibility</th>
              <th>Score</th>
              <th>Explanation</th>
            </tr>
          </thead>
          <tbody>
            {matches.map((m) => {
              const isExpanded = expandedId === m.trialId;
              const eligibilityClass =
                m.eligibility === "Likely eligible"
                  ? "badge-green"
                  : m.eligibility === "Excluded"
                  ? "badge-red"
                  : "badge-amber";

              const visibleReasons = m.reasons.slice(0, 2);
              const remainingCount =
                m.reasons.length > visibleReasons.length
                  ? m.reasons.length - visibleReasons.length
                  : 0;

              return (
                <tr key={m.trialId}>
                  <td>
                    <div style={{ fontWeight: 600 }}>{m.trialTitle}</div>
                    <div style={{ fontSize: 11, color: "var(--muted)" }}>
                      {m.trialId}
                    </div>
                  </td>
                  <td>
                    <div
                      style={{
                        display: "flex",
                        flexDirection: "column",
                        gap: 4
                      }}
                    >
                      <span className={`badge ${eligibilityClass}`}>
                        {m.eligibility}
                      </span>
                      {m.eligibility === "Excluded" && (
                        <span
                          style={{
                            fontSize: 11,
                            color: "var(--danger)"
                          }}
                        >
                          Excluded trial still shown so you can see why it
                          failed eligibility.
                        </span>
                      )}
                    </div>
                  </td>
                  <td style={{ width: 160 }}>
                    <div style={{ fontSize: 11, color: "var(--muted)" }}>
                      overall {(m.overallScore || 0).toFixed(2)}
                    </div>
                    <div className="progress-bar">
                      <div
                        className="progress-inner"
                        style={{
                          width: `${Math.min(
                            100,
                            Math.max(0, (m.overallScore + 1) * 50) // shift -1..1 → 0..100
                          )}%`
                        }}
                      />
                    </div>
                    <div style={{ fontSize: 10, color: "var(--muted)" }}>
                      sim {m.similarityScore.toFixed(2)} · rules{" "}
                      {m.eligibilityScore.toFixed(2)}
                    </div>
                  </td>
                  <td>
                    <div style={{ display: "flex", flexDirection: "column" }}>
                      {/* Short preview */}
                      <div className="reasons">
                        {visibleReasons.map((r, i) => (
                          <div key={i}>• {r}</div>
                        ))}
                        {remainingCount > 0 && !isExpanded && (
                          <div>+ {remainingCount} more…</div>
                        )}
                      </div>

                      {/* Explanation chip */}
                      <div style={{ marginTop: 6 }}>
                        <button
                          type="button"
                          className="chip-button"
                          onClick={() => toggleExpanded(m.trialId)}
                        >
                          {isExpanded ? "Hide explanation" : "Explain match"}
                          <span style={{ fontSize: 10 }}>
                            ({m.reasons.length})
                          </span>
                        </button>
                      </div>

                      {/* Expanded explanation panel */}
                      {isExpanded && (
                        <div className="explain-panel">
                          {m.reasons.map((r, i) => (
                            <div className="explain-panel-line" key={i}>
                              • {r}
                            </div>
                          ))}
                        </div>
                      )}
                    </div>
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
      )}
    </div>
  );
};
