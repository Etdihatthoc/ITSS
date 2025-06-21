// src/pages/admin/OrderManagementPage.tsx
import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import {
  Container,
  Typography,
  Box,
  Paper,
  Button,
  TextField,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  IconButton,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  CircularProgress,
  Alert,
  Chip,
  Pagination,
  InputAdornment,
} from "@mui/material";
import {
  Search as SearchIcon,
  Visibility as VisibilityIcon,
  Edit as EditIcon,
  Check as CheckIcon,
  Cancel as CancelIcon,
  LocalShipping as ShippingIcon,
} from "@mui/icons-material";
import { Order, OrderStatus } from "../../types/order";
import orderService from "../../services/orderService";

const OrderManagementPage: React.FC = () => {
  const navigate = useNavigate();

  // State for orders
  const [orders, setOrders] = useState<Order[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  // State for pagination
  const [page, setPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [searchTerm, setSearchTerm] = useState("");
  const [statusFilter, setStatusFilter] = useState<OrderStatus | "">("");

  // State for status update dialog
  const [openStatusDialog, setOpenStatusDialog] = useState(false);
  const [selectedOrder, setSelectedOrder] = useState<Order | null>(null);
  const [newStatus, setNewStatus] = useState<OrderStatus>(OrderStatus.PENDING);
  const [statusUpdateLoading, setStatusUpdateLoading] = useState(false);

  // Load orders on component mount and when page, search, or status filter changes
  useEffect(() => {
    fetchOrders();
  }, [page, searchTerm, statusFilter]);

  // Fetch orders from API
  const fetchOrders = async () => {
    try {
      setLoading(true);
      setError("");

      const response = await orderService.getOrders({
        page,
        limit: 10,
        search: searchTerm,
        status: statusFilter || undefined,
      });

      setOrders(response.data.data);
      setTotalPages(Math.ceil(response.data.total / 10));
    } catch (err: any) {
      console.error("Failed to fetch orders:", err);
      setError(err.response?.data?.message || "Failed to load orders");
    } finally {
      setLoading(false);
    }
  };

  // Handle page change
  const handlePageChange = (_: React.ChangeEvent<unknown>, value: number) => {
    setPage(value);
  };

  // Handle search
  const handleSearch = () => {
    setPage(1);
    fetchOrders();
  };

  // Handle status filter change
  const handleStatusFilterChange = (
    event: React.ChangeEvent<HTMLInputElement> | { target: { value: unknown } }
  ) => {
    setStatusFilter(event.target.value as OrderStatus | "");
    setPage(1);
  };

  // Open dialog to update order status
  const handleStatusUpdateClick = (order: Order) => {
    setSelectedOrder(order);
    setNewStatus(order.status);
    setOpenStatusDialog(true);
  };

  // Update order status
  const handleStatusUpdateConfirm = async () => {
    if (!selectedOrder) return;

    try {
      setStatusUpdateLoading(true);
      await orderService.updateOrderStatus(selectedOrder.id, newStatus);

      // Refresh order list
      fetchOrders();

      // Close dialog
      setOpenStatusDialog(false);
      setSelectedOrder(null);
    } catch (err: any) {
      console.error("Failed to update order status:", err);
      setError(err.response?.data?.message || "Failed to update order status");
    } finally {
      setStatusUpdateLoading(false);
    }
  };

  // View order details
  const handleViewOrder = (orderId: string) => {
    navigate(`/order/${orderId}`);
  };

  // Approve order
  const handleApproveOrder = async (order: Order) => {
    try {
      await orderService.updateOrderStatus(order.id, OrderStatus.APPROVED);
      fetchOrders();
    } catch (err: any) {
      console.error("Failed to approve order:", err);
      setError(err.response?.data?.message || "Failed to approve order");
    }
  };

  // Reject order
  const handleRejectOrder = async (order: Order) => {
    try {
      await orderService.updateOrderStatus(order.id, OrderStatus.REJECTED);
      fetchOrders();
    } catch (err: any) {
      console.error("Failed to reject order:", err);
      setError(err.response?.data?.message || "Failed to reject order");
    }
  };

  // Ship order
  const handleShipOrder = async (order: Order) => {
    try {
      await orderService.updateOrderStatus(order.id, OrderStatus.SHIPPED);
      fetchOrders();
    } catch (err: any) {
      console.error("Failed to mark order as shipped:", err);
      setError(
        err.response?.data?.message || "Failed to mark order as shipped"
      );
    }
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
      month: "short",
      day: "numeric",
      hour: "2-digit",
      minute: "2-digit",
    });
  };

  // Get status label
  const getStatusLabel = (status: OrderStatus) => {
    const labels = {
      [OrderStatus.PENDING]: "Pending",
      [OrderStatus.APPROVED]: "Approved",
      [OrderStatus.REJECTED]: "Rejected",
      [OrderStatus.SHIPPED]: "Shipped",
      [OrderStatus.DELIVERED]: "Delivered",
      [OrderStatus.CANCELLED]: "Cancelled",
    };
    return labels[status] || status;
  };

  // Get status color
  const getStatusColor = (status: OrderStatus) => {
    const colors = {
      [OrderStatus.PENDING]: "warning",
      [OrderStatus.APPROVED]: "info",
      [OrderStatus.REJECTED]: "error",
      [OrderStatus.SHIPPED]: "primary",
      [OrderStatus.DELIVERED]: "success",
      [OrderStatus.CANCELLED]: "error",
    };
    return colors[status] || "default";
  };

  // Get available status transitions based on current status
  const getAvailableStatusTransitions = (currentStatus: OrderStatus) => {
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

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Box
        sx={{
          display: "flex",
          justifyContent: "space-between",
          alignItems: "center",
          mb: 3,
        }}
      >
        <Typography variant="h4">Order Management</Typography>
      </Box>

      {error && (
        <Alert severity="error" sx={{ mb: 3 }}>
          {error}
        </Alert>
      )}

      <Paper sx={{ p: 2, mb: 3 }}>
        <Box
          sx={{
            display: "flex",
            flexDirection: { xs: "column", md: "row" },
            gap: 2,
            mb: 2,
          }}
        >
          <TextField
            placeholder="Search orders..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            onKeyPress={(e) => e.key === "Enter" && handleSearch()}
            sx={{ flexGrow: 1 }}
            InputProps={{
              startAdornment: (
                <InputAdornment position="start">
                  <SearchIcon />
                </InputAdornment>
              ),
            }}
          />

          <FormControl sx={{ minWidth: 180 }}>
            <InputLabel>Filter by Status</InputLabel>
            <Select
              value={statusFilter}
              onChange={handleStatusFilterChange}
              label="Filter by Status"
            >
              <MenuItem value="">All Statuses</MenuItem>
              {Object.values(OrderStatus).map((status) => (
                <MenuItem key={status} value={status}>
                  {getStatusLabel(status)}
                </MenuItem>
              ))}
            </Select>
          </FormControl>

          <Button variant="outlined" onClick={handleSearch}>
            Search
          </Button>
        </Box>

        {loading ? (
          <Box sx={{ display: "flex", justifyContent: "center", py: 4 }}>
            <CircularProgress />
          </Box>
        ) : (
          <>
            {orders.length === 0 ? (
              <Box sx={{ py: 4, textAlign: "center" }}>
                <Typography variant="body1" color="text.secondary">
                  No orders found.
                </Typography>
              </Box>
            ) : (
              <TableContainer>
                <Table>
                  <TableHead>
                    <TableRow>
                      <TableCell>Order ID</TableCell>
                      <TableCell>Customer</TableCell>
                      <TableCell>Date</TableCell>
                      <TableCell>Status</TableCell>
                      <TableCell>Items</TableCell>
                      <TableCell>Total</TableCell>
                      <TableCell align="right">Actions</TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {orders.map((order) => (
                      <TableRow key={order.id}>
                        <TableCell>
                          <Typography variant="body2" fontFamily="monospace">
                            {order.id.substring(0, 8)}...
                          </Typography>
                        </TableCell>
                        <TableCell>{order.customer.name}</TableCell>
                        <TableCell>{formatDate(order.createdAt)}</TableCell>
                        <TableCell>
                          <Chip
                            label={getStatusLabel(order.status)}
                            color={getStatusColor(order.status) as any}
                            size="small"
                          />
                        </TableCell>
                        <TableCell>{order.items.length}</TableCell>
                        <TableCell>{formatPrice(order.total)}</TableCell>
                        <TableCell align="right">
                          <IconButton
                            color="primary"
                            onClick={() => handleViewOrder(order.id)}
                            title="View Order Details"
                          >
                            <VisibilityIcon />
                          </IconButton>

                          <IconButton
                            color="primary"
                            onClick={() => handleStatusUpdateClick(order)}
                            title="Update Status"
                          >
                            <EditIcon />
                          </IconButton>

                          {order.status === OrderStatus.PENDING && (
                            <>
                              <IconButton
                                color="success"
                                onClick={() => handleApproveOrder(order)}
                                title="Approve Order"
                              >
                                <CheckIcon />
                              </IconButton>
                              <IconButton
                                color="error"
                                onClick={() => handleRejectOrder(order)}
                                title="Reject Order"
                              >
                                <CancelIcon />
                              </IconButton>
                            </>
                          )}

                          {order.status === OrderStatus.APPROVED && (
                            <IconButton
                              color="primary"
                              onClick={() => handleShipOrder(order)}
                              title="Mark as Shipped"
                            >
                              <ShippingIcon />
                            </IconButton>
                          )}
                        </TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </TableContainer>
            )}

            <Box sx={{ display: "flex", justifyContent: "center", mt: 3 }}>
              <Pagination
                count={totalPages}
                page={page}
                onChange={handlePageChange}
                color="primary"
              />
            </Box>
          </>
        )}
      </Paper>

      {/* Status Update Dialog */}
      <Dialog
        open={openStatusDialog}
        onClose={() => setOpenStatusDialog(false)}
      >
        <DialogTitle>Update Order Status</DialogTitle>
        <DialogContent>
          <DialogContentText sx={{ mb: 2 }}>
            Change the status for order ID: {selectedOrder?.id}
          </DialogContentText>

          <FormControl fullWidth sx={{ mt: 1 }}>
            <InputLabel>New Status</InputLabel>
            <Select
              value={newStatus}
              onChange={(e) => setNewStatus(e.target.value as OrderStatus)}
              label="New Status"
            >
              {selectedOrder &&
                [
                  selectedOrder.status,
                  ...getAvailableStatusTransitions(selectedOrder.status),
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
            disabled={
              statusUpdateLoading ||
              (selectedOrder ? newStatus === selectedOrder.status : false)
            }
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
    </Container>
  );
};

export default OrderManagementPage;
