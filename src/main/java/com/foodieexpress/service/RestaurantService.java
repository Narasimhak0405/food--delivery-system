package com.foodieexpress.service;

import com.foodieexpress.model.Restaurant;
import java.util.List;

public interface RestaurantService {
    List<Restaurant> getAllRestaurants();
    Restaurant getRestaurantById(Long id);
    Restaurant createRestaurant(Restaurant restaurant);
    Restaurant updateRestaurant(Long id, Restaurant updatedDetails);
    void deleteRestaurant(Long id);
    List<Restaurant> searchRestaurants(String query);
}

