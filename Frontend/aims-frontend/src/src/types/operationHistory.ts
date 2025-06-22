// src/types/operationHistory.ts
export enum OperationType {
  ADD = "ADD",
  UPDATE = "UPDATE",
  DELETE = "DELETE",
}

export interface OperationHistory {
  id: string;
  operationType: OperationType;
  productId: string;
  productName: string;
  userId: string;
  userName: string;
  timestamp: string;
  changes?: Record<string, { before: any; after: any }>;
  notes?: string;
}

export interface OperationHistoryQueryParams {
  page?: number;
  limit?: number;
  search?: string;
  operationType?: OperationType;
  startDate?: string;
  endDate?: string;
  productId?: string;
  userId?: string;
}
