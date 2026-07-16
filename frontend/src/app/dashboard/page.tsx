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
        <p className="text-muted-foreground">Loading...</p>
      </div>
    );
  }

  if (!user) {
    return (
      <div className="flex min-h-screen flex-col items-center justify-center gap-4">
        <p className="text-muted-foreground">Please sign in to view your dashboard.</p>
        <Link href="/login" className="rounded-md bg-primary px-4 py-2 text-sm text-primary-foreground">
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
          <h1 className="text-3xl font-bold">Welcome back, {user.displayName}!</h1>

          {/* Stats Cards */}
          <div className="mt-8 grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
            <StatCard label="Level" value={String(user.level)} icon="⭐" />
            <StatCard label="Total XP" value={String(user.xpTotal)} icon="✨" />
            <StatCard label="Role" value={user.role} icon="👤" />
            <StatCard label="Status" value="Active" icon="🟢" />
          </div>

          {/* Quick Actions */}
          <div className="mt-8">
            <h2 className="text-xl font-semibold">Quick Actions</h2>
            <div className="mt-4 grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
              <ActionCard
                title="Browse Courses"
                description="Discover new courses to enroll in"
                href="/courses"
              />
              <ActionCard
                title="My Progress"
                description="View your enrolled courses and progress"
                href="/courses"
              />
              <ActionCard
                title="Achievements"
                description="View your badges and certificates"
                href="/dashboard"
              />
            </div>
          </div>
        </div>
      </main>
      <Footer />
    </div>
  );
}

function StatCard({ label, value, icon }: { label: string; value: string; icon: string }) {
  return (
    <div className="rounded-lg border border-border p-4">
      <div className="flex items-center gap-2">
        <span className="text-2xl">{icon}</span>
        <div>
          <p className="text-2xl font-bold">{value}</p>
          <p className="text-sm text-muted-foreground">{label}</p>
        </div>
      </div>
    </div>
  );
}

function ActionCard({ title, description, href }: { title: string; description: string; href: string }) {
  return (
    <Link href={href} className="block rounded-lg border border-border p-4 hover:border-primary hover:bg-accent/50 transition-colors">
      <h3 className="font-semibold">{title}</h3>
      <p className="mt-1 text-sm text-muted-foreground">{description}</p>
    </Link>
  );
}
