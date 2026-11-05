package com.foodieexpress.dto;

public class PaymentVerificationResponseDTO {
    private String status;
    private String message;
    private String orderNumber;
    private String transactionId;
    private String paymentStatus;

    public PaymentVerificationResponseDTO() {}

    public PaymentVerificationResponseDTO(String status, String message, String orderNumber, String transactionId, String paymentStatus) {
        this.status = status;
        this.message = message;
        this.orderNumber = orderNumber;
        this.transactionId = transactionId;
        this.paymentStatus = paymentStatus;
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
}

