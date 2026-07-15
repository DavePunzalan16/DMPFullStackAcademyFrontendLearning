import type { Metadata } from "next";
import { Providers } from "@/lib/providers";
import "./globals.css";

export const metadata: Metadata = {
  title: "DMP Full Stack Academy",
  description: "Gamified full-stack learning management platform",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en" className="dark">
      <body className="font-sans antialiased">
        <Providers>{children}</Providers>
      </body>
    </html>
  );
}
