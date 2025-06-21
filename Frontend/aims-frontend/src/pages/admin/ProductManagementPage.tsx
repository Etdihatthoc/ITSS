// src/pages/admin/ProductManagementPage.tsx
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
  DialogContentText,
  DialogTitle,
  FormControlLabel,
  CircularProgress,
  Alert,
  Pagination,
  InputAdornment,
  Checkbox,
} from "@mui/material";
import { SelectChangeEvent } from "@mui/material/Select";
import {
  Add as AddIcon,
  Edit as EditIcon,
  Delete as DeleteIcon,
  Search as SearchIcon,
} from "@mui/icons-material";
import { Product, MediaType } from "../../types/product";
import productService from "../../services/productService";

const ProductManagementPage: React.FC = () => {
  // State for products
  const [products, setProducts] = useState<Product[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  // State for pagination
  const [page, setPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [searchTerm, setSearchTerm] = useState("");

  // State for product dialog
  const [openProductDialog, setOpenProductDialog] = useState(false);
  const [dialogTitle, setDialogTitle] = useState("");
  const [selectedProduct, setSelectedProduct] = useState<Product | null>(null);
  const [formErrors, setFormErrors] = useState<Record<string, string>>({});
  const [formSubmitting, setFormSubmitting] = useState(false);

  // State for delete confirmation dialog
  const [openDeleteDialog, setOpenDeleteDialog] = useState(false);
  const [productToDelete, setProductToDelete] = useState<Product | null>(null);
  const [deleteLoading, setDeleteLoading] = useState(false);

  // Form data state
  const [formData, setFormData] = useState({
    title: "",
    productDescription: "", // Changed from description
    currentPrice: "", // Changed from price
    quantity: "",
    category: "",
    barcode: "",
    productDimensions: "", // Changed from dimensions
    weight: "",
    imageURL: "", // Changed from imageUrl
    rushOrderEligible: true, // New field
    genre: "", // New field
    value: "", // New field
    warehouseEntryDate: new Date().toISOString().split("T")[0], // Format as YYYY-MM-DD
  });

  // Load products on component mount and when page or search changes
  useEffect(() => {
    fetchProducts();
  }, [page, searchTerm]);

  // Fetch products from API
  const fetchProducts = async () => {
    try {
      setLoading(true);
      setError("");

      const response = await productService.getAllProducts();
      // If search term is provided, filter products

      setProducts(response.data.data);
      setTotalPages(Math.ceil(response.data.total / 10));
    } catch (err: any) {
      console.error("Failed to fetch products:", err);
      setError(err.response?.data?.message || "Failed to load products");
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
    fetchProducts();
  };

  // Open dialog to add a new product
  const handleAddProduct = () => {
    setDialogTitle("Add New Product");
    setSelectedProduct(null);
    resetForm();
    setOpenProductDialog(true);
  };

  // Open dialog to edit a product
  const handleEditProduct = (product: Product) => {
    setDialogTitle("Edit Product");
    setSelectedProduct(product);

    // Populate form with product data
    setFormData({
      title: product.title,
      productDescription: product.productDescription || "",
      currentPrice: product.currentPrice.toString(),
      quantity: product.quantity.toString(),
      category: product.category,
      barcode: product.barcode || "",
      productDimensions: product.productDimensions || "",
      weight: product.weight ? product.weight.toString() : "",
      imageURL: product.imageURL || "",
      rushOrderEligible: product.rushOrderEligible || true,
      genre: product.genre || "",
      value: product.value
        ? product.value.toString()
        : product.currentPrice.toString(),
      warehouseEntryDate:
        product.warehouseEntryDate?.split("T")[0] ||
        new Date().toISOString().split("T")[0],
    });

    setOpenProductDialog(true);
  };

  // Open dialog to confirm product deletion
  const handleDeleteClick = (product: Product) => {
    setProductToDelete(product);
    setOpenDeleteDialog(true);
  };

  // Delete product
  const handleDeleteConfirm = async () => {
    if (!productToDelete) return;

    try {
      setDeleteLoading(true);
      await productService.deleteProduct(productToDelete.id);

      // Refresh product list
      fetchProducts();

      // Close dialog
      setOpenDeleteDialog(false);
      setProductToDelete(null);
    } catch (err: any) {
      console.error("Failed to delete product:", err);
      setError(err.response?.data?.message || "Failed to delete product");
    } finally {
      setDeleteLoading(false);
    }
  };

  // Reset form with all fields
  const resetForm = () => {
    setFormData({
      title: "",
      productDescription: "",
      currentPrice: "",
      quantity: "",
      category: "",
      barcode: "",
      productDimensions: "",
      weight: "",
      imageURL: "",
      rushOrderEligible: true,
      genre: "",
      value: "",
      warehouseEntryDate: new Date().toISOString().split("T")[0],
    });
    setFormErrors({});
  };

  // Handle form input changes
  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;

    setFormData({
      ...formData,
      [name]: value,
    });

    // Clear error when field is edited
    if (formErrors[name]) {
      setFormErrors({
        ...formErrors,
        [name]: "",
      });
    }
  };

  // Handle Select (dropdown) changes
  const handleSelectChange = (
    e: React.ChangeEvent<{ name?: string; value: unknown }> | SelectChangeEvent
  ) => {
    const name = (e.target as HTMLInputElement).name;
    const value = e.target.value;

    setFormData({
      ...formData,
      [name]: value,
    });

    // Clear error when field is edited
    if (formErrors[name]) {
      setFormErrors({
        ...formErrors,
        [name]: "",
      });
    }
  };

  // Validate form
  const validateForm = () => {
    const errors: Record<string, string> = {};

    if (!formData.title.trim()) {
      errors.title = "Title is required";
    }

    if (!formData.currentPrice.trim()) {
      errors.currentPrice = "Price is required";
    } else if (
      isNaN(Number(formData.currentPrice)) ||
      Number(formData.currentPrice) <= 0
    ) {
      errors.currentPrice = "Price must be a positive number";
    }

    if (!formData.value.trim()) {
      errors.value = "Value is required";
    } else if (isNaN(Number(formData.value)) || Number(formData.value) <= 0) {
      errors.value = "Value must be a positive number";
    }

    if (!formData.quantity.trim()) {
      errors.quantity = "Quantity is required";
    } else if (
      isNaN(Number(formData.quantity)) ||
      Number(formData.quantity) < 0
    ) {
      errors.quantity = "Quantity must be a non-negative number";
    }

    if (!formData.category.trim()) {
      errors.category = "Category is required";
    }

    if (
      formData.weight &&
      (isNaN(Number(formData.weight)) || Number(formData.weight) <= 0)
    ) {
      errors.weight = "Weight must be a positive number";
    }

    setFormErrors(errors);
    return Object.keys(errors).length === 0;
  };

  // Handle form submission
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!validateForm()) {
      return;
    }

    try {
      setFormSubmitting(true);

      // Format data to match API requirements
      const productData = {
        title: formData.title,
        description: formData.productDescription,
        price: Number(formData.currentPrice),
        currentPrice: Number(formData.currentPrice),
        quantity: Number(formData.quantity),
        category: formData.category,
        barcode: formData.barcode,
        dimensions: formData.productDimensions,
        productDimensions: formData.productDimensions,
        weight: formData.weight ? Number(formData.weight) : 0.1,
        imageURL: formData.imageURL,
        rushOrderEligible: formData.rushOrderEligible,
        genre: formData.genre || "General",
        value: Number(formData.value) || Number(formData.currentPrice),
        warehouseEntryDate: formData.warehouseEntryDate,
        mediaType: MediaType.BOOK, // Default media type, adjust as needed
      };

      if (selectedProduct) {
        // Update existing product
        await productService.updateProduct(selectedProduct.id, productData);
      } else {
        // Create new product
        await productService.addProduct(productData);
      }

      // Refresh product list
      fetchProducts();

      // Close dialog
      setOpenProductDialog(false);

      // Show success message
      setError(""); // Clear any previous errors
    } catch (err: any) {
      console.error("Failed to save product:", err);
      setError(err.response?.data?.message || "Failed to save product");
    } finally {
      setFormSubmitting(false);
    }
  };

  // Format price for display
  const formatPrice = (price: number) => {
    return new Intl.NumberFormat("vi-VN", {
      style: "currency",
      currency: "VND",
    }).format(price);
  };

  // Get media type label
  const getMediaTypeLabel = (mediaType: MediaType) => {
    const labels = {
      [MediaType.BOOK]: "Book",
      [MediaType.CD]: "CD",
      [MediaType.LP]: "LP Record",
      [MediaType.DVD]: "DVD",
    };
    return labels[mediaType] || mediaType;
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
        <Typography variant="h4">Product Management</Typography>
        <Button
          variant="contained"
          color="primary"
          startIcon={<AddIcon />}
          onClick={handleAddProduct}
        >
          Add Product
        </Button>
      </Box>

      {error && (
        <Alert severity="error" sx={{ mb: 3 }}>
          {error}
        </Alert>
      )}

      <Paper sx={{ p: 2, mb: 3 }}>
        <Box sx={{ display: "flex", alignItems: "center", mb: 2 }}>
          <TextField
            placeholder="Search products..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            onKeyPress={(e) => e.key === "Enter" && handleSearch()}
            sx={{ flexGrow: 1, mr: 2 }}
            InputProps={{
              startAdornment: (
                <InputAdornment position="start">
                  <SearchIcon />
                </InputAdornment>
              ),
            }}
          />
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
            {products.length === 0 ? (
              <Box sx={{ py: 4, textAlign: "center" }}>
                <Typography variant="body1" color="text.secondary">
                  No products found.
                </Typography>
              </Box>
            ) : (
              <TableContainer>
                <Table>
                  <TableHead>
                    <TableRow>
                      <TableCell>Title</TableCell>
                      <TableCell>Type</TableCell>
                      <TableCell>Category</TableCell>
                      <TableCell>Price</TableCell>
                      <TableCell>Quantity</TableCell>
                      <TableCell align="right">Actions</TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {products.map((product) => (
                      <TableRow key={product.id}>
                        <TableCell>{product.title}</TableCell>
                        <TableCell>
                          {getMediaTypeLabel(product.mediaType)}
                        </TableCell>
                        <TableCell>{product.category}</TableCell>
                        <TableCell>
                          {formatPrice(product.currentPrice)}
                        </TableCell>
                        <TableCell>{product.quantity}</TableCell>
                        <TableCell align="right">
                          <IconButton
                            color="primary"
                            onClick={() => handleEditProduct(product)}
                          >
                            <EditIcon />
                          </IconButton>
                          <IconButton
                            color="error"
                            onClick={() => handleDeleteClick(product)}
                          >
                            <DeleteIcon />
                          </IconButton>
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

      {/* Product Form Dialog */}
      <Dialog
        open={openProductDialog}
        onClose={() => setOpenProductDialog(false)}
        maxWidth="md"
        fullWidth
      >
        <DialogTitle>{dialogTitle}</DialogTitle>
        <DialogContent>
          <Box component="form" sx={{ mt: 2 }}>
            <Box sx={{ mb: 2 }}>
              <TextField
                name="title"
                label="Product Title"
                fullWidth
                required
                value={formData.title}
                onChange={handleInputChange}
                error={!!formErrors.title}
                helperText={formErrors.title}
              />
            </Box>

            <Box sx={{ mb: 2 }}>
              <TextField
                name="productDescription"
                label="Description"
                fullWidth
                multiline
                rows={3}
                value={formData.productDescription}
                onChange={handleInputChange}
              />
            </Box>

            <Box sx={{ display: "flex", gap: 2, mb: 2 }}>
              <TextField
                name="currentPrice"
                label="Price (VND)"
                required
                value={formData.currentPrice}
                onChange={handleInputChange}
                error={!!formErrors.currentPrice}
                helperText={formErrors.currentPrice}
                type="number"
                InputProps={{ inputProps: { min: 0 } }}
                sx={{ flex: 1 }}
              />

              <TextField
                name="value"
                label="Value (Cost Price)"
                required
                value={formData.value}
                onChange={handleInputChange}
                error={!!formErrors.value}
                helperText={formErrors.value}
                type="number"
                InputProps={{ inputProps: { min: 0 } }}
                sx={{ flex: 1 }}
              />
            </Box>

            <Box sx={{ display: "flex", gap: 2, mb: 2 }}>
              <TextField
                name="quantity"
                label="Quantity"
                required
                value={formData.quantity}
                onChange={handleInputChange}
                error={!!formErrors.quantity}
                helperText={formErrors.quantity}
                type="number"
                InputProps={{ inputProps: { min: 0 } }}
                sx={{ flex: 1 }}
              />

              <TextField
                name="category"
                label="Category"
                required
                value={formData.category}
                onChange={handleInputChange}
                error={!!formErrors.category}
                helperText={formErrors.category}
                sx={{ flex: 1 }}
              />
            </Box>

            <Box sx={{ display: "flex", gap: 2, mb: 2 }}>
              <TextField
                name="genre"
                label="Genre"
                value={formData.genre}
                onChange={handleInputChange}
                sx={{ flex: 1 }}
              />

              <TextField
                name="barcode"
                label="Barcode"
                value={formData.barcode}
                onChange={handleInputChange}
                sx={{ flex: 1 }}
              />
            </Box>

            <Box sx={{ display: "flex", gap: 2, mb: 2 }}>
              <TextField
                name="productDimensions"
                label="Dimensions"
                value={formData.productDimensions}
                onChange={handleInputChange}
                sx={{ flex: 1 }}
              />

              <TextField
                name="weight"
                label="Weight (kg)"
                value={formData.weight}
                onChange={handleInputChange}
                error={!!formErrors.weight}
                helperText={formErrors.weight}
                type="number"
                InputProps={{ inputProps: { min: 0, step: 0.01 } }}
                sx={{ flex: 1 }}
              />

              <TextField
                name="warehouseEntryDate"
                label="Warehouse Entry Date"
                type="date"
                value={formData.warehouseEntryDate}
                onChange={handleInputChange}
                InputLabelProps={{ shrink: true }}
                sx={{ flex: 1 }}
              />
            </Box>

            <Box sx={{ display: "flex", alignItems: "center", mb: 2 }}>
              <FormControlLabel
                control={
                  <Checkbox
                    checked={formData.rushOrderEligible}
                    onChange={(e) =>
                      setFormData({
                        ...formData,
                        rushOrderEligible: e.target.checked,
                      })
                    }
                    name="rushOrderEligible"
                  />
                }
                label="Eligible for Rush Order"
              />
            </Box>

            <Box sx={{ mb: 2 }}>
              <TextField
                name="imageURL"
                label="Image URL"
                fullWidth
                value={formData.imageURL}
                onChange={handleInputChange}
              />
            </Box>
          </Box>
        </DialogContent>
        <DialogActions>
          <Button
            onClick={() => setOpenProductDialog(false)}
            disabled={formSubmitting}
          >
            Cancel
          </Button>
          <Button
            onClick={handleSubmit}
            variant="contained"
            color="primary"
            disabled={formSubmitting}
          >
            {formSubmitting ? <CircularProgress size={24} /> : "Save"}
          </Button>
        </DialogActions>
      </Dialog>

      {/* Delete Confirmation Dialog */}
      <Dialog
        open={openDeleteDialog}
        onClose={() => setOpenDeleteDialog(false)}
      >
        <DialogTitle>Confirm Deletion</DialogTitle>
        <DialogContent>
          <DialogContentText>
            Are you sure you want to delete "{productToDelete?.title}"? This
            action cannot be undone.
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button
            onClick={() => setOpenDeleteDialog(false)}
            disabled={deleteLoading}
          >
            Cancel
          </Button>
          <Button
            onClick={handleDeleteConfirm}
            color="error"
            disabled={deleteLoading}
            startIcon={
              deleteLoading ? <CircularProgress size={20} /> : <DeleteIcon />
            }
          >
            Delete
          </Button>
        </DialogActions>
      </Dialog>
    </Container>
  );
};

export default ProductManagementPage;
