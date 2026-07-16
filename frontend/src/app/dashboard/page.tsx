"use client";

import { Navbar } from "@/components/shared/navbar";
import { Footer } from "@/components/shared/footer";
import { useAuthStore } from "@/stores/auth-store";
import { useCurrentUser } from "@/features/auth/hooks/use-auth";
import Link from "next/link";

export default function StudentDashboard() {
  useCurrentUser();
  const { user, isLoading } = useAuthStore();

  if (isLoading) {
    return (
      <div className="flex min-h-screen items-center justify-center">
        <div className="flex flex-col items-center gap-3">
          <div className="h-8 w-8 animate-spin rounded-full border-2 border-primary border-t-transparent" />
          <p className="text-sm text-muted-foreground">Loading your dashboard...</p>
        </div>
      </div>
    );
  }

  if (!user) {
    return (
      <div className="flex min-h-screen flex-col items-center justify-center gap-4">
        <p className="text-muted-foreground">Please sign in to view your dashboard.</p>
        <Link href="/login" className="rounded-xl bg-primary px-6 py-2.5 text-sm font-medium text-primary-foreground">
          Sign In
        </Link>
      </div>
    );
  }

  return (
    <div className="flex min-h-screen flex-col">
      <Navbar />
      <main className="flex-1 py-8">
        <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
          {/* Welcome Header */}
          <div className="flex items-center justify-between">
            <div>
              <h1 className="text-3xl font-bold text-white">
                Welcome back, <span className="gradient-text">{user.displayName}</span>!
              </h1>
              <p className="mt-1 text-muted-foreground">Keep up the great work. Here&apos;s your progress.</p>
            </div>
          </div>

          {/* Stats Grid */}
          <div className="mt-8 grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
            <GlassStatCard
              icon="⭐"
              value={`Level ${user.level}`}
              label="Current Level"
              color="from-yellow-500/20 to-orange-500/20"
            />
            <GlassStatCard
              icon="✨"
              value={`${user.xpTotal} XP`}
              label="Total Experience"
              color="from-purple-500/20 to-pink-500/20"
            />
            <GlassStatCard
              icon="🔥"
              value="0 days"
              label="Current Streak"
              color="from-red-500/20 to-orange-500/20"
            />
            <GlassStatCard
              icon="📚"
              value="0"
              label="Courses Enrolled"
              color="from-blue-500/20 to-cyan-500/20"
            />
          </div>

          {/* Quick Actions */}
          <div className="mt-10">
            <h2 className="text-xl font-semibold text-white">Quick Actions</h2>
            <div className="mt-4 grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
              <ActionCard
                icon="🎓"
                title="Browse Courses"
                description="Discover new courses to enroll in and start learning"
                href="/courses"
              />
              <ActionCard
                icon="🎯"
                title="Active Quests"
                description="Check your current quests and earn bonus XP"
                href="/dashboard"
              />
              <ActionCard
                icon="🏅"
                title="My Badges"
                description="View your earned achievements and badges"
                href="/dashboard"
              />
            </div>
          </div>

          {/* Recent Activity Placeholder */}
          <div className="mt-10">
            <h2 className="text-xl font-semibold text-white">Recent Activity</h2>
            <div className="glass-card mt-4 p-8 text-center">
              <p className="text-4xl">📖</p>
              <p className="mt-4 text-muted-foreground">
                No activity yet. Start by enrolling in a course!
              </p>
              <Link
                href="/courses"
                className="mt-4 inline-block rounded-lg bg-primary px-6 py-2 text-sm font-medium text-primary-foreground hover:bg-primary/90"
              >
                Find a Course
              </Link>
            </div>
          </div>
        </div>
      </main>
      <Footer />
    </div>
  );
}

function GlassStatCard({ icon, value, label, color }: { icon: string; value: string; label: string; color: string }) {
  return (
    <div className={`glass-card relative overflow-hidden p-5`}>
      <div className={`absolute inset-0 bg-gradient-to-br ${color} opacity-50`} />
      <div className="relative">
        <span className="text-2xl">{icon}</span>
        <p className="mt-2 text-xl font-bold text-white">{value}</p>
        <p className="text-sm text-muted-foreground">{label}</p>
      </div>
    </div>
  );
}

function ActionCard({ icon, title, description, href }: { icon: string; title: string; description: string; href: string }) {
  return (
    <Link
      href={href}
      className="glass-card block p-5 transition-all hover:border-primary/30 hover:shadow-lg hover:shadow-primary/5"
    >
      <span className="text-2xl">{icon}</span>
      <h3 className="mt-3 font-semibold text-white">{title}</h3>
      <p className="mt-1 text-sm text-muted-foreground">{description}</p>
    </Link>
  );
}
