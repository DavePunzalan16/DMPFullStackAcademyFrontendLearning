"use client";

import { useQuery } from "@tanstack/react-query";
import { api } from "@/lib/api-client";
import { Navbar } from "@/components/shared/navbar";
import { Footer } from "@/components/shared/footer";
import Link from "next/link";
import type { PageResponse } from "@/types/api";

interface PathItem {
  id: string;
  name: string;
  description: string;
  courses: { courseId: string; title: string; orderIndex: number }[];
}

export default function LearningPathsPage() {
  const { data, isLoading } = useQuery({
    queryKey: ["learning-paths"],
    queryFn: () => api.get<PageResponse<PathItem>>("/learning-paths?size=20"),
  });

  return (
    <div className="flex min-h-screen flex-col">
      <Navbar />
      <main className="flex-1 py-12">
        <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
          <div className="text-center">
            <h1 className="text-4xl font-bold text-white">
              Learning <span className="gradient-text">Paths</span>
            </h1>
            <p className="mt-3 text-lg text-muted-foreground">
              Follow a structured track to go from beginner to job-ready developer
            </p>
          </div>

          {isLoading ? (
            <div className="mt-16 flex justify-center">
              <div className="h-8 w-8 animate-spin rounded-full border-2 border-primary border-t-transparent" />
            </div>
          ) : data && data.content.length > 0 ? (
            <div className="mt-12 grid gap-6 lg:grid-cols-3">
              {data.content.map((path) => (
                <PathCard key={path.id} path={path} />
              ))}
            </div>
          ) : (
            <div className="mt-16 text-center">
              <p className="text-5xl">🛤️</p>
              <p className="mt-4 text-lg text-muted-foreground">Learning paths coming soon!</p>
            </div>
          )}
        </div>
      </main>
      <Footer />
    </div>
  );
}

function PathCard({ path }: { path: PathItem }) {
  const courseCount = path.courses.length;
  const estimatedHours = Math.ceil(courseCount * 3);

  const pathIcons: Record<string, string> = {
    "Full-Stack": "🚀",
    "Frontend": "🎨",
    "Backend": "⚙️",
  };

  const icon = Object.entries(pathIcons).find(([key]) => path.name.includes(key))?.[1] || "📚";

  return (
    <Link href={`/paths/${path.id}`} className="glass-card group block p-6 transition-all hover:border-primary/30 hover:shadow-xl hover:shadow-primary/5">
      <span className="text-4xl">{icon}</span>
      <h2 className="mt-4 text-xl font-bold text-white group-hover:text-primary transition-colors">{path.name}</h2>
      <p className="mt-2 text-sm text-muted-foreground line-clamp-3">{path.description}</p>

      <div className="mt-6 flex items-center gap-4 text-xs text-muted-foreground">
        <span className="flex items-center gap-1">📚 {courseCount} courses</span>
        <span className="flex items-center gap-1">⏱️ ~{estimatedHours}h</span>
      </div>

      {/* Course list preview */}
      <div className="mt-4 space-y-1">
        {path.courses.slice(0, 4).map((course, i) => (
          <div key={course.courseId} className="flex items-center gap-2 text-xs text-muted-foreground">
            <span className="flex h-5 w-5 items-center justify-center rounded-full bg-primary/10 text-[10px] font-bold text-primary">
              {i + 1}
            </span>
            <span className="truncate">{course.title}</span>
          </div>
        ))}
        {path.courses.length > 4 && (
          <p className="text-xs text-muted-foreground pl-7">+{path.courses.length - 4} more courses</p>
        )}
      </div>

      <div className="mt-6 flex items-center justify-between">
        <span className="text-xs font-medium text-primary">View Path →</span>
      </div>
    </Link>
  );
}
