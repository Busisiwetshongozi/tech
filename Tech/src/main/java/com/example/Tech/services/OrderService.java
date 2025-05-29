package com.example.Tech.services;

import com.example.Tech.dtos.OrderItemDTO;
import com.example.Tech.dtos.PaymentInitiationResponse;
import com.example.Tech.entities.Order;
import com.example.Tech.entities.OrderItem;
import com.example.Tech.entities.Product;
import com.example.Tech.entities.User;
import com.example.Tech.enums.OrderStatus;
import com.example.Tech.exceptions.NotFoundException;
import com.example.Tech.exceptions.OrderProcessingException;
import com.example.Tech.repos.OrderRepo;
import com.example.Tech.repos.ProductRepo;
import com.example.Tech.repos.UserRepo;
import com.example.Tech.utils.PayFastUtil;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class OrderService {
    private final OrderRepo orderRepo;
    private final UserRepo userRepo;
    private final ProductRepo productRepo;
    private final PayFastService payFastService;
    private final ProductService productService;

    @Value("${payfast.merchant-key}")
    private String merchantKey;

    @Value("${payfast.passphrase}")
    private String passphrase;

    public OrderService(
            OrderRepo orderRepo,
            UserRepo userRepo,
            ProductRepo productRepo,
            PayFastService payFastService,
            ProductService productService
    ) {
        this.orderRepo = orderRepo;
        this.userRepo = userRepo;
        this.productRepo = productRepo;
        this.payFastService = payFastService;
        this.productService = productService;
    }

    @Transactional
    public Order createOrderForUser(String firebaseUid,
                                    List<OrderItemDTO> orderItemDTOs,
                                    OrderStatus status) {
        User user = userRepo.findByFirebaseUid(firebaseUid)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Order order = new Order();
        order.setUser(user);
        order.setStatus(status);
        order.setCustomerEmail(user.getEmail());

        for (OrderItemDTO dto : orderItemDTOs) {
            Product product = productRepo.findById(dto.getProductId())
                    .orElseThrow(() -> new NotFoundException("Product not found"));

            double discountPercent = product.getDiscountPercentage() != null ? product.getDiscountPercentage() : 0;
            double discountedPrice = product.getPrice() * (1 - discountPercent / 100.0);

            OrderItem item = new OrderItem();
            item.setProduct(product);
            item.setQuantity(dto.getQuantity());
            item.setUnitPrice(BigDecimal.valueOf(discountedPrice));

            order.addItem(item);
        }

        order.recalculateTotal();
        return orderRepo.save(order);
    }

    public List<Order> getOrdersByFirebaseUid(String firebaseUid) {
        log.info("Fetching orders for Firebase UID: {}", firebaseUid);

        User user = userRepo.findByFirebaseUid(firebaseUid)
                .orElseThrow(() -> new NotFoundException("User not found"));

        List<Order> orders = orderRepo.findByUser(user);
        log.info("Found {} orders for user ID: {}", orders.size(), user.getId());

        return orders;
    }

    public Order getOrderByIdAndUser(Long orderId, String firebaseUid) {
        User user = userRepo.findByFirebaseUid(firebaseUid)
                .orElseThrow(() -> new NotFoundException("User not found"));

        return orderRepo.findByIdAndUser(orderId, user)
                .orElseThrow(() -> new NotFoundException("Order not found for user"));
    }

    public PaymentInitiationResponse initiatePayment(Order order, String baseUrl) throws OrderProcessingException {
        try {
            if (order.getStatus() != OrderStatus.PENDING) {
                throw new OrderProcessingException("Order is not in a payable state");
            }

            return payFastService.initiatePaymentForm(order, baseUrl);
        } catch (OrderProcessingException e) {
            log.error("Order processing failed for order ID {}: {}", order.getId(), e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during payment initiation for order ID {}: {}", order.getId(), e.getMessage(), e);
            throw new RuntimeException("Error occurred while initiating payment", e);
        }
    }

    public Order getOrderById(Long orderId) {
        return orderRepo.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found"));
    }

    public String handlePaymentNotification(Map<String, String> params) {
        try {
            if (!verifySignature(params)) {
                throw new OrderProcessingException("Invalid signature received from PayFast");
            }

            String orderId = params.get("m_payment_id");
            String paymentStatus = params.get("payment_status");

            if (orderId == null || paymentStatus == null) {
                throw new IllegalArgumentException("Missing order ID or payment status in the notification");
            }

            Order order = orderRepo.findById(Long.parseLong(orderId))
                    .orElseThrow(() -> new NotFoundException("Order with ID " + orderId + " not found"));

            if ("COMPLETE".equals(paymentStatus)) {
                order.setStatus(OrderStatus.PAID);
                log.info("Payment for order ID {} completed successfully", orderId);
            } else {
                order.setStatus(OrderStatus.FAILED);
                log.warn("Payment for order ID {} failed", orderId);
            }

            orderRepo.save(order);
            return "OK";
        } catch (Exception e) {
            log.error("Payment notification error: {}", e.getMessage(), e);
            return "ERROR";
        }
    }

    private boolean verifySignature(Map<String, String> params) {
        try {
            String receivedSignature = params.get("signature");
            String calculatedSignature = PayFastUtil.generateSignature(params, passphrase);
            return calculatedSignature.equals(receivedSignature);
        } catch (Exception e) {
            return false;
        }
    }

    public List<Order> getAllOrders() {
        return orderRepo.findAll();
    }
}
