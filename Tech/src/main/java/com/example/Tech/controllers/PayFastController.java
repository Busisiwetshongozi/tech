package com.example.Tech.controllers;

import com.example.Tech.dtos.PaymentInitiationResponse;
import com.example.Tech.entities.Order;
import com.example.Tech.enums.OrderStatus;
import com.example.Tech.exceptions.NotFoundException;
import com.example.Tech.exceptions.OrderProcessingException;
import com.example.Tech.services.AuthService;
import com.example.Tech.services.OrderService;
import com.example.Tech.services.PayFastService;
import com.google.firebase.auth.FirebaseAuthException;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/payfast")
@RequiredArgsConstructor
public class PayFastController {
    @Value("${app.base-url}")
    private String baseUrl;

    private final PayFastService payFastService;
    private final AuthService authService;
    private final OrderService orderService;

    @PostMapping("/{orderId}/initiate")
    public ResponseEntity<PaymentInitiationResponse> initiatePayment(
            @PathVariable Long orderId,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String firebaseUid = authService.getUidFromToken(authHeader.replace("Bearer ", ""));
            Order order = orderService.getOrderByIdAndUser(orderId, firebaseUid);

            if (order.getStatus() != OrderStatus.PENDING) {
                throw new OrderProcessingException("Order is not in a payable state");
            }

            PaymentInitiationResponse response = payFastService.initiatePaymentForm(order, baseUrl);
            return ResponseEntity.ok(response);
        } catch (FirebaseAuthException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (OrderProcessingException | NotFoundException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            log.error("Payment initiation failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }



    @Data
    public static class PaymentRequest {
        private java.math.BigDecimal amount;
        private String itemName;
        private String email;
    }
}
