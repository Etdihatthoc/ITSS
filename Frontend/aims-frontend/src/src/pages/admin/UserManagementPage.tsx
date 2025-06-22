// src/pages/admin/UserManagementPage.tsx
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
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  CircularProgress,
  Alert,
  Chip,
  Pagination,
  InputAdornment,
} from "@mui/material";
import {
  Add as AddIcon,
  Edit as EditIcon,
  Delete as DeleteIcon,
  Search as SearchIcon,
  Key as KeyIcon,
} from "@mui/icons-material";
import { User, UserRole } from "../../types/user";
import userService from "../../services/userService";

const UserManagementPage: React.FC = () => {
  // State for users
  const [users, setUsers] = useState<User[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  // State for pagination
  const [page, setPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [searchTerm, setSearchTerm] = useState("");

  // State for user dialog
  const [openUserDialog, setOpenUserDialog] = useState(false);
  const [dialogTitle, setDialogTitle] = useState("");
  const [selectedUser, setSelectedUser] = useState<User | null>(null);
  const [formErrors, setFormErrors] = useState<Record<string, string>>({});
  const [formSubmitting, setFormSubmitting] = useState(false);

  // State for delete confirmation dialog
  const [openDeleteDialog, setOpenDeleteDialog] = useState(false);
  const [userToDelete, setUserToDelete] = useState<User | null>(null);
  const [deleteLoading, setDeleteLoading] = useState(false);

  // State for reset password dialog
  const [openResetDialog, setOpenResetDialog] = useState(false);
  const [userToReset, setUserToReset] = useState<User | null>(null);
  const [resetLoading, setResetLoading] = useState(false);
  const [newPassword, setNewPassword] = useState("");

  // Form data state
  const [formData, setFormData] = useState({
    name: "",
    email: "",
    phone: "",
    role: "",
    password: "",
    confirmPassword: "",
  });

  // Load users on component mount and when page or search changes
  useEffect(() => {
    fetchUsers();
  }, [page, searchTerm]);

  // Fetch users from API
  const fetchUsers = async () => {
    try {
      setLoading(true);
      setError("");

      const response = await userService.getUsers({
        page,
        limit: 10,
        search: searchTerm,
      });

      setUsers(response.data.data);
      setTotalPages(Math.ceil(response.data.total / 10));
    } catch (err: any) {
      console.error("Failed to fetch users:", err);
      setError(err.response?.data?.message || "Failed to load users");
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
    fetchUsers();
  };

  // Open dialog to add a new user
  const handleAddUser = () => {
    setDialogTitle("Add New User");
    setSelectedUser(null);
    resetForm();
    setOpenUserDialog(true);
  };

  // Open dialog to edit a user
  const handleEditUser = (user: User) => {
    setDialogTitle("Edit User");
    setSelectedUser(user);

    // Populate form with user data
    setFormData({
      name: user.name,
      email: user.email,
      phone: user.phone || "",
      role: user.roles[0] || "",
      password: "",
      confirmPassword: "",
    });

    setOpenUserDialog(true);
  };

  // Open dialog to confirm user deletion
  const handleDeleteClick = (user: User) => {
    setUserToDelete(user);
    setOpenDeleteDialog(true);
  };

  // Delete user
  const handleDeleteConfirm = async () => {
    if (!userToDelete) return;

    try {
      setDeleteLoading(true);
      await userService.deleteUser(userToDelete.id);

      // Refresh user list
      fetchUsers();

      // Close dialog
      setOpenDeleteDialog(false);
      setUserToDelete(null);
    } catch (err: any) {
      console.error("Failed to delete user:", err);
      setError(err.response?.data?.message || "Failed to delete user");
    } finally {
      setDeleteLoading(false);
    }
  };

  // Open dialog to reset password
  const handleResetPasswordClick = (user: User) => {
    setUserToReset(user);
    setNewPassword("");
    setOpenResetDialog(true);
  };

  // Reset password
  const handleResetPasswordConfirm = async () => {
    if (!userToReset) return;

    try {
      setResetLoading(true);
      await userService.resetPassword(userToReset.id, newPassword);

      // Close dialog
      setOpenResetDialog(false);
      setUserToReset(null);
      setNewPassword("");

      // Show success message
      // In a real app, you would show a toast notification here
    } catch (err: any) {
      console.error("Failed to reset password:", err);
      setError(err.response?.data?.message || "Failed to reset password");
    } finally {
      setResetLoading(false);
    }
  };

  // Reset form
  const resetForm = () => {
    setFormData({
      name: "",
      email: "",
      phone: "",
      role: "",
      password: "",
      confirmPassword: "",
    });
    setFormErrors({});
  };

  // Handle form input changes
  const handleInputChange = (
    e: React.ChangeEvent<any> | { target: { name: string; value: string } }
  ) => {
    const { name, value } = e.target as { name: string; value: string };

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

  // Validate form
  const validateForm = () => {
    const errors: Record<string, string> = {};

    if (!formData.name.trim()) {
      errors.name = "Name is required";
    }

    if (!formData.email.trim()) {
      errors.email = "Email is required";
    } else if (!/\S+@\S+\.\S+/.test(formData.email)) {
      errors.email = "Email is invalid";
    }

    if (
      formData.phone &&
      !/^[0-9]{10,11}$/.test(formData.phone.replace(/\s/g, ""))
    ) {
      errors.phone = "Phone number is invalid";
    }

    if (!formData.role) {
      errors.role = "Role is required";
    }

    // Only validate password for new users
    if (!selectedUser) {
      if (!formData.password) {
        errors.password = "Password is required";
      } else if (formData.password.length < 8) {
        errors.password = "Password must be at least 8 characters";
      }

      if (formData.password !== formData.confirmPassword) {
        errors.confirmPassword = "Passwords do not match";
      }
    } else if (formData.password && formData.password.length < 8) {
      errors.password = "Password must be at least 8 characters";
    } else if (
      formData.password &&
      formData.password !== formData.confirmPassword
    ) {
      errors.confirmPassword = "Passwords do not match";
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

      const userData = {
        name: formData.name,
        email: formData.email,
        phone: formData.phone,
        role: formData.role as UserRole,
        password: formData.password || undefined,
      };

      if (selectedUser) {
        // Update existing user
        await userService.updateUser(selectedUser.id, userData);
      } else {
        // Create new user
        await userService.createUser(userData);
      }

      // Refresh user list
      fetchUsers();

      // Close dialog
      setOpenUserDialog(false);
    } catch (err: any) {
      console.error("Failed to save user:", err);
      setError(err.response?.data?.message || "Failed to save user");
    } finally {
      setFormSubmitting(false);
    }
  };

  // Get role label
  const getRoleLabel = (role: UserRole) => {
    const labels = {
      [UserRole.ADMIN]: "Administrator",
      [UserRole.PRODUCT_MANAGER]: "Product Manager",
      [UserRole.CUSTOMER]: "Customer",
    };
    return labels[role] || role;
  };

  // Get role color
  const getRoleColor = (role: UserRole) => {
    const colors = {
      [UserRole.ADMIN]: "error",
      [UserRole.PRODUCT_MANAGER]: "warning",
      [UserRole.CUSTOMER]: "primary",
    };
    return colors[role] || "default";
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
        <Typography variant="h4">User Management</Typography>
        <Button
          variant="contained"
          color="primary"
          startIcon={<AddIcon />}
          onClick={handleAddUser}
        >
          Add User
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
            placeholder="Search users..."
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
            {users.length === 0 ? (
              <Box sx={{ py: 4, textAlign: "center" }}>
                <Typography variant="body1" color="text.secondary">
                  No users found.
                </Typography>
              </Box>
            ) : (
              <TableContainer>
                <Table>
                  <TableHead>
                    <TableRow>
                      <TableCell>Name</TableCell>
                      <TableCell>Email</TableCell>
                      <TableCell>Phone</TableCell>
                      <TableCell>Role</TableCell>
                      <TableCell>Created</TableCell>
                      <TableCell align="right">Actions</TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {users.map((user) => (
                      <TableRow key={user.id}>
                        <TableCell>{user.name}</TableCell>
                        <TableCell>{user.email}</TableCell>
                        <TableCell>{user.phone || "-"}</TableCell>
                        <TableCell>
                          <Chip
                            label={getRoleLabel(user.roles[0])}
                            color={getRoleColor(user.roles[0]) as any}
                            size="small"
                          />
                        </TableCell>
                        <TableCell>
                          {new Date(user.createdAt).toLocaleDateString()}
                        </TableCell>
                        <TableCell align="right">
                          <IconButton
                            color="primary"
                            onClick={() => handleResetPasswordClick(user)}
                            title="Reset Password"
                          >
                            <KeyIcon />
                          </IconButton>
                          <IconButton
                            color="primary"
                            onClick={() => handleEditUser(user)}
                          >
                            <EditIcon />
                          </IconButton>
                          <IconButton
                            color="error"
                            onClick={() => handleDeleteClick(user)}
                          >
                            <DeleteIcon />
                          </IconButton>
                        </TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </TableContainer>
            )}

            <Box sx={{ display: "flex", justifyContent: "center", mt: 3 }}>
              <Pagination
                count={totalPages}
                page={page}
                onChange={handlePageChange}
                color="primary"
              />
            </Box>
          </>
        )}
      </Paper>

      {/* User Form Dialog */}
      <Dialog
        open={openUserDialog}
        onClose={() => setOpenUserDialog(false)}
        maxWidth="sm"
        fullWidth
      >
        <DialogTitle>{dialogTitle}</DialogTitle>
        <DialogContent>
          <Box component="form" sx={{ mt: 2 }}>
            <Box sx={{ mb: 2 }}>
              <TextField
                name="name"
                label="Full Name"
                fullWidth
                required
                value={formData.name}
                onChange={handleInputChange}
                error={!!formErrors.name}
                helperText={formErrors.name}
              />
            </Box>

            <Box sx={{ mb: 2 }}>
              <TextField
                name="email"
                label="Email Address"
                fullWidth
                required
                type="email"
                value={formData.email}
                onChange={handleInputChange}
                error={!!formErrors.email}
                helperText={formErrors.email}
              />
            </Box>

            <Box sx={{ mb: 2 }}>
              <TextField
                name="phone"
                label="Phone Number"
                fullWidth
                value={formData.phone}
                onChange={handleInputChange}
                error={!!formErrors.phone}
                helperText={formErrors.phone}
              />
            </Box>

            <Box sx={{ mb: 2 }}>
              <FormControl fullWidth error={!!formErrors.role} required>
                <InputLabel>Role</InputLabel>
                <Select
                  name="role"
                  value={formData.role}
                  onChange={handleInputChange}
                  label="Role"
                >
                  {Object.values(UserRole).map((role) => (
                    <MenuItem key={role} value={role}>
                      {getRoleLabel(role)}
                    </MenuItem>
                  ))}
                </Select>
                {formErrors.role && (
                  <Typography variant="caption" color="error">
                    {formErrors.role}
                  </Typography>
                )}
              </FormControl>
            </Box>

            <Box sx={{ mb: 2 }}>
              <TextField
                name="password"
                label={
                  selectedUser
                    ? "New Password (leave blank to keep current)"
                    : "Password"
                }
                fullWidth
                required={!selectedUser}
                type="password"
                value={formData.password}
                onChange={handleInputChange}
                error={!!formErrors.password}
                helperText={formErrors.password}
              />
            </Box>

            <Box sx={{ mb: 2 }}>
              <TextField
                name="confirmPassword"
                label="Confirm Password"
                fullWidth
                required={!selectedUser || !!formData.password}
                type="password"
                value={formData.confirmPassword}
                onChange={handleInputChange}
                error={!!formErrors.confirmPassword}
                helperText={formErrors.confirmPassword}
              />
            </Box>
          </Box>
        </DialogContent>
        <DialogActions>
          <Button
            onClick={() => setOpenUserDialog(false)}
            disabled={formSubmitting}
          >
            Cancel
          </Button>
          <Button
            onClick={handleSubmit}
            variant="contained"
            color="primary"
            disabled={formSubmitting}
          >
            {formSubmitting ? <CircularProgress size={24} /> : "Save"}
          </Button>
        </DialogActions>
      </Dialog>

      {/* Delete Confirmation Dialog */}
      <Dialog
        open={openDeleteDialog}
        onClose={() => setOpenDeleteDialog(false)}
      >
        <DialogTitle>Confirm User Deletion</DialogTitle>
        <DialogContent>
          <DialogContentText>
            Are you sure you want to delete the user "{userToDelete?.name}" (
            {userToDelete?.email})? This action cannot be undone.
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button
            onClick={() => setOpenDeleteDialog(false)}
            disabled={deleteLoading}
          >
            Cancel
          </Button>
          <Button
            onClick={handleDeleteConfirm}
            color="error"
            disabled={deleteLoading}
            startIcon={
              deleteLoading ? <CircularProgress size={20} /> : <DeleteIcon />
            }
          >
            Delete
          </Button>
        </DialogActions>
      </Dialog>

      {/* Reset Password Dialog */}
      <Dialog open={openResetDialog} onClose={() => setOpenResetDialog(false)}>
        <DialogTitle>Reset Password</DialogTitle>
        <DialogContent>
          <DialogContentText sx={{ mb: 2 }}>
            Set a new password for user "{userToReset?.name}" (
            {userToReset?.email}).
          </DialogContentText>
          <TextField
            label="New Password"
            type="password"
            fullWidth
            value={newPassword}
            onChange={(e) => setNewPassword(e.target.value)}
            error={newPassword.length > 0 && newPassword.length < 8}
            helperText={
              newPassword.length > 0 && newPassword.length < 8
                ? "Password must be at least 8 characters"
                : ""
            }
          />
        </DialogContent>
        <DialogActions>
          <Button
            onClick={() => setOpenResetDialog(false)}
            disabled={resetLoading}
          >
            Cancel
          </Button>
          <Button
            onClick={handleResetPasswordConfirm}
            color="primary"
            disabled={resetLoading || !newPassword || newPassword.length < 8}
            startIcon={
              resetLoading ? <CircularProgress size={20} /> : <KeyIcon />
            }
          >
            Reset Password
          </Button>
        </DialogActions>
      </Dialog>
    </Container>
  );
};

export default UserManagementPage;
