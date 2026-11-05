package com.foodieexpress.controller;

import com.foodieexpress.dto.OrderCancellationRequestDTO;
import com.foodieexpress.dto.OrderRequestDTO;
import com.foodieexpress.dto.OrderResponseDTO;
import com.foodieexpress.model.OrderStatus;
import com.foodieexpress.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    @Autowired
    private OrderService orderService;

    // 1. Place a new order
    @PostMapping
    public ResponseEntity<OrderResponseDTO> placeOrder(@RequestBody OrderRequestDTO request) {
        OrderResponseDTO response = orderService.placeOrder(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // 2. Retrieve customer's order history
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderResponseDTO>> getOrderHistory(@PathVariable Long userId) {
        return ResponseEntity.ok(orderService.getOrderHistory(userId));
    }

    // 3. Track order by order number (e.g. ORD-2026-001)
    @GetMapping("/{orderNumber}")
    public ResponseEntity<OrderResponseDTO> getOrderByNumber(@PathVariable String orderNumber) {
        return ResponseEntity.ok(orderService.getOrderByOrderNumber(orderNumber));
    }

    // 4. Update order status (PLACED, PREPARING, OUT_FOR_DELIVERY, DELIVERED, CANCELLED)
    @PatchMapping("/{orderNumber}/status")
    public ResponseEntity<OrderResponseDTO> updateOrderStatus(
            @PathVariable String orderNumber,
            @RequestParam OrderStatus status) {
        return ResponseEntity.ok(orderService.updateOrderStatus(orderNumber, status));
    }

    // 5. Cancel order (with status validation & automatic refund initiation)
    @PostMapping("/cancel")
    public ResponseEntity<OrderResponseDTO> cancelOrder(@RequestBody OrderCancellationRequestDTO request) {
        return ResponseEntity.ok(orderService.cancelOrder(request));
    }
}
