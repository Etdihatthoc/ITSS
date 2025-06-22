// src/types/order.ts
export enum OrderStatus {
  PENDING = "PENDING",
  APPROVED = "APPROVED",
  REJECTED = "REJECTED",
  SHIPPED = "SHIPPED",
  DELIVERED = "DELIVERED",
  CANCELLED = "CANCELLED",
}

export interface CartItem {
  id: number;
  quantity: number;
  product: {
    id: number;
    title: string;
    imageURL?: string;
    currentPrice: number;
    category: string;
    productDescription?: string;
    weight?: number;
    productDimensions?: string;
    rushOrderEligible?: boolean;
    barcode?: string;
    quantity?: number;
    genre?: string;
  };
}

export interface Cart {
  cartId: number;
  totalProductPriceBeforeVAT: number;
  items: CartItem[];
}

export interface Invoice {
  id: number;
  cart: Cart;
  totalProductPriceAfterVAT: number;
  totalAmount: number;
  deliveryFee: number;
}

export interface Transaction {
  transactionID: number;
  id: number;
  bankCode: string;
  amount: number;
  bankTransactionNo: string;
  transactionNo: string;
  cardType: string;
  payDate: string;
  errorMessage: string;
}

export interface DeliveryInfo {
  id: number;
  deliveryAddress: string;
  province: string;
  phoneNumber: string;
  recipientName: string;
  email: string;
}

export interface Order {
  id: number;
  transaction: Transaction;
  invoice: Invoice;
  deliveryInfo: DeliveryInfo;
  status: OrderStatus | string;
}

// For use with search parameters
export interface OrderSearchParams {
  page?: number;
  limit?: number;
  status?: OrderStatus;
  startDate?: string;
  endDate?: string;
  customerId?: string;
  search?: string;
}
