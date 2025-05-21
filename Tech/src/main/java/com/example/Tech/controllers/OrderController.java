package com.example.Tech.controllers;

import com.example.Tech.dtos.OrderItemDTO;
import com.example.Tech.dtos.OrderRequestWrapper;
import com.example.Tech.dtos.PaymentInitiationResponse;
import com.example.Tech.entities.Order;
import com.example.Tech.enums.OrderStatus;
import com.example.Tech.exceptions.*;
import com.example.Tech.services.OrderService;
import com.example.Tech.services.PayFastService;
import com.example.Tech.services.AuthService;
import com.google.firebase.auth.FirebaseAuthException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final AuthService authService;
    private final PayFastService payFastService;

    @Value("${app.base-url}")
    private String baseUrl;

    // Constructor for dependency injection
    public OrderController(OrderService orderService,
                           AuthService authService,
                           PayFastService payFastService) {
        this.orderService = orderService;
        this.authService = authService;
        this.payFastService = payFastService;
    }

    // Create order for a user
    @PostMapping("/create")
    public ResponseEntity<?> createOrder(
            @RequestBody OrderRequestWrapper request,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String firebaseUid = authService.getUidFromToken(authHeader.replace("Bearer ", ""));
            List<OrderItemDTO> items = request.getItem() != null
                    ? List.of(request.getItem()) : request.getItems();

            Order order = orderService.createOrderForUser(firebaseUid, items, OrderStatus.PENDING);
            return ResponseEntity.ok(order);

        } catch (FirebaseAuthException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Order creation failed: " + e.getMessage());
        }
    }

    // Initiate payment for an order
    @PostMapping("/{orderId}/initiate-payment")
    public ResponseEntity<?> initiatePayment(
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
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (OrderProcessingException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Payment initiation failed: " + e.getMessage());
        }
    }

    // Get orders for a specific user
    @GetMapping("/user")
    public ResponseEntity<?> getOrdersByFirebaseUid(
            @RequestHeader("Authorization") String authorizationHeader) {
        try {
            String firebaseUid = authService.getUidFromToken(authorizationHeader.replace("Bearer ", "").trim());
            List<Order> orders = orderService.getOrdersByFirebaseUid(firebaseUid);
            return ResponseEntity.ok(orders);
        } catch (FirebaseAuthException e) {
            return ResponseEntity.status(401).body("Invalid or expired token.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to fetch orders: " + e.getMessage());
        }
    }

    // Get details of a specific order
    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrderDetails(
            @PathVariable Long orderId,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String firebaseUid = authService.getUidFromToken(authHeader.replace("Bearer ", ""));
            Order order = orderService.getOrderByIdAndUser(orderId, firebaseUid);
            return ResponseEntity.ok(order);
        } catch (FirebaseAuthException e) {
            return ResponseEntity.status(401).body("Invalid token");
        } catch (NotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching order");
        }
    }
}
