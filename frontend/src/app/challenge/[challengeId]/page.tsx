"use client";

import { useState } from "react";
import { useParams } from "next/navigation";
import { Navbar } from "@/components/shared/navbar";

interface TestResult {
  testCaseIndex: number;
  passed: boolean;
  actualOutput: string;
  expectedOutput: string;
  error: string | null;
}

export default function ChallengeWorkspacePage() {
  const { challengeId } = useParams<{ challengeId: string }>();
  const [code, setCode] = useState(`function fizzBuzz(n) {\n  for (let i = 1; i <= n; i++) {\n    if (i % 15 === 0) console.log("FizzBuzz");\n    else if (i % 3 === 0) console.log("Fizz");\n    else if (i % 5 === 0) console.log("Buzz");\n    else console.log(i);\n  }\n}\n\nfizzBuzz(15);`);
  const [results, setResults] = useState<TestResult[] | null>(null);
  const [isRunning, setIsRunning] = useState(false);
  const [allPassed, setAllPassed] = useState(false);

  const handleRun = async () => {
    setIsRunning(true);
    setResults(null);

    // Simulate code execution (in production, calls POST /api/v1/challenges/{id}/submit)
    await new Promise((r) => setTimeout(r, 1500));

    const mockResults: TestResult[] = [
      { testCaseIndex: 0, passed: true, actualOutput: "1\n2\nFizz\n4\nBuzz", expectedOutput: "1\n2\nFizz\n4\nBuzz", error: null },
      { testCaseIndex: 1, passed: true, actualOutput: "1\n2\nFizz\n...\nFizzBuzz", expectedOutput: "1\n2\nFizz\n...\nFizzBuzz", error: null },
    ];

    setResults(mockResults);
    setAllPassed(mockResults.every((r) => r.passed));
    setIsRunning(false);
  };

  return (
    <div className="flex h-screen flex-col bg-background">
      <Navbar />

      {/* Challenge Header */}
      <div className="border-b border-white/10 bg-card px-4 py-3">
        <div className="mx-auto flex max-w-full items-center justify-between">
          <div>
            <h1 className="text-lg font-bold text-white">FizzBuzz Challenge</h1>
            <p className="text-xs text-muted-foreground">JavaScript • Timeout: 10s</p>
          </div>
          <div className="flex items-center gap-3">
            {allPassed && (
              <span className="rounded-full bg-green-500/10 border border-green-500/20 px-3 py-1 text-xs font-medium text-green-400">
                ✓ Completed • +50 XP
              </span>
            )}
            <button
              onClick={handleRun}
              disabled={isRunning}
              className="rounded-lg bg-green-600 px-6 py-2 text-sm font-medium text-white shadow-lg shadow-green-600/25 hover:bg-green-500 disabled:opacity-50"
            >
              {isRunning ? "⏳ Running..." : "▶ Run Code"}
            </button>
          </div>
        </div>
      </div>

      {/* Split Pane */}
      <div className="flex flex-1 overflow-hidden">
        {/* Left: Problem Description */}
        <div className="w-2/5 overflow-y-auto border-r border-white/10 p-6">
          <h2 className="text-lg font-semibold text-white">Problem</h2>
          <div className="mt-4 text-sm leading-relaxed text-muted-foreground">
            <p>Write a function that prints numbers from 1 to n.</p>
            <ul className="mt-3 list-disc space-y-1 pl-5">
              <li>For multiples of 3 print <code className="rounded bg-white/10 px-1 text-primary">&quot;Fizz&quot;</code></li>
              <li>For multiples of 5 print <code className="rounded bg-white/10 px-1 text-primary">&quot;Buzz&quot;</code></li>
              <li>For multiples of both print <code className="rounded bg-white/10 px-1 text-primary">&quot;FizzBuzz&quot;</code></li>
            </ul>
          </div>

          <h3 className="mt-6 text-sm font-semibold text-white">Test Cases</h3>
          <div className="mt-3 space-y-3">
            <div className="rounded-lg border border-white/10 bg-white/5 p-3">
              <p className="text-xs text-muted-foreground">Input: <span className="text-white">5</span></p>
              <p className="text-xs text-muted-foreground mt-1">Expected: <span className="text-white">1, 2, Fizz, 4, Buzz</span></p>
            </div>
          </div>

          {/* Test Results */}
          {results && (
            <div className="mt-6">
              <h3 className="text-sm font-semibold text-white">Results</h3>
              <div className="mt-3 space-y-2">
                {results.map((r, i) => (
                  <div
                    key={i}
                    className={`rounded-lg border p-3 ${
                      r.passed
                        ? "border-green-500/20 bg-green-500/5"
                        : "border-red-500/20 bg-red-500/5"
                    }`}
                  >
                    <div className="flex items-center gap-2">
                      <span className={r.passed ? "text-green-400" : "text-red-400"}>
                        {r.passed ? "✓" : "✗"}
                      </span>
                      <span className="text-sm text-white">Test Case {i + 1}</span>
                    </div>
                    {!r.passed && r.error && (
                      <p className="mt-1 text-xs text-red-400">{r.error}</p>
                    )}
                  </div>
                ))}
              </div>
              {allPassed && (
                <div className="mt-4 rounded-xl border border-green-500/20 bg-green-500/10 p-4 text-center">
                  <p className="text-lg font-bold text-green-400">🎉 All tests passed!</p>
                  <p className="text-sm text-muted-foreground">+50 XP earned</p>
                </div>
              )}
            </div>
          )}
        </div>

        {/* Right: Code Editor */}
        <div className="flex flex-1 flex-col">
          <div className="flex items-center border-b border-white/10 bg-card px-4 py-2">
            <span className="text-xs text-muted-foreground">solution.js</span>
          </div>
          <div className="flex-1 p-0">
            <textarea
              value={code}
              onChange={(e) => setCode(e.target.value)}
              spellCheck={false}
              className="h-full w-full resize-none bg-[#0d1117] p-4 font-mono text-sm text-green-300 focus:outline-none"
              style={{ tabSize: 2 }}
            />
          </div>
        </div>
      </div>
    </div>
  );
}
