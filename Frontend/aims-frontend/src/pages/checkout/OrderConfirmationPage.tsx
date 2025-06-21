// src/pages/checkout/OrderConfirmationPage.tsx
import React, { useState, useEffect } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import {
  Container,
  Typography,
  Box,
  Paper,
  Button,
  Divider,
  CircularProgress,
  Alert,
  List,
  ListItem,
} from "@mui/material";
import CheckCircleOutlineIcon from "@mui/icons-material/CheckCircleOutline";
import ErrorOutlineIcon from "@mui/icons-material/ErrorOutline";
import orderService from "../../services/orderService";
import { Order } from "../../types/order";

const OrderConfirmationPage: React.FC = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [paymentSuccess, setPaymentSuccess] = useState(false);
  const [orderDetails, setOrderDetails] = useState<Order | null>(null);
  useEffect(() => {
    const verifyPayment = async () => {
      try {

        setLoading(true);
        
        const searchParams = new URLSearchParams(window.location.search);
        const responseCode = searchParams.get('vnp_ResponseCode');
        const status = responseCode === "00" ? "success" : "failure";
      
        // Check payment status
        if (status === "success") {
          // In a real application, you would fetch the order details from your API
          // const response = await orderService.getOrderById(orderId);
          // setOrderDetails(response.data);

          // For demo purposes, let's create a mock order
          const deliveryInfo = JSON.parse(
          localStorage.getItem("deliveryInfo") || "{}"
          );
          const userEmail = deliveryInfo.email;
          const invoiceData = JSON.parse(
            localStorage.getItem("invoiceData") || "{}"
          );
          const cartData = JSON.parse(localStorage.getItem("cart") || "{}");
          const transactionData = formatTransactionData();
          // const userEmail = deliveryInfo.getItem("email");
          const params = new URLSearchParams({
              toGmail: userEmail?.toString()|| "",
              // body: "okok",
          });
          // Send order confirmation email and log result
            sendOrderConfirmationEmail(params)
            .then((result) => {
            if (!result.success) {
              console.warn("Order confirmation email not sent:", result.message);
            }
            })
            .catch((emailError: any) => {
            console.error("Failed to send order confirmation email:", emailError?.message || emailError);
            });
          // Create request object
          const checkoutRequest = {
            deliveryInfo,
            invoiceData: {
              ...invoiceData,
              cart: cartData,
            },
            transactionData,
            status: "PENDING",
          };

          const response = await orderService.completeCheckout(checkoutRequest);

          sessionStorage.setItem("orderAlreadyCreated", "true");

          setOrderDetails(response.data);
          setPaymentSuccess(true);

          // Clear localStorage
          localStorage.removeItem("cart");
          localStorage.removeItem("deliveryInfo");
          localStorage.removeItem("invoiceData");
          localStorage.removeItem("transactionData");
        } else {
          setPaymentSuccess(false);
          setError("Payment was not successful. Please try again.");
        }
      } catch (err: any) {
        console.error("Payment verification error:", err);
        setError(err.message || "Failed to verify payment");
        setPaymentSuccess(false);
      } finally {
        setLoading(false);
      }
    };

    verifyPayment();
  }, []);
  /**
   * Extracts and formats VNPay transaction data from URL parameters
   */
  function formatTransactionData(): {
    amount: number;
    bankCode: string;
    cardType: string;
    errorMessage: string;
    payDate: string;
    transactionId: string;
  } {
    // Get the current URL's query parameters
    const urlParams = new URLSearchParams(window.location.search);
    const responseCode = urlParams.get('vnp_ResponseCode');
    const isPaymentSuccessful = responseCode === "00";
    setPaymentSuccess(isPaymentSuccessful);
    // Format the payment date
    const formatPayDate = (payDateStr: string | null): string => {
      if (!payDateStr) return new Date().toISOString();
      // Convert YYYYMMDDHHMMSS format to ISO string
      const year = payDateStr.substring(0, 4);
      const month = payDateStr.substring(4, 6);
      const day = payDateStr.substring(6, 8);
      const hour = payDateStr.substring(8, 10);
      const minute = payDateStr.substring(10, 12);
      const second = payDateStr.substring(12, 14);
      return new Date(`${year}-${month}-${day}T${hour}:${minute}:${second}.000Z`).toISOString();
    };
    if (isPaymentSuccessful) {
      // Create and format transaction data
      const transactionData = {
        amount: parseInt(urlParams.get('vnp_Amount') ?? '0') / 100,
        bankCode: urlParams.get('vnp_BankCode'),
        cardType: urlParams.get('vnp_BankCode'),
        errorMessage: "",
        payDate: formatPayDate(urlParams.get('vnp_PayDate')),
        transactionId: urlParams.get('vnp_TransactionNo'),
      };

      // Save transaction data to localStorage
      localStorage.setItem("transactionData", JSON.stringify(transactionData));
      console.log("Transaction data created and saved:", transactionData);

      return transactionData;
    }

    console.log("Payment failed with response code:", responseCode);
    // Return a default object with empty or zero values to satisfy the return type
    return {
      amount: 0,
      bankCode: "",
      cardType: "",
      errorMessage: "Payment failed",
      payDate: new Date().toISOString(),
      transactionId: ""
    };
  }
  
  const formatPrice = (price: number) => {
    return new Intl.NumberFormat("vi-VN", {
      style: "currency",
      currency: "VND",
    }).format(price);
  };

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleString("en-US", {
      year: "numeric",
      month: "long",
      day: "numeric",
      hour: "2-digit",
      minute: "2-digit",
    });
  };

  if (loading) {
    return (
      <Container sx={{ py: 8, textAlign: "center" }}>
        <CircularProgress />
        <Typography variant="h6" sx={{ mt: 2 }}>
          Verifying your payment...
        </Typography>
      </Container>
    );
  }

  return (
    <Container maxWidth="md" sx={{ py: 4 }}>
      {paymentSuccess ? (
        <>
          <Box sx={{ textAlign: "center", mb: 4 }}>
            <CheckCircleOutlineIcon
              color="success"
              sx={{ fontSize: 60, mb: 2 }}
            />
            <Typography variant="h4" gutterBottom>
              Payment Successful
            </Typography>
            <Typography variant="body1">
              Your order has been placed successfully.
            </Typography>
          </Box>

          {orderDetails && (
            <>
              <Paper sx={{ p: 3, mb: 3 }}>
                <Typography variant="h6" gutterBottom>
                  Order Information
                </Typography>

                <Box sx={{ display: "flex", flexWrap: "wrap", gap: 3, mb: 3 }}>
                  <Box sx={{ minWidth: "45%" }}>
                    <Typography variant="subtitle2" color="text.secondary">
                      Order ID
                    </Typography>
                    <Typography variant="body1" gutterBottom>
                      {orderDetails.id}
                    </Typography>
                  </Box>

                  <Box sx={{ minWidth: "45%" }}>
                    <Typography variant="subtitle2" color="text.secondary">
                      Order Date
                    </Typography>
                    <Typography variant="body1" gutterBottom>
                      {formatDate(orderDetails.transaction.payDate)}
                    </Typography>
                  </Box>

                  <Box sx={{ minWidth: "45%" }}>
                    <Typography variant="subtitle2" color="text.secondary">
                      Recipient
                    </Typography>
                    <Typography variant="body1" gutterBottom>
                      {orderDetails.deliveryInfo.recipientName}
                    </Typography>
                  </Box>

                  <Box sx={{ minWidth: "45%" }}>
                    <Typography variant="subtitle2" color="text.secondary">
                      Contact
                    </Typography>
                    <Typography variant="body1" gutterBottom>
                      {orderDetails.deliveryInfo.phoneNumber}
                    </Typography>
                    <Typography
                      variant="body2"
                      color="text.secondary"
                      gutterBottom
                    >
                      {orderDetails.deliveryInfo.email}
                    </Typography>
                  </Box>

                  <Box sx={{ width: "100%" }}>
                    <Typography variant="subtitle2" color="text.secondary">
                      Delivery Address
                    </Typography>
                    <Typography variant="body1" gutterBottom>
                      {orderDetails.deliveryInfo.deliveryAddress},{" "}
                      {orderDetails.deliveryInfo.province}
                    </Typography>
                  </Box>
                </Box>

                <Divider sx={{ my: 2 }} />

                <Typography variant="h6" gutterBottom>
                  Items Ordered
                </Typography>

                {orderDetails.invoice.cart.items &&
                orderDetails.invoice.cart.items.length > 0 ? (
                  <List sx={{ mb: 3 }}>
                    {orderDetails.invoice.cart.items.map((item) => (
                      <ListItem key={item.id} sx={{ py: 1, px: 0 }}>
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
                            {formatPrice(
                              item.product.currentPrice * item.quantity
                            )}
                          </Typography>
                        </Box>
                      </ListItem>
                    ))}
                  </List>
                ) : (
                  <Typography
                    variant="body2"
                    color="text.secondary"
                    sx={{ my: 2 }}
                  >
                    No items in this order.
                  </Typography>
                )}

                <Divider sx={{ my: 2 }} />

                <Typography variant="h6" gutterBottom>
                  Payment Details
                </Typography>

                <Box sx={{ display: "flex", flexWrap: "wrap", gap: 3, mb: 3 }}>
                  <Box sx={{ minWidth: "45%" }}>
                    <Typography variant="subtitle2" color="text.secondary">
                      Payment Method
                    </Typography>
                    <Typography variant="body1" gutterBottom>
                      {orderDetails.transaction.cardType || "Online Payment"}
                    </Typography>
                  </Box>

                  <Box sx={{ minWidth: "45%" }}>
                    <Typography variant="subtitle2" color="text.secondary">
                      Transaction ID
                    </Typography>
                    <Typography variant="body1" gutterBottom>
                      {orderDetails.transaction.transactionNo ||
                        orderDetails.transaction.id}
                    </Typography>
                  </Box>

                  <Box sx={{ minWidth: "45%" }}>
                    <Typography variant="subtitle2" color="text.secondary">
                      Payment Date
                    </Typography>
                    <Typography variant="body1" gutterBottom>
                      {formatDate(orderDetails.transaction.payDate)}
                    </Typography>
                  </Box>

                  <Box sx={{ minWidth: "45%" }}>
                    <Typography variant="subtitle2" color="text.secondary">
                      Payment Status
                    </Typography>
                    <Typography
                      variant="body1"
                      color={
                        orderDetails.status === "COMPLETED"
                          ? "success.main"
                          : "info.main"
                      }
                      gutterBottom
                    >
                      {orderDetails.status}
                    </Typography>
                  </Box>

                  {orderDetails.transaction.bankCode && (
                    <Box sx={{ minWidth: "45%" }}>
                      <Typography variant="subtitle2" color="text.secondary">
                        Bank
                      </Typography>
                      <Typography variant="body1" gutterBottom>
                        {orderDetails.transaction.bankCode}
                      </Typography>
                    </Box>
                  )}
                </Box>

                <Divider sx={{ my: 2 }} />

                <Box
                  sx={{
                    display: "flex",
                    flexDirection: "column",
                    alignItems: "flex-end",
                  }}
                >
                  <Box
                    sx={{
                      display: "flex",
                      justifyContent: "space-between",
                      width: "250px",
                      mb: 1,
                    }}
                  >
                    <Typography variant="body1">Subtotal:</Typography>
                    <Typography variant="body1">
                      {formatPrice(
                        orderDetails.invoice.cart.totalProductPriceBeforeVAT
                      )}
                    </Typography>
                  </Box>

                  <Box
                    sx={{
                      display: "flex",
                      justifyContent: "space-between",
                      width: "250px",
                      mb: 1,
                    }}
                  >
                    <Typography variant="body1">VAT (10%):</Typography>
                    <Typography variant="body1">
                      {formatPrice(
                        orderDetails.invoice.totalProductPriceAfterVAT -
                          orderDetails.invoice.cart.totalProductPriceBeforeVAT
                      )}
                    </Typography>
                  </Box>

                  <Box
                    sx={{
                      display: "flex",
                      justifyContent: "space-between",
                      width: "250px",
                      mb: 1,
                    }}
                  >
                    <Typography variant="body1">Delivery Fee:</Typography>
                    <Typography variant="body1">
                      {formatPrice(orderDetails.invoice.deliveryFee)}
                    </Typography>
                  </Box>

                  <Divider sx={{ my: 1, width: "250px" }} />

                  <Box
                    sx={{
                      display: "flex",
                      justifyContent: "space-between",
                      width: "250px",
                    }}
                  >
                    <Typography variant="h6">Total:</Typography>
                    <Typography variant="h6">
                      {formatPrice(orderDetails.invoice.totalAmount)}
                    </Typography>
                  </Box>
                </Box>
              </Paper>

              <Box sx={{ textAlign: "center" }}>
                <Typography variant="body2" gutterBottom>
                  A confirmation email has been sent to{" "}
                  {orderDetails.deliveryInfo.email}.
                </Typography>

                <Button
                  variant="contained"
                  color="primary"
                  onClick={() => navigate("/")}
                  sx={{ mt: 2 }}
                >
                  Continue Shopping
                </Button>
              </Box>
            </>
          )}
        </>
      ) : (
        <Box sx={{ textAlign: "center" }}>
          <ErrorOutlineIcon color="error" sx={{ fontSize: 60, mb: 2 }} />
          <Typography variant="h4" gutterBottom>
            Payment Failed
          </Typography>

          <Alert severity="error" sx={{ mb: 3 }}>
            {error || "Your payment could not be processed."}
          </Alert>

          <Box sx={{ mt: 3 }}>
            <Button
              variant="contained"
              onClick={() => navigate("/checkout/payment")}
              sx={{ mr: 2 }}
            >
              Try Again
            </Button>

            <Button variant="outlined" onClick={() => navigate("/")}>
              Return to Home
            </Button>
          </Box>
        </Box>
      )}
    </Container>
  );
};

/**
 * Send order confirmation email with proper error handling and typing
 * @param email - Recipient's email address
 * @param orderDetails - Order details for the email body
 * @returns Promise with success status and message
 */
const sendOrderConfirmationEmail = async (
  params: URLSearchParams
): Promise<{success: boolean; message: string}> => {
  try {
    
    // Set timeout to prevent hanging requests
    const controller = new AbortController();
    const timeoutId = setTimeout(() => controller.abort(), 5000);
    
    // Make the request with proper headers and signal
    const response = await fetch(`http://localhost:8080/send-gmail?${params.toString()}`, {
      method: "GET",
      headers: {
        "Accept": "application/json",
        "Content-Type": "application/json"
      },
      signal: controller.signal
    });
    
    clearTimeout(timeoutId);
    
    // Parse the response
    const result = await response.json();
    
    if (!response.ok) {
      return {
        success: false,
        message: result.message || `Failed with status: ${response.status}`
      };
    }
    
    return {
      success: true,
      message: "Order confirmation email sent successfully"
    };
  } catch (error: any) {
    if (error.name === 'AbortError') {
      return {
        success: false, 
        message: "Email request timed out after 5 seconds"
      };
    }
    
    return {
      success: false,
      message: error.message || "Failed to send order confirmation email"
    };
  }
};

export default OrderConfirmationPage;
