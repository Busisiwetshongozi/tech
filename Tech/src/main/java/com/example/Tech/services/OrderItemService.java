package com.example.Tech.services;

import com.example.Tech.entities.Order;
import com.example.Tech.entities.OrderItem;
import com.example.Tech.entities.Product;
import com.example.Tech.repos.OrderItemRepo;
import com.example.Tech.repos.OrderRepo;
import com.example.Tech.repos.ProductRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderItemService {

    private final OrderItemRepo orderItemRepo;
    private final ProductRepo productRepo;
    private final OrderRepo orderRepo;

    @Autowired
    public OrderItemService(OrderItemRepo orderItemRepo,
                            ProductRepo productRepo,
                            OrderRepo orderRepo) {
        this.orderItemRepo = orderItemRepo;
        this.productRepo = productRepo;
        this.orderRepo = orderRepo;
    }

    // Create single order item




    // Delete order item
    public void deleteOrderItem(Long id) {
        if (!orderItemRepo.existsById(id))
            orderItemRepo.deleteById(id);
    }

    // Calculate subtotal for an order item


    // DTO for order item operations
    public static class OrderItemDto {
        private Long productId;
        private int quantity;

        // Getters and setters
        public Long getProductId() {
            return productId;
        }

        public void setProductId(Long productId) {
            this.productId = productId;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }
    }
}
