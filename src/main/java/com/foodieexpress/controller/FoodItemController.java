package com.foodieexpress.controller;

import com.foodieexpress.model.FoodItem;
import com.foodieexpress.service.FoodItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/food-items")
@CrossOrigin(origins = "*")
public class FoodItemController {

    @Autowired
    private FoodItemService foodItemService;

    // 1. Get all food items (or filtered by restaurantId)
    @GetMapping
    public List<FoodItem> getAllFoodItems(@RequestParam(required = false) Long restaurantId) {
        if (restaurantId != null) {
            return foodItemService.getMenuByRestaurant(restaurantId);
        }
        return foodItemService.getAllFoodItems();
    }

    // 2. Get food item by ID
    @GetMapping("/{id}")
    public ResponseEntity<FoodItem> getFoodItemById(@PathVariable Long id) {
        return ResponseEntity.ok(foodItemService.getFoodItemById(id));
    }

    // 3. Search food items by name or description
    @GetMapping("/search")
    public List<FoodItem> searchFoodItems(@RequestParam String query) {
        return foodItemService.searchFoodItems(query);
    }

    // 4. Create new food item
    @PostMapping
    public ResponseEntity<FoodItem> createFoodItem(@RequestBody FoodItem foodItem) {
        FoodItem created = foodItemService.createFoodItem(foodItem);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // 5. Update food item
    @PutMapping("/{id}")
    public ResponseEntity<FoodItem> updateFoodItem(@PathVariable Long id, @RequestBody FoodItem foodItem) {
        FoodItem updated = foodItemService.updateFoodItem(id, foodItem);
        return ResponseEntity.ok(updated);
    }

    // 6. Delete food item
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteFoodItem(@PathVariable Long id) {
        foodItemService.deleteFoodItem(id);
        return ResponseEntity.ok("Food item with id " + id + " deleted successfully.");
    }
}
