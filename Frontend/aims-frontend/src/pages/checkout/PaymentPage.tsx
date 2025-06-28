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
import cartService from "../../services/cartService"; // Import cartService

interface DeliveryInfo {
  recipientName: string;
  email: string;
  phone: string;
  province: string;
  deliveryAddress: string;
  isRushOrder: boolean;
  rushDeliveryTime?: string;
  rushDeliveryInstructions?: string;
  deliveryFee: number;
}

interface PaymentSummary {
  subtotal: number;
  tax: number;
  deliveryFee: number;
  rushDeliveryFee?: number;
  total: number;
}

const PaymentPage: React.FC = () => {
  const navigate = useNavigate();
  const { cart } = useCart();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [deliveryInfo, setDeliveryInfo] = useState<DeliveryInfo | null>(null);
  const [paymentMethod, setPaymentMethod] = useState("credit_card");
  const [paymentSummary, setPaymentSummary] = useState<PaymentSummary | null>(
    null
  );
  const [calculatingPayment, setCalculatingPayment] = useState(false);

  // Fetch delivery info and calculate payment when component loads
  useEffect(() => {
    // Retrieve delivery info from localStorage
    const storedDeliveryInfo = localStorage.getItem("deliveryInfo");

    if (!storedDeliveryInfo) {
      navigate("/checkout/delivery");
      return;
    }

    const parsedDeliveryInfo = JSON.parse(storedDeliveryInfo);
    setDeliveryInfo(parsedDeliveryInfo);

    // Calculate payment summary with backend once we have delivery info
    calculatePaymentSummary(parsedDeliveryInfo);
  }, [navigate]);

  // Function to calculate payment summary using backend API
  const calculatePaymentSummary = async (deliveryInfo: DeliveryInfo) => {
    if (!cart || !cart.items || cart.items.length === 0) {
      return;
    }

    setCalculatingPayment(true);
    try {
      // Map cart items to the format expected by the API
      const cartItems = cart.items.map((item) => ({
        productId: item.product.id,
        quantity: item.quantity,
      }));

      // Call the backend API with cart items, rush delivery status, and province
      const result = await cartService.calculateCart(
        cartItems,
        deliveryInfo.isRushOrder,
        deliveryInfo.province
      );

      // Set payment summary from the API response
      setPaymentSummary({
        subtotal: result.subtotal,
        tax: result.tax,
        deliveryFee: result.deliveryFee,
        rushDeliveryFee: result.rushDeliveryFee,
        total: result.total,
      });

      // Store invoice data for later use
      localStorage.setItem(
        "invoiceData",
        JSON.stringify({
          totalProductPriceBeforeVAT: result.subtotal,
          totalProductPriceAfterVAT: result.subtotal + result.tax,
          deliveryFee: result.deliveryFee,
          totalAmount: result.total,
        })
      );
    } catch (error) {
      console.error("Failed to calculate payment summary:", error);
      setError("Failed to calculate payment details. Please try again.");

      // Fallback to simple calculation if API fails
      const subtotal = cart.subtotal || 0;
      const tax = subtotal * 0.1;
      const deliveryFee = deliveryInfo.deliveryFee || 0;

      setPaymentSummary({
        subtotal,
        tax,
        deliveryFee,
        total: subtotal + tax + deliveryFee,
      });
    } finally {
      setCalculatingPayment(false);
    }
  };

  const handlePaymentMethodChange = (
    event: React.ChangeEvent<HTMLInputElement>
  ) => {
    setPaymentMethod(event.target.value);
  };

  const handleProcessPayment = async () => {
    try {
      setLoading(true);
      setError("");

      if (!cart || !cart.items.length || !deliveryInfo) {
        setError("Missing cart or delivery information");
        return;
      }

      // Calculate final payment amount at payment time (not using stored values)
      const cartItems = cart.items.map((item) => ({
        productId: item.product.id,
        quantity: item.quantity,
      }));

      try {
        // Re-calculate totals at payment time to prevent manipulation
        const finalCalculation = await cartService.calculateCart(
          cartItems,
          deliveryInfo.isRushOrder,
          deliveryInfo.province
        );

        // Create transaction data with the freshly calculated amount
        const transactionData = {
          transactionId: `TXN${Date.now()}`,
          bankCode: "VNPAY",
          amount: finalCalculation.total,
          cardType: paymentMethod,
          payDate: new Date().toISOString(),
          errorMessage: "",
        };

        // Store transaction in localStorage
        localStorage.setItem(
          "transactionData",
          JSON.stringify(transactionData)
        );

        // Create order data with fresh calculation
        const orderData = {
          transaction: transactionData,
          invoice: {
            totalProductPriceBeforeVAT: finalCalculation.subtotal,
            totalProductPriceAfterVAT:
              finalCalculation.subtotal + finalCalculation.tax,
            deliveryFee: finalCalculation.deliveryFee,
            totalAmount: finalCalculation.total,
            cart: cart,
          },
          deliveryInfo: deliveryInfo,
          status: "PENDING",
        };

        // Store temporary order in localStorage
        const tempOrderId = `order_${Date.now()}`;
        localStorage.setItem(
          "orderData",
          JSON.stringify({ ...orderData, id: tempOrderId })
        );

        // Pass the orderId to the backend so it can validate the amount later
        const amount = Math.round(finalCalculation.total);
        const params = new URLSearchParams({
          gateway: "vnpay",
          amount: amount.toString(),
          orderId: tempOrderId,
        });

        window.location.href = `http://localhost:8080/api/pay_test?${params.toString()}`;
      } catch (calculationError) {
        console.error("Payment calculation failed:", calculationError);
        setError("Failed to calculate final payment amount. Please try again.");
      }
    } catch (error) {
      console.error("Payment processing failed:", error);
      setError("Failed to process payment. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  const formatPrice = (price: number) => {
    return new Intl.NumberFormat("vi-VN", {
      style: "currency",
      currency: "VND",
    }).format(price);
  };

  const isPageLoading = !cart || !deliveryInfo || calculatingPayment;

  if (isPageLoading) {
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
        {/* Order Details & Delivery Info - Left Column (unchanged) */}
        <Box sx={{ flex: { xs: "1 1 100%", md: "0 0 60%" } }}>
          {/* Your existing order details and delivery info code */}
          {/* No changes needed here */}
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

          {/* Delivery Information */}
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
                  {deliveryInfo.deliveryAddress}, {deliveryInfo.province}
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

          {/* Payment Method */}
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
                {formatPrice(paymentSummary?.subtotal || 0)}
              </Typography>
            </Box>

            <Box
              sx={{ display: "flex", justifyContent: "space-between", mb: 1 }}
            >
              <Typography variant="body1">VAT (10%):</Typography>
              <Typography variant="body1">
                {formatPrice(paymentSummary?.tax || 0)}
              </Typography>
            </Box>

            <Box
              sx={{ display: "flex", justifyContent: "space-between", mb: 1 }}
            >
              <Typography variant="body1">Delivery Fee:</Typography>
              <Typography variant="body1">
                {formatPrice(paymentSummary?.deliveryFee || 0)}
              </Typography>
            </Box>

            {deliveryInfo.isRushOrder && (
              <Box
                sx={{ display: "flex", justifyContent: "space-between", mb: 1 }}
              >
                <Typography variant="body1" color="primary">
                  Rush Delivery Fee:
                </Typography>
                <Typography variant="body1" color="primary">
                  {formatPrice(paymentSummary?.rushDeliveryFee || 0)}
                </Typography>
              </Box>
            )}

            <Divider sx={{ my: 2 }} />

            <Box
              sx={{ display: "flex", justifyContent: "space-between", mb: 3 }}
            >
              <Typography variant="h6">Total:</Typography>
              <Typography variant="h6">
                {formatPrice(paymentSummary?.total || 0)}
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
