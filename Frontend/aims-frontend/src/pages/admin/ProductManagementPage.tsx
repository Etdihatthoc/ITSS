import React, { useState, useEffect } from "react";
import {
  Container,
  Typography,
  Box,
  Button,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  IconButton,
  Pagination,
  CircularProgress,
  Alert,
  Chip,
  Checkbox,
  FormControl,
  FormHelperText,
  InputLabel,
  MenuItem,
  Select,
} from "@mui/material";
import {
  Add as AddIcon,
  Edit as EditIcon,
  Delete as DeleteIcon,
  Search as SearchIcon,
} from "@mui/icons-material";
import { toast } from "react-toastify";
import productService from "../../services/productService";
import { Product, CoverType, DiscType, MediaType } from "../../types/product";

interface FormData {
  title: string;
  productDescription: string;
  currentPrice: string;
  quantity: string;
  category: string;
  barcode: string;
  productDimensions: string;
  weight: string;
  imageURL: string;
  rushOrderEligible: boolean;
  genre: string;
  value: string;
  warehouseEntryDate: string;
  mediaType: string;
  // Book fields
  author?: string;
  coverType?: CoverType;
  publisher?: string;
  publicationDate?: string;
  numberOfPage?: string;
  language?: string;
  // CD/LP fields
  artist?: string;
  album?: string;
  recordLabel?: string;
  tracklist?: string;
  releaseDate?: string;
  // DVD fields
  director?: string;
  studio?: string;
  runtime?: string;
  discType?: DiscType;
  subtitle?: string;
}

interface ValidationErrors {
  [key: string]: string;
}

const ProductManagementPage: React.FC = () => {
  const [products, setProducts] = useState<Product[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [page, setPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [searchTerm, setSearchTerm] = useState("");
  const [openProductDialog, setOpenProductDialog] = useState(false);
  const [selectedProduct, setSelectedProduct] = useState<Product | null>(null);
  const [dialogTitle, setDialogTitle] = useState("");
  const [saving, setSaving] = useState(false);
  const [selectedProducts, setSelectedProducts] = useState<string[]>([]);
  const [bulkDeleteLoading, setBulkDeleteLoading] = useState(false);
  const [errors, setErrors] = useState<ValidationErrors>({});

  const [formData, setFormData] = useState<FormData>({
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
    warehouseEntryDate: new Date().toISOString().split('T')[0],
    mediaType: "",
    // Book fields
    author: "",
    coverType: undefined,
    publisher: "",
    publicationDate: "",
    numberOfPage: "",
    language: "",
    // CD/LP fields
    artist: "",
    album: "",
    recordLabel: "",
    tracklist: "",
    releaseDate: "",
    // DVD fields
    director: "",
    studio: "",
    runtime: "",
    discType: undefined,
    subtitle: "",
  });

  useEffect(() => {
    fetchProducts();
  }, [page]);

  const fetchProducts = async () => {
    try {
      setLoading(true);
      setError(null);

      let response;
      if (searchTerm.trim()) {
        response = await productService.searchProducts({
          search: searchTerm,
          page: page - 1,
          limit: 10,
        });
      } else {
        response = await productService.getAllProducts();
        const startIndex = (page - 1) * 10;
        const endIndex = startIndex + 10;
        const paginatedData = response.data.slice(startIndex, endIndex);
        response = {
          data: {
            data: paginatedData,
            total: response.data.length,
          }
        };
      }

      setProducts(response.data.data);
      setTotalPages(Math.ceil(response.data.total / 10));
    } catch (err: any) {
      console.error("Failed to fetch products:", err);
      setError(err.response?.data?.message || "Failed to load products");
    } finally {
      setLoading(false);
    }
  };

  const handlePageChange = (_: React.ChangeEvent<unknown>, value: number) => {
    setPage(value);
  };

  const handleSearch = () => {
    setPage(1);
    fetchProducts();
  };

  const handleAddProduct = () => {
    setDialogTitle("Add New Product");
    setSelectedProduct(null);
    resetForm();
    setOpenProductDialog(true);
  };

  const handleEditProduct = (product: Product) => {
    setDialogTitle("Edit Product");
    setSelectedProduct(product);

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
      value: product.value ? product.value.toString() : "",
      warehouseEntryDate: product.warehouseEntryDate || "",
      mediaType: product.mediaType || "BOOK",
      // Book fields
      author: (product as any).author || "",
      coverType: (product as any).coverType,
      publisher: (product as any).publisher || "",
      publicationDate: (product as any).publicationDate || "",
      numberOfPage: (product as any).numberOfPage?.toString() || "",
      language: (product as any).language || "",
      // CD/LP fields
      artist: (product as any).artist || "",
      album: (product as any).album || "",
      recordLabel: (product as any).recordLabel || "",
      tracklist: (product as any).tracklist || "",
      releaseDate: (product as any).releaseDate || "",
      // DVD fields
      director: (product as any).director || "",
      studio: (product as any).studio || "",
      runtime: (product as any).runtime || "",
      discType: (product as any).discType,
      subtitle: (product as any).subtitle || "",
    });
    setOpenProductDialog(true);
  };

  const handleDeleteProduct = async (id: string) => {
    const confirmed = window.confirm(
      "Are you sure you want to delete this product? This action cannot be undone."
    );

    if (!confirmed) return;

    try {
      await productService.deleteProduct(id);
      toast.success("Product deleted successfully");
      fetchProducts();
    } catch (err: any) {
      console.error("Failed to delete product:", err);
      if (err.response?.status === 400) {
        toast.error(err.response.data.message || "Cannot delete product due to business rules");
      } else if (err.response?.status === 409) {
        toast.error("Another operation is in progress. Please try again later.");
      } else {
        toast.error("Failed to delete product");
      }
    }
  };

  const handleSelectProduct = (productId: string, checked: boolean) => {
    if (checked) {
      setSelectedProducts([...selectedProducts, productId]);
    } else {
      setSelectedProducts(selectedProducts.filter(id => id !== productId));
    }
  };

  const handleSelectAll = (event: React.ChangeEvent<HTMLInputElement>) => {
    if (event.target.checked) {
      setSelectedProducts(products.map(product => product.id.toString()));
    } else {
      setSelectedProducts([]);
    }
  };

  const handleBulkDelete = async () => {
    if (selectedProducts.length === 0) return;
    
    if (selectedProducts.length > 10) {
      toast.error("Cannot delete more than 10 products at once");
      return;
    }
    
    const confirmed = window.confirm(
      `Are you sure you want to delete ${selectedProducts.length} products? This action cannot be undone.`
    );
    
    if (!confirmed) return;
    
    setBulkDeleteLoading(true);
    
    try {
      await productService.deleteBulkProducts(selectedProducts);
      toast.success(`Successfully deleted ${selectedProducts.length} products`);
      setSelectedProducts([]);
      fetchProducts();
    } catch (err: any) {
      console.error("Failed to delete products:", err);
      if (err.response?.status === 400) {
        toast.error(err.response.data.message || "Cannot delete products due to business rules");
      } else if (err.response?.status === 409) {
        toast.error("Another operation is in progress. Please try again later.");
      } else {
        toast.error("Failed to delete products");
      }
    } finally {
      setBulkDeleteLoading(false);
    }
  };

  const handleSaveProduct = async () => {
    try {
      setSaving(true);
      setErrors({});
      
      const productData = {
        ...formData,
        currentPrice: parseFloat(formData.currentPrice),
        quantity: parseInt(formData.quantity),
        weight: parseFloat(formData.weight),
        value: parseFloat(formData.value),
        warehouseEntryDate: formData.warehouseEntryDate,
        numberOfPage: formData.numberOfPage ? parseInt(formData.numberOfPage) : undefined,
      };

      if (selectedProduct) {
        await productService.updateProduct(selectedProduct.id.toString(), productData);
        toast.success("Product updated successfully");
      } else {
        await productService.addProduct(productData);
        toast.success("Product added successfully");
      }

      setOpenProductDialog(false);
      fetchProducts();
      resetForm();
    } catch (err: any) {
      console.error("Failed to save product:", err);
      
      if (err.response?.status === 400) {
        const errorMessage = err.response.data.message || err.response.data.error || "Validation failed";
        
        if (errorMessage.includes("author")) {
          setErrors(prev => ({ ...prev, author: "Author is required for books" }));
        }
        if (errorMessage.includes("coverType")) {
          setErrors(prev => ({ ...prev, coverType: "Cover type is required for books" }));
        }
        if (errorMessage.includes("publisher")) {
          setErrors(prev => ({ ...prev, publisher: "Publisher is required for books" }));
        }
        if (errorMessage.includes("artist")) {
          setErrors(prev => ({ ...prev, artist: "Artist is required for CDs/LPs" }));
        }
        if (errorMessage.includes("director")) {
          setErrors(prev => ({ ...prev, director: "Director is required for DVDs" }));
        }
        if (errorMessage.includes("daily limit")) {
          toast.error("Daily operation limit exceeded. Cannot perform more operations today.");
        } else if (errorMessage.includes("price more than 2 times")) {
          toast.error("Cannot update product price more than 2 times per day");
        } else {
          toast.error(errorMessage);
        }
      } else if (err.response?.status === 409) {
        toast.error("Another operation is in progress. Please try again later.");
      } else {
        toast.error("Failed to save product");
      }
    } finally {
      setSaving(false);
    }
  };

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
      warehouseEntryDate: new Date().toISOString().split('T')[0],
      mediaType: "",
      // Book fields
      author: "",
      coverType: undefined,
      publisher: "",
      publicationDate: "",
      numberOfPage: "",
      language: "",
      // CD/LP fields
      artist: "",
      album: "",
      recordLabel: "",
      tracklist: "",
      releaseDate: "",
      // DVD fields
      director: "",
      studio: "",
      runtime: "",
      discType: undefined,
      subtitle: "",
    });
    setErrors({});
  };

  const formatPrice = (price: number) => {
    return new Intl.NumberFormat("vi-VN", {
      style: "currency",
      currency: "VND",
    }).format(price);
  };

  // Get media type label for section header
  const getMediaTypeLabel = (mediaType: string) => {
    const labels: Record<string, string> = {
      'BOOK': 'Book',
      'CD': 'CD',
      'LP': 'LP Record', 
      'DVD': 'DVD'
    };
    return labels[mediaType] || mediaType;
  };

  // Render media-specific fields based on selected type
  const renderMediaSpecificFields = () => {
    switch (formData.mediaType) {
      case 'BOOK':
        return (
          <Box>
            <Typography variant="subtitle2" gutterBottom sx={{ mb: 2, fontWeight: 'bold' }}>
              Required Book Information:
            </Typography>
            
            {/* Author */}
            <TextField
              fullWidth
              label="Author *"
              value={formData.author || ''}
              onChange={(e) => setFormData({ ...formData, author: e.target.value })}
              margin="normal"
              required
              error={!!errors.author}
              helperText={errors.author}
              placeholder="Full name of the author"
            />

            {/* Cover Type and Publisher - Side by side */}
            <Box sx={{ display: 'flex', gap: 2, mt: 2 }}>
              <FormControl fullWidth required error={!!errors.coverType}>
                <InputLabel>Cover Type *</InputLabel>
                <Select
                  value={formData.coverType || ''}
                  onChange={(e) => setFormData({ ...formData, coverType: e.target.value as CoverType })}
                  label="Cover Type *"
                >
                  <MenuItem value="PAPERBACK">Paperback</MenuItem>
                  <MenuItem value="HARDCOVER">Hardcover</MenuItem>
                </Select>
                {errors.coverType && <FormHelperText>{errors.coverType}</FormHelperText>}
              </FormControl>

              <TextField
                fullWidth
                label="Publisher *"
                value={formData.publisher || ''}
                onChange={(e) => setFormData({ ...formData, publisher: e.target.value })}
                required
                error={!!errors.publisher}
                helperText={errors.publisher}
                placeholder="Publishing company"
              />
            </Box>

            {/* Publication Date and Pages - Side by side */}
            <Box sx={{ display: 'flex', gap: 2, mt: 2 }}>
              <TextField
                fullWidth
                label="Publication Date *"
                type="date"
                value={formData.publicationDate || ''}
                onChange={(e) => setFormData({ ...formData, publicationDate: e.target.value })}
                required
                InputLabelProps={{ shrink: true }}
                error={!!errors.publicationDate}
                helperText={errors.publicationDate}
              />

              <TextField
                fullWidth
                label="Number of Pages *"
                type="number"
                value={formData.numberOfPage || ''}
                onChange={(e) => setFormData({ ...formData, numberOfPage: e.target.value })}
                required
                error={!!errors.numberOfPage}
                helperText={errors.numberOfPage}
                InputProps={{ inputProps: { min: 1 } }}
              />
            </Box>

            {/* Language */}
            <TextField
              fullWidth
              label="Language *"
              value={formData.language || ''}
              onChange={(e) => setFormData({ ...formData, language: e.target.value })}
              margin="normal"
              required
              error={!!errors.language}
              helperText={errors.language}
              placeholder="e.g., English, Vietnamese, Spanish"
            />
          </Box>
        );

      case 'CD':
      case 'LP':
        return (
          <Box>
            <Typography variant="subtitle2" gutterBottom sx={{ mb: 2, fontWeight: 'bold' }}>
              Required {formData.mediaType} Information:
            </Typography>

            {/* Artist */}
            <TextField
              fullWidth
              label="Artist/Band *"
              value={formData.artist || ''}
              onChange={(e) => setFormData({ ...formData, artist: e.target.value })}
              margin="normal"
              required
              error={!!errors.artist}
              helperText={errors.artist}
              placeholder="Name of the artist or band"
            />

            {/* Album and Record Label - Side by side */}
            <Box sx={{ display: 'flex', gap: 2, mt: 2 }}>
              <TextField
                fullWidth
                label="Album Name *"
                value={formData.album || ''}
                onChange={(e) => setFormData({ ...formData, album: e.target.value })}
                required
                error={!!errors.album}
                helperText={errors.album}
                placeholder="Name of the album"
              />

              <TextField
                fullWidth
                label="Record Label *"
                value={formData.recordLabel || ''}
                onChange={(e) => setFormData({ ...formData, recordLabel: e.target.value })}
                required
                error={!!errors.recordLabel}
                helperText={errors.recordLabel}
                placeholder="e.g., Sony Music, Universal"
              />
            </Box>

            {/* Tracklist */}
            <TextField
              fullWidth
              label="Tracklist *"
              value={formData.tracklist || ''}
              onChange={(e) => setFormData({ ...formData, tracklist: e.target.value })}
              margin="normal"
              multiline
              rows={4}
              required
              error={!!errors.tracklist}
              helperText={errors.tracklist}
              placeholder="List all tracks, one per line&#10;1. Track Name One&#10;2. Track Name Two&#10;3. Track Name Three"
            />

            {/* Release Date */}
            <TextField
              fullWidth
              label="Release Date"
              type="date"
              value={formData.releaseDate || ''}
              onChange={(e) => setFormData({ ...formData, releaseDate: e.target.value })}
              margin="normal"
              InputLabelProps={{ shrink: true }}
            />
          </Box>
        );

      case 'DVD':
        return (
          <Box>
            <Typography variant="subtitle2" gutterBottom sx={{ mb: 2, fontWeight: 'bold' }}>
              Required DVD Information:
            </Typography>

            {/* Director */}
            <TextField
              fullWidth
              label="Director *"
              value={formData.director || ''}
              onChange={(e) => setFormData({ ...formData, director: e.target.value })}
              margin="normal"
              required
              error={!!errors.director}
              helperText={errors.director}
              placeholder="Name of the director"
            />

            {/* Studio and Runtime - Side by side */}
            <Box sx={{ display: 'flex', gap: 2, mt: 2 }}>
              <TextField
                fullWidth
                label="Studio *"
                value={formData.studio || ''}
                onChange={(e) => setFormData({ ...formData, studio: e.target.value })}
                required
                error={!!errors.studio}
                helperText={errors.studio}
                placeholder="e.g., Warner Bros, Disney"
              />

              <TextField
                fullWidth
                label="Runtime *"
                value={formData.runtime || ''}
                onChange={(e) => setFormData({ ...formData, runtime: e.target.value })}
                required
                error={!!errors.runtime}
                helperText={errors.runtime}
                placeholder="e.g., 2h 30m, 150 minutes"
              />
            </Box>

            {/* Disc Type and Language - Side by side */}
            <Box sx={{ display: 'flex', gap: 2, mt: 2 }}>
              <FormControl fullWidth required error={!!errors.discType}>
                <InputLabel>Disc Type *</InputLabel>
                <Select
                  value={formData.discType || ''}
                  onChange={(e) => setFormData({ ...formData, discType: e.target.value as DiscType })}
                  label="Disc Type *"
                >
                  <MenuItem value="DVD">DVD</MenuItem>
                  <MenuItem value="BLURAY">Blu-ray</MenuItem>
                  <MenuItem value="HDDVD">HD-DVD</MenuItem>
                </Select>
                {errors.discType && <FormHelperText>{errors.discType}</FormHelperText>}
              </FormControl>

              <TextField
                fullWidth
                label="Language *"
                value={formData.language || ''}
                onChange={(e) => setFormData({ ...formData, language: e.target.value })}
                required
                error={!!errors.language}
                helperText={errors.language}
                placeholder="e.g., English, Vietnamese"
              />
            </Box>

            {/* Subtitles */}
            <TextField
              fullWidth
              label="Subtitles"
              value={formData.subtitle || ''}
              onChange={(e) => setFormData({ ...formData, subtitle: e.target.value })}
              margin="normal"
              placeholder="e.g., English, Spanish, French, Vietnamese"
            />
          </Box>
        );

      default:
        return null;
    }
  };

  if (loading) {
    return (
      <Container maxWidth="lg" sx={{ py: 4 }}>
        <Box display="flex" justifyContent="center">
          <CircularProgress />
        </Box>
      </Container>
    );
  }

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
        <Box sx={{ display: "flex", gap: 2 }}>
          <Button
            variant="contained"
            startIcon={<AddIcon />}
            onClick={handleAddProduct}
          >
            Add Product
          </Button>
          {selectedProducts.length > 0 && (
            <Button
              variant="outlined"
              color="error"
              startIcon={<DeleteIcon />}
              onClick={handleBulkDelete}
              disabled={bulkDeleteLoading}
            >
              Delete Selected ({selectedProducts.length})
            </Button>
          )}
        </Box>
      </Box>

      {/* Search Section */}
      <Box sx={{ mb: 3, display: "flex", gap: 2 }}>
        <TextField
          fullWidth
          label="Search products..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          onKeyPress={(e) => e.key === "Enter" && handleSearch()}
        />
        <Button
          variant="outlined"
          startIcon={<SearchIcon />}
          onClick={handleSearch}
        >
          Search
        </Button>
      </Box>

      {error && (
        <Alert severity="error" sx={{ mb: 3 }}>
          {error}
        </Alert>
      )}

      {/* Products Table */}
      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell padding="checkbox">
                <Checkbox
                  indeterminate={selectedProducts.length > 0 && selectedProducts.length < products.length}
                  checked={products.length > 0 && selectedProducts.length === products.length}
                  onChange={handleSelectAll}
                />
              </TableCell>
              <TableCell>ID</TableCell>
              <TableCell>Title</TableCell>
              <TableCell>Type</TableCell>
              <TableCell>Category</TableCell>
              <TableCell>Price</TableCell>
              <TableCell>Quantity</TableCell>
              <TableCell>Actions</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {products.map((product) => (
              <TableRow key={product.id}>
                <TableCell padding="checkbox">
                  <Checkbox
                    checked={selectedProducts.includes(product.id.toString())}
                    onChange={(event) => handleSelectProduct(product.id.toString(), event.target.checked)}
                  />
                </TableCell>
                <TableCell>{product.id}</TableCell>
                <TableCell>{product.title}</TableCell>
                <TableCell>
                  <Chip
                    label={product.mediaType}
                    size="small"
                    color="primary"
                    variant="outlined"
                  />
                </TableCell>
                <TableCell>{product.category}</TableCell>
                <TableCell>{formatPrice(product.currentPrice)}</TableCell>
                <TableCell>{product.quantity}</TableCell>
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
                    onClick={() => handleDeleteProduct(product.id.toString())}
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

      {/* Pagination */}
      <Box sx={{ display: "flex", justifyContent: "center", mt: 3 }}>
        <Pagination
          count={totalPages}
          page={page}
          onChange={handlePageChange}
          color="primary"
        />
      </Box>

      {/* Add/Edit Product Dialog */}
      <Dialog
        open={openProductDialog}
        onClose={() => setOpenProductDialog(false)}
        maxWidth="md"
        fullWidth
      >
        <DialogTitle>{dialogTitle}</DialogTitle>
        <DialogContent>
          <Box sx={{ pt: 1 }}>
            {/* ========== SECTION 1: BASIC PRODUCT INFORMATION ========== */}
            <Typography variant="h6" gutterBottom sx={{ mt: 2, mb: 3, color: 'primary.main' }}>
              📋 Basic Product Information
            </Typography>

            {/* Title */}
            <TextField
              fullWidth
              label="Product Title *"
              value={formData.title}
              onChange={(e) => setFormData({ ...formData, title: e.target.value })}
              margin="normal"
              required
              placeholder="Enter the product title"
            />

            {/* Category */}
            <TextField
              fullWidth
              label="Category *"
              value={formData.category}
              onChange={(e) => setFormData({ ...formData, category: e.target.value })}
              margin="normal"
              required
              placeholder="e.g., Education, Entertainment, Technology"
            />

            {/* Price Fields - Side by side */}
            <Box sx={{ display: 'flex', gap: 2, mt: 2 }}>
              <TextField
                fullWidth
                label="Value (Cost Price) *"
                type="number"
                value={formData.value}
                onChange={(e) => setFormData({ ...formData, value: e.target.value })}
                required
                placeholder="Original cost price"
                InputProps={{
                  startAdornment: <span style={{ marginRight: '8px' }}>₫</span>,
                }}
              />
              <TextField
                fullWidth
                label="Current Price (Selling Price) *"
                type="number"
                value={formData.currentPrice}
                onChange={(e) => setFormData({ ...formData, currentPrice: e.target.value })}
                required
                placeholder="Selling price (excl. VAT)"
                InputProps={{
                  startAdornment: <span style={{ marginRight: '8px' }}>₫</span>,
                }}
              />
            </Box>

            {/* Quantity and Barcode - Side by side */}
            <Box sx={{ display: 'flex', gap: 2, mt: 2 }}>
              <TextField
                fullWidth
                label="Quantity *"
                type="number"
                value={formData.quantity}
                onChange={(e) => setFormData({ ...formData, quantity: e.target.value })}
                required
                placeholder="Available stock"
                InputProps={{ inputProps: { min: 0 } }}
              />
              <TextField
                fullWidth
                label="Barcode *"
                value={formData.barcode}
                onChange={(e) => setFormData({ ...formData, barcode: e.target.value })}
                required
                placeholder="Product barcode/SKU"
              />
            </Box>

            {/* Product Description */}
            <TextField
              fullWidth
              label="Product Description"
              value={formData.productDescription}
              onChange={(e) => setFormData({ ...formData, productDescription: e.target.value })}
              margin="normal"
              multiline
              rows={3}
              placeholder="Detailed description of the product..."
            />

            {/* Physical Properties - Side by side */}
            <Box sx={{ display: 'flex', gap: 2, mt: 2 }}>
              <TextField
                fullWidth
                label="Dimensions *"
                value={formData.productDimensions}
                onChange={(e) => setFormData({ ...formData, productDimensions: e.target.value })}
                required
                placeholder="e.g., 25cm x 18cm x 2cm"
              />
              <TextField
                fullWidth
                label="Weight (kg) *"
                type="number"
                value={formData.weight}
                onChange={(e) => setFormData({ ...formData, weight: e.target.value })}
                required
                placeholder="Weight in kilograms"
                InputProps={{ inputProps: { min: 0, step: 0.1 } }}
              />
            </Box>

            {/* Image URL */}
            <TextField
              fullWidth
              label="Image URL *"
              value={formData.imageURL}
              onChange={(e) => setFormData({ ...formData, imageURL: e.target.value })}
              margin="normal"
              required
              placeholder="https://example.com/product-image.jpg"
            />

            {/* Warehouse Entry Date and Genre - Side by side */}
            <Box sx={{ display: 'flex', gap: 2, mt: 2 }}>
              <TextField
                fullWidth
                label="Warehouse Entry Date *"
                type="date"
                value={formData.warehouseEntryDate}
                onChange={(e) => setFormData({ ...formData, warehouseEntryDate: e.target.value })}
                required
                InputLabelProps={{ shrink: true }}
              />
              <TextField
                fullWidth
                label="Genre"
                value={formData.genre}
                onChange={(e) => setFormData({ ...formData, genre: e.target.value })}
                placeholder="Optional genre classification"
              />
            </Box>

            {/* ========== SECTION 2: MEDIA TYPE SELECTION ========== */}
            <Typography variant="h6" gutterBottom sx={{ mt: 4, mb: 3, color: 'primary.main' }}>
              🎯 Media Type Selection
            </Typography>

            <FormControl fullWidth margin="normal" required>
              <InputLabel>Media Type *</InputLabel>
              <Select
                value={formData.mediaType}
                onChange={(e) => setFormData({ ...formData, mediaType: e.target.value })}
                label="Media Type *"
              >
                <MenuItem value="BOOK">📚 Book</MenuItem>
                <MenuItem value="CD">💿 CD</MenuItem>
                <MenuItem value="LP">🎵 LP Record</MenuItem>
                <MenuItem value="DVD">📀 DVD</MenuItem>
              </Select>
            </FormControl>

            {/* ========== SECTION 3: PRODUCT-SPECIFIC DETAILS ========== */}
            {formData.mediaType && (
              <>
                <Typography variant="h6" gutterBottom sx={{ mt: 4, mb: 3, color: 'primary.main' }}>
                  🔍 {getMediaTypeLabel(formData.mediaType)} Specific Details
                </Typography>

                {renderMediaSpecificFields()}
              </>
            )}
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpenProductDialog(false)}>Cancel</Button>
          <Button
            variant="contained"
            onClick={handleSaveProduct}
            disabled={saving}
            startIcon={saving ? <CircularProgress size={20} /> : null}
          >
            {saving ? "Saving..." : selectedProduct ? "Update" : "Add"}
          </Button>
        </DialogActions>
      </Dialog>
    </Container>
  );
};

export default ProductManagementPage;