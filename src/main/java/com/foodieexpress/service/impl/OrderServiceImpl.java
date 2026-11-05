package com.foodieexpress.service.impl;

import com.foodieexpress.dto.OrderCancellationRequestDTO;
import com.foodieexpress.dto.OrderRequestDTO;
import com.foodieexpress.dto.OrderResponseDTO;
import com.foodieexpress.exception.BadRequestException;
import com.foodieexpress.exception.ResourceNotFoundException;
import com.foodieexpress.model.*;
import com.foodieexpress.repository.*;
import com.foodieexpress.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Override
    @Transactional
    public OrderResponseDTO placeOrder(OrderRequestDTO request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUserId()));

        Restaurant restaurant = restaurantRepository.findById(request.getRestaurantId())
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id: " + request.getRestaurantId()));

        Address address = addressRepository.findById(request.getDeliveryAddressId())
                .orElseThrow(() -> new ResourceNotFoundException("Delivery address not found with id: " + request.getDeliveryAddressId()));

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new BadRequestException("No active cart found for user."));

        if (cart.getItems().isEmpty()) {
            throw new BadRequestException("Cannot place order with an empty cart!");
        }

        Order order = new Order();
        String orderNum = "ORD-" + LocalDateTime.now().getYear() + "-" + (int) (1000 + Math.random() * 9000);
        order.setOrderNumber(orderNum);
        order.setUser(user);
        order.setRestaurant(restaurant);
        order.setDeliveryAddress(address);
        order.setStatus(OrderStatus.PLACED);
        order.setSpecialInstructions(request.getSpecialInstructions());

        // 1. Calculate item subtotals and snapshot line prices
        BigDecimal itemTotal = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem ci : cart.getItems()) {
            OrderItem oi = new OrderItem();
            oi.setOrder(order);
            oi.setFoodItem(ci.getFoodItem());
            oi.setItemName(ci.getFoodItem().getName());
            oi.setItemPrice(ci.getFoodItem().getPrice());
            oi.setQuantity(ci.getQuantity());
            BigDecimal subtotal = ci.getFoodItem().getPrice().multiply(BigDecimal.valueOf(ci.getQuantity()));
            oi.setSubtotal(subtotal);

            orderItems.add(oi);
            itemTotal = itemTotal.add(subtotal);
        }

        order.setOrderItems(orderItems);

        // 2. Minimum order threshold check
        if (restaurant.getMinOrderAmount() != null && itemTotal.compareTo(restaurant.getMinOrderAmount()) < 0) {
            throw new BadRequestException("Order total ₹" + itemTotal + " does not meet restaurant minimum order requirement of ₹" + restaurant.getMinOrderAmount());
        }

        // 3. Dynamic delivery fee calculation: Free delivery over ₹500, else standard ₹40
        BigDecimal deliveryFee = itemTotal.compareTo(BigDecimal.valueOf(500.00)) >= 0 ? BigDecimal.ZERO : BigDecimal.valueOf(40.00);
        order.setDeliveryFee(deliveryFee);

        // 4. Standard 5% GST on food item total
        BigDecimal tax = itemTotal.multiply(BigDecimal.valueOf(0.05)).setScale(2, RoundingMode.HALF_UP);
        order.setTaxAmount(tax);

        // 5. Total payable calculation
        BigDecimal totalAmount = itemTotal.add(deliveryFee).add(tax);
        order.setTotalAmount(totalAmount);

        Order savedOrder = orderRepository.save(order);

        // 6. Record Payment transaction
        Payment payment = new Payment();
        payment.setOrder(savedOrder);
        payment.setTransactionId("TXN_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setPaymentStatus(request.getPaymentMethod() == Payment.PaymentMethod.CASH_ON_DELIVERY ?
                Payment.PaymentStatus.PENDING : Payment.PaymentStatus.COMPLETED);
        payment.setAmount(savedOrder.getTotalAmount());
        paymentRepository.save(payment);

        // 7. Clear user's cart after order placement
        cart.getItems().clear();
        cartRepository.save(cart);

        return mapToOrderResponseDTO(savedOrder, request.getPaymentMethod());
    }

    @Override
    @Transactional
    public OrderResponseDTO cancelOrder(OrderCancellationRequestDTO request) {
        Order order = orderRepository.findByOrderNumber(request.getOrderNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with number: " + request.getOrderNumber()));

        // Cancellation Rules:
        // Can only cancel if PLACED or CONFIRMED.
        // Once food preparation has started, order cannot be cancelled to protect restaurant partners.
        if (order.getStatus() != OrderStatus.PLACED && order.getStatus() != OrderStatus.CONFIRMED) {
            throw new BadRequestException("Order cannot be cancelled at this stage. Current status is: " + order.getStatus());
        }

        order.setStatus(OrderStatus.CANCELLED);
        order.setCancellationReason(request.getReason() != null ? request.getReason() : "Cancelled by customer.");
        order.setCancelledAt(LocalDateTime.now());

        // Check if refund is applicable
        Payment payment = paymentRepository.findByOrderId(order.getId()).orElse(null);
        if (payment != null && payment.getPaymentStatus() == Payment.PaymentStatus.COMPLETED) {
            payment.setPaymentStatus(Payment.PaymentStatus.REFUNDED);
            order.setRefundStatus("REFUND_INITIATED");
            paymentRepository.save(payment);
        }

        Order updated = orderRepository.save(order);
        Payment.PaymentMethod method = payment != null ? payment.getPaymentMethod() : Payment.PaymentMethod.CASH_ON_DELIVERY;
        return mapToOrderResponseDTO(updated, method);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getOrderHistory(Long userId) {
        List<Order> orders = orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return orders.stream()
                .map(o -> {
                    Payment payment = paymentRepository.findByOrderId(o.getId()).orElse(null);
                    Payment.PaymentMethod method = payment != null ? payment.getPaymentMethod() : Payment.PaymentMethod.CASH_ON_DELIVERY;
                    return mapToOrderResponseDTO(o, method);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponseDTO getOrderByOrderNumber(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with order number: " + orderNumber));
        Payment payment = paymentRepository.findByOrderId(order.getId()).orElse(null);
        Payment.PaymentMethod method = payment != null ? payment.getPaymentMethod() : Payment.PaymentMethod.CASH_ON_DELIVERY;
        return mapToOrderResponseDTO(order, method);
    }

    @Override
    @Transactional
    public OrderResponseDTO updateOrderStatus(String orderNumber, OrderStatus newStatus) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with order number: " + orderNumber));
        order.setStatus(newStatus);
        Order updated = orderRepository.save(order);
        Payment payment = paymentRepository.findByOrderId(updated.getId()).orElse(null);
        Payment.PaymentMethod method = payment != null ? payment.getPaymentMethod() : Payment.PaymentMethod.CASH_ON_DELIVERY;
        return mapToOrderResponseDTO(updated, method);
    }

    private OrderResponseDTO mapToOrderResponseDTO(Order order, Payment.PaymentMethod method) {
        OrderResponseDTO dto = new OrderResponseDTO();
        dto.setId(order.getId());
        dto.setOrderNumber(order.getOrderNumber());
        dto.setCustomerName(order.getUser().getName());
        dto.setRestaurantName(order.getRestaurant().getName());
        dto.setDeliveryAddress(order.getDeliveryAddress().getAddressLine1() + ", " + order.getDeliveryAddress().getCity());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setStatus(order.getStatus());
        dto.setPaymentMethod(method);
        dto.setOrderDate(order.getCreatedAt());

        List<OrderResponseDTO.OrderItemDetailDTO> items = order.getOrderItems().stream()
                .map(oi -> new OrderResponseDTO.OrderItemDetailDTO(oi.getItemName(), oi.getItemPrice(), oi.getQuantity(), oi.getSubtotal()))
                .collect(Collectors.toList());
        dto.setItems(items);

        return dto;
    }
}
