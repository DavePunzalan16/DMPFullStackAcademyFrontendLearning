"use client";

interface XpProgressRingProps {
  level: number;
  currentXp: number;
  xpForNextLevel: number;
}

export function XpProgressRing({ level, currentXp, xpForNextLevel }: XpProgressRingProps) {
  const total = currentXp + xpForNextLevel;
  const progress = total > 0 ? (currentXp / total) * 100 : 0;
  const circumference = 2 * Math.PI * 45;
  const strokeDashoffset = circumference - (progress / 100) * circumference;

  return (
    <div className="flex items-center gap-4">
      <div className="relative h-24 w-24">
        <svg className="h-24 w-24 -rotate-90" viewBox="0 0 100 100">
          {/* Background circle */}
          <circle cx="50" cy="50" r="45" fill="none" stroke="rgba(255,255,255,0.05)" strokeWidth="8" />
          {/* Progress circle */}
          <circle
            cx="50" cy="50" r="45" fill="none"
            stroke="hsl(262, 80%, 85%)"
            strokeWidth="8"
            strokeLinecap="round"
            strokeDasharray={circumference}
            strokeDashoffset={strokeDashoffset}
            className="transition-all duration-1000"
          />
        </svg>
        <div className="absolute inset-0 flex flex-col items-center justify-center">
          <span className="text-2xl font-bold text-white">{level}</span>
          <span className="text-[10px] text-muted-foreground">LEVEL</span>
        </div>
      </div>
      <div>
        <p className="text-sm font-medium text-white">{currentXp.toLocaleString()} XP</p>
        <p className="text-xs text-muted-foreground">{xpForNextLevel} XP to next level</p>
      </div>
    </div>
  );
}
