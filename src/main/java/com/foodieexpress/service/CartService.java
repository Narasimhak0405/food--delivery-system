package com.foodieexpress.service;

import com.foodieexpress.dto.CartItemRequestDTO;
import com.foodieexpress.dto.CartResponseDTO;

public interface CartService {
    CartResponseDTO getCartByUserId(Long userId);
    CartResponseDTO addToCart(Long userId, CartItemRequestDTO request);
    CartResponseDTO updateCartItemQuantity(Long userId, Long cartItemId, Integer quantity);
    CartResponseDTO removeFromCart(Long userId, Long cartItemId);
    void clearCart(Long userId);
}

