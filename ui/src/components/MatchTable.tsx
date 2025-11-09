import React from "react";
import { MatchResult } from "../api";

interface MatchTableProps {
  matches: MatchResult[];
}

export const MatchTable: React.FC<MatchTableProps> = ({ matches }) => {
  return (
    <div className="card">
      <div className="section-title">Trial Matches</div>
      {matches.length === 0 ? (
        <div style={{ fontSize: 13, color: "var(--muted)" }}>
          No matches yet. Save a patient and a trial, then click &quot;Run matcher&quot;.
        </div>
      ) : (
        <table className="table">
          <thead>
            <tr>
              <th>Trial</th>
              <th>Eligibility</th>
              <th>Score</th>
              <th>Reasons</th>
            </tr>
          </thead>
          <tbody>
            {matches.map((m) => (
              <tr key={m.trialId}>
                <td>
                  <div style={{ fontWeight: 600 }}>{m.trialTitle}</div>
                  <div style={{ fontSize: 11, color: "var(--muted)" }}>
                    {m.trialId}
                  </div>
                </td>
                <td>
                  <span
                    className={
                      "badge " +
                      (m.eligibility === "Likely eligible"
                        ? "badge-green"
                        : m.eligibility === "Excluded"
                        ? "badge-red"
                        : "badge-amber")
                    }
                  >
                    {m.eligibility}
                  </span>
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
                          Math.max(0, m.overallScore * 100)
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
                  <div className="reasons">
                    {m.reasons.slice(0, 3).map((r, i) => (
                      <div key={i}>• {r}</div>
                    ))}
                    {m.reasons.length > 3 && (
                      <div>+ {m.reasons.length - 3} more…</div>
                    )}
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
};
