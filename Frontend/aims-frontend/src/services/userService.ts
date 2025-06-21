// src/services/userService.ts
import axios from "axios";
import { User, UserRole } from "../types/user";

// Define the base API URL
const API_URL = "http://localhost:8080/api";

// Define the search parameters interface
interface UserSearchParams {
  page?: number;
  limit?: number;
  search?: string;
  role?: UserRole;
}

// Define the user creation/update interface
interface UserData {
  name: string;
  email: string;
  phone?: string;
  role: UserRole;
  password?: string;
}

// Define the response format for paginated results
interface PaginatedResponse<T> {
  data: T[];
  total: number;
  page: number;
  limit: number;
  totalPages: number;
}

// User service methods
const userService = {
  /**
   * Get users with pagination and filtering options
   * @param params Search parameters
   * @returns Promise with paginated user data
   */
  getUsers: async (params: UserSearchParams = {}) => {
    // Build query parameters
    const queryParams = new URLSearchParams();
    if (params.page) queryParams.append("page", params.page.toString());
    if (params.limit) queryParams.append("limit", params.limit.toString());
    if (params.search) queryParams.append("search", params.search);
    if (params.role) queryParams.append("role", params.role);

    // Make the API request
    return axios.get<PaginatedResponse<User>>(
      `${API_URL}/users?${queryParams.toString()}`,
      {
        headers: {
          Authorization: `Bearer ${localStorage.getItem("token")}`,
        },
      }
    );
  },

  /**
   * Get a single user by ID
   * @param id User ID
   * @returns Promise with user data
   */
  getUserById: async (id: string) => {
    return axios.get<User>(`${API_URL}/users/${id}`, {
      headers: {
        Authorization: `Bearer ${localStorage.getItem("token")}`,
      },
    });
  },

  /**
   * Create a new user
   * @param userData User data to create
   * @returns Promise with created user
   */
  createUser: async (userData: UserData) => {
    return axios.post<User>(`${API_URL}/users`, userData, {
      headers: {
        Authorization: `Bearer ${localStorage.getItem("token")}`,
      },
    });
  },

  /**
   * Update an existing user
   * @param id User ID
   * @param userData User data to update
   * @returns Promise with updated user
   */
  updateUser: async (id: string, userData: UserData) => {
    return axios.put<User>(`${API_URL}/users/${id}`, userData, {
      headers: {
        Authorization: `Bearer ${localStorage.getItem("token")}`,
      },
    });
  },

  /**
   * Delete a user
   * @param id User ID
   * @returns Promise with deletion result
   */
  deleteUser: async (id: string) => {
    return axios.delete(`${API_URL}/users/${id}`, {
      headers: {
        Authorization: `Bearer ${localStorage.getItem("token")}`,
      },
    });
  },

  /**
   * Reset a user's password
   * @param id User ID
   * @param newPassword New password
   * @returns Promise with result
   */
  resetPassword: async (id: string, newPassword: string) => {
    return axios.post(
      `${API_URL}/users/${id}/reset-password`,
      { password: newPassword },
      {
        headers: {
          Authorization: `Bearer ${localStorage.getItem("token")}`,
        },
      }
    );
  },

  /**
   * Get the current authenticated user
   * @returns Promise with current user data
   */
  getCurrentUser: async () => {
    return axios.get<User>(`${API_URL}/users/me`, {
      headers: {
        Authorization: `Bearer ${localStorage.getItem("token")}`,
      },
    });
  },
};

export default userService;
