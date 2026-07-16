import { api } from "@/lib/api-client";
import type { AuthUser, LoginRequest, RegisterRequest } from "./types";

export const authApi = {
  register: (data: RegisterRequest) => api.post<AuthUser>("/auth/register", data),

  login: (data: LoginRequest) => api.post<AuthUser>("/auth/login", data),

  logout: () => api.post<void>("/auth/logout"),

  getCurrentUser: () => api.get<AuthUser>("/users/me"),
};
