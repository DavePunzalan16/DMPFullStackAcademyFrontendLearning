"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { useQuery, useMutation } from "@tanstack/react-query";
import { api } from "@/lib/api-client";
import { Navbar } from "@/components/shared/navbar";
import { ApiError } from "@/types/api";

interface CategoryItem {
  id: string;
  name: string;
}

export default function CreateCoursePage() {
  const router = useRouter();
  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");
  const [categoryId, setCategoryId] = useState("");
  const [difficulty, setDifficulty] = useState("BEGINNER");
  const [error, setError] = useState("");

  const { data: categories } = useQuery({
    queryKey: ["categories"],
    queryFn: () => api.get<CategoryItem[]>("/categories"),
  });

  const createCourse = useMutation({
    mutationFn: () => api.post("/courses", { title, description, categoryId, difficulty, isPremium: false }),
    onSuccess: () => router.push("/instructor/dashboard"),
    onError: (err) => {
      if (err instanceof ApiError) setError(err.errorResponse.message);
      else setError("Failed to create course");
    },
  });

  return (
    <div className="flex min-h-screen flex-col">
      <Navbar />
      <main className="flex-1 py-8">
        <div className="mx-auto max-w-2xl px-4">
          <h1 className="text-3xl font-bold text-white">Create New Course</h1>
          <p className="mt-1 text-muted-foreground">Fill in the details to create your course</p>

          {error && (
            <div className="mt-4 rounded-lg bg-destructive/10 p-3 text-sm text-destructive">{error}</div>
          )}

          <div className="mt-8 space-y-6">
            <div className="space-y-2">
              <label className="text-sm font-medium text-white">Course Title</label>
              <input
                value={title} onChange={(e) => setTitle(e.target.value)}
                placeholder="e.g., Advanced React Patterns"
                className="w-full rounded-xl border border-white/10 bg-white/5 px-4 py-3 text-sm text-white placeholder:text-muted-foreground focus:border-primary focus:outline-none"
              />
            </div>

            <div className="space-y-2">
              <label className="text-sm font-medium text-white">Description</label>
              <textarea
                value={description} onChange={(e) => setDescription(e.target.value)}
                placeholder="Describe what students will learn..."
                rows={4}
                className="w-full rounded-xl border border-white/10 bg-white/5 px-4 py-3 text-sm text-white placeholder:text-muted-foreground focus:border-primary focus:outline-none resize-none"
              />
            </div>

            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-2">
                <label className="text-sm font-medium text-white">Category</label>
                <select
                  value={categoryId} onChange={(e) => setCategoryId(e.target.value)}
                  className="w-full rounded-xl border border-white/10 bg-white/5 px-4 py-3 text-sm text-white focus:border-primary focus:outline-none"
                >
                  <option value="">Select category</option>
                  {categories?.map((cat) => (
                    <option key={cat.id} value={cat.id}>{cat.name}</option>
                  ))}
                </select>
              </div>

              <div className="space-y-2">
                <label className="text-sm font-medium text-white">Difficulty</label>
                <select
                  value={difficulty} onChange={(e) => setDifficulty(e.target.value)}
                  className="w-full rounded-xl border border-white/10 bg-white/5 px-4 py-3 text-sm text-white focus:border-primary focus:outline-none"
                >
                  <option value="BEGINNER">Beginner</option>
                  <option value="INTERMEDIATE">Intermediate</option>
                  <option value="ADVANCED">Advanced</option>
                </select>
              </div>
            </div>

            <button
              onClick={() => createCourse.mutate()}
              disabled={createCourse.isPending || !title || !description || !categoryId}
              className="w-full rounded-xl bg-primary px-6 py-3 font-medium text-primary-foreground shadow-lg shadow-primary/25 hover:bg-primary/90 disabled:opacity-50"
            >
              {createCourse.isPending ? "Creating..." : "Create Course"}
            </button>
          </div>
        </div>
      </main>
    </div>
  );
}
