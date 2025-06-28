// src/services/productService.ts
import api from "./api";
import type {
  Product,
  ProductCreate,
  ProductUpdate,
  ProductQueryParams,
} from "../types/product";
import {
  OperationHistory,
  OperationHistoryQueryParams,
} from "../types/operationHistory";

const productService = {
  // Get random products for home page
  getRandomProducts: async (page = 1, limit = 20) => {
    return await api.get<{ data: Product[]; total: number }>(
      "/products/random",
      {
        params: { page, limit },
      }
    );
  },

  getAllProducts: async () => {
    return await api.get<any>("/products/all");
  },

  getProducts: async (page = 1, size = 20) => {
    return await api.get("/products/random", {
      params: { page: page - 1, size },
    });
  },

  // Search products
  searchProducts: async (params: ProductQueryParams) => {
    return await api.get<{
      data: Product[];
      total: number;
      page: number;
      totalPages: number;
    }>("/products/search", {
      params: {
        page: (params.page || 1) - 1, // Convert to 0-based for backend
        size: params.size || 20,
        search: params.search,
        category: params.category,
        productType: params.productType,
        minPrice: params.minPrice,
        maxPrice: params.maxPrice,
        sortBy: params.sortBy || "id",
        sortDirection: params.sortDirection || "asc",
      },
    });
  },

  // Get product by ID
  getProductById: async (id: string) => {
    return await api.get<Product>(`/products/${id}`);
  },

  // Add a new product (product manager only)
  addProduct: async (product: ProductCreate) => {
    return await api.post<Product>("/products", product);
  },

  // Update a product (product manager only)
  updateProduct: async (id: string, product: ProductUpdate) => {
    return await api.put<Product>(`/products/${id}`, product);
  },

  // Delete a product (product manager only)
  deleteProduct: async (id: string) => {
    return await api.delete(`/products/${id}`);
  },

  // Bulk delete products (product manager only) - NEW METHOD
  deleteBulkProducts: async (ids: string[]) => {
    return await api.delete("/products/bulk", { data: ids });
  },

  // Delete multiple products (product manager only)
  deleteMultipleProducts: async (ids: string[]) => {
    return await api.delete("/products/bulk", { data: ids });
  },

  // Get product history (product manager only)
  getProductHistory: async (id: string) => {
    return await api.get(`/products/${id}/history`);
  },

  getProductOperationHistory: async (params: OperationHistoryQueryParams) => {
    return await api.get<{ data: OperationHistory[]; total: number }>(
      "/products/operations",
      { params }
    );
  },

  // Get operation history for a specific product
  getProductOperationHistoryById: async (
    productId: string,
    params: Omit<OperationHistoryQueryParams, "productId"> = {}
  ) => {
    return await api.get<{ data: OperationHistory[]; total: number }>(
      `/products/${productId}/operations`,
      { params }
    );
  },

  updateStock: async (
    productId: string | number,
    quantity: number,
    operation: "increase" | "decrease"
  ) => {
    return await api.patch<Product>(`/products/${productId}/stock`, {
      quantity,
      operation,
    });
  },
};

export default productService;
