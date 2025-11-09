import React, { useState } from "react";
import { ingestTrial } from "../api";

interface TrialFormProps {
  onTrialIngested: () => void;
}

export const TrialForm: React.FC<TrialFormProps> = ({ onTrialIngested }) => {
  const [title, setTitle] = useState("T2D Glycemic Control Study");
  const [condition, setCondition] = useState("type 2 diabetes");
  const [description, setDescription] = useState(
    "Phase II study evaluating improved glycemic control with new therapy."
  );
  const [inclusion, setInclusion] = useState<string>(
    "Age 40-70\nDiagnosed Type 2 diabetes\nHbA1c ≥ 7.0"
  );
  const [exclusion, setExclusion] = useState<string>("History of stroke");
  const [saving, setSaving] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSaving(true);
    try {
      const payload = {
        title,
        condition,
        description,
        inclusionCriteria: inclusion
          .split("\n")
          .map((s) => s.trim())
          .filter(Boolean),
        exclusionCriteria: exclusion
          .split("\n")
          .map((s) => s.trim())
          .filter(Boolean)
      };
      await ingestTrial(payload);
      onTrialIngested();
    } catch (err) {
      console.error(err);
      alert("Failed to save trial");
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="card">
      <div className="section-title">Trial Description</div>
      <form onSubmit={handleSubmit}>
        <div className="field">
          <div className="label">Title</div>
          <input
            className="input"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
          />
        </div>
        <div className="field">
          <div className="label">Condition</div>
          <input
            className="input"
            value={condition}
            onChange={(e) => setCondition(e.target.value)}
          />
        </div>
        <div className="field">
          <div className="label">Description</div>
          <textarea
            className="textarea"
            value={description}
            onChange={(e) => setDescription(e.target.value)}
          />
        </div>
        <div className="field">
          <div className="label">Inclusion criteria (one per line)</div>
          <textarea
            className="textarea"
            value={inclusion}
            onChange={(e) => setInclusion(e.target.value)}
          />
        </div>
        <div className="field">
          <div className="label">Exclusion criteria (one per line)</div>
          <textarea
            className="textarea"
            value={exclusion}
            onChange={(e) => setExclusion(e.target.value)}
          />
        </div>
        <button className="btn" type="submit" disabled={saving}>
          {saving ? "Saving..." : "Save trial"}
        </button>
      </form>
    </div>
  );
};
