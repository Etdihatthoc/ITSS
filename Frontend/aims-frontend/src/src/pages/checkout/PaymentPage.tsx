// src/pages/checkout/PaymentPage.tsx
import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import {
  Container,
  Typography,
  Box,
  Button,
  Paper,
  Divider,
  List,
  ListItem,
  CircularProgress,
  Alert,
  Radio,
  RadioGroup,
  FormControlLabel,
  FormControl,
} from "@mui/material";
import { useCart } from "../../contexts/CartContext";

interface DeliveryInfo {
  recipientName: string;
  email: string;
  phone: string;
  province: string;
  address: string;
  isRushOrder: boolean;
  rushDeliveryTime?: string;
  rushDeliveryInstructions?: string;
  deliveryFee: number;
}

const PaymentPage: React.FC = () => {
  const navigate = useNavigate();
  const { cart, clearCart } = useCart();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [deliveryInfo, setDeliveryInfo] = useState<DeliveryInfo | null>(null);
  const [paymentMethod, setPaymentMethod] = useState("credit_card");

  useEffect(() => {
    // Retrieve delivery info from session storage
    const storedDeliveryInfo = sessionStorage.getItem("deliveryInfo");

    if (!storedDeliveryInfo) {
      navigate("/checkout/delivery");
      return;
    }

    setDeliveryInfo(JSON.parse(storedDeliveryInfo));
  }, [navigate]);

  const handlePaymentMethodChange = (
    event: React.ChangeEvent<HTMLInputElement>
  ) => {
    setPaymentMethod(event.target.value);
  };

  const handleProcessPayment = async () => {
    try {
      setLoading(true);
      setError("");

      // Get stored information from localStorage
      const deliveryInfo = JSON.parse(
        localStorage.getItem("deliveryInfo") || "{}"
      );
      const invoiceData = JSON.parse(
        localStorage.getItem("invoiceData") || "{}"
      );

      // Create transaction data
      const transactionData = {
        transactionId: `TXN${Date.now()}`,
        bankCode: "VNPAY",
        amount: calculateTotal(),
        cardType: paymentMethod,
        payDate: new Date().toISOString(),
        errorMessage: "",
      };

      // Store transaction in localStorage
      localStorage.setItem("transactionData", JSON.stringify(transactionData));

      // Create order data (but don't submit to API yet)
      const orderData = {
        transaction: transactionData,
        invoice: invoiceData,
        deliveryInfo: deliveryInfo,
        status: "PENDING",
      };

      // Store temporary order in localStorage
      const tempOrderId = `order_${Date.now()}`;
      localStorage.setItem(
        "orderData",
        JSON.stringify({ ...orderData, id: tempOrderId })
      );

      // For demonstration, we'll use the temp order ID
      // In a real implementation, you'd get this from your payment gateway

      // Navigate to confirmation page
      // Redirect to backend /pay endpoint with required parameters
      const amount = Math.round(parseInt(invoiceData.totalAmount));

      const params = new URLSearchParams({
        gateway: "vnpay",
        amount: amount.toString(),
        orderId: Date.now().toString(),
        orderInfo: 'Thanh toan hoa don',
      });
      window.location.href = `http://localhost:8080/api/pay_test?${params.toString()}`;
      console.log("Redirecting to payment gateway with params:", params.toString());
      // navigate(
      //   `/checkout/confirmation?orderId=${tempOrderId}&status=success&transactionId=${transactionData.transactionId}`
      // );
      // Don't remove storage yet - we'll need it for the confirmation page
      // localStorage.removeItem("deliveryInfo");
      // localStorage.removeItem("invoiceData");
      // localStorage.removeItem("transactionData");
      // localStorage.removeItem("orderData");
    } catch (error) {
      console.error("Payment processing failed:", error);
      setError("Failed to process payment. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  const calculateSubtotal = () => {
    return cart?.subtotal || 0;
  };

  const calculateTax = () => {
    return calculateSubtotal() * 0.1; // 10% tax
  };

  const calculateDeliveryFee = () => {
    return deliveryInfo?.deliveryFee || 0;
  };

  const calculateTotal = () => {
    return calculateSubtotal() + calculateTax() + calculateDeliveryFee();
  };

  const formatPrice = (price: number) => {
    return new Intl.NumberFormat("vi-VN", {
      style: "currency",
      currency: "VND",
    }).format(price);
  };

  if (!cart || !deliveryInfo) {
    return (
      <Container sx={{ py: 4, textAlign: "center" }}>
        <CircularProgress />
      </Container>
    );
  }

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Typography variant="h4" gutterBottom>
        Payment
      </Typography>

      {error && (
        <Alert severity="error" sx={{ mb: 3 }}>
          {error}
        </Alert>
      )}

      <Box
        sx={{
          display: "flex",
          flexDirection: { xs: "column", md: "row" },
          gap: 4,
        }}
      >
        {/* Payment Details - Left Column */}
        <Box sx={{ flex: { xs: "1 1 100%", md: "0 0 60%" } }}>
          <Paper sx={{ p: 3, mb: 3 }}>
            <Typography variant="h6" gutterBottom>
              Order Details
            </Typography>

            <List sx={{ mb: 2 }}>
              {cart.items.map((item) => (
                <ListItem key={item.product.id} sx={{ py: 1, px: 0 }}>
                  <Box sx={{ display: "flex", width: "100%" }}>
                    <Box
                      component="img"
                      sx={{
                        width: 50,
                        height: 50,
                        mr: 2,
                        objectFit: "contain",
                      }}
                      src={item.product.imageURL || "/placeholder.jpg"}
                      alt={item.product.title}
                    />
                    <Box sx={{ flexGrow: 1 }}>
                      <Typography variant="body1">
                        {item.product.title}
                      </Typography>
                      <Typography variant="body2" color="text.secondary">
                        Quantity: {item.quantity}
                      </Typography>
                    </Box>
                    <Typography variant="body1">
                      {formatPrice(item.quantity * item.product.currentPrice)}
                    </Typography>
                  </Box>
                </ListItem>
              ))}
            </List>
          </Paper>

          <Paper sx={{ p: 3, mb: 3 }}>
            <Typography variant="h6" gutterBottom>
              Delivery Information
            </Typography>

            <Box sx={{ display: "flex", flexWrap: "wrap", gap: 3 }}>
              <Box sx={{ minWidth: "40%" }}>
                <Typography variant="subtitle2" color="text.secondary">
                  Recipient Name
                </Typography>
                <Typography variant="body1" gutterBottom>
                  {deliveryInfo.recipientName}
                </Typography>
              </Box>

              <Box sx={{ minWidth: "40%" }}>
                <Typography variant="subtitle2" color="text.secondary">
                  Contact
                </Typography>
                <Typography variant="body1" gutterBottom>
                  {deliveryInfo.phone}
                </Typography>
                <Typography variant="body2" color="text.secondary" gutterBottom>
                  {deliveryInfo.email}
                </Typography>
              </Box>

              <Box sx={{ width: "100%" }}>
                <Typography variant="subtitle2" color="text.secondary">
                  Delivery Address
                </Typography>
                <Typography variant="body1" gutterBottom>
                  {deliveryInfo.address}, {deliveryInfo.province}
                </Typography>
              </Box>

              {deliveryInfo.isRushOrder && (
                <Box sx={{ width: "100%" }}>
                  <Typography variant="subtitle2" color="primary">
                    Rush Order Delivery
                  </Typography>
                  {deliveryInfo.rushDeliveryTime && (
                    <Typography variant="body2" gutterBottom>
                      Delivery Time: {deliveryInfo.rushDeliveryTime}
                    </Typography>
                  )}
                  {deliveryInfo.rushDeliveryInstructions && (
                    <Typography variant="body2" gutterBottom>
                      Instructions: {deliveryInfo.rushDeliveryInstructions}
                    </Typography>
                  )}
                </Box>
              )}
            </Box>
          </Paper>

          <Paper sx={{ p: 3 }}>
            <Typography variant="h6" gutterBottom>
              Payment Method
            </Typography>

            <FormControl component="fieldset">
              <RadioGroup
                aria-label="payment-method"
                name="payment-method"
                value={paymentMethod}
                onChange={handlePaymentMethodChange}
              >
                <FormControlLabel
                  value="credit_card"
                  control={<Radio />}
                  label="Credit/Debit Card (VNPay)"
                />
                <FormControlLabel
                  value="bank_transfer"
                  control={<Radio />}
                  label="Bank Transfer"
                  disabled
                />
                <FormControlLabel
                  value="cod"
                  control={<Radio />}
                  label="Cash on Delivery"
                  disabled
                />
              </RadioGroup>
            </FormControl>

            <Alert severity="info" sx={{ mt: 2 }}>
              For this demo, only credit card payments via VNPay are enabled.
            </Alert>
          </Paper>
        </Box>

        {/* Payment Summary - Right Column */}
        <Box sx={{ flex: { xs: "1 1 100%", md: "0 0 40%" } }}>
          <Paper sx={{ p: 3, position: { md: "sticky" }, top: { md: "20px" } }}>
            <Typography variant="h6" gutterBottom>
              Payment Summary
            </Typography>

            <Box
              sx={{ display: "flex", justifyContent: "space-between", mb: 1 }}
            >
              <Typography variant="body1">Subtotal:</Typography>
              <Typography variant="body1">
                {formatPrice(calculateSubtotal())}
              </Typography>
            </Box>

            <Box
              sx={{ display: "flex", justifyContent: "space-between", mb: 1 }}
            >
              <Typography variant="body1">VAT (10%):</Typography>
              <Typography variant="body1">
                {formatPrice(calculateTax())}
              </Typography>
            </Box>

            <Box
              sx={{ display: "flex", justifyContent: "space-between", mb: 1 }}
            >
              <Typography variant="body1">Delivery Fee:</Typography>
              <Typography variant="body1">
                {formatPrice(calculateDeliveryFee())}
              </Typography>
            </Box>

            <Divider sx={{ my: 2 }} />

            <Box
              sx={{ display: "flex", justifyContent: "space-between", mb: 3 }}
            >
              <Typography variant="h6">Total:</Typography>
              <Typography variant="h6">
                {formatPrice(calculateTotal())}
              </Typography>
            </Box>

            <Box sx={{ display: "flex", justifyContent: "space-between" }}>
              <Button
                variant="outlined"
                onClick={() => navigate("/checkout/delivery")}
                disabled={loading}
              >
                Back
              </Button>

              <Button
                variant="contained"
                color="primary"
                onClick={handleProcessPayment}
                disabled={loading}
                sx={{ minWidth: 150 }}
              >
                {loading ? (
                  <CircularProgress size={24} color="inherit" />
                ) : (
                  "Pay Now"
                )}
              </Button>
            </Box>
          </Paper>
        </Box>
      </Box>
    </Container>
  );
};

export default PaymentPage;
