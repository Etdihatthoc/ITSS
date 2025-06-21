// src/pages/orders/ViewOrderPage.tsx
import React, { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import {
  Container,
  Typography,
  Box,
  Paper,
  Button,
  Divider,
  CircularProgress,
  Alert,
  Chip,
  List,
  ListItem,
  ListItemText,
  TextField,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Table,
  TableHead,
  TableBody,
  TableRow,
  TableCell,
} from "@mui/material";
import {
  Print as PrintIcon,
  Edit as EditIcon,
  KeyboardReturn as ReturnIcon,
} from "@mui/icons-material";
import { Order, OrderStatus } from "../../types/order";
import orderService from "../../services/orderService";
import { useAuth } from "../../contexts/AuthContext";
import { UserRole } from "../../types/user";

const ViewOrderPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { user } = useAuth();

  // State for order
  const [order, setOrder] = useState<Order | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  // State for status update dialog (for admins)
  const [openStatusDialog, setOpenStatusDialog] = useState(false);
  const [newStatus, setNewStatus] = useState<string>("PENDING");
  const [statusUpdateLoading, setStatusUpdateLoading] = useState(false);

  // State for notes editing
  const [isEditingNotes, setIsEditingNotes] = useState(false);
  const [notes, setNotes] = useState("");
  const [savingNotes, setSavingNotes] = useState(false);

  // Load order on component mount
  useEffect(() => {
    if (id) {
      fetchOrder(id);
    }
  }, [id]);

  // Fetch order from API
  const fetchOrder = async (orderId: string) => {
    try {
      setLoading(true);
      setError("");

      const response = await orderService.getOrderById(orderId);
      setOrder(response.data);
      console.log("Fetched order:", response.data);
    } catch (err: any) {
      console.error("Failed to fetch order:", err);
      setError(err.response?.data?.message || "Failed to load order details");
    } finally {
      setLoading(false);
    }
  };

  // Check if user is admin or product manager
  const isAdmin = true;
  // user &&
  // (user.roles.includes(UserRole.ADMIN) ||
  //   user.roles.includes(UserRole.PRODUCT_MANAGER));

  // Open dialog to update order status (admin only)
  const handleStatusUpdateClick = () => {
    if (order) {
      setNewStatus(order.status);
      setOpenStatusDialog(true);
    }
  };

  // Update order status
  const handleStatusUpdateConfirm = async () => {
    if (!order) return;

    try {
      setStatusUpdateLoading(true);
      await orderService.updateOrderStatus(order.id.toString(), newStatus);

      // Refresh order details
      fetchOrder(order.id.toString());

      // Close dialog
      setOpenStatusDialog(false);
    } catch (err: any) {
      console.error("Failed to update order status:", err);
      setError(err.response?.data?.message || "Failed to update order status");
    } finally {
      setStatusUpdateLoading(false);
    }
  };

  // Start editing notes
  const handleEditNotes = () => {
    setIsEditingNotes(true);
  };

  // Save notes
  const handleSaveNotes = async () => {
    if (!order) return;

    try {
      setSavingNotes(true);

      // Refresh order details
      fetchOrder(order.id.toString());

      // Exit edit mode
      setIsEditingNotes(false);
    } catch (err: any) {
      console.error("Failed to update notes:", err);
      setError(err.response?.data?.message || "Failed to update notes");
    } finally {
      setSavingNotes(false);
    }
  };

  // Print order receipt
  const handlePrintReceipt = () => {
    window.print();
  };

  // Format price for display
  const formatPrice = (price: number) => {
    return new Intl.NumberFormat("vi-VN", {
      style: "currency",
      currency: "VND",
    }).format(price);
  };

  // Format date for display
  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleString("en-US", {
      year: "numeric",
      month: "long",
      day: "numeric",
      hour: "2-digit",
      minute: "2-digit",
    });
  };

  // Get status label
  const getStatusLabel = (status: string) => {
    return status;
  };

  // Get status color
  const getStatusColor = (status: string) => {
    return status;
  };

  // Get available status transitions based on current status
  const getAvailableStatusTransitions = (currentStatus: string) => {
    switch (currentStatus) {
      case OrderStatus.PENDING:
        return [OrderStatus.APPROVED, OrderStatus.REJECTED];
      case OrderStatus.APPROVED:
        return [OrderStatus.SHIPPED, OrderStatus.CANCELLED];
      case OrderStatus.SHIPPED:
        return [OrderStatus.DELIVERED];
      case OrderStatus.DELIVERED:
        return [];
      case OrderStatus.REJECTED:
        return [];
      case OrderStatus.CANCELLED:
        return [];
      default:
        return [];
    }
  };

  if (loading) {
    return (
      <Container sx={{ py: 4, textAlign: "center" }}>
        <CircularProgress />
        <Typography variant="h6" sx={{ mt: 2 }}>
          Loading order details...
        </Typography>
      </Container>
    );
  }

  if (error) {
    return (
      <Container sx={{ py: 4 }}>
        <Alert severity="error" sx={{ mb: 3 }}>
          {error}
        </Alert>
        <Button
          startIcon={<ReturnIcon />}
          variant="outlined"
          onClick={() => navigate(-1)}
        >
          Go Back
        </Button>
      </Container>
    );
  }

  if (!order) {
    return (
      <Container sx={{ py: 4 }}>
        <Alert severity="warning" sx={{ mb: 3 }}>
          Order not found. It may have been deleted or you don't have permission
          to view it.
        </Alert>
        <Button
          startIcon={<ReturnIcon />}
          variant="outlined"
          onClick={() => navigate(-1)}
        >
          Go Back
        </Button>
      </Container>
    );
  }

  return (
    <Container maxWidth="lg" sx={{ py: 4 }} className="print-container">
      <Box
        sx={{
          display: "flex",
          justifyContent: "space-between",
          alignItems: "center",
          mb: 3,
        }}
        className="no-print"
      >
        <Typography variant="h4">Order Details</Typography>
        <Box sx={{ display: "flex", gap: 2 }}>
          <Button
            variant="outlined"
            startIcon={<ReturnIcon />}
            onClick={() => navigate(-1)}
          >
            Back
          </Button>

          <Button
            variant="outlined"
            startIcon={<PrintIcon />}
            onClick={handlePrintReceipt}
          >
            Print Receipt
          </Button>

          {isAdmin && (
            <Button
              variant="contained"
              startIcon={<EditIcon />}
              onClick={handleStatusUpdateClick}
              disabled={
                order.status === OrderStatus.DELIVERED ||
                order.status === OrderStatus.REJECTED ||
                order.status === OrderStatus.CANCELLED
              }
            >
              Update Status
            </Button>
          )}
        </Box>
      </Box>

      <Paper sx={{ p: 3, mb: 3 }} elevation={2}>
        <Box
          sx={{
            display: "flex",
            justifyContent: "space-between",
            alignItems: "center",
            mb: 2,
          }}
        >
          <Box>
            <Typography variant="h5" gutterBottom>
              Order #{order.id}
            </Typography>
            <Typography variant="body2" color="text.secondary">
              Placed on {formatDate(order.transaction.payDate)}
            </Typography>
          </Box>
          <Chip
            label={getStatusLabel(order.status)}
            color={getStatusColor(order.status) as any}
            sx={{ fontWeight: "bold" }}
          />
        </Box>

        <Divider sx={{ my: 2 }} />

        <Box
          sx={{
            display: "flex",
            flexDirection: { xs: "column", md: "row" },
            gap: 4,
          }}
        >
          {/* Customer and Shipping Information */}
          <Box sx={{ flex: { xs: "1 1 100%", md: "0 0 50%" } }}>
            <Typography variant="h6" gutterBottom>
              Customer Information
            </Typography>

            <List dense disablePadding>
              <ListItem disableGutters>
                <ListItemText
                  primary="Name"
                  secondary={order.deliveryInfo.recipientName}
                  primaryTypographyProps={{
                    color: "text.secondary",
                    variant: "body2",
                  }}
                  secondaryTypographyProps={{
                    color: "text.primary",
                    variant: "body1",
                  }}
                />
              </ListItem>

              <ListItem disableGutters>
                <ListItemText
                  primary="Email"
                  secondary={order.deliveryInfo.email}
                  primaryTypographyProps={{
                    color: "text.secondary",
                    variant: "body2",
                  }}
                  secondaryTypographyProps={{
                    color: "text.primary",
                    variant: "body1",
                  }}
                />
              </ListItem>

              <ListItem disableGutters>
                <ListItemText
                  primary="Phone"
                  secondary={order.deliveryInfo.phoneNumber}
                  primaryTypographyProps={{
                    color: "text.secondary",
                    variant: "body2",
                  }}
                  secondaryTypographyProps={{
                    color: "text.primary",
                    variant: "body1",
                  }}
                />
              </ListItem>
            </List>

            <Typography variant="h6" sx={{ mt: 3, mb: 1 }}>
              Shipping Address
            </Typography>

            <List dense disablePadding>
              <ListItem disableGutters>
                <ListItemText
                  primary="Recipient"
                  secondary={order.deliveryInfo.recipientName}
                  primaryTypographyProps={{
                    color: "text.secondary",
                    variant: "body2",
                  }}
                  secondaryTypographyProps={{
                    color: "text.primary",
                    variant: "body1",
                  }}
                />
              </ListItem>

              <ListItem disableGutters>
                <ListItemText
                  primary="Address"
                  secondary={`${order.deliveryInfo.deliveryAddress}, ${order.deliveryInfo.province}`}
                  primaryTypographyProps={{
                    color: "text.secondary",
                    variant: "body2",
                  }}
                  secondaryTypographyProps={{
                    color: "text.primary",
                    variant: "body1",
                  }}
                />
              </ListItem>

              <ListItem disableGutters>
                <ListItemText
                  primary="Phone"
                  secondary={order.deliveryInfo.phoneNumber}
                  primaryTypographyProps={{
                    color: "text.secondary",
                    variant: "body2",
                  }}
                  secondaryTypographyProps={{
                    color: "text.primary",
                    variant: "body1",
                  }}
                />
              </ListItem>

              {/* {order.deliveryInfo.isRushOrder && (
                <ListItem disableGutters>
                  <ListItemText
                    primary="Rush Delivery"
                    secondary={`Requested time: ${
                      order.address.rushDeliveryTime || "Not specified"
                    }`}
                    primaryTypographyProps={{
                      color: "primary",
                      variant: "body2",
                      fontWeight: "bold",
                    }}
                    secondaryTypographyProps={{
                      color: "text.primary",
                      variant: "body1",
                    }}
                  />
                </ListItem>
              )} */}

              {/* {order.address.rushDeliveryInstructions && (
                <ListItem disableGutters>
                  <ListItemText
                    primary="Delivery Instructions"
                    secondary={order.address.rushDeliveryInstructions}
                    primaryTypographyProps={{
                      color: "text.secondary",
                      variant: "body2",
                    }}
                    secondaryTypographyProps={{
                      color: "text.primary",
                      variant: "body1",
                    }}
                  />
                </ListItem>
              )} */}
            </List>
          </Box>

          {/* Payment Information */}
          <Box sx={{ flex: { xs: "1 1 100%", md: "0 0 50%" } }}>
            <Typography variant="h6" gutterBottom>
              Payment Information
            </Typography>

            <List dense disablePadding>
              <ListItem disableGutters>
                <ListItemText
                  primary="Payment Method"
                  secondary={order.transaction.cardType}
                  primaryTypographyProps={{
                    color: "text.secondary",
                    variant: "body2",
                  }}
                  secondaryTypographyProps={{
                    color: "text.primary",
                    variant: "body1",
                  }}
                />
              </ListItem>

              <ListItem disableGutters>
                <ListItemText
                  primary="Transaction ID"
                  secondary={order.transaction.transactionNo}
                  primaryTypographyProps={{
                    color: "text.secondary",
                    variant: "body2",
                  }}
                  secondaryTypographyProps={{
                    color: "text.primary",
                    variant: "body1",
                    fontFamily: "monospace",
                  }}
                />
              </ListItem>

              <ListItem disableGutters>
                <ListItemText
                  primary="Payment Date"
                  secondary={formatDate(order.transaction.payDate)}
                  primaryTypographyProps={{
                    color: "text.secondary",
                    variant: "body2",
                  }}
                  secondaryTypographyProps={{
                    color: "text.primary",
                    variant: "body1",
                  }}
                />
              </ListItem>

              <ListItem disableGutters>
                <ListItemText
                  primary="Payment Status"
                  secondary={order.status}
                  primaryTypographyProps={{
                    color: "text.secondary",
                    variant: "body2",
                  }}
                  secondaryTypographyProps={{
                    color: "success.main",
                    variant: "body1",
                    fontWeight: "bold",
                  }}
                />
              </ListItem>
            </List>

            {isAdmin && (
              <Box sx={{ mt: 3 }}>
                <Typography variant="h6" gutterBottom>
                  Order Notes
                  {!isEditingNotes && (
                    <Button
                      size="small"
                      sx={{ ml: 1 }}
                      onClick={handleEditNotes}
                      className="no-print"
                    >
                      Edit
                    </Button>
                  )}
                </Typography>

                {isEditingNotes ? (
                  <Box sx={{ mb: 2 }}>
                    <TextField
                      fullWidth
                      multiline
                      rows={3}
                      placeholder="Add notes about this order (internal use only)"
                      value={notes}
                      onChange={(e) => setNotes(e.target.value)}
                      disabled={savingNotes}
                    />
                    <Box
                      sx={{
                        display: "flex",
                        justifyContent: "flex-end",
                        mt: 1,
                      }}
                    >
                      <Button
                        size="small"
                        sx={{ mr: 1 }}
                        onClick={() => {
                          setIsEditingNotes(false);
                        }}
                        disabled={savingNotes}
                      >
                        Cancel
                      </Button>
                      <Button
                        size="small"
                        variant="contained"
                        onClick={handleSaveNotes}
                        disabled={savingNotes}
                      >
                        {savingNotes ? <CircularProgress size={20} /> : "Save"}
                      </Button>
                    </Box>
                  </Box>
                ) : (
                  <Typography variant="body1" sx={{ fontStyle: "normal" }}>
                    {"No notes added yet."}
                  </Typography>
                )}
              </Box>
            )}
          </Box>
        </Box>
      </Paper>

      <Paper sx={{ p: 3, mb: 3 }} elevation={2}>
        <Typography variant="h6" gutterBottom>
          Order Items
        </Typography>

        <Box sx={{ overflow: "auto" }}>
          <Table sx={{ minWidth: 650 }}>
            <TableHead>
              <TableRow>
                <TableCell sx={{ width: "50%" }}>Product</TableCell>
                <TableCell align="right">Price</TableCell>
                <TableCell align="center">Quantity</TableCell>
                <TableCell align="right">Subtotal</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {order.invoice.cart.items.map((item) => (
                <TableRow key={item.id}>
                  <TableCell>
                    <Box sx={{ display: "flex", alignItems: "center" }}>
                      {item.product.imageURL && (
                        <Box
                          component="img"
                          src={item.product.imageURL}
                          alt={item.product.title}
                          sx={{
                            width: 40,
                            height: 40,
                            mr: 2,
                            objectFit: "contain",
                          }}
                        />
                      )}
                      <Typography variant="body1">
                        {item.product.title}
                      </Typography>
                    </Box>
                  </TableCell>
                  <TableCell align="right">
                    {formatPrice(item.product.currentPrice)}
                  </TableCell>
                  <TableCell align="center">{item.quantity}</TableCell>
                  <TableCell align="right">
                    {formatPrice(item.product.currentPrice * item.quantity)}
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </Box>

        <Box
          sx={{
            display: "flex",
            flexDirection: "column",
            alignItems: "flex-end",
            mt: 3,
          }}
        >
          <Box
            sx={{
              width: {
                xs: "100%",
                sm: "calc(50% - 16px)",
                md: "calc(33.33% - 16px)",
                lg: "calc(25% - 16px)",
                xl: "calc(20% - 16px)", // Add support for extra large screens
              },
            }}
          >
            <Box
              sx={{ display: "flex", justifyContent: "space-between", mb: 1 }}
            >
              <Typography variant="body1">Subtotal:</Typography>
              <Typography variant="body1">
                {formatPrice(order.invoice.cart.totalProductPriceBeforeVAT)}
              </Typography>
            </Box>

            <Box
              sx={{ display: "flex", justifyContent: "space-between", mb: 1 }}
            >
              <Typography variant="body1">Tax (10%):</Typography>
              <Typography variant="body1">
                {formatPrice(
                  order.invoice.totalProductPriceAfterVAT -
                    order.invoice.cart.totalProductPriceBeforeVAT
                )}
              </Typography>
            </Box>

            <Box
              sx={{ display: "flex", justifyContent: "space-between", mb: 1 }}
            >
              <Typography variant="body1">Delivery Fee:</Typography>
              <Typography variant="body1">
                {formatPrice(order.invoice.deliveryFee)}
              </Typography>
            </Box>

            {/* {order.address.isRushOrder && order.rushDeliveryFee > 0 && (
              <Box
                sx={{ display: "flex", justifyContent: "space-between", mb: 1 }}
              >
                <Typography variant="body1">Rush Delivery Fee:</Typography>
                <Typography variant="body1">
                  {formatPrice(order.rushDeliveryFee)}
                </Typography>
              </Box>
            )} */}

            <Divider sx={{ my: 1 }} />

            <Box sx={{ display: "flex", justifyContent: "space-between" }}>
              <Typography variant="h6">Total:</Typography>
              <Typography variant="h6">
                {formatPrice(order.invoice.totalAmount)}
              </Typography>
            </Box>
          </Box>
        </Box>
      </Paper>

      <Box sx={{ textAlign: "center", mt: 4 }} className="print-only">
        <Typography variant="body2" color="text.secondary">
          Thank you for your order!
        </Typography>
        <Typography variant="body2" color="text.secondary">
          If you have any questions, please contact our customer service at
          support@aims.com
        </Typography>
      </Box>

      {/* Status Update Dialog (Admin Only) */}
      {isAdmin && (
        <Dialog
          open={openStatusDialog}
          onClose={() => setOpenStatusDialog(false)}
        >
          <DialogTitle>Update Order Status</DialogTitle>
          <DialogContent>
            <Typography variant="body1" gutterBottom>
              Current status:{" "}
              <Chip
                label={getStatusLabel(order.status)}
                color={getStatusColor(order.status) as any}
                size="small"
                sx={{ ml: 1 }}
              />
            </Typography>

            <FormControl fullWidth sx={{ mt: 2 }}>
              <InputLabel>New Status</InputLabel>
              <Select
                value={newStatus}
                onChange={(e) => setNewStatus(e.target.value as OrderStatus)}
                label="New Status"
              >
                {[
                  order.status,
                  ...getAvailableStatusTransitions(order.status),
                ].map((status) => (
                  <MenuItem key={status} value={status}>
                    {getStatusLabel(status)}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>
          </DialogContent>
          <DialogActions>
            <Button
              onClick={() => setOpenStatusDialog(false)}
              disabled={statusUpdateLoading}
            >
              Cancel
            </Button>
            <Button
              onClick={handleStatusUpdateConfirm}
              color="primary"
              disabled={statusUpdateLoading || newStatus === order.status}
              variant="contained"
            >
              {statusUpdateLoading ? (
                <CircularProgress size={24} />
              ) : (
                "Update Status"
              )}
            </Button>
          </DialogActions>
        </Dialog>
      )}

      {/* Print Styles - Add to your CSS */}
      <style>
        {`
          @media print {
            .no-print {
              display: none !important;
            }

            .print-only {
              display: block !important;
            }

            .print-container {
              padding: 0 !important;
              max-width: 100% !important;
            }

            body {
              background: white !important;
            }
          }

          @media screen {
            .print-only {
              display: none !important;
            }
          }
        `}
      </style>
    </Container>
  );
};

export default ViewOrderPage;
