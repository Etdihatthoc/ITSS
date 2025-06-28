// src/App.tsx
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import { ThemeProvider, createTheme } from "@mui/material/styles";
import CssBaseline from "@mui/material/CssBaseline";
import { ToastContainer } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";

// Contexts
import { CartProvider } from "./contexts/CartContext";

// Components
import MainLayout from "./components/layout/MainLayout";
import ProtectedRoute from "./components/common/ProtectedRoute";

// Pages
import HomePage from "./pages/home/HomePage";
import ProductDetailPage from "./pages/product/ProductDetailPage";
import CartPage from "./pages/cart/CartPage";
import DeliveryInfoPage from "./pages/checkout/DeliveryInfoPage";
import PaymentPage from "./pages/checkout/PaymentPage";
import OrderConfirmationPage from "./pages/checkout/OrderConfirmationPage";
import LoginPage from "./pages/login/LoginPage";
import ProductManagementPage from "./pages/admin/ProductManagementPage";
import UserManagementPage from "./pages/admin/UserManagementPage";
import OrderManagementPage from "./pages/admin/OrderManagementPage";
import ViewOrderPage from "./pages/orders/ViewOrderPage";
import NotFoundPage from "./pages/NotFoundPage";

// User roles
import { UserRole } from "./types/user";
import OperationHistoryPage from "./pages/admin/OperationHistoryPage";

const theme = createTheme({
  palette: {
    primary: {
      main: "#1976d2",
    },
    secondary: {
      main: "#f50057",
    },
  },
  typography: {
    fontFamily: '"Roboto", "Helvetica", "Arial", sans-serif',
  },
});

function App() {
  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      {/* <AuthProvider> */}
      <CartProvider>
        <Router>
          <Routes>
            <Route path="/" element={<MainLayout />}>
              <Route index element={<HomePage />} />
              <Route path="product/:id" element={<ProductDetailPage />} />
              <Route path="cart" element={<CartPage />} />
              <Route path="checkout/delivery" element={<DeliveryInfoPage />} />
              <Route path="checkout/payment" element={<PaymentPage />} />
              <Route
                path="checkout/confirmation"
                element={<OrderConfirmationPage />}
              />
              <Route path="order/:id" element={<ViewOrderPage />} />

              <Route
                path="admin/operation-history"
                element={<OperationHistoryPage />}
              />

              {/* Protected Admin Routes */}
              <Route
                path="admin/products"
                element={
                  // <ProtectedRoute requiredRole={UserRole.PRODUCT_MANAGER}>
                  <ProductManagementPage />
                  // </ProtectedRoute>
                }
              />
              <Route
                path="admin/users"
                element={
                  <ProtectedRoute requiredRole={UserRole.ADMIN}>
                    <UserManagementPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="admin/orders"
                element={
                  // <ProtectedRoute requiredRole={UserRole.PRODUCT_MANAGER}>
                  <OrderManagementPage />
                  // </ProtectedRoute>
                }
              />

              <Route path="*" element={<NotFoundPage />} />
            </Route>

            <Route path="/login" element={<LoginPage />} />
          </Routes>
        </Router>
        <ToastContainer position="bottom-right" />
      </CartProvider>
      {/* </AuthProvider> */}
    </ThemeProvider>
  );
}

export default App;
