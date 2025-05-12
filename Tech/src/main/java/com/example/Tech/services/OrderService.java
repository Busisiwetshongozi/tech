package com.example.Tech.services;

import com.example.Tech.dtos.OrderItemDTO;
import com.example.Tech.entities.Order;
import com.example.Tech.entities.OrderItem;
import com.example.Tech.entities.Product;
import com.example.Tech.entities.User;
import com.example.Tech.repos.OrderRepo;
import com.example.Tech.repos.ProductRepo;
import com.example.Tech.repos.UserRepo;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Slf4j
@Service
public class OrderService {
    @Autowired
    private OrderRepo orderRepo;
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ProductRepo productRepo;

    @Transactional
    public Order createOrderForUser(String firebaseUid, List<OrderItemDTO> orderItemDTOs) {
        log.info("Attempting to create order for Firebase UID: {}", firebaseUid);

        // 1. Verify user exists
        User user = userRepo.findByFirebaseUid(firebaseUid)
                .orElseThrow(() -> {
                    log.error("User not found for Firebase UID: {}", firebaseUid);
                    return new RuntimeException("User not found");
                });

        log.info("Found user: ID={}, Email={}", user.getId(), user.getEmail());

        // 2. Create order
        Order order = new Order(user);
        log.info("Created order with user ID: {}", order.getUser().getId());

        // 3. Add items
        for (OrderItemDTO dto : orderItemDTOs) {
            Product product = productRepo.findById(dto.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            OrderItem item = new OrderItem();
            item.setProduct(product);
            item.setQuantity(dto.getQuantity());
            item.setUnitPrice(product.getPrice());
            order.addItem(item);
        }

        // 4. Save and verify
        Order savedOrder = orderRepo.save(order);
        log.info("Saved order ID: {} with user ID: {}",
                savedOrder.getId(),
                savedOrder.getUser().getId());

        return savedOrder;
    }
    public List<Order> getOrdersByFirebaseUid(String firebaseUid) {
        log.info("Fetching orders for Firebase UID: {}", firebaseUid);

        User user = userRepo.findByFirebaseUid(firebaseUid)
                .orElseThrow(() -> {
                    log.error("User not found for Firebase UID: {}", firebaseUid);
                    return new RuntimeException("User not found");
                });

        List<Order> orders = orderRepo.findByUser(user);
        log.info("Found {} orders for user ID: {}", orders.size(), user.getId());

        return orders;
    }












}

