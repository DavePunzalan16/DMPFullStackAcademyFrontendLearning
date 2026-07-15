import { API_V1 } from "./constants";
import { ApiError, type ErrorResponse } from "@/types/api";

type RequestOptions = Omit<RequestInit, "body"> & {
  body?: unknown;
};

async function refreshAccessToken(): Promise<boolean> {
  try {
    const response = await fetch(`${API_V1}/auth/refresh`, {
      method: "POST",
      credentials: "include",
    });
    return response.ok;
  } catch {
    return false;
  }
}

async function handleResponse<T>(response: Response): Promise<T> {
  if (response.status === 204) {
    return undefined as T;
  }

  const contentType = response.headers.get("content-type");
  if (!contentType?.includes("application/json")) {
    if (!response.ok) {
      throw new ApiError(response.status, {
        timestamp: new Date().toISOString(),
        status: response.status,
        error: response.statusText,
        message: "An unexpected error occurred",
      });
    }
    return undefined as T;
  }

  const data = await response.json();

  if (!response.ok) {
    throw new ApiError(response.status, data as ErrorResponse);
  }

  return data as T;
}

export async function apiClient<T>(endpoint: string, options: RequestOptions = {}): Promise<T> {
  const { body, headers: customHeaders, ...restOptions } = options;

  const headers: HeadersInit = {
    "Content-Type": "application/json",
    ...customHeaders,
  };

  const config: RequestInit = {
    ...restOptions,
    headers,
    credentials: "include",
    body: body ? JSON.stringify(body) : undefined,
  };

  let response = await fetch(`${API_V1}${endpoint}`, config);

  // If 401, attempt token refresh and retry once
  if (response.status === 401 && !endpoint.includes("/auth/")) {
    const refreshed = await refreshAccessToken();
    if (refreshed) {
      response = await fetch(`${API_V1}${endpoint}`, config);
    }
  }

  return handleResponse<T>(response);
}

// Convenience methods
export const api = {
  get: <T>(endpoint: string, options?: RequestOptions) =>
    apiClient<T>(endpoint, { ...options, method: "GET" }),

  post: <T>(endpoint: string, body?: unknown, options?: RequestOptions) =>
    apiClient<T>(endpoint, { ...options, method: "POST", body }),

  put: <T>(endpoint: string, body?: unknown, options?: RequestOptions) =>
    apiClient<T>(endpoint, { ...options, method: "PUT", body }),

  delete: <T>(endpoint: string, options?: RequestOptions) =>
    apiClient<T>(endpoint, { ...options, method: "DELETE" }),
};
