"use client";

interface StreakIndicatorProps {
  currentStreak: number;
  longestStreak: number;
}

export function StreakIndicator({ currentStreak, longestStreak }: StreakIndicatorProps) {
  return (
    <div className="glass-card p-5">
      <div className="flex items-center gap-3">
        <span className={`text-3xl ${currentStreak > 0 ? "animate-pulse" : ""}`}>
          {currentStreak > 0 ? "🔥" : "❄️"}
        </span>
        <div>
          <p className="text-xl font-bold text-white">{currentStreak} days</p>
          <p className="text-xs text-muted-foreground">
            {currentStreak > 0 ? "Current streak" : "Start your streak today!"}
          </p>
        </div>
      </div>
      {longestStreak > 0 && (
        <p className="mt-2 text-xs text-muted-foreground">
          🏆 Best: {longestStreak} days
        </p>
      )}
    </div>
  );
}
