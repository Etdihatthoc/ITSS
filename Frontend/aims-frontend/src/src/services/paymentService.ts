// src/services/paymentService.ts
import api from "./api";
import type {
  PaymentRequest,
  PaymentResponse,
  PaymentTransaction,
} from "../types/payment";

const paymentService = {
  // Create payment request
  createPayment: async (request: PaymentRequest) => {
    return await api.post<PaymentResponse>("/pay", request);
  },

  // Verify payment return from VNPay
  verifyPayment: async (params: Record<string, string>) => {
    return await api.get<PaymentTransaction>("/payments/verify", { params });
  },

  // Get payment transaction by ID
  getTransactionById: async (id: string) => {
    return await api.get<PaymentTransaction>(`/payments/transactions/${id}`);
  },

  // Process refund for an order
  processRefund: async (orderId: string) => {
    return await api.post<PaymentTransaction>(`/payments/refund/${orderId}`);
  },
};

export default paymentService;
