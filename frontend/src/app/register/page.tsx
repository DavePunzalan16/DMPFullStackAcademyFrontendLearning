import Image from "next/image";
import { RegisterForm } from "@/features/auth/components/register-form";
import Link from "next/link";

export default function RegisterPage() {
  return (
    <main className="flex min-h-screen">
      {/* Left panel - branding */}
      <div className="hidden w-1/2 flex-col items-center justify-center bg-gradient-to-br from-purple-900/40 to-background p-12 lg:flex">
        <Image
          src="/images/DMPdark.jpg"
          alt="DMP Academy"
          width={300}
          height={300}
          className="rounded-2xl shadow-2xl"
        />
        <h2 className="mt-8 text-2xl font-bold text-white">Start Your Journey</h2>
        <p className="mt-2 text-center text-muted-foreground">
          Join thousands of learners building real-world skills through practice.
        </p>
        <div className="mt-6 flex gap-4">
          <div className="rounded-lg border border-white/10 bg-white/5 px-4 py-2 text-center">
            <p className="text-lg font-bold text-primary">50</p>
            <p className="text-xs text-muted-foreground">Levels</p>
          </div>
          <div className="rounded-lg border border-white/10 bg-white/5 px-4 py-2 text-center">
            <p className="text-lg font-bold text-primary">9+</p>
            <p className="text-xs text-muted-foreground">Badges</p>
          </div>
          <div className="rounded-lg border border-white/10 bg-white/5 px-4 py-2 text-center">
            <p className="text-lg font-bold text-primary">Free</p>
            <p className="text-xs text-muted-foreground">Forever</p>
          </div>
        </div>
      </div>

      {/* Right panel - form */}
      <div className="flex flex-1 flex-col items-center justify-center p-6">
        <Link href="/" className="mb-8 flex items-center gap-2">
          <Image src="/images/DmpFSALogo.jpg" alt="Logo" width={32} height={32} className="rounded-lg" />
          <span className="text-lg font-bold text-white">DMP <span className="text-primary">Academy</span></span>
        </Link>
        <RegisterForm />
      </div>
    </main>
  );
}
