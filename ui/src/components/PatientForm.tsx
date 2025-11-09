import React, { useState } from "react";
import { ingestPatient } from "../api";

interface PatientFormProps {
  onPatientSaved: (patientId: string) => void;
}

export const PatientForm: React.FC<PatientFormProps> = ({ onPatientSaved }) => {
  const [note, setNote] = useState(
    "56-year-old male with poorly controlled type 2 diabetes, HbA1c 8.3%, history of hypertension, no prior stroke."
  );
  const [age, setAge] = useState<string>("");
  const [sex, setSex] = useState<string>("male");
  const [saving, setSaving] = useState(false);
  const [lastId, setLastId] = useState<string | null>(null);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSaving(true);
    try {
      const payload: any = {
        note,
        sex
      };
      if (age) payload.age = Number(age);
      const res = await ingestPatient(payload);
      setLastId(res.id);
      onPatientSaved(res.id);
    } catch (err) {
      console.error(err);
      alert("Failed to save patient");
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="card">
      <div className="section-title">Patient Profile</div>
      <form onSubmit={handleSubmit}>
        <div className="field">
          <div className="label">Clinical note</div>
          <textarea
            className="textarea"
            value={note}
            onChange={(e) => setNote(e.target.value)}
          />
        </div>
        <div className="row-3">
          <div className="field">
            <div className="label">Age (optional)</div>
            <input
              className="input"
              value={age}
              onChange={(e) => setAge(e.target.value)}
              placeholder="56"
            />
          </div>
          <div className="field">
            <div className="label">Sex</div>
            <input
              className="input"
              value={sex}
              onChange={(e) => setSex(e.target.value)}
              placeholder="male"
            />
          </div>
          <div className="field" style={{ display: "flex", alignItems: "flex-end", justifyContent: "flex-end" }}>
            <button className="btn" type="submit" disabled={saving}>
              {saving ? "Saving..." : "Save patient"}
            </button>
          </div>
        </div>
        {lastId && (
          <div style={{ marginTop: 6, fontSize: 11, color: "var(--muted)" }}>
            Current patientId: <strong>{lastId}</strong>
          </div>
        )}
      </form>
    </div>
  );
};
