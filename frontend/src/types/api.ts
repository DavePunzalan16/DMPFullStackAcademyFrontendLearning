export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

export interface FieldError {
  field: string;
  message: string;
}

export interface ErrorResponse {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  fieldErrors?: FieldError[];
}

export class ApiError extends Error {
  status: number;
  errorResponse: ErrorResponse;

  constructor(status: number, errorResponse: ErrorResponse) {
    super(errorResponse.message);
    this.status = status;
    this.errorResponse = errorResponse;
  }
}
