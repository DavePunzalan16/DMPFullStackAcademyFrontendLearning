"use client";

import { createContext, useContext, useState, useCallback, type ReactNode } from "react";
import { XpPopup } from "./XpPopup";

interface GamificationContextType {
  showXpPopup: (amount: number) => void;
  showLevelUp: (level: number) => void;
  showBadgeToast: (badgeName: string) => void;
}

const GamificationContext = createContext<GamificationContextType | null>(null);

export function useGamification() {
  const ctx = useContext(GamificationContext);
  if (!ctx) throw new Error("useGamification must be used within GamificationProvider");
  return ctx;
}

export function GamificationProvider({ children }: { children: ReactNode }) {
  const [xpAmount, setXpAmount] = useState(0);
  const [showXp, setShowXp] = useState(false);
  const [levelUp, setLevelUp] = useState<number | null>(null);
  const [badge, setBadge] = useState<string | null>(null);

  const showXpPopup = useCallback((amount: number) => {
    setXpAmount(amount);
    setShowXp(true);
  }, []);

  const showLevelUp = useCallback((level: number) => {
    setLevelUp(level);
    setTimeout(() => setLevelUp(null), 4000);
  }, []);

  const showBadgeToast = useCallback((badgeName: string) => {
    setBadge(badgeName);
    setTimeout(() => setBadge(null), 5000);
  }, []);

  return (
    <GamificationContext.Provider value={{ showXpPopup, showLevelUp, showBadgeToast }}>
      {children}

      {/* XP Popup */}
      <XpPopup amount={xpAmount} show={showXp} onComplete={() => setShowXp(false)} />

      {/* Level Up Celebration */}
      {levelUp && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 backdrop-blur-sm"
             onClick={() => setLevelUp(null)}>
          <div className="animate-bounce text-center">
            <p className="text-6xl">🎉</p>
            <p className="mt-4 text-3xl font-bold text-white">Level Up!</p>
            <p className="mt-2 text-xl text-primary">You reached Level {levelUp}</p>
          </div>
        </div>
      )}

      {/* Badge Toast */}
      {badge && (
        <div className="fixed top-4 right-4 z-50 animate-pulse">
          <div className="rounded-xl border border-yellow-500/30 bg-yellow-500/10 px-6 py-3 shadow-lg backdrop-blur-sm">
            <p className="text-sm font-bold text-yellow-400">🏅 Badge Unlocked!</p>
            <p className="text-xs text-muted-foreground">{badge}</p>
          </div>
        </div>
      )}
    </GamificationContext.Provider>
  );
}
