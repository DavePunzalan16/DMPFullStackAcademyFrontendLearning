"use client";

import Link from "next/link";
import Image from "next/image";
import { useAuthStore } from "@/stores/auth-store";
import { useLogout } from "@/features/auth/hooks/use-auth";
import { useRouter } from "next/navigation";

export function Navbar() {
  const { user, isAuthenticated } = useAuthStore();
  const logout = useLogout();
  const router = useRouter();

  const handleLogout = async () => {
    await logout.mutateAsync();
    router.push("/");
  };

  return (
    <nav className="sticky top-0 z-50 border-b border-white/10 bg-background/80 backdrop-blur-xl" aria-label="Main navigation">
      <div className="mx-auto flex h-16 max-w-7xl items-center justify-between px-4 sm:px-6 lg:px-8">
        <Link href="/" className="flex items-center gap-2">
          <Image
            src="/images/DmpFSALogo.jpg"
            alt="DMP Academy Logo"
            width={36}
            height={36}
            className="rounded-lg"
          />
          <span className="text-lg font-bold tracking-tight text-white">
            DMP <span className="text-primary">Academy</span>
          </span>
        </Link>

        <div className="flex items-center gap-6">
          <Link href="/courses" className="text-sm text-muted-foreground transition-colors hover:text-white">
            Courses
          </Link>
          <Link href="/paths" className="text-sm text-muted-foreground transition-colors hover:text-white">
            Paths
          </Link>

          {isAuthenticated && user ? (
            <>
              {user.role === "STUDENT" && (
                <Link href="/dashboard" className="text-sm text-muted-foreground transition-colors hover:text-white">
                  Dashboard
                </Link>
              )}
              {user.role === "INSTRUCTOR" && (
                <Link href="/instructor/dashboard" className="text-sm text-muted-foreground transition-colors hover:text-white">
                  Instructor
                </Link>
              )}
              {user.role === "ADMIN" && (
                <Link href="/admin/dashboard" className="text-sm text-muted-foreground transition-colors hover:text-white">
                  Admin
                </Link>
              )}
              <div className="flex items-center gap-3">
                <div className="flex items-center gap-2 rounded-full border border-white/10 bg-white/5 px-3 py-1">
                  <span className="text-xs text-primary">⭐ Lv.{user.level}</span>
                  <span className="text-xs text-muted-foreground">{user.xpTotal} XP</span>
                </div>
                <span className="text-sm font-medium text-white">
                  {user.displayName}
                </span>
                <button
                  onClick={handleLogout}
                  className="rounded-lg border border-white/10 px-3 py-1.5 text-xs text-muted-foreground transition-colors hover:border-white/20 hover:text-white"
                >
                  Logout
                </button>
              </div>
            </>
          ) : (
            <div className="flex items-center gap-3">
              <Link
                href="/login"
                className="rounded-lg border border-white/10 px-4 py-2 text-sm text-white transition-colors hover:border-white/20 hover:bg-white/5"
              >
                Sign In
              </Link>
              <Link
                href="/register"
                className="rounded-lg bg-primary px-4 py-2 text-sm font-medium text-primary-foreground transition-all hover:bg-primary/90 hover:shadow-lg hover:shadow-primary/25"
              >
                Get Started
              </Link>
            </div>
          )}
        </div>
      </div>
    </nav>
  );
}
