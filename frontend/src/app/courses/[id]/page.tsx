"use client";

import { useParams } from "next/navigation";
import { useQuery } from "@tanstack/react-query";
import { api } from "@/lib/api-client";
import { Navbar } from "@/components/shared/navbar";
import { Footer } from "@/components/shared/footer";
import Link from "next/link";

interface CourseDetail {
  id: string;
  title: string;
  description: string;
  categoryName: string;
  difficulty: string;
  instructorName: string;
  enrollmentCount: number;
  averageRating: number | null;
  isPremium: boolean;
  status: string;
}

interface ModuleItem {
  id: string;
  title: string;
  orderIndex: number;
}

export default function CourseDetailPage() {
  const { id } = useParams<{ id: string }>();

  const { data: course, isLoading } = useQuery({
    queryKey: ["course", id],
    queryFn: () => api.get<CourseDetail>(`/courses/${id}`),
    enabled: !!id,
  });

  const { data: modules } = useQuery({
    queryKey: ["modules", id],
    queryFn: () => api.get<ModuleItem[]>(`/courses/${id}/modules`),
    enabled: !!id,
  });

  if (isLoading) {
    return (
      <div className="flex min-h-screen flex-col">
        <Navbar />
        <div className="flex flex-1 items-center justify-center">
          <div className="h-8 w-8 animate-spin rounded-full border-2 border-primary border-t-transparent" />
        </div>
      </div>
    );
  }

  if (!course) {
    return (
      <div className="flex min-h-screen flex-col">
        <Navbar />
        <div className="flex flex-1 items-center justify-center">
          <p className="text-muted-foreground">Course not found.</p>
        </div>
      </div>
    );
  }

  const difficultyColors: Record<string, string> = {
    BEGINNER: "bg-green-500/10 text-green-400 border-green-500/20",
    INTERMEDIATE: "bg-yellow-500/10 text-yellow-400 border-yellow-500/20",
    ADVANCED: "bg-red-500/10 text-red-400 border-red-500/20",
  };

  return (
    <div className="flex min-h-screen flex-col">
      <Navbar />
      <main className="flex-1 py-8">
        <div className="mx-auto max-w-4xl px-4 sm:px-6 lg:px-8">
          {/* Course Header */}
          <div className="glass-card p-8">
            <div className="flex items-center gap-3">
              <span className={`rounded-full border px-3 py-1 text-xs font-medium ${difficultyColors[course.difficulty]}`}>
                {course.difficulty}
              </span>
              {course.isPremium && (
                <span className="rounded-full border border-primary/30 bg-primary/10 px-3 py-1 text-xs font-medium text-primary">
                  ⭐ Premium
                </span>
              )}
              <span className="rounded-full border border-white/10 bg-white/5 px-3 py-1 text-xs text-muted-foreground">
                {course.categoryName}
              </span>
            </div>

            <h1 className="mt-4 text-3xl font-bold text-white">{course.title}</h1>
            <p className="mt-4 text-muted-foreground leading-relaxed">{course.description}</p>

            <div className="mt-6 flex items-center gap-6 text-sm text-muted-foreground">
              <span>👨‍🏫 {course.instructorName}</span>
              <span>👥 {course.enrollmentCount} enrolled</span>
              {course.averageRating && <span>⭐ {course.averageRating}/5</span>}
            </div>

            <div className="mt-6">
              <Link
                href="/login"
                className="inline-block rounded-xl bg-primary px-8 py-3 font-medium text-primary-foreground shadow-lg shadow-primary/25 transition-all hover:bg-primary/90"
              >
                Enroll Now — Free
              </Link>
            </div>
          </div>

          {/* Modules */}
          {modules && modules.length > 0 && (
            <div className="mt-8">
              <h2 className="text-xl font-semibold text-white">Course Content</h2>
              <div className="mt-4 space-y-3">
                {modules.map((module) => (
                  <div key={module.id} className="glass-card flex items-center gap-4 p-4">
                    <span className="flex h-8 w-8 items-center justify-center rounded-full bg-primary/10 text-sm font-bold text-primary">
                      {module.orderIndex}
                    </span>
                    <span className="font-medium text-white">{module.title}</span>
                  </div>
                ))}
              </div>
            </div>
          )}
        </div>
      </main>
      <Footer />
    </div>
  );
}
