"use client";

import { useState } from "react";
import { useQuery } from "@tanstack/react-query";
import { api } from "@/lib/api-client";
import { Navbar } from "@/components/shared/navbar";
import { Footer } from "@/components/shared/footer";
import type { PageResponse } from "@/types/api";
import Link from "next/link";

interface CourseItem {
  id: string;
  title: string;
  description: string;
  categoryName: string;
  difficulty: string;
  instructorName: string;
  enrollmentCount: number;
  averageRating: number | null;
  isPremium: boolean;
}

export default function CoursesPage() {
  const [search, setSearch] = useState("");
  const [difficulty, setDifficulty] = useState("");

  const { data, isLoading } = useQuery({
    queryKey: ["courses", search, difficulty],
    queryFn: () => {
      const params = new URLSearchParams();
      if (search) params.set("query", search);
      if (difficulty) params.set("difficulty", difficulty);
      params.set("size", "20");
      return api.get<PageResponse<CourseItem>>(`/courses/search?${params.toString()}`);
    },
  });

  return (
    <div className="flex min-h-screen flex-col">
      <Navbar />
      <main className="flex-1 py-8">
        <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
          {/* Header */}
          <div className="text-center">
            <h1 className="text-4xl font-bold text-white">
              Explore <span className="gradient-text">Courses</span>
            </h1>
            <p className="mt-3 text-lg text-muted-foreground">
              Find your next learning adventure from our curated catalog
            </p>
          </div>

          {/* Filters */}
          <div className="mx-auto mt-8 flex max-w-2xl flex-col gap-4 sm:flex-row">
            <div className="relative flex-1">
              <span className="absolute left-3 top-1/2 -translate-y-1/2 text-muted-foreground">🔍</span>
              <input
                type="text"
                value={search}
                onChange={(e) => setSearch(e.target.value)}
                placeholder="Search courses..."
                className="w-full rounded-xl border border-white/10 bg-white/5 py-3 pl-10 pr-4 text-sm text-white placeholder:text-muted-foreground focus:border-primary focus:outline-none focus:ring-1 focus:ring-primary"
              />
            </div>
            <select
              value={difficulty}
              onChange={(e) => setDifficulty(e.target.value)}
              className="rounded-xl border border-white/10 bg-white/5 px-4 py-3 text-sm text-white focus:border-primary focus:outline-none focus:ring-1 focus:ring-primary"
              aria-label="Filter by difficulty"
            >
              <option value="">All Levels</option>
              <option value="BEGINNER">Beginner</option>
              <option value="INTERMEDIATE">Intermediate</option>
              <option value="ADVANCED">Advanced</option>
            </select>
          </div>

          {/* Course Grid */}
          {isLoading ? (
            <div className="mt-16 flex justify-center">
              <div className="h-8 w-8 animate-spin rounded-full border-2 border-primary border-t-transparent" />
            </div>
          ) : data && data.content.length > 0 ? (
            <div className="mt-10 grid gap-6 sm:grid-cols-2 lg:grid-cols-3">
              {data.content.map((course) => (
                <CourseCard key={course.id} course={course} />
              ))}
            </div>
          ) : (
            <div className="mt-16 text-center">
              <p className="text-5xl">📚</p>
              <p className="mt-4 text-lg text-muted-foreground">No courses found.</p>
              <p className="mt-1 text-sm text-muted-foreground">Try adjusting your search or check back later.</p>
            </div>
          )}

          {data && data.totalPages > 1 && (
            <div className="mt-10 text-center text-sm text-muted-foreground">
              Page {data.page + 1} of {data.totalPages} • {data.totalElements} total courses
            </div>
          )}
        </div>
      </main>
      <Footer />
    </div>
  );
}

function CourseCard({ course }: { course: CourseItem }) {
  const difficultyColors: Record<string, string> = {
    BEGINNER: "bg-green-500/10 text-green-400 border-green-500/20",
    INTERMEDIATE: "bg-yellow-500/10 text-yellow-400 border-yellow-500/20",
    ADVANCED: "bg-red-500/10 text-red-400 border-red-500/20",
  };

  return (
    <Link
      href={`/courses/${course.id}`}
      className="glass-card group block overflow-hidden transition-all hover:border-primary/30 hover:shadow-xl hover:shadow-primary/5"
    >
      <div className="p-6">
        <div className="flex items-center gap-2">
          <span className={`rounded-full border px-2.5 py-0.5 text-xs font-medium ${difficultyColors[course.difficulty] || ""}`}>
            {course.difficulty}
          </span>
          {course.isPremium && (
            <span className="rounded-full border border-primary/30 bg-primary/10 px-2.5 py-0.5 text-xs font-medium text-primary">
              ⭐ Premium
            </span>
          )}
        </div>

        <h3 className="mt-4 text-lg font-semibold text-white line-clamp-2 group-hover:text-primary transition-colors">
          {course.title}
        </h3>
        <p className="mt-2 text-sm leading-relaxed text-muted-foreground line-clamp-3">
          {course.description}
        </p>

        <div className="mt-5 flex items-center justify-between border-t border-white/5 pt-4">
          <span className="text-xs text-muted-foreground">{course.categoryName}</span>
          <div className="flex items-center gap-3 text-xs text-muted-foreground">
            <span>👥 {course.enrollmentCount}</span>
            {course.averageRating && <span>⭐ {course.averageRating}</span>}
          </div>
        </div>
        <p className="mt-2 text-xs text-muted-foreground">By {course.instructorName}</p>
      </div>
    </Link>
  );
}
