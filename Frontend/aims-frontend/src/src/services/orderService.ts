// src/services/orderService.ts
import axios from "axios";
import { Order, OrderStatus } from "../types/order";

// Define the base API URL
const API_URL = "http://localhost:8080/api";

// Define the search parameters interface
interface OrderSearchParams {
  page?: number;
  limit?: number;
  search?: string;
  status?: OrderStatus;
  startDate?: string;
  endDate?: string;
}

// Define the response format for paginated results
interface PaginatedResponse<T> {
  data: T[];
  total: number;
  page: number;
  limit: number;
  totalPages: number;
}

// Order service methods
const orderService = {
  // Add this method to your orderService object

  /**
   * Create a new order
   * @param orderData Order creation data
   * @returns Promise with created order
   */
  createOrder: async (orderData: {
    transactionId: number | string;
    invoiceId: number;
    deliveryId: number;
    status?: string; // Optional as it might have a default on the server
  }) => {
    return axios.post<Order>(`${API_URL}/orders`, orderData, {
      headers: {
        Authorization: `Bearer ${localStorage.getItem("token")}`,
        "Content-Type": "application/json",
      },
    });
  },

  /**
   * Get orders with pagination and filtering options
   * @param params Search parameters
   * @returns Promise with paginated order data
   */
  getOrders: async (params: OrderSearchParams = {}) => {
    // Build query parameters
    const queryParams = new URLSearchParams();
    if (params.page) queryParams.append("page", params.page.toString());
    if (params.limit) queryParams.append("limit", params.limit.toString());
    if (params.search) queryParams.append("search", params.search);
    if (params.status) queryParams.append("status", params.status);
    if (params.startDate) queryParams.append("startDate", params.startDate);
    if (params.endDate) queryParams.append("endDate", params.endDate);

    // Make the API request
    return axios.get<PaginatedResponse<Order>>(
      `${API_URL}/orders?${queryParams.toString()}`,
      {
        headers: {
          Authorization: `Bearer ${localStorage.getItem("token")}`,
        },
      }
    );
  },

  /**
   * Get a single order by ID
   * @param id Order ID
   * @returns Promise with order data
   */
  getOrderById: async (id: string) => {
    return axios.get<Order>(`${API_URL}/orders/${id}`, {
      headers: {
        Authorization: `Bearer ${localStorage.getItem("token")}`,
      },
    });
  },

  /**
   * Update an order's status
   * @param id Order ID
   * @param status New status
   * @returns Promise with updated order
   */
  updateOrderStatus: async (id: string, status: string) => {
    return axios.patch<Order>(
      `${API_URL}/orders/${id}/status`,
      { status },
      {
        headers: {
          Authorization: `Bearer ${localStorage.getItem("token")}`,
        },
      }
    );
  },

  /**
   * Update order notes
   * @param id Order ID
   * @param notes Notes content
   * @returns Promise with updated order
   */
  updateOrderNotes: async (id: string, notes: string) => {
    return axios.patch<Order>(
      `${API_URL}/orders/${id}/notes`,
      { notes },
      {
        headers: {
          Authorization: `Bearer ${localStorage.getItem("token")}`,
        },
      }
    );
  },

  /**
   * Get orders statistics
   * @returns Promise with order statistics
   */
  getOrderStatistics: async () => {
    return axios.get(`${API_URL}/orders/statistics`, {
      headers: {
        Authorization: `Bearer ${localStorage.getItem("token")}`,
      },
    });
  },

  completeCheckout: async (checkoutRequest: {
    deliveryInfo: any;
    invoiceData: {
      cart: any;
      totalProductPriceBeforeVAT: number;
      totalProductPriceAfterVAT: number;
      deliveryFee: number;
      totalAmount: number;
    };
    transactionData: {
      transactionId: string;
      bankCode: string;
      amount: number;
      cardType: string;
      payDate: string;
      errorMessage: string;
    };
    status: string;
  }) => {
    // Create a sanitized copy of the request that removes problematic IDs
    const sanitizedRequest = {
      ...checkoutRequest,
      deliveryInfo: {
        ...checkoutRequest.deliveryInfo,
      },
      invoiceData: {
        ...checkoutRequest.invoiceData,
        cart: {
          // Don't include cartId
          totalProductPriceBeforeVAT:
            checkoutRequest.invoiceData.cart.totalProductPriceBeforeVAT,
          // Map items and remove any IDs
          items: checkoutRequest.invoiceData.cart.items.map((item: any) => ({
            quantity: item.quantity,
            productId: item.product.id,
            // Include any other necessary product details but NOT IDs
          })),
        },
      },
      transactionData: {
        ...checkoutRequest.transactionData,
        // Format the date as YYYY-MM-DD for LocalDate
        payDate: new Date().toISOString().slice(0, 19),
      },
    };

    // Delete any temporary IDs
    delete sanitizedRequest.deliveryInfo.deliveryId;
    delete sanitizedRequest.deliveryInfo.id;

    return axios.post<Order>(
      `${API_URL}/orders/checkout/create-order`,
      sanitizedRequest,
      {
        headers: {
          Authorization: `Bearer ${localStorage.getItem("token")}`,
          "Content-Type": "application/json",
        },
      }
    );
  },
};

export default orderService;
