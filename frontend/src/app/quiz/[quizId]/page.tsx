"use client";

import { useState } from "react";
import { useParams } from "next/navigation";
import { useMutation } from "@tanstack/react-query";
import { api } from "@/lib/api-client";
import { Navbar } from "@/components/shared/navbar";
import Link from "next/link";

interface QuizResult {
  score: number;
  passed: boolean;
  totalQuestions: number;
  correctAnswers: number;
  questionResults: { questionId: string; correct: boolean; correctOptionId: string }[];
}

export default function QuizPlayerPage() {
  const { quizId } = useParams<{ quizId: string }>();
  const [answers, setAnswers] = useState<Record<string, string>>({});
  const [currentQuestion, setCurrentQuestion] = useState(0);
  const [result, setResult] = useState<QuizResult | null>(null);

  // Mock quiz data - in production this comes from API
  const quiz = {
    id: quizId,
    title: "Git Basics Quiz",
    passingScore: 70,
    questions: [
      {
        id: "q1",
        questionText: "What command initializes a new Git repository?",
        options: [
          { id: "o1", optionText: "git init" },
          { id: "o2", optionText: "git start" },
          { id: "o3", optionText: "git new" },
          { id: "o4", optionText: "git create" },
        ],
      },
      {
        id: "q2",
        questionText: 'What does "git add" do?',
        options: [
          { id: "o5", optionText: "Stages changes for the next commit" },
          { id: "o6", optionText: "Deletes files from the repo" },
          { id: "o7", optionText: "Pushes code to GitHub" },
        ],
      },
      {
        id: "q3",
        questionText: "What is a commit in Git?",
        options: [
          { id: "o8", optionText: "A snapshot of your staged changes" },
          { id: "o9", optionText: "A branch of code" },
          { id: "o10", optionText: "A backup file" },
        ],
      },
    ],
  };

  const submitMutation = useMutation({
    mutationFn: () => api.post<QuizResult>(`/quizzes/${quizId}/submit`, { answers }),
    onSuccess: (data) => setResult(data),
    onError: () => {
      // Demo mode - show mock result
      const mockCorrect = Object.keys(answers).length;
      setResult({
        score: Math.floor((mockCorrect / quiz.questions.length) * 100),
        passed: mockCorrect >= 2,
        totalQuestions: quiz.questions.length,
        correctAnswers: mockCorrect,
        questionResults: [],
      });
    },
  });

  const selectAnswer = (questionId: string, optionId: string) => {
    setAnswers((prev) => ({ ...prev, [questionId]: optionId }));
  };

  if (result) {
    return (
      <div className="flex min-h-screen flex-col bg-background">
        <Navbar />
        <main className="flex flex-1 items-center justify-center p-4">
          <div className="w-full max-w-lg glass-card p-8 text-center">
            <div className={`text-6xl ${result.passed ? "" : "grayscale"}`}>
              {result.passed ? "🎉" : "😔"}
            </div>
            <h1 className="mt-4 text-3xl font-bold text-white">
              {result.passed ? "Congratulations!" : "Keep Trying!"}
            </h1>
            <p className="mt-2 text-muted-foreground">
              {result.passed ? "You passed the quiz!" : "You didn't reach the passing score."}
            </p>

            <div className="mt-6 flex items-center justify-center gap-8">
              <div>
                <p className={`text-4xl font-bold ${result.passed ? "text-green-400" : "text-red-400"}`}>
                  {result.score}%
                </p>
                <p className="text-sm text-muted-foreground">Your Score</p>
              </div>
              <div>
                <p className="text-4xl font-bold text-white">{quiz.passingScore}%</p>
                <p className="text-sm text-muted-foreground">Passing Score</p>
              </div>
            </div>

            <p className="mt-4 text-sm text-muted-foreground">
              {result.correctAnswers} / {result.totalQuestions} correct answers
            </p>

            {result.passed && (
              <div className="mt-6 rounded-xl border border-primary/20 bg-primary/10 p-4">
                <p className="font-medium text-primary">🎯 +25 XP earned!</p>
              </div>
            )}

            <div className="mt-8 flex gap-4 justify-center">
              <Link href="/dashboard" className="rounded-lg border border-white/10 px-4 py-2 text-sm text-muted-foreground hover:text-white">
                Back to Dashboard
              </Link>
              {!result.passed && (
                <button
                  onClick={() => { setResult(null); setAnswers({}); setCurrentQuestion(0); }}
                  className="rounded-lg bg-primary px-4 py-2 text-sm font-medium text-primary-foreground"
                >
                  Try Again
                </button>
              )}
            </div>
          </div>
        </main>
      </div>
    );
  }

  const question = quiz.questions[currentQuestion];
  const totalQuestions = quiz.questions.length;
  const answeredCount = Object.keys(answers).length;

  return (
    <div className="flex min-h-screen flex-col bg-background">
      <Navbar />
      <main className="flex flex-1 items-center justify-center p-4">
        <div className="w-full max-w-2xl">
          {/* Quiz Header */}
          <div className="mb-6 flex items-center justify-between">
            <h1 className="text-xl font-bold text-white">{quiz.title}</h1>
            <span className="rounded-full bg-white/5 border border-white/10 px-3 py-1 text-sm text-muted-foreground">
              {currentQuestion + 1} / {totalQuestions}
            </span>
          </div>

          {/* Progress */}
          <div className="mb-8 h-1.5 rounded-full bg-white/10">
            <div
              className="h-full rounded-full bg-primary transition-all"
              style={{ width: `${((currentQuestion + 1) / totalQuestions) * 100}%` }}
            />
          </div>

          {/* Question */}
          <div className="glass-card p-8">
            <p className="text-lg font-medium text-white">{question.questionText}</p>

            <div className="mt-6 space-y-3">
              {question.options.map((option) => (
                <button
                  key={option.id}
                  onClick={() => selectAnswer(question.id, option.id)}
                  className={`w-full rounded-xl border p-4 text-left text-sm transition-all ${
                    answers[question.id] === option.id
                      ? "border-primary bg-primary/10 text-white"
                      : "border-white/10 text-muted-foreground hover:border-white/20 hover:text-white"
                  }`}
                >
                  <span className={`mr-3 inline-flex h-6 w-6 items-center justify-center rounded-full border text-xs ${
                    answers[question.id] === option.id
                      ? "border-primary bg-primary text-primary-foreground"
                      : "border-white/20"
                  }`}>
                    {String.fromCharCode(65 + question.options.indexOf(option))}
                  </span>
                  {option.optionText}
                </button>
              ))}
            </div>
          </div>

          {/* Navigation */}
          <div className="mt-6 flex items-center justify-between">
            <button
              onClick={() => setCurrentQuestion((c) => Math.max(0, c - 1))}
              disabled={currentQuestion === 0}
              className="rounded-lg border border-white/10 px-4 py-2 text-sm text-muted-foreground hover:text-white disabled:opacity-30"
            >
              ← Previous
            </button>

            {currentQuestion < totalQuestions - 1 ? (
              <button
                onClick={() => setCurrentQuestion((c) => c + 1)}
                className="rounded-lg bg-white/10 px-4 py-2 text-sm font-medium text-white hover:bg-white/20"
              >
                Next →
              </button>
            ) : (
              <button
                onClick={() => submitMutation.mutate()}
                disabled={answeredCount < totalQuestions}
                className="rounded-xl bg-primary px-6 py-2.5 text-sm font-medium text-primary-foreground shadow-lg shadow-primary/25 hover:bg-primary/90 disabled:opacity-50"
              >
                Submit Quiz ({answeredCount}/{totalQuestions} answered)
              </button>
            )}
          </div>
        </div>
      </main>
    </div>
  );
}
