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
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Chip,
} from "@mui/material";
import { SelectChangeEvent } from "@mui/material/Select";
import {
  Add as AddIcon,
  Edit as EditIcon,
  Delete as DeleteIcon,
  Search as SearchIcon,
  History as HistoryIcon,
} from "@mui/icons-material";
import { Product, MediaType, CoverType, DiscType, ProductCreate } from "../../types/product";
import productService from "../../services/productService";
import { MediaSpecificFields } from "../../components/forms/MediaSpecificFields";

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

  // Form data state with all media-specific fields
  const [formData, setFormData] = useState({
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
    mediaType: MediaType.BOOK,
    
    // Book-specific fields
    author: "",
    coverType: "",
    publisher: "",
    publicationDate: "",
    language: "",
    pages: "",
    
    // CD/LP-specific fields
    artist: "",
    album: "",
    recordLabel: "",
    releaseDate: "",
    tracklist: "",
    
    // DVD-specific fields
    director: "",
    studio: "",
    runtime: "",
    discType: "",
    subtitle: "",
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
      
      if (response.data) {
        // Handle both paginated and non-paginated responses
        if (Array.isArray(response.data)) {
          setProducts(response.data);
          setTotalPages(1);
        } else if (response.data.data) {
          setProducts(response.data.data);
          setTotalPages(Math.ceil(response.data.total / 10));
        } else {
          setProducts([]);
          setTotalPages(1);
        }
      }
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
      value: product.value ? product.value.toString() : product.currentPrice.toString(),
      warehouseEntryDate: product.warehouseEntryDate?.split("T")[0] || new Date().toISOString().split("T")[0],
      mediaType: product.mediaType,
      
      // Media-specific fields - populate based on product type
      author: (product as any).author || "",
      coverType: (product as any).coverType || "",
      publisher: (product as any).publisher || "",
      publicationDate: (product as any).publicationDate?.split("T")[0] || "",
      language: (product as any).language || "",
      pages: (product as any).pages?.toString() || "",
      
      artist: (product as any).artist || "",
      album: (product as any).album || "",
      recordLabel: (product as any).recordLabel || "",
      releaseDate: (product as any).releaseDate?.split("T")[0] || "",
      tracklist: (product as any).tracklist || "",
      
      director: (product as any).director || "",
      studio: (product as any).studio || "",
      runtime: (product as any).runtime || "",
      discType: (product as any).discType || "",
      subtitle: (product as any).subtitle || "",
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
      
      setError(""); // Clear any previous errors
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
      mediaType: MediaType.BOOK,
      
      // Reset media-specific fields
      author: "",
      coverType: "",
      publisher: "",
      publicationDate: "",
      language: "",
      pages: "",
      artist: "",
      album: "",
      recordLabel: "",
      releaseDate: "",
      tracklist: "",
      director: "",
      studio: "",
      runtime: "",
      discType: "",
      subtitle: "",
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
  const handleSelectChange = (e: SelectChangeEvent<string>) => {
    const name = e.target.name as string;
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

    // Reset media-specific fields when media type changes
    if (name === "mediaType") {
      setFormData(prev => ({
        ...prev,
        [name]: value,
        // Reset all media-specific fields
        author: "",
        coverType: "",
        publisher: "",
        publicationDate: "",
        language: "",
        pages: "",
        artist: "",
        album: "",
        recordLabel: "",
        releaseDate: "",
        tracklist: "",
        director: "",
        studio: "",
        runtime: "",
        discType: "",
        subtitle: "",
      }));
    }
  };

  // Enhanced form validation
  const validateForm = () => {
    const errors: Record<string, string> = {};

    // Common validations
    if (!formData.title.trim()) {
      errors.title = "Title is required";
    }

    if (!formData.currentPrice.trim()) {
      errors.currentPrice = "Price is required";
    } else if (isNaN(Number(formData.currentPrice)) || Number(formData.currentPrice) <= 0) {
      errors.currentPrice = "Price must be a positive number";
    }

    if (!formData.value.trim()) {
      errors.value = "Value is required";
    } else if (isNaN(Number(formData.value)) || Number(formData.value) <= 0) {
      errors.value = "Value must be a positive number";
    }

    // Price range validation (30%-150% of value)
    if (formData.value && formData.currentPrice) {
      const value = Number(formData.value);
      const price = Number(formData.currentPrice);
      const minPrice = value * 0.3;
      const maxPrice = value * 1.5;
      
      if (price < minPrice || price > maxPrice) {
        errors.currentPrice = `Price must be between ${minPrice.toFixed(0)} and ${maxPrice.toFixed(0)} VND (30%-150% of value)`;
      }
    }

    if (!formData.quantity.trim()) {
      errors.quantity = "Quantity is required";
    } else if (isNaN(Number(formData.quantity)) || Number(formData.quantity) < 0) {
      errors.quantity = "Quantity must be a non-negative number";
    }

    if (!formData.category.trim()) {
      errors.category = "Category is required";
    }

    if (!formData.mediaType) {
      errors.mediaType = "Media type is required";
    }

    if (!formData.barcode.trim()) {
      errors.barcode = "Barcode is required";
    }

    if (!formData.productDimensions.trim()) {
      errors.productDimensions = "Product dimensions are required";
    }

    if (!formData.warehouseEntryDate) {
      errors.warehouseEntryDate = "Warehouse entry date is required";
    }

    if (formData.weight && (isNaN(Number(formData.weight)) || Number(formData.weight) <= 0)) {
      errors.weight = "Weight must be a positive number";
    }

    // Media-specific validations
    switch (formData.mediaType) {
      case MediaType.BOOK:
        if (!formData.author?.trim()) {
          errors.author = "Author is required for books";
        }
        if (!formData.publisher?.trim()) {
          errors.publisher = "Publisher is required for books";
        }
        if (!formData.coverType) {
          errors.coverType = "Cover type is required for books";
        }
        if (!formData.publicationDate) {
          errors.publicationDate = "Publication date is required for books";
        }
        if (!formData.language?.trim()) {
          errors.language = "Language is required for books";
        }
        if (formData.pages && (isNaN(Number(formData.pages)) || Number(formData.pages) <= 0)) {
          errors.pages = "Number of pages must be a positive number";
        }
        break;

      case MediaType.CD:
      case MediaType.LP:
        if (!formData.artist?.trim()) {
          errors.artist = "Artist is required for CDs/LPs";
        }
        if (!formData.album?.trim()) {
          errors.album = "Album is required for CDs/LPs";
        }
        if (!formData.recordLabel?.trim()) {
          errors.recordLabel = "Record label is required for CDs/LPs";
        }
        break;

      case MediaType.DVD:
        if (!formData.director?.trim()) {
          errors.director = "Director is required for DVDs";
        }
        if (!formData.studio?.trim()) {
          errors.studio = "Studio is required for DVDs";
        }
        if (!formData.runtime?.trim()) {
          errors.runtime = "Runtime is required for DVDs";
        }
        if (!formData.discType) {
          errors.discType = "Disc type is required for DVDs";
        }
        if (!formData.language?.trim()) {
          errors.language = "Language is required for DVDs";
        }
        break;
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
      const productData: ProductCreate = {
        title: formData.title,
        productDescription: formData.productDescription,
        currentPrice: Number(formData.currentPrice),
        quantity: Number(formData.quantity),
        category: formData.category,
        barcode: formData.barcode,
        productDimensions: formData.productDimensions,
        weight: Number(formData.weight) || 0.1,
        imageURL: formData.imageURL,
        rushOrderEligible: formData.rushOrderEligible,
        genre: formData.genre || "General",
        value: Number(formData.value),
        warehouseEntryDate: formData.warehouseEntryDate,
        mediaType: formData.mediaType,
      };

      // Add media-specific fields based on type
      switch (formData.mediaType) {
        case MediaType.BOOK:
          Object.assign(productData, {
            author: formData.author,
            coverType: formData.coverType as CoverType,
            publisher: formData.publisher,
            publicationDate: formData.publicationDate,
            language: formData.language,
            pages: formData.pages ? Number(formData.pages) : undefined,
          });
          break;

        case MediaType.CD:
        case MediaType.LP:
          Object.assign(productData, {
            artist: formData.artist,
            album: formData.album,
            recordLabel: formData.recordLabel,
            releaseDate: formData.releaseDate,
            tracklist: formData.tracklist,
          });
          break;

        case MediaType.DVD:
          Object.assign(productData, {
            director: formData.director,
            studio: formData.studio,
            runtime: formData.runtime,
            discType: formData.discType as DiscType,
            language: formData.language,
            subtitle: formData.subtitle,
            releaseDate: formData.releaseDate,
          });
          break;
      }

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
      setError("");
    } catch (err: any) {
      console.error("Failed to save product:", err);
      setError(err.response?.data || err.response?.data?.message || "Failed to save product");
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

  // Get media type color for chip
  const getMediaTypeColor = (mediaType: MediaType) => {
    const colors = {
      [MediaType.BOOK]: "primary",
      [MediaType.CD]: "secondary",
      [MediaType.LP]: "success",
      [MediaType.DVD]: "warning",
    };
    return colors[mediaType] as any || "default";
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
            <TableContainer>
              <Table>
                <TableHead>
                  <TableRow>
                    <TableCell>Title</TableCell>
                    <TableCell>Type</TableCell>
                    <TableCell>Category</TableCell>
                    <TableCell>Price</TableCell>
                    <TableCell>Quantity</TableCell>
                    <TableCell>Barcode</TableCell>
                    <TableCell>Actions</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {products.map((product) => (
                    <TableRow key={product.id}>
                      <TableCell>
                        <Box>
                          <Typography variant="subtitle2">
                            {product.title}
                          </Typography>
                          {product.genre && (
                            <Typography variant="caption" color="text.secondary">
                              {product.genre}
                            </Typography>
                          )}
                        </Box>
                      </TableCell>
                      <TableCell>
                        <Chip
                          label={getMediaTypeLabel(product.mediaType)}
                          color={getMediaTypeColor(product.mediaType)}
                          size="small"
                        />
                      </TableCell>
                      <TableCell>{product.category}</TableCell>
                      <TableCell>{formatPrice(product.currentPrice)}</TableCell>
                      <TableCell>{product.quantity}</TableCell>
                      <TableCell>{product.barcode}</TableCell>
                      <TableCell>
                        <IconButton
                          color="primary"
                          onClick={() => handleEditProduct(product)}
                          size="small"
                        >
                          <EditIcon />
                        </IconButton>
                        <IconButton
                          color="error"
                          onClick={() => handleDeleteClick(product)}
                          size="small"
                        >
                          <DeleteIcon />
                        </IconButton>
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </TableContainer>

            {totalPages > 1 && (
              <Box sx={{ display: "flex", justifyContent: "center", mt: 3 }}>
                <Pagination
                  count={totalPages}
                  page={page}
                  onChange={handlePageChange}
                />
              </Box>
            )}
          </>
        )}
      </Paper>

      {/* Add/Edit Product Dialog */}
      <Dialog
        open={openProductDialog}
        onClose={() => setOpenProductDialog(false)}
        maxWidth="md"
        fullWidth
      >
        <DialogTitle>{dialogTitle}</DialogTitle>
        <form onSubmit={handleSubmit}>
          <DialogContent>
            {/* Media Type Selection */}
            <Box sx={{ mb: 2 }}>
              <FormControl fullWidth required error={!!formErrors.mediaType}>
                <InputLabel>Media Type</InputLabel>
                <Select
                  name="mediaType"
                  value={formData.mediaType}
                  onChange={handleSelectChange}
                  label="Media Type"
                >
                  <MenuItem value={MediaType.BOOK}>Book</MenuItem>
                  <MenuItem value={MediaType.CD}>CD</MenuItem>
                  <MenuItem value={MediaType.LP}>LP Record</MenuItem>
                  <MenuItem value={MediaType.DVD}>DVD</MenuItem>
                </Select>
                {formErrors.mediaType && (
                  <Typography variant="caption" color="error">
                    {formErrors.mediaType}
                  </Typography>
                )}
              </FormControl>
            </Box>

            {/* Title Field */}
            <Box sx={{ mb: 2 }}>
              <TextField
                name="title"
                label="Title"
                fullWidth
                required
                value={formData.title}
                onChange={handleInputChange}
                error={!!formErrors.title}
                helperText={formErrors.title}
              />
            </Box>

            {/* Media-Specific Fields */}
            <MediaSpecificFields
              mediaType={formData.mediaType}
              formData={formData}
              handleInputChange={handleInputChange}
              handleSelectChange={handleSelectChange}
              formErrors={formErrors}
            />

            {/* Common Fields */}
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
                required
                value={formData.barcode}
                onChange={handleInputChange}
                error={!!formErrors.barcode}
                helperText={formErrors.barcode}
                sx={{ flex: 1 }}
              />
            </Box>

            <Box sx={{ display: "flex", gap: 2, mb: 2 }}>
              <TextField
                name="productDimensions"
                label="Dimensions"
                required
                value={formData.productDimensions}
                onChange={handleInputChange}
                error={!!formErrors.productDimensions}
                helperText={formErrors.productDimensions}
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
                InputProps={{ inputProps: { min: 0, step: 0.1 } }}
                sx={{ flex: 1 }}
              />
            </Box>

            <Box sx={{ display: "flex", gap: 2, mb: 2 }}>
              <TextField
                name="imageURL"
                label="Image URL"
                value={formData.imageURL}
                onChange={handleInputChange}
                sx={{ flex: 1 }}
              />

              <TextField
                name="warehouseEntryDate"
                label="Warehouse Entry Date"
                type="date"
                required
                value={formData.warehouseEntryDate}
                onChange={handleInputChange}
                error={!!formErrors.warehouseEntryDate}
                helperText={formErrors.warehouseEntryDate}
                InputLabelProps={{ shrink: true }}
                sx={{ flex: 1 }}
              />
            </Box>

            <Box sx={{ mb: 2 }}>
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
                  />
                }
                label="Rush Order Eligible"
              />
            </Box>
          </DialogContent>

          <DialogActions>
            <Button onClick={() => setOpenProductDialog(false)}>
              Cancel
            </Button>
            <Button
              type="submit"
              variant="contained"
              disabled={formSubmitting}
            >
              {formSubmitting ? <CircularProgress size={20} /> : "Save"}
            </Button>
          </DialogActions>
        </form>
      </Dialog>

      {/* Delete Confirmation Dialog */}
      <Dialog
        open={openDeleteDialog}
        onClose={() => setOpenDeleteDialog(false)}
      >
        <DialogTitle>Confirm Delete</DialogTitle>
        <DialogContent>
          <DialogContentText>
            Are you sure you want to delete "{productToDelete?.title}"? This
            action cannot be undone.
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpenDeleteDialog(false)}>Cancel</Button>
          <Button
            onClick={handleDeleteConfirm}
            color="error"
            variant="contained"
            disabled={deleteLoading}
          >
            {deleteLoading ? <CircularProgress size={20} /> : "Delete"}
          </Button>
        </DialogActions>
      </Dialog>
    </Container>
  );
};

export default ProductManagementPage;