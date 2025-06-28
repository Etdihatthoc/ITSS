// src/services/cartService.ts
import api from "./api";
import type {
  Cart,
  AddToCartRequest,
  InventoryCheckResponse,
  CartItem,
} from "../types/cart";

interface CartItemDTO {
  productId: string | number;
  quantity: number;
}

interface CartCalculationRequest {
  items: CartItemDTO[];
  isRushDelivery: boolean;
  province: string;
}

export interface CartCalculationResponse {
  subtotal: number;
  tax: number;
  deliveryFee: number;
  total: number;
  items: CartItemDetailDTO[];
  allItemsAvailable: boolean;
  outOfStockItems?: InsufficientStockDTO[];
}

interface CartItemDetailDTO {
  productId: number;
  title: string;
  price: number;
  quantity: number;
  subtotal: number;
  imageURL: string;
  category: string;
}

interface InsufficientStockDTO {
  productId: number;
  title: string;
  available: number;
  requested: number;
  message: string;
}

const cartService = {
  // Get current cart
  getCart: async () => {
    const response = await api.get<Cart[]>("/carts");
    // Since API returns an array, get the first cart
    return response.data[0] || null;
  },

  // Add item to cart
  addToCart: async (request: AddToCartRequest) => {
    return await api.post<Cart>("/carts/1/items", request);
  },

  // Update cart item quantity
  updateCartItem: async (productId: string, quantity: number) => {
    return await api.put<Cart>(`/carts/1/items/${productId}`, { quantity });
  },

  // Remove item from cart
  removeFromCart: async (productId: string) => {
    return await api.delete<Cart>(`/carts/1/items/${productId}`);
  },

  // Clear cart
  clearCart: async () => {
    return await api.delete<void>("/carts/1/items");
  },

  // Check inventory status for cart
  checkInventory: async () => {
    try {
      // Get cart from localStorage
      const storedCart = localStorage.getItem("cart");
      if (!storedCart) {
        return {
          success: true,
          data: { allAvailable: true },
        };
      }

      const cart = JSON.parse(storedCart);

      // Convert cart items to format needed for API
      const itemsToCheck = cart.items.map((item: CartItem) => ({
        productId: item.product.id,
        quantity: item.quantity,
      }));

      // Call the new API endpoint
      const response = await api.post<InventoryCheckResponse>(
        "/products/check-inventory",
        { items: itemsToCheck }
      );

      return {
        success: true,
        data: response.data,
      };
    } catch (error: any) {
      // If request returns 400, handle the error response
      if (error.response && error.response.status === 400) {
        return {
          success: false,
          data: error.response.data,
        };
      }
      throw error;
    }
  },

  calculateCart: async (
    items: CartItemDTO[],
    isRushDelivery: boolean = false,
    province: string = ""
  ): Promise<CartCalculationResponse> => {
    const request: CartCalculationRequest = {
      items,
      isRushDelivery,
      province,
    };

    const response = await api.post<CartCalculationResponse>(
      `/carts/calculate`,
      request,
      {
        headers: {
          Authorization: `Bearer ${localStorage.getItem("token")}`,
          "Content-Type": "application/json",
        },
      }
    );

    return response.data;
  },
};

export default cartService;
