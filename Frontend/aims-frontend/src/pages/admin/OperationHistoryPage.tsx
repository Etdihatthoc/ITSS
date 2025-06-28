// src/pages/admin/OperationHistoryPage.tsx
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
  IconButton,
  Dialog,
  DialogActions,
  DialogContent,
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
  Collapse,
} from "@mui/material";
import {
  Search as SearchIcon,
  FilterList as FilterIcon,
  Visibility as VisibilityIcon,
  KeyboardArrowDown as ExpandMoreIcon,
  KeyboardArrowUp as ExpandLessIcon,
} from "@mui/icons-material";
import productService from "../../services/productService";
import { OperationHistory, OperationType } from "../../types/operationHistory";

const OperationHistoryPage: React.FC = () => {
  // State for operation history
  const [operations, setOperations] = useState<OperationHistory[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  // State for pagination
  const [page, setPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);

  // State for filtering
  const [searchTerm, setSearchTerm] = useState("");
  const [operationType, setOperationType] = useState<OperationType | "">("");
  const [startDate, setStartDate] = useState<string>("");
  const [endDate, setEndDate] = useState<string>("");
  const [showFilters, setShowFilters] = useState(false);

  // State for details dialog
  const [selectedOperation, setSelectedOperation] =
    useState<OperationHistory | null>(null);
  const [openDetailsDialog, setOpenDetailsDialog] = useState(false);

  // State for expanded rows
  const [expandedRows, setExpandedRows] = useState<Record<string, boolean>>({});

  // Load operations on component mount and when page or filters change
  useEffect(() => {
    fetchOperationHistory();
  }, [page]);

  // Fetch operation history from API
  const fetchOperationHistory = async () => {
    try {
      setLoading(true);
      setError("");

      const params = {
        page: page - 1, // Backend expects 0-based pagination
        limit: 10,
        search: searchTerm,
        operationType: operationType || undefined,
        startDate: startDate || undefined,
        endDate: endDate || undefined,
      };

      console.log("Fetching operations with params:", params);

      const response = await productService.getProductOperationHistory(params);

      console.log("Raw backend response:", response.data);

      // Transform backend data to frontend format
      const transformedOperations: OperationHistory[] = response.data.data.map(
        (backendOp: any) => ({
          id: backendOp.operationID.toString(),
          operationType: backendOp.operationType as OperationType, // Now matches the enum
          productId: backendOp.product?.id?.toString() || "N/A",
          productName: backendOp.product?.title || "Unknown Product",
          userId: "system", // Default value since backend doesn't have this
          userName: "System User", // Default value since backend doesn't have this
          timestamp: backendOp.timestamp,
          changes: {}, // Could be enhanced later with actual change tracking
          notes: `Operation on ${
            backendOp.product?.category || "Unknown"
          } category product`,
        })
      );

      console.log("Transformed operations:", transformedOperations);

      setOperations(transformedOperations);
      setTotalPages(
        response.data.totalPages || Math.ceil(response.data.total / 10)
      );
    } catch (err: any) {
      console.error("Failed to fetch operation history:", err);
      setError(
        err.response?.data?.message || "Failed to load operation history"
      );
    } finally {
      setLoading(false);
    }
  };
  // Handle page change
  const handlePageChange = (_: React.ChangeEvent<unknown>, value: number) => {
    setPage(value);
  };

  // Handle search and filter
  const handleSearch = () => {
    setPage(1);
    fetchOperationHistory();
  };

  // Reset filters
  const handleResetFilters = () => {
    setSearchTerm("");
    setOperationType("");
    setStartDate("");
    setEndDate("");
    setPage(1);
    fetchOperationHistory();
  };

  // View operation details
  const handleViewDetails = (operation: OperationHistory) => {
    setSelectedOperation(operation);
    setOpenDetailsDialog(true);
  };

  // Toggle row expansion
  const toggleRowExpansion = (operationId: string) => {
    setExpandedRows((prev) => ({
      ...prev,
      [operationId]: !prev[operationId],
    }));
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

  // Get operation type label
  const getOperationTypeLabel = (type: OperationType) => {
    const labels = {
      [OperationType.ADD_PRODUCT]: "Add Product",
      [OperationType.UPDATE_PRODUCT]: "Update Product",
      [OperationType.DELETE_PRODUCT]: "Delete Product",
    };
    return labels[type] || type;
  };

  // Get operation type color
  const getOperationTypeColor = (type: OperationType) => {
    const colors = {
      [OperationType.ADD_PRODUCT]: "success",
      [OperationType.UPDATE_PRODUCT]: "info",
      [OperationType.DELETE_PRODUCT]: "error",
    };
    return colors[type] || "default";
  };

  // Render changes in a readable format
  const renderChanges = (
    changes: Record<string, { before: any; after: any }>
  ) => {
    return Object.entries(changes).map(([field, { before, after }]) => (
      <Box key={field} sx={{ mb: 1 }}>
        <Typography variant="subtitle2" component="span">
          {field.charAt(0).toUpperCase() + field.slice(1)}:
        </Typography>
        <Box sx={{ display: "flex", ml: 2 }}>
          <Typography
            variant="body2"
            color="text.secondary"
            sx={{ mr: 2, minWidth: 100 }}
          >
            Before:{" "}
            <span style={{ fontFamily: "monospace" }}>
              {before !== undefined ? JSON.stringify(before) : "N/A"}
            </span>
          </Typography>
          <Typography variant="body2" color="primary">
            After:{" "}
            <span style={{ fontFamily: "monospace" }}>
              {after !== undefined ? JSON.stringify(after) : "N/A"}
            </span>
          </Typography>
        </Box>
      </Box>
    ));
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
        <Typography variant="h4">Operation History</Typography>
        <Button
          variant="outlined"
          startIcon={<FilterIcon />}
          onClick={() => setShowFilters(!showFilters)}
        >
          {showFilters ? "Hide Filters" : "Show Filters"}
        </Button>
      </Box>

      {error && (
        <Alert severity="error" sx={{ mb: 3 }}>
          {error}
        </Alert>
      )}

      <Paper sx={{ p: 2, mb: 3 }}>
        <Box sx={{ mb: 2 }}>
          <TextField
            placeholder="Search by product name or user..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            onKeyPress={(e) => e.key === "Enter" && handleSearch()}
            sx={{ width: { xs: "100%", md: "50%" } }}
            InputProps={{
              startAdornment: (
                <InputAdornment position="start">
                  <SearchIcon />
                </InputAdornment>
              ),
            }}
          />
        </Box>

        <Collapse in={showFilters}>
          <Box sx={{ display: "flex", flexWrap: "wrap", gap: 2, mb: 2 }}>
            <FormControl sx={{ minWidth: 200 }}>
              <InputLabel>Operation Type</InputLabel>
              <Select
                value={operationType}
                onChange={(e) =>
                  setOperationType(e.target.value as OperationType | "")
                }
                label="Operation Type"
              >
                <MenuItem value="">All</MenuItem>
                <MenuItem value={OperationType.ADD_PRODUCT}>
                  Add Product
                </MenuItem>
                <MenuItem value={OperationType.UPDATE_PRODUCT}>
                  Update Product
                </MenuItem>
                <MenuItem value={OperationType.DELETE_PRODUCT}>
                  Delete Product
                </MenuItem>
              </Select>
            </FormControl>

            {/* Simple date inputs instead of DatePicker */}
            <TextField
              label="Start Date"
              type="date"
              value={startDate}
              onChange={(e) => setStartDate(e.target.value)}
              InputLabelProps={{ shrink: true }}
              sx={{ minWidth: 180 }}
            />

            <TextField
              label="End Date"
              type="date"
              value={endDate}
              onChange={(e) => setEndDate(e.target.value)}
              InputLabelProps={{ shrink: true }}
              sx={{ minWidth: 180 }}
            />

            <Box sx={{ display: "flex", gap: 1, alignItems: "flex-end" }}>
              <Button variant="contained" onClick={handleSearch}>
                Apply Filters
              </Button>
              <Button variant="outlined" onClick={handleResetFilters}>
                Reset
              </Button>
            </Box>
          </Box>
        </Collapse>

        {loading ? (
          <Box sx={{ display: "flex", justifyContent: "center", py: 4 }}>
            <CircularProgress />
          </Box>
        ) : (
          <>
            {operations.length === 0 ? (
              <Box sx={{ py: 4, textAlign: "center" }}>
                <Typography variant="body1" color="text.secondary">
                  No operation history found.
                </Typography>
              </Box>
            ) : (
              <TableContainer>
                <Table>
                  <TableHead>
                    <TableRow>
                      <TableCell />
                      <TableCell>Date</TableCell>
                      <TableCell>Operation Type</TableCell>
                      <TableCell>Product</TableCell>
                      <TableCell>User</TableCell>
                      <TableCell align="right">Actions</TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {operations.map((operation) => (
                      <React.Fragment key={operation.id}>
                        <TableRow>
                          <TableCell>
                            <IconButton
                              size="small"
                              onClick={() => toggleRowExpansion(operation.id)}
                            >
                              {expandedRows[operation.id] ? (
                                <ExpandLessIcon />
                              ) : (
                                <ExpandMoreIcon />
                              )}
                            </IconButton>
                          </TableCell>
                          <TableCell>
                            {formatDate(operation.timestamp)}
                          </TableCell>
                          <TableCell>
                            <Chip
                              label={getOperationTypeLabel(
                                operation.operationType
                              )}
                              color={
                                getOperationTypeColor(
                                  operation.operationType
                                ) as any
                              }
                              size="small"
                            />
                          </TableCell>
                          <TableCell>{operation.productName}</TableCell>
                          <TableCell>{operation.userName}</TableCell>
                          <TableCell align="right">
                            <IconButton
                              color="primary"
                              onClick={() => handleViewDetails(operation)}
                              title="View Details"
                            >
                              <VisibilityIcon />
                            </IconButton>
                          </TableCell>
                        </TableRow>

                        {expandedRows[operation.id] && (
                          <TableRow>
                            <TableCell colSpan={6} sx={{ py: 0 }}>
                              <Box
                                sx={{
                                  p: 2,
                                  backgroundColor: "rgba(0, 0, 0, 0.02)",
                                }}
                              >
                                <Typography variant="subtitle1" gutterBottom>
                                  Changes
                                </Typography>
                                {operation.changes &&
                                  renderChanges(operation.changes)}

                                {operation.notes && (
                                  <Box sx={{ mt: 2 }}>
                                    <Typography variant="subtitle2">
                                      Notes:
                                    </Typography>
                                    <Typography variant="body2">
                                      {operation.notes}
                                    </Typography>
                                  </Box>
                                )}
                              </Box>
                            </TableCell>
                          </TableRow>
                        )}
                      </React.Fragment>
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

      {/* Operation Details Dialog */}
      <Dialog
        open={openDetailsDialog}
        onClose={() => setOpenDetailsDialog(false)}
        maxWidth="md"
        fullWidth
      >
        <DialogTitle>Operation Details</DialogTitle>
        <DialogContent>
          {selectedOperation && (
            <Box sx={{ mt: 2 }}>
              <Typography variant="subtitle1" gutterBottom>
                Operation Information
              </Typography>

              <Box
                sx={{
                  display: "grid",
                  gridTemplateColumns: "1fr 1fr",
                  gap: 2,
                  mb: 3,
                }}
              >
                <Box>
                  <Typography variant="body2" color="text.secondary">
                    Operation ID
                  </Typography>
                  <Typography variant="body1" fontFamily="monospace">
                    {selectedOperation.id}
                  </Typography>
                </Box>

                <Box>
                  <Typography variant="body2" color="text.secondary">
                    Timestamp
                  </Typography>
                  <Typography variant="body1">
                    {formatDate(selectedOperation.timestamp)}
                  </Typography>
                </Box>

                <Box>
                  <Typography variant="body2" color="text.secondary">
                    Operation Type
                  </Typography>
                  <Chip
                    label={getOperationTypeLabel(
                      selectedOperation.operationType
                    )}
                    color={
                      getOperationTypeColor(
                        selectedOperation.operationType
                      ) as any
                    }
                    size="small"
                  />
                </Box>

                <Box>
                  <Typography variant="body2" color="text.secondary">
                    Performed By
                  </Typography>
                  <Typography variant="body1">
                    {selectedOperation.userName}
                  </Typography>
                </Box>

                <Box>
                  <Typography variant="body2" color="text.secondary">
                    Product ID
                  </Typography>
                  <Typography variant="body1" fontFamily="monospace">
                    {selectedOperation.productId}
                  </Typography>
                </Box>

                <Box>
                  <Typography variant="body2" color="text.secondary">
                    Product Name
                  </Typography>
                  <Typography variant="body1">
                    {selectedOperation.productName}
                  </Typography>
                </Box>
              </Box>

              <Typography variant="subtitle1" gutterBottom>
                Changes
              </Typography>

              {selectedOperation.changes && (
                <Paper variant="outlined" sx={{ p: 2, mb: 2 }}>
                  {renderChanges(selectedOperation.changes)}
                </Paper>
              )}

              {selectedOperation.notes && (
                <>
                  <Typography variant="subtitle1" gutterBottom>
                    Notes
                  </Typography>
                  <Paper variant="outlined" sx={{ p: 2 }}>
                    <Typography variant="body2">
                      {selectedOperation.notes}
                    </Typography>
                  </Paper>
                </>
              )}
            </Box>
          )}
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpenDetailsDialog(false)}>Close</Button>
        </DialogActions>
      </Dialog>
    </Container>
  );
};

export default OperationHistoryPage;
