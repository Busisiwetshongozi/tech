package com.example.Tech.dtos;

import lombok.Data;
import java.util.Map;

@Data
public class PaymentInitiationResponse {
    private Long orderId;
    private String paymentUrl;
    private Map<String, String> paymentParameters;
    private String message; // New field for error message

    // Constructor for success
    public PaymentInitiationResponse(Long orderId, String paymentUrl, Map<String, String> paymentParameters) {
        this.orderId = orderId;
        this.paymentUrl = paymentUrl;
        this.paymentParameters = paymentParameters;
    }

    // Constructor for error message
    public PaymentInitiationResponse(String message) {
        this.message = message;
    }
}
