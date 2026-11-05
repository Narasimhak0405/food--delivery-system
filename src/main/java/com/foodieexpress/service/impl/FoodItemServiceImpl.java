package com.foodieexpress.service.impl;

import com.foodieexpress.exception.ResourceNotFoundException;
import com.foodieexpress.model.FoodItem;
import com.foodieexpress.repository.FoodItemRepository;
import com.foodieexpress.service.FoodItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FoodItemServiceImpl implements FoodItemService {

    @Autowired
    private FoodItemRepository foodItemRepository;

    @Override
    public List<FoodItem> getAllFoodItems() {
        return foodItemRepository.findAll();
    }

    @Override
    public FoodItem getFoodItemById(Long id) {
        return foodItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Food item not found with id: " + id));
    }

    @Override
    public List<FoodItem> getMenuByRestaurant(Long restaurantId) {
        return foodItemRepository.findByRestaurantIdAndIsAvailableTrue(restaurantId);
    }

    @Override
    public FoodItem createFoodItem(FoodItem foodItem) {
        return foodItemRepository.save(foodItem);
    }

    @Override
    public FoodItem updateFoodItem(Long id, FoodItem updatedDetails) {
        FoodItem existing = getFoodItemById(id);
        existing.setName(updatedDetails.getName());
        existing.setDescription(updatedDetails.getDescription());
        existing.setPrice(updatedDetails.getPrice());
        existing.setIsVeg(updatedDetails.getIsVeg());
        existing.setIsAvailable(updatedDetails.getIsAvailable());
        existing.setImageUrl(updatedDetails.getImageUrl());
        return foodItemRepository.save(existing);
    }

    @Override
    public void deleteFoodItem(Long id) {
        FoodItem existing = getFoodItemById(id);
        foodItemRepository.delete(existing);
    }

    @Override
    public List<FoodItem> searchFoodItems(String query) {
        return foodItemRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query);
    }
}

