// src/types/user.ts
export enum UserRole {
  ADMIN = "ADMIN",
  PRODUCT_MANAGER = "PRODUCT_MANAGER",
  CUSTOMER = "CUSTOMER",
}

export interface User {
  id: string;
  username: string;
  name: string;
  email: string;
  fullName: string;
  roles: UserRole[];
  isActive: boolean;
  phone?: string;
  createdAt: string;
}

export interface LoginCredentials {
  username: string;
  password: string;
}
