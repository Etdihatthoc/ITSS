// src/pages/home/HomePage.tsx
import React, { useState, useEffect } from "react";
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
  MenuItem,
  FormControl,
  InputLabel,
  Select,
  Pagination,
} from "@mui/material";
import SearchIcon from "@mui/icons-material/Search";
import productService from "../../services/productService";
import { Product, MediaType } from "../../types/product";
import { useCart } from "../../contexts/CartContext";

const HomePage: React.FC = () => {
  const navigate = useNavigate();
  const [products, setProducts] = useState<Product[]>([]);
  const [loading, setLoading] = useState(true);
  const [totalPages, setTotalPages] = useState(1);
  const [page, setPage] = useState(1);
  const [searchTerm, setSearchTerm] = useState("");
  const [selectedMediaType, setSelectedMediaType] = useState<MediaType | "">(
    ""
  );
  const { addToCart } = useCart();

  useEffect(() => {
    fetchProducts();
  }, [page, selectedMediaType]);

  const fetchProducts = async () => {
    try {
      setLoading(true);
      const params = {
        page,
        limit: 12,
        search: searchTerm,
        mediaType: selectedMediaType || undefined,
      };
      setLoading(true);
      console.log("Fetching products...");
      const response = await productService.getAllProducts();
      console.log("API response:", response.data);
      setProducts(response.data);
    } catch (error) {
      console.error("Failed to fetch products:", error);
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = () => {
    setPage(1);
    fetchProducts();
  };

  const handleAddToCart = (product: Product, quantity: number) => {
    addToCart(product, quantity);
  };

  const handlePageChange = (_: React.ChangeEvent<unknown>, value: number) => {
    setPage(value);
  };

  const handleViewProduct = (id: string) => {
    navigate(`/product/${id}`);
  };

  const formatPrice = (price: number) => {
    return new Intl.NumberFormat("vi-VN", {
      style: "currency",
      currency: "VND",
    }).format(price);
  };

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
        }}
      >
        <Box sx={{ flex: 1 }}>
          <TextField
            fullWidth
            placeholder="Search products..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            onKeyPress={(e) => e.key === "Enter" && handleSearch()}
            InputProps={{
              endAdornment: (
                <Button
                  variant="contained"
                  onClick={handleSearch}
                  startIcon={<SearchIcon />}
                >
                  Search
                </Button>
              ),
            }}
          />
        </Box>
        <Box sx={{ flex: 1 }}>
          <FormControl fullWidth>
            <InputLabel>Filter by Media Type</InputLabel>
            <Select
              value={selectedMediaType}
              onChange={(e) => {
                setSelectedMediaType(e.target.value as MediaType | "");
                setPage(1);
              }}
              label="Filter by Media Type"
            >
              <MenuItem value="">All Media Types</MenuItem>
              {Object.values(MediaType).map((type) => (
                <MenuItem key={type} value={type}>
                  {getMediaTypeLabel(type)}
                </MenuItem>
              ))}
            </Select>
          </FormControl>
        </Box>
      </Box>

      {loading ? (
        <Box sx={{ display: "flex", justifyContent: "center", py: 8 }}>
          <CircularProgress />
        </Box>
      ) : (
        <>
          {products.length === 0 ? (
            <Box sx={{ py: 8, textAlign: "center" }}>
              <Typography variant="h6" color="text.secondary">
                No products found.
              </Typography>
            </Box>
          ) : (
            <Box sx={{ display: "flex", flexWrap: "wrap", gap: 3 }}>
              {products.map((product) => (
                <Box
                  key={product.id}
                  sx={{
                    width: {
                      xs: "100%",
                      sm: "calc(50% - 16px)",
                      md: "calc(33.33% - 16px)",
                      lg: "calc(25% - 16px)",
                    },
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
                        {getMediaTypeLabel(product.mediaType)}
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
        </>
      )}
    </Container>
  );
};

export default HomePage;
