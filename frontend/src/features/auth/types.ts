export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  email: string;
  password: string;
  displayName: string;
}

export interface AuthUser {
  id: string;
  email: string;
  displayName: string;
  role: "STUDENT" | "INSTRUCTOR" | "ADMIN";
  xpTotal: number;
  level: number;
}
