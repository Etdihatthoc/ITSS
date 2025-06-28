// src/pages/checkout/DeliveryInfoPage.tsx
import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import {
  Container,
  Typography,
  Box,
  TextField,
  Button,
  Paper,
  Divider,
  FormControlLabel,
  Checkbox,
  Alert,
  CircularProgress,
} from "@mui/material";
import { useCart } from "../../contexts/CartContext";
import rushOrderService from "../../services/RushOrderService";
import cartService from "../../services/cartService";

interface DeliveryFormData {
  recipientName: string;
  email: string;
  phone: string;
  province: string;
  district: string;
  address: string;
  isRushOrder: boolean;
  rushDeliveryTime: string;
  rushDeliveryInstructions: string;
}

interface FormErrors {
  recipientName?: string;
  email?: string;
  phone?: string;
  province?: string;
  address?: string;
  rushDeliveryTime?: string;
}

const DeliveryInfoPage: React.FC = () => {
  const navigate = useNavigate();
  const { cart } = useCart();
  const [loading, setLoading] = useState(false);
  const [deliveryFee, setDeliveryFee] = useState(15000); // Default delivery fee
  const [rushDeliveryEligible, setRushDeliveryEligible] = useState(false);
  const [errors, setErrors] = useState<FormErrors>({});
  const [rushEligibilityChecked, setRushEligibilityChecked] = useState(false);
  const [rushEligibilityError, setRushEligibilityError] = useState<string | null>(null);
  const [subtotal, setSubtotal] = useState(0);
  const [tax, setTax] = useState(0);
  const [total, setTotal] = useState(0);

  const [formData, setFormData] = useState<DeliveryFormData>({
    recipientName: "",
    email: "",
    phone: "",
    province: "",
    district: "",
    address: "",
    isRushOrder: false,
    rushDeliveryTime: "",
    rushDeliveryInstructions: "",
  });

  useEffect(() => {
    // Check if cart is empty, redirect to cart page if it is
    if (!cart || cart.items.length === 0) {
      navigate("/cart");
    }
  }, [cart, navigate]);

  useEffect(() => {
    // Check rush delivery eligibility if province changes
    if (formData.province.toLowerCase().includes("hanoi")) {
      setRushDeliveryEligible(true);
    } else {
      setRushDeliveryEligible(false);
      if (formData.isRushOrder) {
        setFormData((prev) => ({
          ...prev,
          isRushOrder: false,
        }));
      }
    }

    // Calculate delivery fee based on province
    if (formData.province) {
      calculateDeliveryFee();
    }
  }, [formData.province]);

  const calculateDeliveryFee = () => {
    setLoading(true);

    // Simple delivery fee calculation logic
    // In a real app, this would be an API call
    setTimeout(() => {
      const baseDeliveryFee = 15000; // 15,000 VND base fee

      // Adjust fee based on province
      let fee = baseDeliveryFee;
      const province = formData.province.toLowerCase();

      if (province.includes("hanoi") || province.includes("hà nội")) {
        fee = 15000;
      } else if (
        province.includes("ho chi minh") ||
        province.includes("hồ chí minh")
      ) {
        fee = 20000;
      } else {
        fee = 30000; // Other provinces
      }

      // Rush delivery fee
      if (formData.isRushOrder) {
        fee += 50000; // Additional 50,000 VND for rush delivery
      }

      setDeliveryFee(fee);
      setLoading(false);
    }, 500); // Simulate API delay
  };

  const validateForm = () => {
    const newErrors: FormErrors = {};

    if (!formData.recipientName.trim()) {
      newErrors.recipientName = "Recipient name is required";
    }

    if (!formData.email.trim()) {
      newErrors.email = "Email is required";
    } else if (!/\S+@\S+\.\S+/.test(formData.email)) {
      newErrors.email = "Email is invalid";
    }

    if (!formData.phone.trim()) {
      newErrors.phone = "Phone number is required";
    } else if (!/^[0-9]{10,11}$/.test(formData.phone.replace(/\s/g, ""))) {
      newErrors.phone = "Phone number is invalid";
    }

    if (!formData.province.trim()) {
      newErrors.province = "Province is required";
    }

    if (!formData.address.trim()) {
      newErrors.address = "Address is required";
    }

    if (formData.isRushOrder && !formData.rushDeliveryTime) {
      newErrors.rushDeliveryTime = "Delivery time is required for rush orders";
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value, type, checked } = e.target;

    // For checkboxes, use the checked property
    const inputValue = type === "checkbox" ? checked : value;

    setFormData({
      ...formData,
      [name]: inputValue,
    });

    // Clear error when field is edited
    if (errors[name as keyof FormErrors]) {
      setErrors({
        ...errors,
        [name]: undefined,
      });
    }

    // Recalculate delivery fee when rush order changes
    if (name === "isRushOrder") {
      calculateDeliveryFee();
    }
  };

  const handleRushOrderChange = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const checked = e.target.checked;
    setRushEligibilityError(null);
    if (checked) {
      setLoading(true);
      try {
        // Map cartRequestDTO từ cart context
        const cartRequestDTO = cart
          ? {
              cartId: cart.cartId,
              totalProductPriceBeforeVAT: cart.totalProductPriceBeforeVAT,
              items: cart.items.map((item) => ({
                productId: item.product.id,
                quantity: item.quantity,
              })),
            }
          : undefined;
        // Map deliveryInfoDTO từ formData
        const deliveryInfoDTO = {
          deliveryAddress: formData.address,
          province: formData.province,
          phoneNumber: formData.phone,
          recipientName: formData.recipientName,
          email: formData.email,
          district: formData.district,
        };
        await rushOrderService.checkEligibility({
          cartRequestDTO,
          deliveryInfoDTO,
        });
        // Gọi lại API tính giá tiền
        if (cart) {
          const items = cart.items.map((item) => ({
            productId: item.product.id,
            quantity: item.quantity,
          }));
          const isRushDelivery = true; // Đảm bảo luôn true sau khi checkEligibility thành công
          const calc = await cartService.calculateCart(
            items,
            isRushDelivery,
            formData.province
          );
          setSubtotal(calc.subtotal);
          setTax(calc.tax);
          setDeliveryFee(calc.deliveryFee);
          setTotal(calc.total);
        }
        setFormData({ ...formData, isRushOrder: true });
        setRushEligibilityChecked(true);
      } catch (error: any) {
        setRushEligibilityError(
          error?.response?.data?.message ||
            "Địa chỉ hoặc sản phẩm trong giỏ hàng không hỗ trợ giao hàng nhanh."
        );
        setFormData({ ...formData, isRushOrder: false });
        setRushEligibilityChecked(false);
      } finally {
        setLoading(false);
      }
    } else {
      setFormData({ ...formData, isRushOrder: false });
      setRushEligibilityChecked(false);
    }
  };

  // Updated handleSubmit function - rest of the code stays the same
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (validateForm()) {
      try {
        setLoading(true);

        // Generate temporary IDs
        const tempDeliveryId = `delivery_${Date.now()}`;
        const tempInvoiceId = `invoice_${Date.now()}`;

        // Create delivery info object
        const deliveryInfo = {
          deliveryId: tempDeliveryId,
          deliveryAddress: formData.address,
          province: formData.province,
          phoneNumber: formData.phone,
          recipientName: formData.recipientName,
          email: formData.email,
          // Include other delivery details
          isRushOrder: formData.isRushOrder,
          rushDeliveryTime: formData.rushDeliveryTime,
          rushDeliveryInstructions: formData.rushDeliveryInstructions,
          deliveryFee: deliveryFee,
        };

        // Create invoice object with temporary ID
        const invoiceData = {
          invoiceId: tempInvoiceId,
          cartId: cart?.cartId,
          totalProductPriceBeforeVAT: cart?.subtotal || 0,
          totalProductPriceAfterVAT: (cart?.subtotal || 0) * 1.1,
          deliveryFee: deliveryFee,
          totalAmount: (cart?.subtotal || 0) * 1.1 + deliveryFee,
        };

        // Store in localStorage
        localStorage.setItem("deliveryInfo", JSON.stringify(deliveryInfo));
        localStorage.setItem("invoiceData", JSON.stringify(invoiceData));

        // For compatibility with existing code
        sessionStorage.setItem(
          "deliveryInfo",
          JSON.stringify({
            ...formData,
            deliveryFee,
            deliveryId: tempDeliveryId,
            invoiceId: tempInvoiceId,
          })
        );
        // Navigate to payment page
        navigate("/checkout/payment");
      } catch (error) {
        console.error("Failed to process delivery info:", error);
        // Display error message to user
        alert("Failed to process delivery information. Please try again.");
      } finally {
        setLoading(false);
      }
    }
  };

  const formatPrice = (price: number) => {
    return new Intl.NumberFormat("vi-VN", {
      style: "currency",
      currency: "VND",
    }).format(price);
  };

  if (!cart) {
    return (
      <Container sx={{ py: 4 }}>
        <CircularProgress />
      </Container>
    );
  }

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Typography variant="h4" gutterBottom>
        Delivery Information
      </Typography>

      <Box
        sx={{
          display: "flex",
          flexDirection: { xs: "column", md: "row" },
          gap: 4,
        }}
      >
        {/* Delivery Form - Left Column */}
        <Box sx={{ flex: { xs: "1 1 100%", md: "0 0 60%" } }}>
          <Paper sx={{ p: 3 }}>
            <Box component="form" onSubmit={handleSubmit}>
              <Box sx={{ mb: 3 }}>
                <Typography variant="h6" gutterBottom>
                  Recipient Information
                </Typography>

                <Box sx={{ display: "flex", flexDirection: "column", gap: 2 }}>
                  <TextField
                    name="recipientName"
                    label="Recipient Name"
                    fullWidth
                    required
                    value={formData.recipientName}
                    onChange={handleInputChange}
                    error={!!errors.recipientName}
                    helperText={errors.recipientName}
                  />

                  <Box
                    sx={{
                      display: "flex",
                      flexDirection: { xs: "column", sm: "row" },
                      gap: 2,
                    }}
                  >
                    <TextField
                      name="email"
                      label="Email"
                      fullWidth
                      required
                      type="email"
                      value={formData.email}
                      onChange={handleInputChange}
                      error={!!errors.email}
                      helperText={errors.email}
                    />

                    <TextField
                      name="phone"
                      label="Phone Number"
                      fullWidth
                      required
                      value={formData.phone}
                      onChange={handleInputChange}
                      error={!!errors.phone}
                      helperText={errors.phone}
                    />
                  </Box>
                </Box>
              </Box>

              <Divider sx={{ my: 3 }} />

              <Box sx={{ mb: 3 }}>
                <Typography variant="h6" gutterBottom>
                  Delivery Address
                </Typography>

                <Box sx={{ display: "flex", flexDirection: "column", gap: 2 }}>
                  <TextField
                    name="province"
                    label="Province/City"
                    fullWidth
                    required
                    value={formData.province}
                    onChange={handleInputChange}
                    error={!!errors.province}
                    helperText={errors.province}
                  />

                  <TextField
                    name="district"
                    label="District (Quận/Huyện)"
                    required
                    value={formData.district}
                    onChange={handleInputChange}
                    sx={{ mt: 2 }}
                  />

                  <TextField
                    name="address"
                    label="Delivery Address"
                    fullWidth
                    required
                    value={formData.address}
                    onChange={handleInputChange}
                    error={!!errors.address}
                    helperText={errors.address}
                    multiline
                    rows={2}
                  />
                </Box>
              </Box>

              <Divider sx={{ my: 3 }} />

              <Box sx={{ mb: 3 }}>
                <Typography variant="h6" gutterBottom>
                  Delivery Options
                </Typography>

                <FormControlLabel
                  control={
                    <Checkbox
                      name="isRushOrder"
                      checked={formData.isRushOrder}
                      onChange={handleRushOrderChange}
                      disabled={!rushDeliveryEligible}
                    />
                  }
                  label="Rush Order Delivery (2-hour delivery time)"
                />

                {!rushDeliveryEligible && formData.province && (
                  <Alert severity="info" sx={{ mt: 1 }}>
                    Rush delivery is only available for addresses within Hanoi.
                  </Alert>
                )}

                {rushEligibilityError && (
                  <Alert severity="error" sx={{ mt: 1 }}>{rushEligibilityError}</Alert>
                )}

                {formData.isRushOrder && rushEligibilityChecked && (
                  <Box
                    sx={{
                      mt: 2,
                      display: "flex",
                      flexDirection: "column",
                      gap: 2,
                    }}
                  >
                    <TextField
                      name="rushDeliveryTime"
                      label="Preferred Delivery Time"
                      type="time"
                      value={formData.rushDeliveryTime}
                      onChange={handleInputChange}
                      error={!!errors.rushDeliveryTime}
                      helperText={errors.rushDeliveryTime}
                      InputLabelProps={{
                        shrink: true,
                      }}
                    />
                    <TextField
                      name="rushDeliveryInstructions"
                      label="Delivery Instructions"
                      fullWidth
                      value={formData.rushDeliveryInstructions}
                      onChange={handleInputChange}
                      multiline
                      rows={2}
                      placeholder="Additional instructions for delivery"
                    />
                  </Box>
                )}
              </Box>

              <Box
                sx={{ mt: 3, display: "flex", justifyContent: "space-between" }}
              >
                <Button variant="outlined" onClick={() => navigate("/cart")}>
                  Back to Cart
                </Button>

                <Button
                  type="submit"
                  variant="contained"
                  color="primary"
                  disabled={loading}
                >
                  {loading ? (
                    <>
                      <CircularProgress size={24} sx={{ mr: 1 }} />
                      Processing...
                    </>
                  ) : (
                    "Continue to Payment"
                  )}
                </Button>
              </Box>
            </Box>
          </Paper>
        </Box>

        {/* Order Summary - Right Column */}
        <Box sx={{ flex: { xs: "1 1 100%", md: "0 0 40%" } }}>
          <Paper sx={{ p: 3 }}>
            <Typography variant="h6" gutterBottom>
              Cart Summary
            </Typography>

            <Box sx={{ my: 2 }}>
              {cart.items.map((item) => {
                // Tính giá hiển thị: nếu là rush order thì (BasePrice + 10000) * quantity, ngược lại thì giữ nguyên
                const displayPrice = formData.isRushOrder && rushEligibilityChecked
                  ? (item.product.currentPrice + 10000) * item.quantity
                  : item.quantity * item.product.currentPrice;
                
                return (
                  <Box
                    key={item.product.id}
                    sx={{
                      display: "flex",
                      justifyContent: "space-between",
                      mb: 1,
                    }}
                  >
                    <Typography variant="body2">
                      {item.quantity} × {item.product.title}
                    </Typography>
                    <Typography variant="body2">
                      {formatPrice(displayPrice)}
                    </Typography>
                  </Box>
                );
              })}
            </Box>

            <Divider sx={{ my: 2 }} />

            <Box
              sx={{ display: "flex", justifyContent: "space-between", mb: 1 }}
            >
              <Typography variant="body1">Subtotal:</Typography>
              <Typography variant="body1">
                {formData.isRushOrder && rushEligibilityChecked
                  ? formatPrice(subtotal)
                  : formatPrice(cart.subtotal ? cart.subtotal : 0)}
              </Typography>
            </Box>

            <Box
              sx={{ display: "flex", justifyContent: "space-between", mb: 1 }}
            >
              <Typography variant="body1">VAT (10%):</Typography>
              <Typography variant="body1">
                {formData.isRushOrder && rushEligibilityChecked
                  ? formatPrice(tax)
                  : formatPrice(cart.subtotal ? cart.subtotal * 0.1 : 0)}
              </Typography>
            </Box>

            <Divider sx={{ my: 2 }} />

            <Box sx={{ display: "flex", justifyContent: "space-between" }}>
              <Typography variant="h6">Total:</Typography>
              <Typography variant="h6">
                {formData.isRushOrder && rushEligibilityChecked
                  ? formatPrice(subtotal + tax)
                  : formatPrice(cart.subtotal ? cart.subtotal + cart.subtotal * 0.1 : 0)}
              </Typography>
            </Box>
          </Paper>
        </Box>
      </Box>
    </Container>
  );
};

export default DeliveryInfoPage;
