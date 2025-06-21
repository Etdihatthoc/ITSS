// src/components/layout/Footer.tsx
import React from "react";
import { Box, Container, Typography, Link, Divider } from "@mui/material";

const Footer: React.FC = () => {
  return (
    <Box
      component="footer"
      sx={{
        py: 3,
        px: 2,
        mt: "auto",
        backgroundColor: (theme) =>
          theme.palette.mode === "light"
            ? theme.palette.grey[200]
            : theme.palette.grey[800],
      }}
    >
      <Container maxWidth={false}>
        <Divider sx={{ mb: 2 }} />
        <Box
          sx={{
            display: "flex",
            flexDirection: { xs: "column", md: "row" },
            justifyContent: "space-between",
            alignItems: { xs: "center", md: "flex-start" },
          }}
        >
          <Box sx={{ mb: { xs: 2, md: 0 } }}>
            <Typography variant="h6" color="text.primary" gutterBottom>
              AIMS: Internet Media Store
            </Typography>
            <Typography variant="body2" color="text.secondary">
              Your one-stop shop for books, CDs, LPs, and DVDs
            </Typography>
          </Box>

          <Box
            sx={{
              display: "flex",
              flexDirection: "column",
              alignItems: { xs: "center", md: "flex-start" },
            }}
          >
            <Typography variant="subtitle1" color="text.secondary" gutterBottom>
              Quick Links
            </Typography>
            <Link href="/" color="inherit" underline="hover">
              Home
            </Link>
            <Link href="/cart" color="inherit" underline="hover">
              Cart
            </Link>
            <Link color="inherit" underline="hover">
              Terms & Conditions
            </Link>
            <Link color="inherit" underline="hover">
              Privacy Policy
            </Link>
          </Box>

          <Box
            sx={{
              display: "flex",
              flexDirection: "column",
              alignItems: { xs: "center", md: "flex-start" },
              mt: { xs: 2, md: 0 },
            }}
          >
            <Typography variant="subtitle1" color="text.secondary" gutterBottom>
              Contact Us
            </Typography>
            <Typography variant="body2" color="text.secondary">
              Email: contact@aimsstore.com
            </Typography>
            <Typography variant="body2" color="text.secondary">
              Phone: +84 123 456 789
            </Typography>
            <Typography variant="body2" color="text.secondary">
              Address: Hanoi, Vietnam
            </Typography>
          </Box>
        </Box>
        <Box sx={{ mt: 2, textAlign: "center" }}>
          <Typography variant="body2" color="text.secondary">
            {"© "}
            {new Date().getFullYear()}
            {" AIMS Store. All rights reserved."}
          </Typography>
        </Box>
      </Container>
    </Box>
  );
};

export default Footer;
