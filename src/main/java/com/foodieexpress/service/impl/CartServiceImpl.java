package com.foodieexpress.service.impl;

import com.foodieexpress.dto.CartItemRequestDTO;
import com.foodieexpress.dto.CartItemResponseDTO;
import com.foodieexpress.dto.CartResponseDTO;
import com.foodieexpress.exception.BadRequestException;
import com.foodieexpress.exception.ResourceNotFoundException;
import com.foodieexpress.model.Cart;
import com.foodieexpress.model.CartItem;
import com.foodieexpress.model.FoodItem;
import com.foodieexpress.model.User;
import com.foodieexpress.repository.CartItemRepository;
import com.foodieexpress.repository.CartRepository;
import com.foodieexpress.repository.FoodItemRepository;
import com.foodieexpress.repository.UserRepository;
import com.foodieexpress.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private FoodItemRepository foodItemRepository;

    @Autowired
    private UserRepository userRepository;

    private Cart getOrCreateCart(Long userId) {
        return cartRepository.findByUserId(userId).orElseGet(() -> {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
            Cart newCart = new Cart(user);
            return cartRepository.save(newCart);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public CartResponseDTO getCartByUserId(Long userId) {
        Cart cart = getOrCreateCart(userId);
        return mapToCartResponseDTO(cart);
    }

    @Override
    @Transactional
    public CartResponseDTO addToCart(Long userId, CartItemRequestDTO request) {
        Cart cart = getOrCreateCart(userId);
        FoodItem foodItem = foodItemRepository.findById(request.getFoodItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Food item not found with id: " + request.getFoodItemId()));

        if (!foodItem.getIsAvailable()) {
            throw new BadRequestException("Item " + foodItem.getName() + " is currently unavailable.");
        }

        CartItem cartItem = cartItemRepository.findByCartIdAndFoodItemId(cart.getId(), foodItem.getId())
                .orElse(null);

        if (cartItem != null) {
            cartItem.setQuantity(cartItem.getQuantity() + request.getQuantity());
        } else {
            cartItem = new CartItem(cart, foodItem, request.getQuantity());
            cart.getItems().add(cartItem);
        }

        cartItemRepository.save(cartItem);
        return mapToCartResponseDTO(cart);
    }

    @Override
    @Transactional
    public CartResponseDTO updateCartItemQuantity(Long userId, Long cartItemId, Integer quantity) {
        Cart cart = getOrCreateCart(userId);
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id: " + cartItemId));

        if (quantity <= 0) {
            cart.getItems().remove(item);
            cartItemRepository.delete(item);
        } else {
            item.setQuantity(quantity);
            cartItemRepository.save(item);
        }

        return mapToCartResponseDTO(cart);
    }

    @Override
    @Transactional
    public CartResponseDTO removeFromCart(Long userId, Long cartItemId) {
        Cart cart = getOrCreateCart(userId);
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id: " + cartItemId));

        cart.getItems().remove(item);
        cartItemRepository.delete(item);
        return mapToCartResponseDTO(cart);
    }

    @Override
    @Transactional
    public void clearCart(Long userId) {
        Cart cart = getOrCreateCart(userId);
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    private CartResponseDTO mapToCartResponseDTO(Cart cart) {
        CartResponseDTO response = new CartResponseDTO();
        response.setCartId(cart.getId());
        response.setUserId(cart.getUser().getId());

        List<CartItemResponseDTO> itemDTOs = new ArrayList<>();
        BigDecimal itemTotal = BigDecimal.ZERO;

        for (CartItem ci : cart.getItems()) {
            CartItemResponseDTO dto = new CartItemResponseDTO();
            dto.setId(ci.getId());
            dto.setFoodItemId(ci.getFoodItem().getId());
            dto.setName(ci.getFoodItem().getName());
            dto.setPrice(ci.getFoodItem().getPrice());
            dto.setQuantity(ci.getQuantity());
            BigDecimal subtotal = ci.getFoodItem().getPrice().multiply(BigDecimal.valueOf(ci.getQuantity()));
            dto.setSubtotal(subtotal);
            dto.setIsVeg(ci.getFoodItem().getIsVeg());
            dto.setImageUrl(ci.getFoodItem().getImageUrl());

            itemDTOs.add(dto);
            itemTotal = itemTotal.add(subtotal);
        }

        response.setItems(itemDTOs);
        response.setItemTotal(itemTotal);

        if (itemTotal.compareTo(BigDecimal.ZERO) > 0) {
            response.setDeliveryFee(BigDecimal.valueOf(40.00));
            response.setPlatformFee(BigDecimal.valueOf(5.00));
            BigDecimal tax = itemTotal.multiply(BigDecimal.valueOf(0.05)).setScale(2, RoundingMode.HALF_UP);
            response.setTaxAmount(tax);
            BigDecimal grandTotal = itemTotal.add(response.getDeliveryFee()).add(response.getPlatformFee()).add(tax);
            response.setGrandTotal(grandTotal);
        } else {
            response.setDeliveryFee(BigDecimal.ZERO);
            response.setPlatformFee(BigDecimal.ZERO);
            response.setTaxAmount(BigDecimal.ZERO);
            response.setGrandTotal(BigDecimal.ZERO);
        }

        return response;
    }
}

