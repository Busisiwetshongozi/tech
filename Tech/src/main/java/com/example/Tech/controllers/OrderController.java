package com.example.Tech.controllers;

import com.example.Tech.dtos.OrderItemDTO;
import com.example.Tech.dtos.OrderRequestWrapper;
import com.example.Tech.entities.Order;
import com.example.Tech.exceptions.NotFoundException;
import com.example.Tech.exceptions.OrderProcessingException;
import com.example.Tech.services.AuthService;
import com.example.Tech.services.OrderService;
import com.google.firebase.auth.FirebaseAuthException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;
    private final AuthService authService;

    @Autowired
    public OrderController(OrderService orderService, AuthService authService) {
        this.orderService = orderService;
        this.authService = authService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createOrder(
            @RequestBody OrderRequestWrapper request,
            @RequestHeader("Authorization") String authHeader) {

        try {
            // 1. Extract and verify token
            String idToken = authHeader.replace("Bearer ", "");
            String firebaseUid = authService.getUidFromToken(idToken); // MUST return UID

            // 2. Process items
            List<OrderItemDTO> items = request.getItem() != null
                    ? List.of(request.getItem())
                    : request.getItems();

            // 3. Create order WITH the verified UID
            Order order = orderService.createOrderForUser(firebaseUid, items);
            return ResponseEntity.ok(order);

        } catch (FirebaseAuthException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");

        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(e.getMessage());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred: " + e.getMessage());
        }
    }
    @GetMapping("/user")
    public ResponseEntity<?>  getOrdersByFirebaseUid(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            // Extract token by removing "Bearer " prefix
            String idToken = authorizationHeader.replace("Bearer ", "").trim();

            // Get UID from token
            String firebaseUid = authService.getUidFromToken(idToken);

            // Fetch orders
            List<Order> orders = orderService.getOrdersByFirebaseUid(firebaseUid);
            return ResponseEntity.ok(orders);

        } catch (FirebaseAuthException e) {
            return ResponseEntity.status(401).body("Invalid or expired token.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to fetch orders: " + e.getMessage());
        }
    }

}