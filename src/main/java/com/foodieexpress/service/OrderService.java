package com.foodieexpress.service;

import com.foodieexpress.dto.OrderCancellationRequestDTO;
import com.foodieexpress.dto.OrderRequestDTO;
import com.foodieexpress.dto.OrderResponseDTO;
import com.foodieexpress.model.OrderStatus;

import java.util.List;

public interface OrderService {
    OrderResponseDTO placeOrder(OrderRequestDTO request);
    List<OrderResponseDTO> getOrderHistory(Long userId);
    OrderResponseDTO getOrderByOrderNumber(String orderNumber);
    OrderResponseDTO updateOrderStatus(String orderNumber, OrderStatus newStatus);
    OrderResponseDTO cancelOrder(OrderCancellationRequestDTO request);
}
