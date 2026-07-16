"use client";

import { Navbar } from "@/components/shared/navbar";
import { Footer } from "@/components/shared/footer";
import { useAuthStore } from "@/stores/auth-store";
import { useCurrentUser } from "@/features/auth/hooks/use-auth";
import Link from "next/link";

export default function InstructorDashboard() {
  useCurrentUser();
  const { user, isLoading } = useAuthStore();

  if (isLoading) {
    return (
      <div className="flex min-h-screen items-center justify-center">
        <div className="h-8 w-8 animate-spin rounded-full border-2 border-primary border-t-transparent" />
      </div>
    );
  }

  if (!user || user.role !== "INSTRUCTOR") {
    return (
      <div className="flex min-h-screen flex-col items-center justify-center gap-4">
        <p className="text-muted-foreground">Access restricted to instructors.</p>
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
          <div className="flex items-center justify-between">
            <div>
              <h1 className="text-3xl font-bold text-white">
                Instructor <span className="gradient-text">Dashboard</span>
              </h1>
              <p className="mt-1 text-muted-foreground">Manage your courses and view analytics</p>
            </div>
            <Link
              href="/instructor/courses/new"
              className="rounded-xl bg-primary px-6 py-2.5 text-sm font-medium text-primary-foreground shadow-lg shadow-primary/25 hover:bg-primary/90"
            >
              + Create Course
            </Link>
          </div>

          {/* Stats */}
          <div className="mt-8 grid gap-4 sm:grid-cols-3">
            <div className="glass-card p-5">
              <p className="text-3xl font-bold text-white">0</p>
              <p className="text-sm text-muted-foreground">Published Courses</p>
            </div>
            <div className="glass-card p-5">
              <p className="text-3xl font-bold text-white">0</p>
              <p className="text-sm text-muted-foreground">Total Enrollments</p>
            </div>
            <div className="glass-card p-5">
              <p className="text-3xl font-bold text-white">0%</p>
              <p className="text-sm text-muted-foreground">Avg. Completion Rate</p>
            </div>
          </div>

          {/* Courses List */}
          <div className="mt-10">
            <h2 className="text-xl font-semibold text-white">My Courses</h2>
            <div className="glass-card mt-4 p-8 text-center">
              <p className="text-4xl">📝</p>
              <p className="mt-4 text-muted-foreground">No courses yet. Create your first course!</p>
              <Link
                href="/instructor/courses/new"
                className="mt-4 inline-block rounded-lg bg-primary px-6 py-2 text-sm font-medium text-primary-foreground hover:bg-primary/90"
              >
                Create Course
              </Link>
            </div>
          </div>
        </div>
      </main>
      <Footer />
    </div>
  );
}
