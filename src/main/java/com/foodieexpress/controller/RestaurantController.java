package com.foodieexpress.controller;

import com.foodieexpress.model.FoodItem;
import com.foodieexpress.model.Restaurant;
import com.foodieexpress.service.FoodItemService;
import com.foodieexpress.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/restaurants")
@CrossOrigin(origins = "*")
public class RestaurantController {

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private FoodItemService foodItemService;

    // 1. Get all active restaurants
    @GetMapping
    public List<Restaurant> getAllRestaurants() {
        return restaurantService.getAllRestaurants();
    }

    // 2. Get restaurant by ID
    @GetMapping("/{id}")
    public ResponseEntity<Restaurant> getRestaurantById(@PathVariable Long id) {
        return ResponseEntity.ok(restaurantService.getRestaurantById(id));
    }

    // 3. Search restaurants by name or cuisine
    @GetMapping("/search")
    public List<Restaurant> searchRestaurants(@RequestParam String query) {
        return restaurantService.searchRestaurants(query);
    }

    // 4. Get active menu dishes for a specific restaurant
    @GetMapping("/{id}/menu")
    public List<FoodItem> getRestaurantMenu(@PathVariable Long id) {
        return foodItemService.getMenuByRestaurant(id);
    }

    // 5. Create a new restaurant
    @PostMapping
    public ResponseEntity<Restaurant> createRestaurant(@RequestBody Restaurant restaurant) {
        Restaurant created = restaurantService.createRestaurant(restaurant);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // 6. Update restaurant details
    @PutMapping("/{id}")
    public ResponseEntity<Restaurant> updateRestaurant(@PathVariable Long id, @RequestBody Restaurant restaurant) {
        Restaurant updated = restaurantService.updateRestaurant(id, restaurant);
        return ResponseEntity.ok(updated);
    }

    // 7. Delete (soft-delete) a restaurant
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRestaurant(@PathVariable Long id) {
        restaurantService.deleteRestaurant(id);
        return ResponseEntity.ok("Restaurant with id " + id + " deactivated successfully.");
    }
}
