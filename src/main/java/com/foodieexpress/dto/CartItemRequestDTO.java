package com.foodieexpress.dto;

public class CartItemRequestDTO {
    private Long foodItemId;
    private Integer quantity = 1;

    public CartItemRequestDTO() {}

    public Long getFoodItemId() { return foodItemId; }
    public void setFoodItemId(Long foodItemId) { this.foodItemId = foodItemId; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}

