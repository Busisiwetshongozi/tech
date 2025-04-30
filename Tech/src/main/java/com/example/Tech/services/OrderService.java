package com.example.Tech.services;

import com.example.Tech.entities.Order;
import com.example.Tech.entities.OrderItem;
import com.example.Tech.entities.User;
import com.example.Tech.enums.OrderStatus;
import com.example.Tech.repos.OrderRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {
    private final OrderRepo orderRepo;
    @Autowired
    public OrderService(OrderRepo orderRepo) {
        this.orderRepo = orderRepo;
}


    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal(); // Cast to your User class
    }

    // Create a new order with validation
    @Transactional
    public Order createOrder(List<OrderItem> items) {
        Order order = new Order();
        order.setUser(getCurrentUser());
        order.setStatus(OrderStatus.PENDING);
        order.setOrderNumber("ORD-" + UUID.randomUUID());
        order.setOrderDate(LocalDateTime.now());

        items.forEach(item -> item.setOrder(order));
        order.setItems(items);

        order.setTotalAmount(items.stream()
                .mapToDouble(i -> i.getUnitPrice() * i.getQuantity())
                .sum());

        return orderRepo.save(order);
    }

    // Get order by ID
    public Order getOrderById(Long orderId) {
        User loggedInUser = getCurrentUser();  // Now returns your custom User entity
        return orderRepo.findByIdAndUser(orderId, loggedInUser)
                .orElseThrow(() -> new RuntimeException("Order not found or unauthorized"));
    }

    // Get all orders for a user
    public List<Order> getCurrentUserOrders() {
        User user = getCurrentUser();
        return orderRepo.findByUser(user); // Or use orderRepo.findByUserId(user.getId());
    }

    // Update order status (e.g., "SHIPPED", "CANCELLED")
    @Transactional
    public Order updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = getOrderById(orderId); // Already checks user ownership
        order.setStatus(newStatus);
        return orderRepo.save(order);
    }

    // Delete order (cascade deletes OrderItems due to CascadeType.ALL)
    @Transactional
    public void deleteOrder(Long orderId) {
        Order order = getOrderById(orderId); // Checks user ownership
        orderRepo.delete(order);
    }
}
