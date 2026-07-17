"use client";

import { useParams } from "next/navigation";
import { useQuery } from "@tanstack/react-query";
import { api } from "@/lib/api-client";
import Image from "next/image";
import Link from "next/link";

interface CertificateData {
  certificateId: string;
  courseTitle: string;
  studentName: string;
  issuedAt: string;
}

export default function CertificateVerifyPage() {
  const { certificateId } = useParams<{ certificateId: string }>();

  const { data: cert, isLoading, error } = useQuery({
    queryKey: ["certificate-verify", certificateId],
    queryFn: () => api.get<CertificateData>(`/certificates/${certificateId}/verify`),
    enabled: !!certificateId,
    retry: false,
  });

  if (isLoading) {
    return (
      <div className="flex min-h-screen items-center justify-center bg-background">
        <div className="h-8 w-8 animate-spin rounded-full border-2 border-primary border-t-transparent" />
      </div>
    );
  }

  if (error || !cert) {
    return (
      <div className="flex min-h-screen flex-col items-center justify-center bg-background p-4">
        <p className="text-5xl">❌</p>
        <h1 className="mt-4 text-2xl font-bold text-white">Certificate Not Found</h1>
        <p className="mt-2 text-muted-foreground">This certificate ID is invalid or does not exist.</p>
        <Link href="/" className="mt-6 rounded-lg bg-primary px-6 py-2 text-sm font-medium text-primary-foreground">
          Go to Homepage
        </Link>
      </div>
    );
  }

  const issuedDate = new Date(cert.issuedAt).toLocaleDateString("en-US", {
    year: "numeric", month: "long", day: "numeric",
  });

  return (
    <div className="flex min-h-screen flex-col items-center justify-center bg-background p-4">
      {/* Verified Badge */}
      <div className="mb-6 flex items-center gap-2 rounded-full border border-green-500/30 bg-green-500/10 px-4 py-2">
        <span className="text-green-400">✓</span>
        <span className="text-sm font-medium text-green-400">Verified Certificate</span>
      </div>

      {/* Certificate Card */}
      <div className="w-full max-w-2xl rounded-2xl border border-white/10 bg-gradient-to-br from-card to-background p-12 text-center shadow-2xl">
        <Image src="/images/DmpFSALogo.jpg" alt="DMP Academy" width={48} height={48} className="mx-auto rounded-lg" />

        <p className="mt-6 text-sm uppercase tracking-widest text-muted-foreground">Certificate of Completion</p>

        <h1 className="mt-4 text-lg text-muted-foreground">This certifies that</h1>
        <p className="mt-2 text-3xl font-bold text-white">{cert.studentName}</p>

        <p className="mt-6 text-muted-foreground">has successfully completed the course</p>
        <p className="mt-2 text-2xl font-bold gradient-text">{cert.courseTitle}</p>

        <div className="mt-8 border-t border-white/10 pt-6">
          <p className="text-sm text-muted-foreground">Issued: {issuedDate}</p>
          <p className="mt-1 text-xs text-muted-foreground font-mono">ID: {cert.certificateId}</p>
        </div>

        <p className="mt-6 text-xs text-muted-foreground">DMP Full Stack Academy</p>
      </div>

      {/* Copy Link */}
      <button
        onClick={() => {
          navigator.clipboard.writeText(window.location.href);
          alert("Link copied!");
        }}
        className="mt-6 rounded-lg border border-white/10 px-4 py-2 text-sm text-muted-foreground hover:text-white"
      >
        📋 Copy Verification Link
      </button>

      <Link href="/" className="mt-4 text-sm text-primary hover:underline">
        ← Back to DMP Academy
      </Link>
    </div>
  );
}
