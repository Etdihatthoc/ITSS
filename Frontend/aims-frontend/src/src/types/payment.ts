// src/types/payment.ts
export interface PaymentRequest {
  orderId: string;
  amount: number;
  orderInfo: string;
  returnUrl: string;
}

export interface PaymentResponse {
  paymentUrl: string;
}

export interface PaymentTransaction {
  id: string;
  orderId: string;
  amount: number;
  transactionNo: string;
  transactionStatus: string;
  bankCode: string;
  cardType: string;
  payDate: string;
  orderInfo: string;
}
