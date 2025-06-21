package com.hust.ict.aims.service.impl;
import com.hust.ict.aims.model.CartItem;
import com.hust.ict.aims.repository.CartItemRepository;
import com.hust.ict.aims.service.CartItemService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CartItemServiceImpl implements CartItemService {

    private final CartItemRepository cartItemRepository;

    public CartItemServiceImpl(CartItemRepository cartItemRepository) {
        this.cartItemRepository = cartItemRepository;
    }

    @Override
    public CartItem save(CartItem cartItem) {
        return cartItemRepository.save(cartItem);
    }

    @Override
    public CartItem findById(Long id) {
        Optional<CartItem> optionalCartItem = cartItemRepository.findById(id);
        return optionalCartItem.orElse(null);
    }

    @Override
    public List<CartItem> findAll() {
        return cartItemRepository.findAll();
    }

    @Override
    public CartItem update(Long id, CartItem cartItem) {
        if (!cartItemRepository.existsById(id)) {
            return null;
        }
        cartItem.setId(id);
        return cartItemRepository.save(cartItem);
    }

    @Override
    public boolean delete(Long id) {
        cartItemRepository.deleteById(id);
        return false;
    }

    @Override
    public List<CartItem> findByCartId(Long cartId) {
        return cartItemRepository.findByCart_CartId(cartId);
    }
}

