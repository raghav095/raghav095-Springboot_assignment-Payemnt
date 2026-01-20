package com.assignment.assignment.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateOrderRequest {
    @NotBlank(message = "User ID is required")
    private String userId;

    public CreateOrderRequest() {}

    public CreateOrderRequest(String userId) {
        this.userId = userId;
    }

    // Getters and setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
