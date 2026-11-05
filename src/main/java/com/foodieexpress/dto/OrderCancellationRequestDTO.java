package com.foodieexpress.dto;

public class OrderCancellationRequestDTO {
    private String orderNumber;
    private String reason;

    public OrderCancellationRequestDTO() {}

    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}

