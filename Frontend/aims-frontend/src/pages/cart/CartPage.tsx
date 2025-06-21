// src/pages/cart/CartPage.tsx
import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import {
  Container,
  Typography,
  Box,
  Button,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  IconButton,
  TextField,
  Divider,
  Alert,
  CircularProgress,
} from "@mui/material";
import DeleteIcon from "@mui/icons-material/Delete";
import { useCart } from "../../contexts/CartContext";

const CartPage: React.FC = () => {
  const { cart, loading, updateCartItem, removeFromCart, checkInventory } =
    useCart();
  const [inventoryError, setInventoryError] = useState<Record<string, number>>(
    {}
  );
  const [checkingInventory, setCheckingInventory] = useState(false);
  const navigate = useNavigate();

  const handleQuantityChange = (productId: string, newQuantity: number) => {
    if (newQuantity > 0) {
      updateCartItem(productId, newQuantity);
    }
  };

  const handleRemoveItem = (productId: string) => {
    removeFromCart(productId);
  };

  const handleContinueShopping = () => {
    navigate("/");
  };

  const handleProceedToCheckout = async () => {
    setCheckingInventory(true);
    setInventoryError({});

    try {
      const result = await checkInventory();

      if (result.allAvailable) {
        // All items are available, proceed to checkout
        navigate("/checkout/delivery");
      } else {
        // Some items are out of stock, create inventory error map
        const errorMap: Record<string, number> = {};

        result.outOfStockProducts?.forEach((item) => {
          // Convert numeric productId to string for consistency
          errorMap[item.productId.toString()] = item.available;
        });

        setInventoryError(errorMap);

        // Scroll to the top to make the error alert visible
        window.scrollTo(0, 0);
      }
    } catch (error) {
      console.error("Failed to check inventory:", error);
      // You might want to add a general error state here
    } finally {
      setCheckingInventory(false);
    }
  };

  const formatPrice = (price: number) => {
    return new Intl.NumberFormat("vi-VN", {
      style: "currency",
      currency: "VND",
    }).format(price);
  };

  if (loading) {
    return (
      <Container sx={{ py: 4 }}>
        <Box sx={{ display: "flex", justifyContent: "center", py: 4 }}>
          <CircularProgress />
        </Box>
      </Container>
    );
  }

  if (!cart) {
    return (
      <Container sx={{ py: 4 }}>
        <Paper sx={{ p: 3, textAlign: "center" }}>
          <Typography variant="h5" gutterBottom>
            Your cart is empty
          </Typography>
          <Typography variant="body1" sx={{ mb: 3 }}>
            Looks like you haven't added any products to your cart yet.
          </Typography>
          <Button
            variant="contained"
            color="primary"
            onClick={handleContinueShopping}
          >
            Continue Shopping
          </Button>
        </Paper>
      </Container>
    );
  }

  return (
    <Container sx={{ py: 4 }}>
      <Typography variant="h4" gutterBottom>
        Your Shopping Cart
      </Typography>

      {Object.keys(inventoryError).length > 0 && (
        <Alert severity="error" sx={{ mb: 3 }}>
          Some products in your cart have insufficient inventory.
        </Alert>
      )}

      <TableContainer component={Paper} sx={{ mb: 4 }}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>Product</TableCell>
              <TableCell align="right">Price</TableCell>
              <TableCell align="center">Quantity</TableCell>
              <TableCell align="right">Subtotal</TableCell>
              <TableCell align="center">Actions</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {cart.items.map((item) => (
              <TableRow key={item.product.id}>
                <TableCell component="th" scope="row">
                  <Box sx={{ display: "flex", alignItems: "center" }}>
                    <Box
                      component="img"
                      sx={{ width: 60, mr: 2, borderRadius: 1 }}
                      src={item.product.imageURL || "/placeholder.jpg"}
                      alt={item.product.title}
                    />
                    <Box>
                      <Typography variant="subtitle1">
                        {item.product.title}
                      </Typography>
                      <Typography variant="body2" color="text.secondary">
                        PRODUCT TYPE • {item.product.category}
                      </Typography>
                      {inventoryError[item.product.id] !== undefined && (
                        <Typography variant="body2" color="error">
                          Only {inventoryError[item.product.id]} available
                        </Typography>
                      )}
                    </Box>
                  </Box>
                </TableCell>
                <TableCell align="right">
                  {formatPrice(item.product.currentPrice)}
                </TableCell>
                <TableCell align="center">
                  <TextField
                    type="number"
                    value={item.quantity}
                    onChange={(e) =>
                      handleQuantityChange(
                        item.product.id.toString(),
                        parseInt(e.target.value) || 1
                      )
                    }
                    InputProps={{
                      inputProps: {
                        min: 1,
                        max: item.product.quantity,
                        style: { textAlign: "center" },
                      },
                    }}
                    size="small"
                    sx={{ width: 70 }}
                    error={inventoryError[item.product.id] !== undefined}
                  />
                </TableCell>
                <TableCell align="right">
                  {formatPrice(
                    item.subtotal
                      ? item.subtotal
                      : item.product.currentPrice * item.quantity
                  )}
                </TableCell>
                <TableCell align="center">
                  <IconButton
                    color="error"
                    onClick={() => handleRemoveItem(item.product.id.toString())}
                  >
                    <DeleteIcon />
                  </IconButton>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>

      <Box sx={{ display: "flex", justifyContent: "flex-end" }}>
        <Paper
          sx={{
            p: 3,
            width: {
              xs: "100%",
              sm: "calc(50% - 16px)",
              md: "calc(33.33% - 16px)",
              lg: "calc(25% - 16px)",
              xl: "calc(20% - 16px)", // Add support for extra large screens
            },
          }}
        >
          <Typography variant="h6" gutterBottom>
            Order Summary
          </Typography>

          <Box sx={{ display: "flex", justifyContent: "space-between", mb: 1 }}>
            <Typography variant="body1">Subtotal:</Typography>
            <Typography variant="body1">
              {formatPrice(
                cart.subtotal ? cart.subtotal : cart.totalProductPriceBeforeVAT
              )}
            </Typography>
          </Box>

          <Box sx={{ display: "flex", justifyContent: "space-between", mb: 2 }}>
            <Typography variant="body1">VAT (10%):</Typography>
            <Typography variant="body1">
              {formatPrice(cart.tax ? cart.tax : 0)}
            </Typography>
          </Box>

          <Divider sx={{ mb: 2 }} />

          <Box sx={{ display: "flex", justifyContent: "space-between", mb: 3 }}>
            <Typography variant="h6">Total:</Typography>
            <Typography variant="h6">
              {formatPrice(cart.total ? cart.total : 0)}
            </Typography>
          </Box>

          <Box sx={{ display: "flex", gap: 2 }}>
            <Button
              variant="outlined"
              onClick={handleContinueShopping}
              sx={{ flex: 1 }}
            >
              Continue Shopping
            </Button>

            <Button
              variant="contained"
              color="primary"
              onClick={handleProceedToCheckout}
              disabled={checkingInventory}
              sx={{ flex: 1 }}
            >
              {checkingInventory ? <CircularProgress size={24} /> : "Checkout"}
            </Button>
          </Box>
        </Paper>
      </Box>
    </Container>
  );
};

export default CartPage;
