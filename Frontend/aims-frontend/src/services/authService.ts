// src/services/authService.ts
import api from "./api";
import type { User, LoginCredentials, UserRole } from "../types/user";

const authService = {
  login: async (credentials: LoginCredentials) => {
    const response = await api.post<{ token: string; user: User }>(
      "/auth/login",
      credentials
    );
    if (response.data.token) {
      localStorage.setItem("token", response.data.token);
      localStorage.setItem("user", JSON.stringify(response.data.user));
    }
    console.log("Login successful:", response.data.user);
    return response.data;
  },

  logout: () => {
    localStorage.removeItem("token");
    localStorage.removeItem("user");
  },

  getCurrentUser: (): User | null => {
    const userStr = localStorage.getItem("user");
    return userStr ? JSON.parse(userStr) : null;
  },

  isAuthenticated: (): boolean => {
    return !!localStorage.getItem("token");
  },

  hasRole: (role: UserRole): boolean => {
    const user = authService.getCurrentUser();
    return user ? user.roles.includes(role) : false;
  },

  changePassword: async (oldPassword: string, newPassword: string) => {
    return await api.post("/auth/change-password", {
      oldPassword,
      newPassword,
    });
  },
};

export default authService;
