package com.foodieexpress.controller;

import com.foodieexpress.dto.AdminDashboardStatsDTO;
import com.foodieexpress.dto.OrderResponseDTO;
import com.foodieexpress.exception.ResourceNotFoundException;
import com.foodieexpress.model.Order;
import com.foodieexpress.model.OrderStatus;
import com.foodieexpress.model.User;
import com.foodieexpress.repository.FoodItemRepository;
import com.foodieexpress.repository.OrderRepository;
import com.foodieexpress.repository.RestaurantRepository;
import com.foodieexpress.repository.UserRepository;
import com.foodieexpress.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FoodItemRepository foodItemRepository;

    @Autowired
    private OrderService orderService;

    // 1. Dashboard statistics
    @GetMapping("/stats")
    public ResponseEntity<AdminDashboardStatsDTO> getAdminDashboardStats() {
        AdminDashboardStatsDTO stats = new AdminDashboardStatsDTO();

        List<Order> allOrders = orderRepository.findAll();
        BigDecimal revenue = allOrders.stream()
                .filter(o -> o.getStatus() != OrderStatus.CANCELLED)
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long pendingOrders = allOrders.stream()
                .filter(o -> o.getStatus() == OrderStatus.PLACED || o.getStatus() == OrderStatus.PREPARING)
                .count();

        stats.setTotalRevenue(revenue);
        stats.setTotalOrders((long) allOrders.size());
        stats.setTotalRestaurants(restaurantRepository.count());
        stats.setTotalCustomers(userRepository.count());
        stats.setTotalFoodItems(foodItemRepository.count());
        stats.setPendingOrdersCount(pendingOrders);

        // Fetch recent orders
        List<OrderResponseDTO> recentOrders = allOrders.stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .limit(10)
                .map(o -> orderService.getOrderByOrderNumber(o.getOrderNumber()))
                .collect(Collectors.toList());
        stats.setRecentOrders(recentOrders);

        return ResponseEntity.ok(stats);
    }

    // 2. Manage all users
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    // 3. Update user role (CUSTOMER <-> RESTAURANT_OWNER <-> ADMIN)
    @PatchMapping("/users/{id}/role")
    public ResponseEntity<User> updateUserRole(@PathVariable Long id, @RequestParam User.Role role) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        user.setRole(role);
        return ResponseEntity.ok(userRepository.save(user));
    }

    // 4. Delete user account
    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        userRepository.delete(user);
        return ResponseEntity.ok("User account deleted successfully.");
    }

    // 5. View all orders across all restaurants
    @GetMapping("/orders")
    public ResponseEntity<List<OrderResponseDTO>> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        List<OrderResponseDTO> dtos = orders.stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .map(o -> orderService.getOrderByOrderNumber(o.getOrderNumber()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
}

