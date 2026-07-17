"use client";

import { useParams } from "next/navigation";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { api } from "@/lib/api-client";
import { Navbar } from "@/components/shared/navbar";
import Link from "next/link";

interface LessonDetail {
  id: string;
  moduleId: string;
  title: string;
  textContent: string;
  videoUrl: string;
  videoId: string;
  isPremium: boolean;
  orderIndex: number;
}

interface ProgressData {
  courseId: string;
  courseTitle: string;
  completionPercentage: number;
  completedCount: number;
  totalCount: number;
}

export default function LessonViewerPage() {
  const { courseId, lessonId } = useParams<{ courseId: string; lessonId: string }>();
  const queryClient = useQueryClient();

  const { data: lesson, isLoading } = useQuery({
    queryKey: ["lesson", lessonId],
    queryFn: () => api.get<LessonDetail>(`/lessons/${lessonId}`),
    enabled: !!lessonId,
  });

  const { data: progress } = useQuery({
    queryKey: ["progress", courseId],
    queryFn: () => api.get<ProgressData>(`/courses/${courseId}/progress`),
    enabled: !!courseId,
  });

  const completeMutation = useMutation({
    mutationFn: () => api.post<ProgressData>(`/lessons/${lessonId}/complete`),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["progress", courseId] });
    },
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

  return (
    <div className="flex min-h-screen flex-col bg-background">
      <Navbar />
      <main className="flex-1">
        <div className="mx-auto max-w-5xl px-4 py-8">
          {/* Progress Bar */}
          {progress && (
            <div className="mb-6 glass-card p-4">
              <div className="flex items-center justify-between text-sm">
                <span className="text-muted-foreground">{progress.courseTitle}</span>
                <span className="font-medium text-primary">{progress.completionPercentage}% complete</span>
              </div>
              <div className="mt-2 h-2 rounded-full bg-white/10">
                <div
                  className="h-full rounded-full bg-gradient-to-r from-primary to-pink-400 transition-all"
                  style={{ width: `${progress.completionPercentage}%` }}
                />
              </div>
              <p className="mt-1 text-xs text-muted-foreground">
                {progress.completedCount} / {progress.totalCount} lessons completed
              </p>
            </div>
          )}

          {/* Lesson Title */}
          <h1 className="text-2xl font-bold text-white">{lesson?.title}</h1>

          {/* YouTube Video */}
          {lesson?.videoId && (
            <div className="mt-6 overflow-hidden rounded-xl border border-white/10">
              <div className="relative pb-[56.25%]">
                <iframe
                  className="absolute inset-0 h-full w-full"
                  src={`https://www.youtube.com/embed/${lesson.videoId}`}
                  title={lesson.title}
                  allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
                  allowFullScreen
                />
              </div>
            </div>
          )}

          {/* Lesson Text Content */}
          {lesson?.textContent && (
            <div className="mt-8 glass-card p-6">
              <h2 className="text-lg font-semibold text-white mb-4">📝 Lesson Notes</h2>
              <div className="prose prose-invert max-w-none text-muted-foreground leading-relaxed whitespace-pre-wrap">
                {lesson.textContent}
              </div>
            </div>
          )}

          {/* Mark Complete Button */}
          <div className="mt-8 flex items-center justify-between">
            <Link
              href={`/courses/${courseId}`}
              className="rounded-lg border border-white/10 px-4 py-2 text-sm text-muted-foreground hover:text-white"
            >
              ← Back to Course
            </Link>
            <button
              onClick={() => completeMutation.mutate()}
              disabled={completeMutation.isPending}
              className="rounded-xl bg-green-600 px-8 py-3 font-medium text-white shadow-lg shadow-green-600/25 transition-all hover:bg-green-500 disabled:opacity-50"
            >
              {completeMutation.isPending ? "Completing..." : completeMutation.isSuccess ? "✓ Completed!" : "✓ Mark as Complete"}
            </button>
          </div>

          {/* XP Reward Notice */}
          {completeMutation.isSuccess && (
            <div className="mt-4 rounded-xl border border-green-500/20 bg-green-500/10 p-4 text-center">
              <p className="text-lg font-bold text-green-400">🎉 +10 XP earned!</p>
              <p className="text-sm text-muted-foreground">Great job! Keep going to maintain your streak.</p>
            </div>
          )}
        </div>
      </main>
    </div>
  );
}
