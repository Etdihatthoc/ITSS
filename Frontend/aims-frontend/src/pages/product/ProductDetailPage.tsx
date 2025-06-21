// src/pages/product/ProductDetailPage.tsx
import React, { useState, useEffect } from "react";
import { useParams } from "react-router-dom";
import {
  Container,
  Typography,
  Box,
  Button,
  Divider,
  List,
  ListItem,
  ListItemText,
  TextField,
  CircularProgress,
  Chip,
  Paper,
} from "@mui/material";
import { MediaType, Product, Book, CD, LP, DVD } from "../../types/product";
import productService from "../../services/productService";
import { useCart } from "../../contexts/CartContext";

// Type guard functions to check product types
const isBook = (product: Product): product is Book => {
  return product.mediaType === MediaType.BOOK;
};

const isCD = (product: Product): product is CD => {
  return product.mediaType === MediaType.CD;
};

const isLP = (product: Product): product is LP => {
  return product.mediaType === MediaType.LP;
};

const isDVD = (product: Product): product is DVD => {
  return product.mediaType === MediaType.DVD;
};

const ProductDetailPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const [product, setProduct] = useState<Product | null>(null);
  const [loading, setLoading] = useState(true);
  const [quantity, setQuantity] = useState(1);
  const { addToCart } = useCart();

  useEffect(() => {
    const fetchProduct = async () => {
      try {
        setLoading(true);
        if (id) {
          const response = await productService.getProductById(id);
          setProduct(response.data);
        }
      } catch (error) {
        console.error("Failed to fetch product details:", error);
      } finally {
        setLoading(false);
      }
    };

    fetchProduct();
  }, [id]);

  const handleAddToCart = async () => {
    if (product) {
      addToCart(product, quantity);
    }
  };

  const formatPrice = (price: number) => {
    return new Intl.NumberFormat("vi-VN", {
      style: "currency",
      currency: "VND",
    }).format(price);
  };

  const renderMediaSpecificDetails = (product: Product) => {
    if (isBook(product)) {
      return (
        <>
          <ListItem>
            <ListItemText
              primary="Authors"
              secondary={product.authors.join(", ")}
            />
          </ListItem>
          <ListItem>
            <ListItemText primary="Cover Type" secondary={product.coverType} />
          </ListItem>
          <ListItem>
            <ListItemText primary="Publisher" secondary={product.publisher} />
          </ListItem>
          <ListItem>
            <ListItemText
              primary="Publication Date"
              secondary={new Date(product.publicationDate).toLocaleDateString()}
            />
          </ListItem>
          {product.pages && (
            <ListItem>
              <ListItemText primary="Pages" secondary={product.pages} />
            </ListItem>
          )}
          {product.language && (
            <ListItem>
              <ListItemText primary="Language" secondary={product.language} />
            </ListItem>
          )}
          {product.genre && (
            <ListItem>
              <ListItemText primary="Genre" secondary={product.genre} />
            </ListItem>
          )}
        </>
      );
    } else if (isCD(product) || isLP(product)) {
      return (
        <>
          <ListItem>
            <ListItemText
              primary="Artists"
              secondary={product.artists.join(", ")}
            />
          </ListItem>
          <ListItem>
            <ListItemText
              primary="Record Label"
              secondary={product.recordLabel}
            />
          </ListItem>
          <ListItem>
            <ListItemText primary="Genre" secondary={product.genre} />
          </ListItem>
          {product.releaseDate && (
            <ListItem>
              <ListItemText
                primary="Release Date"
                secondary={new Date(product.releaseDate).toLocaleDateString()}
              />
            </ListItem>
          )}
          <ListItem>
            <ListItemText
              primary="Tracklist"
              secondary={
                <ul style={{ margin: 0, paddingLeft: 16 }}>
                  {product.tracklist.map((track, index) => (
                    <li key={index}>{track}</li>
                  ))}
                </ul>
              }
            />
          </ListItem>
        </>
      );
    } else if (isDVD(product)) {
      return (
        <>
          <ListItem>
            <ListItemText primary="Disc Type" secondary={product.discType} />
          </ListItem>
          <ListItem>
            <ListItemText primary="Director" secondary={product.director} />
          </ListItem>
          <ListItem>
            <ListItemText
              primary="Runtime"
              secondary={`${product.runtime} minutes`}
            />
          </ListItem>
          <ListItem>
            <ListItemText primary="Studio" secondary={product.studio} />
          </ListItem>
          <ListItem>
            <ListItemText
              primary="Languages"
              secondary={product.language.join(", ")}
            />
          </ListItem>
          <ListItem>
            <ListItemText
              primary="Subtitles"
              secondary={product.subtitles.join(", ")}
            />
          </ListItem>
          {product.releaseDate && (
            <ListItem>
              <ListItemText
                primary="Release Date"
                secondary={new Date(product.releaseDate).toLocaleDateString()}
              />
            </ListItem>
          )}
          {product.genre && (
            <ListItem>
              <ListItemText primary="Genre" secondary={product.genre} />
            </ListItem>
          )}
        </>
      );
    }
    return null;
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

  if (!product) {
    return (
      <Container sx={{ py: 4 }}>
        <Typography variant="h5" color="error">
          Product not found
        </Typography>
      </Container>
    );
  }

  return (
    <Container sx={{ py: 4 }}>
      <Box
        sx={{
          display: "flex",
          flexDirection: { xs: "column", md: "row" },
          gap: 4,
        }}
      >
        {/* Product Image - Left Column */}
        <Box sx={{ flex: { xs: "1 1 100%", md: "0 0 40%" } }}>
          <Box
            component="img"
            sx={{
              width: "100%",
              borderRadius: 2,
              boxShadow: 3,
            }}
            src={product.imageURL || "/placeholder.jpg"}
            alt={product.title}
          />
        </Box>

        {/* Product Details - Right Column */}
        <Box sx={{ flex: { xs: "1 1 100%", md: "0 0 60%" } }}>
          <Typography variant="h4" component="h1" gutterBottom>
            {product.title}
          </Typography>

          <Box sx={{ display: "flex", gap: 1, mb: 2 }}>
            <Chip label={product.mediaType} color="primary" />
            <Chip label={product.category} variant="outlined" />
          </Box>

          <Typography variant="h5" color="primary" sx={{ mb: 2 }}>
            {formatPrice(product.currentPrice)}
          </Typography>

          <Typography variant="body1" sx={{ mb: 3 }}>
            {product.productDescription || "No description available."}
          </Typography>

          <Box sx={{ display: "flex", alignItems: "center", mb: 3 }}>
            <Typography variant="body2" sx={{ mr: 2 }}>
              Available: {product.quantity} in stock
            </Typography>

            <TextField
              type="number"
              label="Quantity"
              value={quantity}
              onChange={(e) =>
                setQuantity(Math.max(1, parseInt(e.target.value) || 1))
              }
              InputProps={{ inputProps: { min: 1, max: product.quantity } }}
              size="small"
              sx={{ width: 100, mr: 2 }}
            />

            <Button
              variant="contained"
              color="primary"
              onClick={handleAddToCart}
              disabled={product.quantity <= 0}
              size="large"
            >
              Add to Cart
            </Button>
          </Box>

          <Divider sx={{ my: 3 }} />

          <Paper elevation={2} sx={{ p: 2, mb: 3 }}>
            <Typography variant="h6" gutterBottom>
              Product Specifications
            </Typography>

            <List dense>
              <ListItem>
                <ListItemText primary="Category" secondary={product.category} />
              </ListItem>
              <ListItem>
                <ListItemText primary="Barcode" secondary={product.barcode} />
              </ListItem>
              <ListItem>
                <ListItemText
                  primary="Dimensions"
                  secondary={product.productDimensions}
                />
              </ListItem>
              <ListItem>
                <ListItemText
                  primary="Weight"
                  secondary={`${product.weight} kg`}
                />
              </ListItem>
              <ListItem>
                <ListItemText
                  primary="Warehouse Entry Date"
                  secondary={new Date(
                    product.warehouseEntryDate
                  ).toLocaleDateString()}
                />
              </ListItem>
            </List>
          </Paper>

          <Paper elevation={2} sx={{ p: 2 }}>
            <Typography variant="h6" gutterBottom>
              Media Details
            </Typography>

            <List dense>{renderMediaSpecificDetails(product)}</List>
          </Paper>
        </Box>
      </Box>
    </Container>
  );
};

export default ProductDetailPage;
