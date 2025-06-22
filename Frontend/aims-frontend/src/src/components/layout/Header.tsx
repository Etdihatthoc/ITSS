// src/components/layout/Header.tsx
import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import {
  AppBar,
  Toolbar,
  Typography,
  Button,
  IconButton,
  Badge,
  Menu,
  MenuItem,
  Box,
  TextField,
  InputAdornment,
} from "@mui/material";
import {
  Menu as MenuIcon,
  ShoppingCart,
  Search as SearchIcon,
  Person,
} from "@mui/icons-material";
import { useCart } from "../../contexts/CartContext";
import authService from "../../services/authService";
import { UserRole } from "../../types/user";

interface HeaderProps {
  toggleSidebar: () => void;
}

const Header: React.FC<HeaderProps> = ({ toggleSidebar }) => {
  const { cart } = useCart();
  const navigate = useNavigate();
  const [searchTerm, setSearchTerm] = useState("");
  const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);
  const user = authService.getCurrentUser();
  const isAuthenticated = authService.isAuthenticated();

  const handleMenu = (event: React.MouseEvent<HTMLElement>) => {
    setAnchorEl(event.currentTarget);
  };

  const handleClose = () => {
    setAnchorEl(null);
  };

  const handleLogout = () => {
    authService.logout();
    handleClose();
    navigate("/login");
  };

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    if (searchTerm.trim()) {
      navigate(`/?search=${encodeURIComponent(searchTerm)}`);
    }
  };

  return (
    <AppBar position="static">
      <Toolbar>
        <IconButton
          edge="start"
          color="inherit"
          aria-label="menu"
          onClick={toggleSidebar}
          sx={{ mr: 2 }}
        >
          <MenuIcon />
        </IconButton>

        <Typography
          variant="h6"
          component={Link}
          to="/"
          sx={{ textDecoration: "none", color: "inherit", flexGrow: 0 }}
        >
          AIMS Store
        </Typography>

        <Box
          component="form"
          onSubmit={handleSearch}
          sx={{
            mx: 2,
            flexGrow: 1,
            display: { xs: "none", md: "block" },
          }}
        >
          <TextField
            size="small"
            placeholder="Search products..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            sx={{
              bgcolor: "rgba(255, 255, 255, 0.15)",
              borderRadius: 1,
              "& .MuiOutlinedInput-root": {
                color: "white",
                "& fieldset": { border: "none" },
              },
              width: "100%",
              maxWidth: 500,
            }}
            InputProps={{
              endAdornment: (
                <InputAdornment position="end">
                  <IconButton type="submit" edge="end" sx={{ color: "white" }}>
                    <SearchIcon />
                  </IconButton>
                </InputAdornment>
              ),
            }}
          />
        </Box>

        <Button
          color="inherit"
          component={Link}
          to="/cart"
          startIcon={
            <Badge badgeContent={cart?.totalItems || 0} color="error" max={99}>
              <ShoppingCart />
            </Badge>
          }
          sx={{ mr: 1 }}
        >
          Cart
        </Button>

        {isAuthenticated ? (
          <>
            <IconButton
              size="large"
              edge="end"
              color="inherit"
              aria-label="account"
              aria-controls="menu-appbar"
              aria-haspopup="true"
              onClick={handleMenu}
            >
              <Person />
            </IconButton>
            <Menu
              id="menu-appbar"
              anchorEl={anchorEl}
              anchorOrigin={{
                vertical: "bottom",
                horizontal: "right",
              }}
              keepMounted
              transformOrigin={{
                vertical: "top",
                horizontal: "right",
              }}
              open={Boolean(anchorEl)}
              onClose={handleClose}
            >
              <MenuItem disabled>
                <Typography variant="body2">{user?.email}</Typography>
              </MenuItem>
              {user?.roles.includes(UserRole.ADMIN) && (
                <MenuItem
                  onClick={() => {
                    navigate("/admin/users");
                    handleClose();
                  }}
                >
                  User Management
                </MenuItem>
              )}
              {user?.roles.includes(UserRole.PRODUCT_MANAGER) && (
                <MenuItem
                  onClick={() => {
                    navigate("/admin/products");
                    handleClose();
                  }}
                >
                  Product Management
                </MenuItem>
              )}
              {user?.roles.includes(UserRole.PRODUCT_MANAGER) && (
                <MenuItem
                  onClick={() => {
                    navigate("/admin/orders");
                    handleClose();
                  }}
                >
                  Order Management
                </MenuItem>
              )}
              <MenuItem onClick={handleLogout}>Logout</MenuItem>
            </Menu>
          </>
        ) : (
          <Button color="inherit" component={Link} to="/login">
            Login
          </Button>
        )}
      </Toolbar>
    </AppBar>
  );
};

export default Header;
