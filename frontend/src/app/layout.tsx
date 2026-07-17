import type { Metadata } from "next";
import { Providers } from "@/lib/providers";
import { GamificationProvider } from "@/components/gamification/GamificationProvider";
import "./globals.css";

export const metadata: Metadata = {
  title: "DMP Full Stack Academy",
  description: "Gamified full-stack learning management platform",
  icons: {
    icon: "/images/DmpFSALogo.jpg",
  },
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en" className="dark">
      <body className="font-sans antialiased">
        <Providers>
          <GamificationProvider>{children}</GamificationProvider>
        </Providers>
      </body>
    </html>
  );
}
