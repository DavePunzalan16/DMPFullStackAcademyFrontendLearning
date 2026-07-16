import Link from "next/link";
import Image from "next/image";
import { Navbar } from "@/components/shared/navbar";
import { Footer } from "@/components/shared/footer";

export default function Home() {
  return (
    <div className="flex min-h-screen flex-col bg-background">
      <Navbar />

      <main className="flex-1">
        {/* Hero Section */}
        <section className="relative overflow-hidden py-24 sm:py-32">
          {/* Background gradient orbs */}
          <div className="absolute -top-40 left-1/4 h-[500px] w-[500px] rounded-full bg-purple-600/20 blur-[128px]" />
          <div className="absolute -bottom-40 right-1/4 h-[400px] w-[400px] rounded-full bg-pink-600/10 blur-[128px]" />

          <div className="relative mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
            <div className="flex flex-col items-center gap-12 lg:flex-row lg:gap-16">
              {/* Text */}
              <div className="flex-1 text-center lg:text-left">
                <div className="inline-flex items-center gap-2 rounded-full border border-white/10 bg-white/5 px-4 py-1.5 text-sm text-muted-foreground">
                  <span className="inline-block h-2 w-2 rounded-full bg-green-400 animate-pulse" />
                  Free to learn • No credit card required
                </div>

                <h1 className="mt-6 text-4xl font-bold leading-tight tracking-tight sm:text-5xl lg:text-6xl">
                  Master Full Stack
                  <br />
                  <span className="gradient-text">Development</span>
                  <br />
                  By Doing
                </h1>

                <p className="mt-6 max-w-lg text-lg text-muted-foreground lg:text-xl">
                  Learn through hands-on coding challenges, video lessons, quizzes — and earn XP,
                  level up, and collect badges along the way.
                </p>

                <div className="mt-8 flex flex-col items-center gap-4 sm:flex-row lg:justify-start">
                  <Link
                    href="/register"
                    className="w-full rounded-xl bg-primary px-8 py-3.5 text-center font-medium text-primary-foreground shadow-lg shadow-primary/25 transition-all hover:bg-primary/90 hover:shadow-xl hover:shadow-primary/30 sm:w-auto"
                  >
                    Start Learning Free →
                  </Link>
                  <Link
                    href="/courses"
                    className="w-full rounded-xl border border-white/10 px-8 py-3.5 text-center font-medium text-white transition-colors hover:border-white/20 hover:bg-white/5 sm:w-auto"
                  >
                    Browse Courses
                  </Link>
                </div>

                <div className="mt-8 flex items-center justify-center gap-6 lg:justify-start">
                  <Stat value="50+" label="Courses" />
                  <Stat value="1000+" label="Students" />
                  <Stat value="50" label="Levels" />
                </div>
              </div>

              {/* Hero Image */}
              <div className="flex-1">
                <div className="relative mx-auto max-w-md lg:max-w-none">
                  <div className="glow rounded-2xl border border-white/10 bg-white/5 p-3">
                    <Image
                      src="/images/DMP_Full_Stack_Academy.jpg"
                      alt="DMP Full Stack Academy"
                      width={600}
                      height={400}
                      className="rounded-xl"
                      priority
                    />
                  </div>
                </div>
              </div>
            </div>
          </div>
        </section>

        {/* Features */}
        <section className="border-t border-white/5 py-24">
          <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
            <div className="text-center">
              <h2 className="text-3xl font-bold sm:text-4xl">
                Everything you need to{" "}
                <span className="gradient-text">level up</span>
              </h2>
              <p className="mt-4 text-lg text-muted-foreground">
                A complete gamified learning platform built for developers
              </p>
            </div>

            <div className="mt-16 grid gap-6 sm:grid-cols-2 lg:grid-cols-3">
              <FeatureCard
                icon="🎮"
                title="Gamified Experience"
                description="Earn XP for every lesson, quiz, and challenge. Level up through 50 tiers and show off your progress."
              />
              <FeatureCard
                icon="💻"
                title="Live Code Execution"
                description="Write and run code directly in your browser. Instant feedback with automated test case validation."
              />
              <FeatureCard
                icon="🔥"
                title="Daily Streaks"
                description="Build a learning habit with daily streaks. Hit 7, 30, or 100 days to earn exclusive badges."
              />
              <FeatureCard
                icon="📹"
                title="Video Lessons"
                description="Structured YouTube-based lessons from expert instructors. Learn at your own pace."
              />
              <FeatureCard
                icon="🏆"
                title="Certificates"
                description="Complete courses to earn verifiable digital certificates you can share anywhere."
              />
              <FeatureCard
                icon="🎯"
                title="Quests & Badges"
                description="Complete daily quests for bonus XP. Unlock 9+ achievement badges as you progress."
              />
            </div>
          </div>
        </section>

        {/* How it works */}
        <section className="border-t border-white/5 py-24">
          <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
            <h2 className="text-center text-3xl font-bold sm:text-4xl">How it works</h2>
            <div className="mt-16 grid gap-8 sm:grid-cols-3">
              <Step number="01" title="Sign Up Free" description="Create your account in seconds and start browsing courses immediately." />
              <Step number="02" title="Learn & Practice" description="Watch lessons, take quizzes, solve coding challenges — earn XP for each." />
              <Step number="03" title="Get Certified" description="Complete courses, earn certificates, and level up your developer career." />
            </div>
          </div>
        </section>

        {/* CTA */}
        <section className="border-t border-white/5 py-24">
          <div className="mx-auto max-w-3xl px-4 text-center">
            <div className="glass-card p-12">
              <Image
                src="/images/DMPdark.jpg"
                alt="DMP Academy"
                width={80}
                height={80}
                className="mx-auto rounded-xl"
              />
              <h2 className="mt-6 text-3xl font-bold">Ready to start your journey?</h2>
              <p className="mt-4 text-muted-foreground">
                Join the community of developers learning full-stack development the fun way.
              </p>
              <Link
                href="/register"
                className="mt-8 inline-block rounded-xl bg-primary px-10 py-4 font-medium text-primary-foreground shadow-lg shadow-primary/25 transition-all hover:bg-primary/90 hover:shadow-xl"
              >
                Create Free Account
              </Link>
            </div>
          </div>
        </section>
      </main>

      <Footer />
    </div>
  );
}

function Stat({ value, label }: { value: string; label: string }) {
  return (
    <div className="text-center">
      <p className="text-2xl font-bold text-white">{value}</p>
      <p className="text-xs text-muted-foreground">{label}</p>
    </div>
  );
}

function FeatureCard({ icon, title, description }: { icon: string; title: string; description: string }) {
  return (
    <div className="glass-card p-6 transition-all hover:border-primary/30 hover:shadow-lg hover:shadow-primary/5">
      <span className="text-3xl">{icon}</span>
      <h3 className="mt-4 text-lg font-semibold text-white">{title}</h3>
      <p className="mt-2 text-sm leading-relaxed text-muted-foreground">{description}</p>
    </div>
  );
}

function Step({ number, title, description }: { number: string; title: string; description: string }) {
  return (
    <div className="text-center">
      <span className="inline-flex h-12 w-12 items-center justify-center rounded-full bg-primary/10 text-lg font-bold text-primary">
        {number}
      </span>
      <h3 className="mt-4 text-lg font-semibold text-white">{title}</h3>
      <p className="mt-2 text-sm text-muted-foreground">{description}</p>
    </div>
  );
}
