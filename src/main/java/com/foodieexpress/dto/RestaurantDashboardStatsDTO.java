package com.foodieexpress.dto;

import java.math.BigDecimal;
import java.util.List;

public class RestaurantDashboardStatsDTO {
    private String restaurantName;
    private BigDecimal todayRevenue;
    private Long todayOrdersCount;
    private Long totalMenuDishes;
    private Long activeOrdersCount;
    private List<OrderResponseDTO> liveOrders;

    public RestaurantDashboardStatsDTO() {}

    public String getRestaurantName() { return restaurantName; }
    public void setRestaurantName(String restaurantName) { this.restaurantName = restaurantName; }

    public BigDecimal getTodayRevenue() { return todayRevenue; }
    public void setTodayRevenue(BigDecimal todayRevenue) { this.todayRevenue = todayRevenue; }

    public Long getTodayOrdersCount() { return todayOrdersCount; }
    public void setTodayOrdersCount(Long todayOrdersCount) { this.todayOrdersCount = todayOrdersCount; }

    public Long getTotalMenuDishes() { return totalMenuDishes; }
    public void setTotalMenuDishes(Long totalMenuDishes) { this.totalMenuDishes = totalMenuDishes; }

    public Long getActiveOrdersCount() { return activeOrdersCount; }
    public void setActiveOrdersCount(Long activeOrdersCount) { this.activeOrdersCount = activeOrdersCount; }

    public List<OrderResponseDTO> getLiveOrders() { return liveOrders; }
    public void setLiveOrders(List<OrderResponseDTO> liveOrders) { this.liveOrders = liveOrders; }
}

