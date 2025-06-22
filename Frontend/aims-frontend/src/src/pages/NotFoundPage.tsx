// src/pages/NotFoundPage.tsx
import React from "react";
import { Box, Typography, Button, Container } from "@mui/material";
import { useNavigate } from "react-router-dom";

const NotFoundPage: React.FC = () => {
  const navigate = useNavigate();

  return (
    <Container maxWidth={false} sx={{ mt: 4, mb: 4 }}>
      <Box
        sx={{
          display: "flex",
          flexDirection: "column",
          alignItems: "center",
          justifyContent: "center",
          minHeight: "70vh",
          textAlign: "center",
          py: 8,
        }}
      >
        <Typography
          variant="h1"
          sx={{
            fontSize: { xs: "6rem", md: "10rem" },
            fontWeight: 700,
            color: "primary.main",
            mb: 2,
          }}
        >
          404
        </Typography>

        <Typography
          variant="h4"
          sx={{
            mb: 3,
            fontWeight: 500,
            color: "text.primary",
          }}
        >
          Page Not Found
        </Typography>

        <Typography
          variant="body1"
          sx={{
            mb: 4,
            maxWidth: "600px",
            color: "text.secondary",
          }}
        >
          The page you are looking for might have been removed, had its name
          changed, or is temporarily unavailable.
        </Typography>

        <Box sx={{ display: "flex", gap: 2 }}>
          <Button
            variant="contained"
            color="primary"
            size="large"
            onClick={() => navigate("/")}
          >
            Go to Homepage
          </Button>

          <Button
            variant="outlined"
            color="primary"
            size="large"
            onClick={() => navigate(-1)}
          >
            Go Back
          </Button>
        </Box>
      </Box>
    </Container>
  );
};

export default NotFoundPage;
