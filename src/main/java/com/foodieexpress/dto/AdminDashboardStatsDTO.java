package com.foodieexpress.dto;

import java.math.BigDecimal;
import java.util.List;

public class AdminDashboardStatsDTO {
    private BigDecimal totalRevenue;
    private Long totalOrders;
    private Long totalRestaurants;
    private Long totalCustomers;
    private Long totalFoodItems;
    private Long pendingOrdersCount;
    private List<OrderResponseDTO> recentOrders;

    public AdminDashboardStatsDTO() {}

    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }

    public Long getTotalOrders() { return totalOrders; }
    public void setTotalOrders(Long totalOrders) { this.totalOrders = totalOrders; }

    public Long getTotalRestaurants() { return totalRestaurants; }
    public void setTotalRestaurants(Long totalRestaurants) { this.totalRestaurants = totalRestaurants; }

    public Long getTotalCustomers() { return totalCustomers; }
    public void setTotalCustomers(Long totalCustomers) { this.totalCustomers = totalCustomers; }

    public Long getTotalFoodItems() { return totalFoodItems; }
    public void setTotalFoodItems(Long totalFoodItems) { this.totalFoodItems = totalFoodItems; }

    public Long getPendingOrdersCount() { return pendingOrdersCount; }
    public void setPendingOrdersCount(Long pendingOrdersCount) { this.pendingOrdersCount = pendingOrdersCount; }

    public List<OrderResponseDTO> getRecentOrders() { return recentOrders; }
    public void setRecentOrders(List<OrderResponseDTO> recentOrders) { this.recentOrders = recentOrders; }
}

