package com.example.Tech.controllers;

import com.example.Tech.entities.Order;
import com.example.Tech.entities.OrderItem;
import com.example.Tech.enums.OrderStatus;
import com.example.Tech.services.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // Create a new order
// Control
    @PostMapping("/create")
    public ResponseEntity<Order> createOrder(@RequestBody List<OrderItem> items) {
        Order order = orderService.createOrder(items);
        return ResponseEntity.ok(order);
    }

    // Get a specific order (only if owned by user)
    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrder(@PathVariable Long orderId) {
        Order order = orderService.getOrderById(orderId);
        return ResponseEntity.ok(order);
    }

    // Get all orders for the logged-in user
    @GetMapping
    public ResponseEntity<List<Order>> getUserOrders() {
        List<Order> orders = orderService.getCurrentUserOrders();
        return ResponseEntity.ok(orders);
    }

    // Update order status (e.g., "CANCELLED")
    @PatchMapping("/{orderId}/status")
    public ResponseEntity<Order> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam OrderStatus newStatus) {
        Order order = orderService.updateOrderStatus(orderId, newStatus);
        return ResponseEntity.ok(order);
    }

    // Delete an order (cascades to items)
    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long orderId) {
        orderService.deleteOrder(orderId);
        return ResponseEntity.noContent().build();
    }
}
