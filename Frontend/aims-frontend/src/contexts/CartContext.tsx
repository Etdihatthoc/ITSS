// src/contexts/CartContext.tsx
import React, { createContext, useContext, useState, useEffect } from "react";
import type { Cart, CartItem, InventoryCheckResponse } from "../types/cart";
import type { Product } from "../types/product";
import cartService from "../services/cartService";
import { toast } from "react-toastify";

interface CartContextType {
  cart: Cart | null;
  loading: boolean;
  addToCart: (product: Product, quantity: number) => void;
  updateCartItem: (productId: string | number, quantity: number) => void;
  removeFromCart: (productId: string | number) => void;
  clearCart: () => void;
  checkInventory: () => Promise<InventoryCheckResponse>;
}

const CartContext = createContext<CartContextType | undefined>(undefined);

// Create an empty cart structure
const createEmptyCart = (): Cart => ({
  cartId: Date.now(), // Generate temporary ID
  totalProductPriceBeforeVAT: 0,
  items: [],
  subtotal: 0,
  tax: 0,
  total: 0,
});

export const CartProvider: React.FC<{ children: React.ReactNode }> = ({
  children,
}) => {
  const [cart, setCart] = useState<Cart | null>(null);
  const [loading, setLoading] = useState(false);

  // Load cart from localStorage on initialization
  useEffect(() => {
    const storedCart = localStorage.getItem("cart");
    if (storedCart) {
      try {
        const parsedCart = JSON.parse(storedCart);
        setCart(processCart(parsedCart));
      } catch (error) {
        console.error("Failed to parse cart from localStorage:", error);
        const emptyCart = createEmptyCart();
        setCart(emptyCart);
        localStorage.setItem("cart", JSON.stringify(emptyCart));
      }
    } else {
      // Initialize empty cart
      const emptyCart = createEmptyCart();
      setCart(emptyCart);
      localStorage.setItem("cart", JSON.stringify(emptyCart));
    }
  }, []);

  // Save cart to localStorage whenever it changes
  useEffect(() => {
    if (cart) {
      localStorage.setItem("cart", JSON.stringify(cart));
    }
  }, [cart]);

  // Process cart to calculate totals
  const processCart = (cartData: Cart | null): Cart => {
    if (!cartData) return createEmptyCart();

    // Calculate item subtotals
    const updatedItems = cartData.items.map((item) => ({
      ...item,
      subtotal: item.quantity * (item.product.currentPrice || 0),
    }));

    // Calculate cart totals
    const totalBeforeVAT = updatedItems.reduce(
      (sum, item) => sum + (item.subtotal || 0),
      0
    );
    const tax = totalBeforeVAT * 0.1; // 10% VAT
    const total = totalBeforeVAT + tax;

    return {
      ...cartData,
      items: updatedItems,
      totalProductPriceBeforeVAT: totalBeforeVAT,
      subtotal: totalBeforeVAT,
      tax,
      total,
    };
  };

  // Add product to cart
  const addToCart = (product: Product, quantity: number) => {
    setLoading(true);
    try {
      const currentCart = cart || createEmptyCart();
      const existingItemIndex = currentCart.items.findIndex(
        (item) => item.product.id === product.id
      );

      let updatedItems;
      if (existingItemIndex >= 0) {
        // Update existing item
        updatedItems = [...currentCart.items];
        updatedItems[existingItemIndex] = {
          ...updatedItems[existingItemIndex],
          quantity: updatedItems[existingItemIndex].quantity + quantity,
        };
      } else {
        // Add new item
        const newItem: CartItem = {
          product: product,
          quantity: quantity,
        };
        updatedItems = [...currentCart.items, newItem];
      }

      const updatedCart = {
        ...currentCart,
        items: updatedItems,
      };

      setCart(processCart(updatedCart));
      toast.success("Product added to cart");
    } catch (error) {
      toast.error("Failed to add product to cart");
    } finally {
      setLoading(false);
    }
  };

  // Update cart item quantity
  const updateCartItem = (productId: string | number, quantity: number) => {
    setLoading(true);
    try {
      if (!cart) return;

      const updatedItems = cart.items.map((item) =>
        item.product.id.toString() === productId.toString()
          ? { ...item, quantity }
          : item
      );

      const updatedCart = {
        ...cart,
        items: updatedItems,
      };

      setCart(processCart(updatedCart));
      toast.success("Cart updated");
    } catch (error) {
      toast.error("Failed to update cart");
    } finally {
      setLoading(false);
    }
  };

  // Remove item from cart
  const removeFromCart = (productId: string | number) => {
    setLoading(true);
    try {
      if (!cart) return;

      const updatedItems = cart.items.filter(
        (item) => item.product.id.toString() !== productId.toString()
      );

      const updatedCart = {
        ...cart,
        items: updatedItems,
      };

      setCart(processCart(updatedCart));
      toast.success("Product removed from cart");
    } catch (error) {
      toast.error("Failed to remove product from cart");
    } finally {
      setLoading(false);
    }
  };

  // Clear cart
  const clearCart = () => {
    setLoading(true);
    try {
      const emptyCart = createEmptyCart();
      setCart(emptyCart);
      localStorage.setItem("cart", JSON.stringify(emptyCart));
      toast.success("Cart cleared");
    } catch (error) {
      toast.error("Failed to clear cart");
    } finally {
      setLoading(false);
    }
  };

  // Still keep the API call for inventory check
  const checkInventory = async () => {
    const response = await cartService.checkInventory();
    return response.data;
  };

  return (
    <CartContext.Provider
      value={{
        cart: cart,
        loading,
        addToCart,
        updateCartItem,
        removeFromCart,
        clearCart,
        checkInventory,
      }}
    >
      {children}
    </CartContext.Provider>
  );
};

export const useCart = () => {
  const context = useContext(CartContext);
  if (context === undefined) {
    throw new Error("useCart must be used within a CartProvider");
  }
  return context;
};
