"use client";

import Link from "next/link";
import { useQuery } from "@tanstack/react-query";
import { api } from "@/lib/api-client";

interface ModuleWithLessons {
  id: string;
  title: string;
  orderIndex: number;
  lessons: { id: string; title: string; orderIndex: number }[];
}

interface LessonSidebarProps {
  courseId: string;
  currentLessonId: string;
  completedLessonIds?: string[];
}

export function LessonSidebar({ courseId, currentLessonId, completedLessonIds = [] }: LessonSidebarProps) {
  const { data: modules } = useQuery({
    queryKey: ["course-modules", courseId],
    queryFn: () => api.get<ModuleWithLessons[]>(`/courses/${courseId}/modules`),
    enabled: !!courseId,
  });

  // For now, fetch lessons per module from the modules list
  // In production, use /courses/{id}/lessons-tree endpoint

  return (
    <aside className="h-full w-64 overflow-y-auto border-r border-white/10 bg-card/50 p-4">
      <h3 className="text-xs font-semibold uppercase tracking-wider text-muted-foreground">Course Content</h3>

      {modules ? (
        <nav className="mt-4 space-y-4" aria-label="Lesson navigation">
          {modules.map((module) => (
            <div key={module.id}>
              <p className="text-xs font-medium text-white/80">{module.title}</p>
              {/* Lessons will be listed when the tree endpoint is available */}
              <div className="mt-1 text-xs text-muted-foreground pl-2">
                Module {module.orderIndex}
              </div>
            </div>
          ))}
        </nav>
      ) : (
        <div className="mt-4 space-y-2">
          {[1, 2, 3].map((i) => (
            <div key={i} className="h-6 animate-pulse rounded bg-white/5" />
          ))}
        </div>
      )}
    </aside>
  );
}
