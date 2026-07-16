import Image from "next/image";
import { LoginForm } from "@/features/auth/components/login-form";
import Link from "next/link";

export default function LoginPage() {
  return (
    <main className="flex min-h-screen">
      {/* Left panel - branding */}
      <div className="hidden w-1/2 flex-col items-center justify-center bg-gradient-to-br from-purple-900/40 to-background p-12 lg:flex">
        <Image
          src="/images/DMP_Full_Stack_Academy.jpg"
          alt="DMP Academy"
          width={400}
          height={300}
          className="rounded-2xl shadow-2xl"
        />
        <h2 className="mt-8 text-2xl font-bold text-white">Welcome Back!</h2>
        <p className="mt-2 text-center text-muted-foreground">
          Continue your learning journey and keep that streak going.
        </p>
      </div>

      {/* Right panel - form */}
      <div className="flex flex-1 flex-col items-center justify-center p-6">
        <Link href="/" className="mb-8 flex items-center gap-2">
          <Image src="/images/DmpFSALogo.jpg" alt="Logo" width={32} height={32} className="rounded-lg" />
          <span className="text-lg font-bold text-white">DMP <span className="text-primary">Academy</span></span>
        </Link>
        <LoginForm />
      </div>
    </main>
  );
}
