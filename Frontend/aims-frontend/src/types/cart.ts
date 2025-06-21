// src/types/cart.ts
import type { Product } from "./product";

export interface Cart {
  cartId: number;
  totalProductPriceBeforeVAT: number;
  items: CartItem[];
  subtotal?: number;
  tax?: number;
  total?: number;
}

export interface CartItem {
  product: Product;
  quantity: number;
  subtotal?: number;
}

export interface AddToCartRequest {
  productId: string;
  quantity: number;
}

export interface InventoryCheckResponse {
  allAvailable: boolean;
  outOfStockProducts?: Array<{
    requested: number;
    productId: number;
    available: number;
    title: string;
    message: string;
  }>;
}
