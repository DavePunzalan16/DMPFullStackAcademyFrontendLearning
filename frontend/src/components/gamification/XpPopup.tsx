"use client";

import { useEffect, useState } from "react";

interface XpPopupProps {
  amount: number;
  show: boolean;
  onComplete?: () => void;
}

export function XpPopup({ amount, show, onComplete }: XpPopupProps) {
  const [visible, setVisible] = useState(false);

  useEffect(() => {
    if (show) {
      setVisible(true);
      const timer = setTimeout(() => {
        setVisible(false);
        onComplete?.();
      }, 3000);
      return () => clearTimeout(timer);
    }
  }, [show, onComplete]);

  if (!visible) return null;

  return (
    <div
      className="fixed bottom-8 right-8 z-50 animate-bounce"
      role="status"
      aria-live="polite"
    >
      <div className="rounded-xl border border-primary/30 bg-primary/10 px-6 py-3 shadow-2xl shadow-primary/20 backdrop-blur-sm">
        <p className="text-lg font-bold text-primary">+{amount} XP ✨</p>
        <p className="text-xs text-muted-foreground">Keep it up!</p>
      </div>
    </div>
  );
}
