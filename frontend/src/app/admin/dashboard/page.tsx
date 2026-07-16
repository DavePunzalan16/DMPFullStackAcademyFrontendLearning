"use client";

import { Navbar } from "@/components/shared/navbar";
import { Footer } from "@/components/shared/footer";
import { useAuthStore } from "@/stores/auth-store";
import { useCurrentUser } from "@/features/auth/hooks/use-auth";
import Link from "next/link";

export default function AdminDashboard() {
  useCurrentUser();
  const { user, isLoading } = useAuthStore();

  if (isLoading) {
    return (
      <div className="flex min-h-screen items-center justify-center">
        <div className="h-8 w-8 animate-spin rounded-full border-2 border-primary border-t-transparent" />
      </div>
    );
  }

  if (!user || user.role !== "ADMIN") {
    return (
      <div className="flex min-h-screen flex-col items-center justify-center gap-4">
        <p className="text-muted-foreground">Access restricted to administrators.</p>
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
          <h1 className="text-3xl font-bold text-white">
            Admin <span className="gradient-text">Dashboard</span>
          </h1>
          <p className="mt-1 text-muted-foreground">Platform overview and management</p>

          {/* Platform Stats */}
          <div className="mt-8 grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
            <AdminStatCard icon="👥" value="1" label="Total Users" />
            <AdminStatCard icon="📚" value="0" label="Total Courses" />
            <AdminStatCard icon="📜" value="0" label="Certificates Issued" />
            <AdminStatCard icon="📊" value="0%" label="Avg. Completion" />
          </div>

          {/* Management Links */}
          <div className="mt-10">
            <h2 className="text-xl font-semibold text-white">Management</h2>
            <div className="mt-4 grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
              <AdminLink icon="👥" title="Users" description="Manage user accounts and roles" href="/admin/users" />
              <AdminLink icon="📚" title="Courses" description="View and moderate all courses" href="/admin/courses" />
              <AdminLink icon="🏷️" title="Categories" description="Manage course categories" href="/admin/categories" />
              <AdminLink icon="🎯" title="Quests" description="Create and manage quests" href="/admin/quests" />
              <AdminLink icon="⭐" title="Reviews" description="Moderate course reviews" href="/admin/reviews" />
              <AdminLink icon="📢" title="Announcements" description="Send announcements to all users" href="/admin/announcements" />
            </div>
          </div>
        </div>
      </main>
      <Footer />
    </div>
  );
}

function AdminStatCard({ icon, value, label }: { icon: string; value: string; label: string }) {
  return (
    <div className="glass-card p-5">
      <div className="flex items-center gap-3">
        <span className="text-2xl">{icon}</span>
        <div>
          <p className="text-2xl font-bold text-white">{value}</p>
          <p className="text-sm text-muted-foreground">{label}</p>
        </div>
      </div>
    </div>
  );
}

function AdminLink({ icon, title, description, href }: { icon: string; title: string; description: string; href: string }) {
  return (
    <Link href={href} className="glass-card block p-5 transition-all hover:border-primary/30 hover:shadow-lg hover:shadow-primary/5">
      <span className="text-2xl">{icon}</span>
      <h3 className="mt-3 font-semibold text-white">{title}</h3>
      <p className="mt-1 text-sm text-muted-foreground">{description}</p>
    </Link>
  );
}
