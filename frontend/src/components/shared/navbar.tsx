"use client";

import Link from "next/link";
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
    <nav className="border-b border-border bg-card" aria-label="Main navigation">
      <div className="mx-auto flex h-16 max-w-7xl items-center justify-between px-4 sm:px-6 lg:px-8">
        <Link href="/" className="text-xl font-bold text-primary">
          DMP Academy
        </Link>

        <div className="flex items-center gap-4">
          <Link href="/courses" className="text-sm text-muted-foreground hover:text-foreground">
            Courses
          </Link>

          {isAuthenticated && user ? (
            <>
              {user.role === "STUDENT" && (
                <Link href="/dashboard" className="text-sm text-muted-foreground hover:text-foreground">
                  Dashboard
                </Link>
              )}
              {user.role === "INSTRUCTOR" && (
                <Link href="/instructor/dashboard" className="text-sm text-muted-foreground hover:text-foreground">
                  Instructor
                </Link>
              )}
              {user.role === "ADMIN" && (
                <Link href="/admin/dashboard" className="text-sm text-muted-foreground hover:text-foreground">
                  Admin
                </Link>
              )}
              <span className="text-sm text-muted-foreground">
                {user.displayName}
              </span>
              <button
                onClick={handleLogout}
                className="rounded-md border border-border px-3 py-1.5 text-sm text-muted-foreground hover:bg-accent"
              >
                Logout
              </button>
            </>
          ) : (
            <>
              <Link
                href="/login"
                className="rounded-md border border-border px-3 py-1.5 text-sm hover:bg-accent"
              >
                Sign In
              </Link>
              <Link
                href="/register"
                className="rounded-md bg-primary px-3 py-1.5 text-sm font-medium text-primary-foreground hover:bg-primary/90"
              >
                Sign Up
              </Link>
            </>
          )}
        </div>
      </div>
    </nav>
  );
}
