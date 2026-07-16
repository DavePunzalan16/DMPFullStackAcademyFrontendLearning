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
          <h1 className="text-3xl font-bold">Explore Courses</h1>
          <p className="mt-2 text-muted-foreground">Find your next learning adventure</p>

          {/* Filters */}
          <div className="mt-6 flex flex-col gap-4 sm:flex-row">
            <input
              type="text"
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              placeholder="Search courses..."
              className="flex-1 rounded-md border border-input bg-background px-3 py-2 text-sm placeholder:text-muted-foreground focus:outline-none focus:ring-2 focus:ring-ring"
            />
            <select
              value={difficulty}
              onChange={(e) => setDifficulty(e.target.value)}
              className="rounded-md border border-input bg-background px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-ring"
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
            <div className="mt-12 text-center text-muted-foreground">Loading courses...</div>
          ) : data && data.content.length > 0 ? (
            <div className="mt-8 grid gap-6 sm:grid-cols-2 lg:grid-cols-3">
              {data.content.map((course) => (
                <CourseCard key={course.id} course={course} />
              ))}
            </div>
          ) : (
            <div className="mt-12 text-center">
              <p className="text-lg text-muted-foreground">No courses found.</p>
              <p className="mt-2 text-sm text-muted-foreground">Try adjusting your search or filters.</p>
            </div>
          )}

          {data && data.totalPages > 1 && (
            <div className="mt-8 text-center text-sm text-muted-foreground">
              Showing page {data.page + 1} of {data.totalPages} ({data.totalElements} courses)
            </div>
          )}
        </div>
      </main>
      <Footer />
    </div>
  );
}

function CourseCard({ course }: { course: CourseItem }) {
  return (
    <Link href={`/courses/${course.id}`} className="block rounded-lg border border-border p-6 hover:border-primary hover:shadow-sm transition-all">
      <div className="flex items-center gap-2">
        <span className={`rounded-full px-2 py-0.5 text-xs font-medium ${
          course.difficulty === "BEGINNER" ? "bg-green-500/10 text-green-500" :
          course.difficulty === "INTERMEDIATE" ? "bg-yellow-500/10 text-yellow-500" :
          "bg-red-500/10 text-red-500"
        }`}>
          {course.difficulty}
        </span>
        {course.isPremium && (
          <span className="rounded-full bg-primary/10 px-2 py-0.5 text-xs font-medium text-primary">
            Premium
          </span>
        )}
      </div>
      <h3 className="mt-3 text-lg font-semibold line-clamp-2">{course.title}</h3>
      <p className="mt-2 text-sm text-muted-foreground line-clamp-3">{course.description}</p>
      <div className="mt-4 flex items-center justify-between text-xs text-muted-foreground">
        <span>{course.categoryName}</span>
        <span>{course.enrollmentCount} enrolled</span>
      </div>
      <div className="mt-2 text-xs text-muted-foreground">
        By {course.instructorName}
        {course.averageRating && ` • ⭐ ${course.averageRating}`}
      </div>
    </Link>
  );
}
