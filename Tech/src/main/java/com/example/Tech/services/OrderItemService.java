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
    public OrderItem createOrderItem(Long productId, int quantity, Long orderId) {
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> null);

        Order order = orderId != null ? orderRepo.findById(orderId)
                .orElse(null) : null;

        OrderItem orderItem = new OrderItem();
        orderItem.setQuantity(quantity);
        orderItem.setUnitPrice(product.getPrice()); // Use current product price
        orderItem.setProduct(product);
        orderItem.setOrder(order);

        return orderItemRepo.save(orderItem);
    }

    // Batch create order items
    public List<OrderItem> createOrderItems(List<OrderItemDto> itemDtos, Long orderId) {
        return itemDtos.stream()
                .map(dto -> createOrderItem(
                        dto.getProductId(),
                        dto.getQuantity(),
                        orderId))
                .collect(Collectors.toList());
    }

    // Get order item by ID
    public OrderItem getOrderItemById(Long id) {
        return orderItemRepo.findById(id)
                .orElseThrow(() -> null);
    }

    // Get all order items for a specific order
    public List<OrderItem> getOrderItemsByOrderId(Long orderId) {
        return orderItemRepo.findByOrderId(orderId);
    }

    // Update order item
    public OrderItem updateOrderItem(Long id, OrderItemDto itemDto) {
        OrderItem existingItem = getOrderItemById(id);

        if (itemDto.getProductId() != null) {
            Product product = productRepo.findById(itemDto.getProductId())
                    .orElseThrow(() -> null);
            existingItem.setProduct(product);
            existingItem.setUnitPrice(product.getPrice()); // Update price if product changed
        }

        if (itemDto.getQuantity() > 0) {
            existingItem.setQuantity(itemDto.getQuantity());
        }

        return orderItemRepo.save(existingItem);
    }

    // Delete order item
    public void deleteOrderItem(Long id) {
        if (!orderItemRepo.existsById(id))
        orderItemRepo.deleteById(id);
    }

    // Calculate subtotal for an order item
    public BigDecimal calculateSubtotal(Long orderItemId) {
        OrderItem item = getOrderItemById(orderItemId);
        return BigDecimal.valueOf(item.getUnitPrice())
                .multiply(BigDecimal.valueOf(item.getQuantity()));
    }

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
