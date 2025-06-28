import React, { useState, useEffect } from "react";
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
  Chip,
  Pagination,
  CircularProgress,
  Alert,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  IconButton,
  Tooltip,
} from "@mui/material";
import {
  Search as SearchIcon,
  FilterList as FilterIcon,
  Visibility as ViewIcon,
  CheckCircle as ApproveIcon,
  Cancel as RejectIcon,
} from "@mui/icons-material";
import { useNavigate } from "react-router-dom";
import { Order, OrderStatus } from "../../types/order";
import orderService from "../../services/orderService";
import productService from "../../services/productService";

const OrderManagementPage: React.FC = () => {
  const navigate = useNavigate();

  // State for orders list
  const [orders, setOrders] = useState<Order[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  // State for pagination
  const [page, setPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const ITEMS_PER_PAGE = 30; // Show 30 orders per page

  // State for filtering and search
  const [searchTerm, setSearchTerm] = useState("");
  const [statusFilter, setStatusFilter] = useState<string>("PENDING"); // Default to PENDING
  const [filterVisible, setFilterVisible] = useState(false);

  // State for order approval/rejection
  const [selectedOrder, setSelectedOrder] = useState<Order | null>(null);
  const [openRejectDialog, setOpenRejectDialog] = useState(false);
  const [rejectionReason, setRejectionReason] = useState("");
  const [processingAction, setProcessingAction] = useState(false);
  const [stockCheckResult, setStockCheckResult] = useState<{
    isValid: boolean;
    message: string;
    insufficientItems?: {
      productId: number;
      title: string;
      available: number;
      required: number;
    }[];
  } | null>(null);

  useEffect(() => {
    fetchOrders();
  }, [page, statusFilter]);

  const fetchOrders = async () => {
    try {
      setLoading(true);
      setError("");

      const response = await orderService.getOrders({
        page,
        limit: ITEMS_PER_PAGE,
        search: searchTerm,
        status: statusFilter ? (statusFilter as OrderStatus) : undefined,
      });

      // Handle different response formats
      if (Array.isArray(response.data)) {
        setOrders(response.data);
        setTotalPages(Math.ceil(response.data.length / ITEMS_PER_PAGE));
      } else if (response.data && response.data.data) {
        setOrders(response.data.data);
        setTotalPages(Math.ceil(response.data.total / ITEMS_PER_PAGE));
      } else {
        setOrders([]);
        setTotalPages(1);
      }
    } catch (err: any) {
      console.error("Failed to fetch orders:", err);
      setError(err.response?.data?.message || "Failed to load orders");
      setOrders([]);
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = () => {
    setPage(1); // Reset to first page when searching
    fetchOrders();
  };

  const handlePageChange = (
    event: React.ChangeEvent<unknown>,
    value: number
  ) => {
    setPage(value);
  };

  const handleStatusFilterChange = (
    event: React.ChangeEvent<HTMLInputElement> | { target: { value: string } }
  ) => {
    setStatusFilter(event.target.value as string);
    setPage(1); // Reset to first page when changing filters
  };

  const handleViewOrder = (orderId: number) => {
    navigate(`/order/${orderId}`);
  };

  // Check if all products in the order are in stock
  const checkStock = async (order: Order) => {
    try {
      setProcessingAction(true);

      // Get current stock levels for all products in the order
      const stockCheckPromises = order.invoice.cart.items.map(async (item) => {
        const response = await productService.getProductById(
          item.product.id.toString()
        );
        const product = response.data;
        return {
          productId: item.product.id,
          title: item.product.title,
          available: product.quantity,
          required: item.quantity,
          isAvailable: product.quantity >= item.quantity,
        };
      });

      const stockCheckResults = await Promise.all(stockCheckPromises);
      const insufficientItems = stockCheckResults.filter(
        (item) => !item.isAvailable
      );

      console.log("Stock check results:", stockCheckResults);

      const isValid = insufficientItems.length === 0;

      setStockCheckResult({
        isValid,
        message: isValid
          ? "All items are in stock and available for this order."
          : "Some items in this order are out of stock.",
        insufficientItems: insufficientItems.map((item) => ({
          productId: item.productId,
          title: item.title,
          available: item.available,
          required: item.required,
        })),
      });

      return isValid;
    } catch (error) {
      console.error("Error checking stock:", error);
      setStockCheckResult({
        isValid: false,
        message: "Error checking stock availability.",
      });
      return false;
    } finally {
      setProcessingAction(false);
    }
  };

  // Handle approving an order
  const handleApproveOrder = async (order: Order) => {
    setSelectedOrder(order);

    // Check stock before approving
    const stockAvailable = await checkStock(order);

    if (stockAvailable) {
      try {
        setProcessingAction(true);
        await orderService.updateOrderStatus(order.id, "APPROVED");

        // Record the operation for audit
        // await orderService.addOrderNote(
        //   order.id,
        //   "Order approved by product manager."
        // );

        // Update product stock quantities
        for (const item of order.invoice.cart.items) {
          await productService.updateStock(
            item.product.id,
            item.quantity,
            "decrease"
          );
        }

        const autoRejectResponse =
          await orderService.autoRejectInsufficientStockOrders();

        setStockCheckResult(null);
        fetchOrders(); // Refresh the list
      } catch (err: any) {
        console.error("Failed to approve order:", err);
        setError(err.response?.data?.message || "Failed to approve order");
      } finally {
        setProcessingAction(false);
      }
    }
  };

  // Open reject dialog
  const handleOpenRejectDialog = (order: Order) => {
    setSelectedOrder(order);
    setRejectionReason("");
    setOpenRejectDialog(true);
  };

  // Handle rejecting an order
  const handleRejectOrder = async () => {
    if (!selectedOrder) return;

    try {
      setProcessingAction(true);
      await orderService.updateOrderStatus(selectedOrder.id, "REJECTED");

      // Save rejection reason
      // await orderService.addOrderNote(
      //   selectedOrder.id,
      //   `Order rejected by product manager. Reason: ${rejectionReason}`
      // );

      setOpenRejectDialog(false);
      setRejectionReason("");
      setSelectedOrder(null);
      fetchOrders(); // Refresh the list
    } catch (err: any) {
      console.error("Failed to reject order:", err);
      setError(err.response?.data?.message || "Failed to reject order");
    } finally {
      setProcessingAction(false);
    }
  };

  // Get status label
  const getStatusLabel = (status: string) => {
    switch (status) {
      case "PENDING":
        return "Pending";
      case "APPROVED":
        return "Approved";
      case "SHIPPED":
        return "Shipped";
      case "DELIVERED":
        return "Delivered";
      case "REJECTED":
        return "Rejected";
      case "CANCELLED":
        return "Cancelled";
      default:
        return status;
    }
  };

  // Get status color
  const getStatusColor = (status: string) => {
    switch (status) {
      case "PENDING":
        return "warning";
      case "APPROVED":
        return "success";
      case "SHIPPED":
        return "primary";
      case "DELIVERED":
        return "success";
      case "REJECTED":
      case "CANCELLED":
        return "error";
      default:
        return "default";
    }
  };

  // Format date
  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleString();
  };

  // Format price
  const formatPrice = (price: number) => {
    return new Intl.NumberFormat("vi-VN", {
      style: "currency",
      currency: "VND",
    }).format(price);
  };

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Typography variant="h4" gutterBottom>
        Order Management
      </Typography>

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
            placeholder="Search orders by ID, customer name, or email..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            onKeyPress={(e) => e.key === "Enter" && handleSearch()}
            sx={{ flexGrow: 1 }}
            InputProps={{
              startAdornment: (
                <SearchIcon sx={{ color: "action.active", mr: 1 }} />
              ),
            }}
          />

          <Button
            startIcon={<FilterIcon />}
            onClick={() => setFilterVisible(!filterVisible)}
            variant="outlined"
          >
            Filters
          </Button>

          <Button variant="outlined" onClick={handleSearch}>
            Search
          </Button>
        </Box>

        {filterVisible && (
          <Box sx={{ mt: 2, mb: 2 }}>
            <FormControl variant="outlined" sx={{ minWidth: 200 }}>
              <InputLabel>Status</InputLabel>
              <Select
                value={statusFilter}
                onChange={handleStatusFilterChange}
                label="Status"
              >
                <MenuItem value="">All Statuses</MenuItem>
                <MenuItem value="PENDING">Pending</MenuItem>
                <MenuItem value="APPROVED">Approved</MenuItem>
                <MenuItem value="SHIPPED">Shipped</MenuItem>
                <MenuItem value="DELIVERED">Delivered</MenuItem>
                <MenuItem value="REJECTED">Rejected</MenuItem>
                <MenuItem value="CANCELLED">Cancelled</MenuItem>
              </Select>
            </FormControl>
          </Box>
        )}
      </Paper>

      {loading ? (
        <Box sx={{ display: "flex", justifyContent: "center", py: 4 }}>
          <CircularProgress />
        </Box>
      ) : (
        <>
          {!orders || orders.length === 0 ? (
            <Box sx={{ py: 4, textAlign: "center" }}>
              <Typography variant="body1" color="text.secondary">
                No orders found matching your criteria.
              </Typography>
            </Box>
          ) : (
            <TableContainer component={Paper}>
              <Table>
                <TableHead>
                  <TableRow>
                    <TableCell>ID</TableCell>
                    <TableCell>Date</TableCell>
                    <TableCell>Customer</TableCell>
                    <TableCell align="right">Total</TableCell>
                    <TableCell>Status</TableCell>
                    <TableCell align="center">Actions</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {orders.map((order) => (
                    <TableRow key={order.id}>
                      <TableCell>{order.id}</TableCell>
                      <TableCell>
                        {formatDate(order.transaction.payDate)}
                      </TableCell>
                      <TableCell>
                        {order.deliveryInfo.recipientName}
                        <Typography variant="body2" color="text.secondary">
                          {order.deliveryInfo.email}
                        </Typography>
                      </TableCell>
                      <TableCell align="right">
                        {formatPrice(order.invoice.totalAmount)}
                      </TableCell>
                      <TableCell>
                        <Chip
                          label={getStatusLabel(order.status)}
                          color={getStatusColor(order.status) as any}
                          size="small"
                        />
                      </TableCell>
                      <TableCell align="center">
                        <Box sx={{ display: "flex", justifyContent: "center" }}>
                          <Tooltip title="View Order Details">
                            <IconButton
                              size="small"
                              onClick={() => handleViewOrder(order.id)}
                            >
                              <ViewIcon />
                            </IconButton>
                          </Tooltip>

                          {order.status === "PENDING" && (
                            <>
                              <Tooltip title="Approve Order">
                                <IconButton
                                  size="small"
                                  color="success"
                                  onClick={() => handleApproveOrder(order)}
                                  disabled={processingAction}
                                >
                                  <ApproveIcon />
                                </IconButton>
                              </Tooltip>

                              <Tooltip title="Reject Order">
                                <IconButton
                                  size="small"
                                  color="error"
                                  onClick={() => handleOpenRejectDialog(order)}
                                  disabled={processingAction}
                                >
                                  <RejectIcon />
                                </IconButton>
                              </Tooltip>
                            </>
                          )}
                        </Box>
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </TableContainer>
          )}

          {totalPages > 1 && (
            <Box sx={{ display: "flex", justifyContent: "center", mt: 3 }}>
              <Pagination
                count={totalPages}
                page={page}
                onChange={handlePageChange}
                color="primary"
              />
            </Box>
          )}
        </>
      )}

      {/* Stock check result alert */}
      <Dialog
        open={!!stockCheckResult}
        onClose={() => setStockCheckResult(null)}
      >
        <DialogTitle>
          {stockCheckResult?.isValid
            ? "Stock Check Passed"
            : "Stock Check Failed"}
        </DialogTitle>
        <DialogContent>
          <Typography variant="body1" gutterBottom>
            {stockCheckResult?.message}
          </Typography>

          {stockCheckResult?.insufficientItems &&
            stockCheckResult.insufficientItems.length > 0 && (
              <>
                <Typography variant="subtitle1" sx={{ mt: 2, mb: 1 }}>
                  Out of stock items:
                </Typography>
                <TableContainer component={Paper} variant="outlined">
                  <Table size="small">
                    <TableHead>
                      <TableRow>
                        <TableCell>Product</TableCell>
                        <TableCell align="right">Required</TableCell>
                        <TableCell align="right">In Stock</TableCell>
                        <TableCell align="right">Shortage</TableCell>
                      </TableRow>
                    </TableHead>
                    <TableBody>
                      {stockCheckResult.insufficientItems.map((item) => (
                        <TableRow key={item.productId}>
                          <TableCell>{item.title}</TableCell>
                          <TableCell align="right">{item.required}</TableCell>
                          <TableCell align="right">{item.available}</TableCell>
                          <TableCell align="right" sx={{ color: "error.main" }}>
                            {item.required - item.available}
                          </TableCell>
                        </TableRow>
                      ))}
                    </TableBody>
                  </Table>
                </TableContainer>
              </>
            )}
        </DialogContent>
        <DialogActions>
          {stockCheckResult?.isValid ? (
            <>
              <Button onClick={() => setStockCheckResult(null)}>Cancel</Button>
              <Button
                onClick={() => {
                  setStockCheckResult(null);
                  if (selectedOrder) {
                    handleApproveOrder(selectedOrder);
                  }
                }}
                variant="contained"
                color="success"
              >
                Confirm Approval
              </Button>
            </>
          ) : (
            <>
              <Button onClick={() => setStockCheckResult(null)}>Close</Button>
              <Button
                onClick={() => {
                  setStockCheckResult(null);
                  if (selectedOrder) {
                    handleOpenRejectDialog(selectedOrder);
                  }
                }}
                variant="contained"
                color="error"
              >
                Reject Order
              </Button>
            </>
          )}
        </DialogActions>
      </Dialog>

      {/* Rejection dialog */}
      <Dialog
        open={openRejectDialog}
        onClose={() => setOpenRejectDialog(false)}
      >
        <DialogTitle>Reject Order</DialogTitle>
        <DialogContent>
          <Typography variant="body1" gutterBottom>
            Please provide a reason for rejecting this order:
          </Typography>
          <TextField
            autoFocus
            margin="dense"
            id="rejection-reason"
            label="Rejection Reason"
            fullWidth
            multiline
            rows={4}
            value={rejectionReason}
            onChange={(e) => setRejectionReason(e.target.value)}
            placeholder="e.g., Item out of stock, Unable to deliver to this location, etc."
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpenRejectDialog(false)}>Cancel</Button>
          <Button
            onClick={handleRejectOrder}
            variant="contained"
            color="error"
            disabled={!rejectionReason.trim() || processingAction}
          >
            {processingAction ? <CircularProgress size={24} /> : "Reject Order"}
          </Button>
        </DialogActions>
      </Dialog>
    </Container>
  );
};

export default OrderManagementPage;
