import React, { useState, useEffect, useCallback } from "react";
import { useNavigate } from "react-router-dom";
import {
  Container,
  Typography,
  Box,
  Card,
  CardMedia,
  CardContent,
  CardActions,
  Button,
  CircularProgress,
  TextField,
  Pagination,
  IconButton,
  Popover,
  Divider,
  Stack,
} from "@mui/material";
import SearchIcon from "@mui/icons-material/Search";
import SortIcon from "@mui/icons-material/Sort";
import productService from "../../services/productService";
import { Product, MediaType } from "../../types/product";
import { useCart } from "../../contexts/CartContext";

const sortOptions = [
  {
    group: "Price",
    options: [
      { label: "High to low", sortBy: "currentPrice", sortDirection: "desc" },
      { label: "Low to high", sortBy: "currentPrice", sortDirection: "asc" },
    ],
  },
  {
    group: "Title",
    options: [
      { label: "A to Z", sortBy: "title", sortDirection: "asc" },
      { label: "Z to A", sortBy: "title", sortDirection: "desc" },
    ],
  },
  {
    group: "Date added",
    options: [
      { label: "Newest first", sortBy: "warehouseEntryDate", sortDirection: "desc" },
      { label: "Oldest first", sortBy: "warehouseEntryDate", sortDirection: "asc" },
    ],
  },
];

type SortBy = "title" | "currentPrice" | "warehouseEntryDate" | undefined;
type SortDirection = "asc" | "desc" | undefined;

const HomePage: React.FC = () => {
  const navigate = useNavigate();
  const [products, setProducts] = useState<Product[]>([]);
  const [loading, setLoading] = useState(true);
  const [totalPages, setTotalPages] = useState(1);
  const [page, setPage] = useState(1);
  const [totalItems, setTotalItems] = useState(0);
  const [isSearchMode, setIsSearchMode] = useState(false);
  const { addToCart } = useCart();

  // Filter states
  const [searchTerm, setSearchTerm] = useState("");
  const [selectedMediaType, setSelectedMediaType] = useState<MediaType | "">("");
  const [sortBy, setSortBy] = useState<SortBy>(undefined);
  const [sortDirection, setSortDirection] = useState<SortDirection>(undefined);

  // Popup state
  const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);

  // Fetch sản phẩm với search/filter
  const fetchProductsWithFilters = useCallback(async (pageNumber: number = 1) => {
    try {
      setLoading(true);
      // Luôn gọi search API nếu có sort/filter
      if (searchTerm.trim() || sortBy || sortDirection) {
        const params: any = {
          page: pageNumber,
          size: 20,
          search: searchTerm.trim() || undefined,
        };
        if (sortBy) params.sortBy = sortBy;
        if (sortDirection) params.sortDirection = sortDirection;
        const response = await productService.searchProducts(params);
        setProducts(response.data.data);
        setTotalPages(response.data.totalPages);
        setTotalItems(response.data.total);
        setIsSearchMode(true);
      } else {
        const response = await productService.getProducts(pageNumber, 20);
        setProducts(response.data.data);
        setTotalPages(response.data.totalPages);
        setTotalItems(response.data.total);
        setIsSearchMode(false);
      }
    } catch (error) {
      console.error("Failed to fetch products:", error);
    } finally {
      setLoading(false);
    }
  }, [searchTerm, sortBy, sortDirection]);

  useEffect(() => {
    fetchProductsWithFilters(1);
  }, []);

  const handlePageChange = useCallback((_: React.ChangeEvent<unknown>, value: number) => {
    setPage(value);
    fetchProductsWithFilters(value);
  }, [fetchProductsWithFilters]);

  const handleApplyFilters = useCallback(() => {
    setPage(1);
    fetchProductsWithFilters(1);
  }, [fetchProductsWithFilters]);

  const handleResetFilters = useCallback(() => {
    setSearchTerm("");
    setSortBy(undefined);
    setSortDirection(undefined);
    setPage(1);
    setIsSearchMode(false);
    fetchProductsWithFilters(1);
  }, [fetchProductsWithFilters]);

  const handleAddToCart = useCallback((product: Product, quantity: number) => {
    addToCart(product, quantity);
  }, [addToCart]);

  const handleViewProduct = useCallback((id: number) => {
    navigate(`/product/${id}`);
  }, [navigate]);

  const formatPrice = useCallback((price: number) => {
    return new Intl.NumberFormat("vi-VN", {
      style: "currency",
      currency: "VND",
    }).format(price);
  }, []);

  const getMediaTypeLabel = useCallback((type: string) => {
    const labels: Record<string, string> = {
      BOOK: "Book",
      CD: "CD",
      LP: "LP Record",
      DVD: "DVD",
    };
    return labels[type] || type;
  }, []);

  // Popup handlers
  const handleSortClick = (event: React.MouseEvent<HTMLElement>) => {
    setAnchorEl(event.currentTarget);
  };
  const handleSortClose = () => {
    setAnchorEl(null);
  };
  const handleSortSelect = (sortByValue: SortBy, sortDirectionValue: SortDirection) => {
    setSortBy(sortByValue);
    setSortDirection(sortDirectionValue);
    setAnchorEl(null);
  };

  const getSortLabel = useCallback(() => {
    if (sortBy === "currentPrice" && sortDirection === "asc") return "Price: Low to High";
    if (sortBy === "currentPrice" && sortDirection === "desc") return "Price: High to Low";
    if (sortBy === "title" && sortDirection === "asc") return "Title: A to Z";
    if (sortBy === "title" && sortDirection === "desc") return "Title: Z to A";
    if (sortBy === "warehouseEntryDate" && sortDirection === "desc") return "Date: Newest first";
    if (sortBy === "warehouseEntryDate" && sortDirection === "asc") return "Date: Oldest first";
    return "Sort";
  }, [sortBy, sortDirection]);

  return (
    <Container maxWidth={false} sx={{ py: 4, px: 4 }}>
      <Typography variant="h4" component="h1" gutterBottom>
        AIMS Internet Media Store
      </Typography>

      {/* Search and Filter Controls */}
      <Box
        sx={{
          mb: 4,
          display: "flex",
          flexDirection: { xs: "column", md: "row" },
          gap: 2,
          alignItems: "flex-end",
        }}
      >
        <Box sx={{ flex: 1 }}>
          <TextField
            fullWidth
            placeholder="Search products by title..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            onKeyPress={(e) => e.key === "Enter" && handleApplyFilters()}
            label="Search"
          />
        </Box>
        <Box sx={{ flex: 1 }}>
          <IconButton
            aria-label="sort"
            onClick={handleSortClick}
            sx={{ border: 1, borderColor: 'divider', borderRadius: 2, px: 2, py: 1, width: '100%' }}
          >
            <SortIcon sx={{ mr: 1 }} />
            <Typography variant="body1" sx={{ fontWeight: 500 }}>{getSortLabel()}</Typography>
          </IconButton>
          <Popover
            open={Boolean(anchorEl)}
            anchorEl={anchorEl}
            onClose={handleSortClose}
            anchorOrigin={{ vertical: 'bottom', horizontal: 'left' }}
          >
            <Box sx={{ p: 2, minWidth: 220 }}>
              {sortOptions.map((group, idx) => (
                <Box key={group.group}>
                  <Typography variant="subtitle2" sx={{ mb: 1 }}>{group.group}</Typography>
                  <Stack direction="row" spacing={2} sx={{ mb: 1 }}>
                    {group.options.map(opt => (
                      <Button
                        key={opt.label}
                        variant={sortBy === opt.sortBy && sortDirection === opt.sortDirection ? "contained" : "outlined"}
                        onClick={() => handleSortSelect(opt.sortBy as SortBy, opt.sortDirection as SortDirection)}
                        sx={{ minWidth: 90 }}
                      >
                        {opt.label}
                      </Button>
                    ))}
                  </Stack>
                  {idx < sortOptions.length - 1 && <Divider sx={{ my: 1 }} />}
                </Box>
              ))}
            </Box>
          </Popover>
        </Box>
        <Box sx={{ display: "flex", gap: 1 }}>
          <Button
            variant="contained"
            onClick={handleApplyFilters}
            sx={{ minWidth: 100 }}
          >
            Apply
          </Button>
          <Button
            variant="outlined"
            onClick={handleResetFilters}
            disabled={!isSearchMode}
            sx={{ minWidth: 100 }}
          >
            Reset
          </Button>
        </Box>
      </Box>

      {/* Results summary */}
      {isSearchMode && (
        <Box sx={{ mb: 3 }}>
          <Typography variant="body2" color="text.secondary">
            Found {totalItems} product{totalItems !== 1 ? 's' : ''}
            {searchTerm && ` matching "${searchTerm}"`}
            {selectedMediaType && ` in ${getMediaTypeLabel(selectedMediaType)} category`}
            {sortBy && ` sorted by ${getSortLabel()}`}
          </Typography>
        </Box>
      )}

      {loading ? (
        <Box sx={{ display: "flex", justifyContent: "center", py: 8 }}>
          <CircularProgress />
        </Box>
      ) : (
        <>
          {products.length === 0 ? (
            <Box sx={{ py: 8, textAlign: "center" }}>
              <Typography variant="h6" color="text.secondary">
                {isSearchMode 
                  ? "No products found matching your criteria." 
                  : "No products available."}
              </Typography>
              {isSearchMode && (
                <Button 
                  variant="outlined" 
                  onClick={handleResetFilters}
                  sx={{ mt: 2 }}
                >
                  Clear Filters
                </Button>
              )}
            </Box>
          ) : (
            <Box sx={{ display: "flex", flexWrap: "wrap", gap: 2 }}>
              {products.map((product) => (
                <Box
                  key={product.id}
                  sx={{
                    width: "calc(25% - 16px)",
                    mb: 2,
                  }}
                >
                  <Card
                    sx={{
                      height: "100%",
                      display: "flex",
                      flexDirection: "column",
                    }}
                  >
                    <CardMedia
                      component="img"
                      height="200"
                      image={product.imageURL || "/placeholder.jpg"}
                      alt={product.title || "Product Image"}
                      sx={{ objectFit: "contain", p: 1 }}
                    />
                    <CardContent sx={{ flexGrow: 1 }}>
                      <Typography
                        gutterBottom
                        variant="h6"
                        component="div"
                        sx={{
                          overflow: "hidden",
                          textOverflow: "ellipsis",
                          display: "-webkit-box",
                          WebkitLineClamp: 2,
                          WebkitBoxOrient: "vertical",
                          height: "3em",
                        }}
                      >
                        {product.title}
                      </Typography>
                      <Typography variant="body2" color="text.secondary">
                        {getMediaTypeLabel(product.productType)}
                      </Typography>
                      <Typography variant="h6" color="primary" sx={{ mt: 2 }}>
                        {formatPrice(product.currentPrice)}
                      </Typography>
                    </CardContent>
                    <CardActions>
                      <Button
                        size="small"
                        onClick={() => handleViewProduct(product.id)}
                      >
                        Details
                      </Button>
                      <Button
                        size="small"
                        variant="contained"
                        onClick={() => handleAddToCart(product, 1)}
                        disabled={product.quantity <= 0}
                        sx={{ ml: "auto" }}
                      >
                        Add to Cart
                      </Button>
                    </CardActions>
                  </Card>
                </Box>
              ))}
            </Box>
          )}

          {totalPages > 1 && (
            <Box
              sx={{
                display: "flex",
                justifyContent: "center",
                mt: 4,
              }}
            >
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
    </Container>
  );
};

export default HomePage;