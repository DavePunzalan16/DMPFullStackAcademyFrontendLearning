"use client";

import { useParams } from "next/navigation";
import { useQuery } from "@tanstack/react-query";
import { api } from "@/lib/api-client";
import { Navbar } from "@/components/shared/navbar";
import { Footer } from "@/components/shared/footer";
import Link from "next/link";

interface PathDetail {
  id: string;
  name: string;
  description: string;
  courses: { courseId: string; title: string; orderIndex: number }[];
}

export default function PathDetailPage() {
  const { pathId } = useParams<{ pathId: string }>();

  const { data: path, isLoading } = useQuery({
    queryKey: ["learning-path", pathId],
    queryFn: () => api.get<PathDetail>(`/learning-paths/${pathId}`),
    enabled: !!pathId,
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

  if (!path) {
    return (
      <div className="flex min-h-screen flex-col">
        <Navbar />
        <div className="flex flex-1 items-center justify-center">
          <p className="text-muted-foreground">Path not found.</p>
        </div>
      </div>
    );
  }

  return (
    <div className="flex min-h-screen flex-col">
      <Navbar />
      <main className="flex-1 py-12">
        <div className="mx-auto max-w-3xl px-4 sm:px-6 lg:px-8">
          {/* Path Header */}
          <div className="text-center">
            <Link href="/paths" className="text-sm text-muted-foreground hover:text-primary">
              ← All Learning Paths
            </Link>
            <h1 className="mt-4 text-3xl font-bold text-white">{path.name}</h1>
            <p className="mt-3 text-muted-foreground">{path.description}</p>
            <div className="mt-4 flex items-center justify-center gap-4 text-sm text-muted-foreground">
              <span>📚 {path.courses.length} courses</span>
              <span>⏱️ ~{path.courses.length * 3}h estimated</span>
            </div>
          </div>

          {/* Course Track */}
          <div className="relative mt-12">
            {/* Vertical connector line */}
            <div className="absolute left-6 top-0 bottom-0 w-0.5 bg-gradient-to-b from-primary via-primary/50 to-primary/20" />

            <div className="space-y-6">
              {path.courses.map((course, index) => (
                <div key={course.courseId} className="relative flex items-start gap-4">
                  {/* Node */}
                  <div className="relative z-10 flex h-12 w-12 flex-shrink-0 items-center justify-center rounded-full border-2 border-primary bg-background">
                    <span className="text-sm font-bold text-primary">{index + 1}</span>
                  </div>

                  {/* Course Card */}
                  <Link
                    href={`/courses/${course.courseId}`}
                    className="glass-card flex-1 p-5 transition-all hover:border-primary/30 hover:shadow-lg hover:shadow-primary/5"
                  >
                    <h3 className="font-semibold text-white">{course.title}</h3>
                    <div className="mt-2 flex items-center gap-3 text-xs text-muted-foreground">
                      <span>Step {index + 1} of {path.courses.length}</span>
                      <span className="rounded-full bg-white/5 px-2 py-0.5">Start →</span>
                    </div>
                  </Link>
                </div>
              ))}
            </div>

            {/* Completion node */}
            <div className="relative mt-6 flex items-center gap-4">
              <div className="relative z-10 flex h-12 w-12 flex-shrink-0 items-center justify-center rounded-full border-2 border-green-500 bg-background">
                <span className="text-lg">🏆</span>
              </div>
              <div className="glass-card flex-1 p-5 border-green-500/20">
                <h3 className="font-semibold text-green-400">Path Complete!</h3>
                <p className="mt-1 text-xs text-muted-foreground">Earn a path completion certificate</p>
              </div>
            </div>
          </div>

          {/* CTA */}
          <div className="mt-12 text-center">
            <Link
              href={path.courses.length > 0 ? `/courses/${path.courses[0].courseId}` : "/courses"}
              className="inline-block rounded-xl bg-primary px-8 py-3 font-medium text-primary-foreground shadow-lg shadow-primary/25 hover:bg-primary/90"
            >
              Start This Path
            </Link>
          </div>
        </div>
      </main>
      <Footer />
    </div>
  );
}
