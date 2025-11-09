import React from "react";

interface HeaderProps {
  dark: boolean;
  onToggleTheme: () => void;
}

export const Header: React.FC<HeaderProps> = ({ dark, onToggleTheme }) => {
  return (
    <div className="header">
      <div className="brand">
        TrialBridge-J
        <span className="brand-pill">Clinical Trial Matcher</span>
      </div>
      <button className="toggle-btn" onClick={onToggleTheme}>
        {dark ? "☀ Light mode" : "🌙 Dark mode"}
      </button>
    </div>
  );
};
