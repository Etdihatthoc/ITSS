// src/components/layout/Sidebar.tsx
import React from "react";
import { Link, useLocation } from "react-router-dom";
import {
  Drawer,
  List,
  ListItem,
  ListItemButton,
  ListItemIcon,
  ListItemText,
  Divider,
  Box,
  Typography,
} from "@mui/material";
import {
  Home,
  Category,
  Book,
  Album,
  Movie,
  ShoppingCart,
  People,
  Receipt,
} from "@mui/icons-material";
import authService from "../../services/authService";
import { UserRole } from "../../types/user";

interface SidebarProps {
  open: boolean;
  onClose: () => void;
}

const Sidebar: React.FC<SidebarProps> = ({ open, onClose }) => {
  const location = useLocation();
  const user = authService.getCurrentUser();

  const isAdmin = user?.roles.includes(UserRole.ADMIN);
  const isProductManager = user?.roles.includes(UserRole.PRODUCT_MANAGER);

  const drawerWidth = 240;

  return (
    <Drawer
      variant="temporary"
      open={open}
      onClose={onClose}
      ModalProps={{
        keepMounted: true, // Better open performance on mobile
      }}
      sx={{
        width: drawerWidth,
        flexShrink: 0,
        "& .MuiDrawer-paper": {
          width: drawerWidth,
          boxSizing: "border-box",
        },
      }}
    >
      <Box sx={{ p: 2 }}>
        <Typography variant="h6" component="div">
          AIMS Store
        </Typography>
      </Box>
      <Divider />
      <List>
        <ListItem disablePadding>
          <ListItemButton
            component={Link}
            to="/"
            selected={location.pathname === "/"}
            onClick={onClose}
          >
            <ListItemIcon>
              <Home />
            </ListItemIcon>
            <ListItemText primary="Home" />
          </ListItemButton>
        </ListItem>

        <ListItem disablePadding>
          <ListItemButton
            component={Link}
            to="/?mediaType=BOOK"
            selected={location.search.includes("mediaType=BOOK")}
            onClick={onClose}
          >
            <ListItemIcon>
              <Book />
            </ListItemIcon>
            <ListItemText primary="Books" />
          </ListItemButton>
        </ListItem>

        <ListItem disablePadding>
          <ListItemButton
            component={Link}
            to="/?mediaType=CD"
            selected={location.search.includes("mediaType=CD")}
            onClick={onClose}
          >
            <ListItemIcon>
              <Album />
            </ListItemIcon>
            <ListItemText primary="CDs" />
          </ListItemButton>
        </ListItem>

        <ListItem disablePadding>
          <ListItemButton
            component={Link}
            to="/?mediaType=LP"
            selected={location.search.includes("mediaType=LP")}
            onClick={onClose}
          >
            <ListItemIcon>
              <Album />
            </ListItemIcon>
            <ListItemText primary="LP Records" />
          </ListItemButton>
        </ListItem>

        <ListItem disablePadding>
          <ListItemButton
            component={Link}
            to="/?mediaType=DVD"
            selected={location.search.includes("mediaType=DVD")}
            onClick={onClose}
          >
            <ListItemIcon>
              <Movie />
            </ListItemIcon>
            <ListItemText primary="DVDs" />
          </ListItemButton>
        </ListItem>

        <ListItem disablePadding>
          <ListItemButton
            component={Link}
            to="/cart"
            selected={location.pathname === "/cart"}
            onClick={onClose}
          >
            <ListItemIcon>
              <ShoppingCart />
            </ListItemIcon>
            <ListItemText primary="Cart" />
          </ListItemButton>
        </ListItem>
      </List>

      {(isAdmin || isProductManager) && (
        <>
          <Divider />
          <List>
            <ListItem disablePadding>
              <ListItemButton
                component={Link}
                to="/admin/products"
                selected={location.pathname === "/admin/products"}
                onClick={onClose}
              >
                <ListItemIcon>
                  <Category />
                </ListItemIcon>
                <ListItemText primary="Product Management" />
              </ListItemButton>
            </ListItem>
            <ListItem disablePadding>
              <ListItemButton
                component={Link}
                to="/admin/users"
                selected={location.pathname === "/admin/users"}
                onClick={onClose}
              >
                <ListItemIcon>
                  <People />
                </ListItemIcon>
                <ListItemText primary="User Management" />
              </ListItemButton>
            </ListItem>
            <ListItem disablePadding>
              <ListItemButton
                component={Link}
                to="/admin/orders"
                selected={location.pathname === "/admin/orders"}
                onClick={onClose}
              >
                <ListItemIcon>
                  <Receipt />
                </ListItemIcon>
                <ListItemText primary="Order Management" />
              </ListItemButton>
            </ListItem>
          </List>
        </>
      )}
    </Drawer>
  );
};

export default Sidebar;
