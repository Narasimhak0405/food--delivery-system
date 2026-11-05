package com.foodieexpress.controller;

import com.foodieexpress.dto.OrderResponseDTO;
import com.foodieexpress.dto.RestaurantDashboardStatsDTO;
import com.foodieexpress.exception.ResourceNotFoundException;
import com.foodieexpress.model.FoodItem;
import com.foodieexpress.model.Order;
import com.foodieexpress.model.OrderStatus;
import com.foodieexpress.model.Restaurant;
import com.foodieexpress.repository.FoodItemRepository;
import com.foodieexpress.repository.OrderRepository;
import com.foodieexpress.repository.RestaurantRepository;
import com.foodieexpress.service.FoodItemService;
import com.foodieexpress.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/restaurant-portal")
@CrossOrigin(origins = "*")
public class RestaurantPortalController {

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private FoodItemRepository foodItemRepository;

    @Autowired
    private FoodItemService foodItemService;

    @Autowired
    private OrderService orderService;

    // 1. Restaurant dashboard statistics
    @GetMapping("/stats")
    public ResponseEntity<RestaurantDashboardStatsDTO> getRestaurantStats(@RequestParam Long restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id: " + restaurantId));

        List<Order> restaurantOrders = orderRepository.findByRestaurantIdOrderByCreatedAtDesc(restaurantId);

        BigDecimal revenue = restaurantOrders.stream()
                .filter(o -> o.getStatus() != OrderStatus.CANCELLED)
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long activeCount = restaurantOrders.stream()
                .filter(o -> o.getStatus() == OrderStatus.PLACED ||
                        o.getStatus() == OrderStatus.CONFIRMED ||
                        o.getStatus() == OrderStatus.PREPARING ||
                        o.getStatus() == OrderStatus.OUT_FOR_DELIVERY)
                .count();

        List<OrderResponseDTO> liveOrders = restaurantOrders.stream()
                .limit(15)
                .map(o -> orderService.getOrderByOrderNumber(o.getOrderNumber()))
                .collect(Collectors.toList());

        RestaurantDashboardStatsDTO stats = new RestaurantDashboardStatsDTO();
        stats.setRestaurantName(restaurant.getName());
        stats.setTodayRevenue(revenue);
        stats.setTodayOrdersCount((long) restaurantOrders.size());
        stats.setTotalMenuDishes((long) foodItemRepository.findByRestaurantId(restaurantId).size());
        stats.setActiveOrdersCount(activeCount);
        stats.setLiveOrders(liveOrders);

        return ResponseEntity.ok(stats);
    }

    // 2. View incoming live orders
    @GetMapping("/orders")
    public ResponseEntity<List<OrderResponseDTO>> getIncomingOrders(@RequestParam Long restaurantId) {
        List<Order> orders = orderRepository.findByRestaurantIdOrderByCreatedAtDesc(restaurantId);
        List<OrderResponseDTO> dtos = orders.stream()
                .map(o -> orderService.getOrderByOrderNumber(o.getOrderNumber()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // 3. Update order status: Accept (CONFIRMED), Reject (CANCELLED), PREPARING, OUT_FOR_DELIVERY, DELIVERED
    @PatchMapping("/orders/{orderNumber}/status")
    public ResponseEntity<OrderResponseDTO> updateOrderStatus(
            @PathVariable String orderNumber,
            @RequestParam OrderStatus status) {
        return ResponseEntity.ok(orderService.updateOrderStatus(orderNumber, status));
    }

    // 4. Quick toggle dish availability (In Stock / Out of Stock)
    @PatchMapping("/food-items/{id}/availability")
    public ResponseEntity<FoodItem> toggleFoodItemAvailability(
            @PathVariable Long id,
            @RequestParam Boolean isAvailable) {
        FoodItem item = foodItemService.getFoodItemById(id);
        item.setIsAvailable(isAvailable);
        return ResponseEntity.ok(foodItemRepository.save(item));
    }
}

