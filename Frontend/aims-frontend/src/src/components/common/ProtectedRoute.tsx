// src/components/common/ProtectedRoute.tsx
import React from "react";
import { Navigate } from "react-router-dom";
import authService from "../../services/authService";
import { UserRole } from "../../types/user";

interface ProtectedRouteProps {
  children: React.ReactNode;
  requiredRole?: UserRole;
}

const ProtectedRoute: React.FC<ProtectedRouteProps> = ({
  children,
  requiredRole,
}) => {
  const isAuthenticated = authService.isAuthenticated();
  const user = authService.getCurrentUser();

  // Check if user is authenticated
  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  // If a specific role is required, check if user has that role
  if (requiredRole && user && !user.roles.includes(requiredRole)) {
    // User is authenticated but doesn't have the required role
    return <Navigate to="/" replace />;
  }

  return <>{children}</>;
};

export default ProtectedRoute;
