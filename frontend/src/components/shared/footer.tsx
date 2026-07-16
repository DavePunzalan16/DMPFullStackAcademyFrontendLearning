import Image from "next/image";
import Link from "next/link";

export function Footer() {
  return (
    <footer className="border-t border-white/10 bg-background py-12">
      <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
        <div className="flex flex-col items-center justify-between gap-6 sm:flex-row">
          <div className="flex items-center gap-3">
            <Image
              src="/images/DmpFSALogo.jpg"
              alt="DMP Academy"
              width={28}
              height={28}
              className="rounded-md"
            />
            <span className="text-sm font-medium text-white">DMP Full Stack Academy</span>
          </div>
          <div className="flex items-center gap-6 text-sm text-muted-foreground">
            <Link href="/courses" className="hover:text-white transition-colors">Courses</Link>
            <Link href="/register" className="hover:text-white transition-colors">Get Started</Link>
          </div>
          <p className="text-xs text-muted-foreground">
            © 2026 DMP Academy. Built with ❤️ for learners.
          </p>
        </div>
      </div>
    </footer>
  );
}
