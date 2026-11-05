package com.foodieexpress.service.impl;

import com.foodieexpress.exception.ResourceNotFoundException;
import com.foodieexpress.model.Restaurant;
import com.foodieexpress.repository.RestaurantRepository;
import com.foodieexpress.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RestaurantServiceImpl implements RestaurantService {

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Override
    public List<Restaurant> getAllRestaurants() {
        return restaurantRepository.findByIsActiveTrue();
    }

    @Override
    public Restaurant getRestaurantById(Long id) {
        return restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id: " + id));
    }

    @Override
    public Restaurant createRestaurant(Restaurant restaurant) {
        return restaurantRepository.save(restaurant);
    }

    @Override
    public Restaurant updateRestaurant(Long id, Restaurant updatedDetails) {
        Restaurant existing = getRestaurantById(id);
        existing.setName(updatedDetails.getName());
        existing.setCuisineType(updatedDetails.getCuisineType());
        existing.setDescription(updatedDetails.getDescription());
        existing.setAddress(updatedDetails.getAddress());
        existing.setPhone(updatedDetails.getPhone());
        existing.setRating(updatedDetails.getRating());
        existing.setDeliveryTimeMins(updatedDetails.getDeliveryTimeMins());
        existing.setMinOrderAmount(updatedDetails.getMinOrderAmount());
        existing.setImageUrl(updatedDetails.getImageUrl());
        return restaurantRepository.save(existing);
    }

    @Override
    public void deleteRestaurant(Long id) {
        Restaurant existing = getRestaurantById(id);
        existing.setIsActive(false); // Soft delete
        restaurantRepository.save(existing);
    }

    @Override
    public List<Restaurant> searchRestaurants(String query) {
        return restaurantRepository.findByNameContainingIgnoreCaseOrCuisineTypeContainingIgnoreCase(query, query);
    }
}

