package com.foodieexpress.service;

import com.foodieexpress.model.FoodItem;
import java.util.List;

public interface FoodItemService {
    List<FoodItem> getAllFoodItems();
    FoodItem getFoodItemById(Long id);
    List<FoodItem> getMenuByRestaurant(Long restaurantId);
    FoodItem createFoodItem(FoodItem foodItem);
    FoodItem updateFoodItem(Long id, FoodItem updatedDetails);
    void deleteFoodItem(Long id);
    List<FoodItem> searchFoodItems(String query);
}

