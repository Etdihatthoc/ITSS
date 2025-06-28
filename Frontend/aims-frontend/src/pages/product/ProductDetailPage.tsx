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
import productService from "../../services/productService";
import { useCart } from "../../contexts/CartContext";
import { Product, Book, CD, LP, DVD } from "../../types/product";

// Type guards for different product types
function isBook(product: Product): product is Book {
  return product.productType === "BOOK";
}

function isCD(product: Product): product is CD {
  return product.productType === "CD";
}

function isLP(product: Product): product is LP {
  return product.productType === "LP";
}

function isDVD(product: Product): product is DVD {
  return product.productType === "DVD";
}

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

  const handleAddToCart = () => {
    if (product) {
      addToCart(product, quantity);
    }
  };

  const formatPrice = (price: number) =>
    new Intl.NumberFormat("vi-VN", {
      style: "currency",
      currency: "VND",
    }).format(price);

  // Render media-specific details based on product type
  const renderMediaSpecificDetails = (product: Product) => {
    if (isBook(product)) {
      return (
        <>
          <ListItem>
            <ListItemText primary="Author" secondary={product.author || "N/A"} />
          </ListItem>
          <ListItem>
            <ListItemText primary="Cover Type" secondary={product.coverType || "N/A"} />
          </ListItem>
          <ListItem>
            <ListItemText primary="Publisher" secondary={product.publisher || "N/A"} />
          </ListItem>
          <ListItem>
            <ListItemText
              primary="Publication Date"
              secondary={product.publicationDate ? new Date(product.publicationDate).toLocaleDateString() : "N/A"}
            />
          </ListItem>
          <ListItem>
            <ListItemText primary="Number of Pages" secondary={product.numberOfPage ?? "N/A"} />
          </ListItem>
          <ListItem>
            <ListItemText primary="Language" secondary={product.language || "N/A"} />
          </ListItem>
        </>
      );
    }

    if (isCD(product) || isLP(product)) {
      return (
        <>
          <ListItem>
            <ListItemText primary="Album" secondary={product.album || "N/A"} />
          </ListItem>
          <ListItem>
            <ListItemText primary="Artist" secondary={product.artist || "N/A"} />
          </ListItem>
          <ListItem>
            <ListItemText primary="Record Label" secondary={product.recordLabel || "N/A"} />
          </ListItem>
          <ListItem>
            <ListItemText
              primary="Release Date"
              secondary={product.releaseDate ? new Date(product.releaseDate).toLocaleDateString() : "N/A"}
            />
          </ListItem>
          <ListItem>
            <ListItemText
              primary="Tracklist"
              secondary={
                product.tracklist
                  ? (
                      <ul style={{ margin: 0, paddingLeft: 16 }}>
                        {product.tracklist.split("\n").map((track: string, idx: number) => (
                          <li key={idx}>{track}</li>
                        ))}
                      </ul>
                    )
                  : "N/A"
              }
            />
          </ListItem>
        </>
      );
    }

    if (isDVD(product)) {
      return (
        <>
          <ListItem>
            <ListItemText primary="Director" secondary={product.director || "N/A"} />
          </ListItem>
          <ListItem>
            <ListItemText primary="Studio" secondary={product.studio || "N/A"} />
          </ListItem>
          <ListItem>
            <ListItemText primary="Runtime" secondary={product.runtime || "N/A"} />
          </ListItem>
          <ListItem>
            <ListItemText primary="Disc Type" secondary={product.discType || "N/A"} />
          </ListItem>
          <ListItem>
            <ListItemText primary="Subtitle" secondary={product.subtitle || "N/A"} />
          </ListItem>
          <ListItem>
            <ListItemText primary="Language" secondary={product.language || "N/A"} />
          </ListItem>
          <ListItem>
            <ListItemText
              primary="Release Date"
              secondary={product.releaseDate ? new Date(product.releaseDate).toLocaleDateString() : "N/A"}
            />
          </ListItem>
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
            <Chip label={product.productType} color="primary" />
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
                  secondary={product.warehouseEntryDate ? new Date(product.warehouseEntryDate).toLocaleDateString() : "N/A"}
                />
              </ListItem>
              {product.genre && (
                <ListItem>
                  <ListItemText primary="Genre" secondary={product.genre} />
                </ListItem>
              )}
              {product.rushOrderEligible !== undefined && (
                <ListItem>
                  <ListItemText 
                    primary="Rush Order Eligible" 
                    secondary={product.rushOrderEligible ? "Yes" : "No"} 
                  />
                </ListItem>
              )}
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