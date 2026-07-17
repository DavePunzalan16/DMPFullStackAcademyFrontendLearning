"use client";

interface Badge {
  id: string;
  name: string;
  description: string;
  iconRef: string;
  awardedAt: string;
}

interface BadgeShowcaseProps {
  badges: Badge[];
}

export function BadgeShowcase({ badges }: BadgeShowcaseProps) {
  if (badges.length === 0) {
    return (
      <div className="glass-card p-5 text-center">
        <p className="text-2xl">🏅</p>
        <p className="mt-2 text-sm text-muted-foreground">
          No badges earned yet. Complete lessons and quizzes to unlock achievements!
        </p>
      </div>
    );
  }

  return (
    <div className="glass-card p-5">
      <h3 className="text-sm font-semibold text-white">Badges Earned</h3>
      <div className="mt-3 grid grid-cols-3 gap-3">
        {badges.slice(0, 6).map((badge) => (
          <div key={badge.id} className="flex flex-col items-center rounded-lg bg-white/5 p-2 text-center">
            <span className="text-2xl">🏅</span>
            <p className="mt-1 text-[10px] font-medium text-white truncate w-full">{badge.name}</p>
          </div>
        ))}
      </div>
      {badges.length > 6 && (
        <p className="mt-2 text-center text-xs text-primary">View all {badges.length} badges →</p>
      )}
    </div>
  );
}
