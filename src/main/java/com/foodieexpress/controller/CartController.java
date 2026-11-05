package com.foodieexpress.controller;

import com.foodieexpress.dto.CartItemRequestDTO;
import com.foodieexpress.dto.CartResponseDTO;
import com.foodieexpress.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "*")
public class CartController {

    @Autowired
    private CartService cartService;

    // 1. Get user cart with itemized breakdown
    @GetMapping
    public ResponseEntity<CartResponseDTO> getCart(@RequestParam Long userId) {
        return ResponseEntity.ok(cartService.getCartByUserId(userId));
    }

    // 2. Add an item to cart
    @PostMapping("/items")
    public ResponseEntity<CartResponseDTO> addToCart(@RequestParam Long userId, @RequestBody CartItemRequestDTO request) {
        return ResponseEntity.ok(cartService.addToCart(userId, request));
    }

    // 3. Update quantity of a cart item
    @PutMapping("/items/{cartItemId}")
    public ResponseEntity<CartResponseDTO> updateQuantity(
            @RequestParam Long userId,
            @PathVariable Long cartItemId,
            @RequestParam Integer quantity) {
        return ResponseEntity.ok(cartService.updateCartItemQuantity(userId, cartItemId, quantity));
    }

    // 4. Remove a specific item from cart
    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<CartResponseDTO> removeFromCart(
            @RequestParam Long userId,
            @PathVariable Long cartItemId) {
        return ResponseEntity.ok(cartService.removeFromCart(userId, cartItemId));
    }

    // 5. Clear entire cart
    @DeleteMapping("/clear")
    public ResponseEntity<String> clearCart(@RequestParam Long userId) {
        cartService.clearCart(userId);
        return ResponseEntity.ok("Cart cleared successfully.");
    }
}

