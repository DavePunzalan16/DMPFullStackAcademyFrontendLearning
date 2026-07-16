import Link from "next/link";
import { Navbar } from "@/components/shared/navbar";
import { Footer } from "@/components/shared/footer";

export default function Home() {
  return (
    <div className="flex min-h-screen flex-col">
      <Navbar />

      <main className="flex-1">
        {/* Hero Section */}
        <section className="py-20 sm:py-32">
          <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
            <div className="mx-auto max-w-3xl text-center">
              <h1 className="text-4xl font-bold tracking-tight sm:text-6xl">
                Learn Full Stack Development{" "}
                <span className="text-primary">The Fun Way</span>
              </h1>
              <p className="mt-6 text-lg text-muted-foreground">
                Master programming through hands-on courses, coding challenges, quizzes,
                and a gamified experience. Earn XP, level up, maintain streaks, and earn
                certificates — all for free.
              </p>
              <div className="mt-10 flex items-center justify-center gap-4">
                <Link
                  href="/register"
                  className="rounded-md bg-primary px-6 py-3 text-sm font-medium text-primary-foreground hover:bg-primary/90"
                >
                  Start Learning Free
                </Link>
                <Link
                  href="/courses"
                  className="rounded-md border border-border px-6 py-3 text-sm font-medium hover:bg-accent"
                >
                  Browse Courses
                </Link>
              </div>
            </div>
          </div>
        </section>

        {/* Features */}
        <section className="border-t border-border py-20">
          <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
            <h2 className="text-center text-3xl font-bold">Why DMP Academy?</h2>
            <div className="mt-12 grid gap-8 sm:grid-cols-2 lg:grid-cols-3">
              <FeatureCard
                title="🎮 Gamified Learning"
                description="Earn XP, level up, maintain daily streaks, and unlock badges as you learn."
              />
              <FeatureCard
                title="💻 Code Challenges"
                description="Practice with real coding problems that run in the browser with instant feedback."
              />
              <FeatureCard
                title="📹 Video Lessons"
                description="Learn from structured YouTube-based lessons created by expert instructors."
              />
              <FeatureCard
                title="📝 Quizzes"
                description="Test your understanding with multiple-choice quizzes and get instant scores."
              />
              <FeatureCard
                title="🏆 Certificates"
                description="Earn verifiable certificates upon completing courses to showcase your skills."
              />
              <FeatureCard
                title="🎯 Quests"
                description="Complete daily and weekly quests for bonus XP and stay motivated."
              />
            </div>
          </div>
        </section>

        {/* CTA */}
        <section className="border-t border-border py-20">
          <div className="mx-auto max-w-3xl px-4 text-center">
            <h2 className="text-3xl font-bold">Ready to Start?</h2>
            <p className="mt-4 text-muted-foreground">
              Join thousands of learners building real skills through practice.
            </p>
            <Link
              href="/register"
              className="mt-8 inline-block rounded-md bg-primary px-8 py-3 text-sm font-medium text-primary-foreground hover:bg-primary/90"
            >
              Create Free Account
            </Link>
          </div>
        </section>
      </main>

      <Footer />
    </div>
  );
}

function FeatureCard({ title, description }: { title: string; description: string }) {
  return (
    <div className="rounded-lg border border-border p-6">
      <h3 className="text-lg font-semibold">{title}</h3>
      <p className="mt-2 text-sm text-muted-foreground">{description}</p>
    </div>
  );
}
