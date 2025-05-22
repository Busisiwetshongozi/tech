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
    private final PayFastService payFastService; // Inject PayFastService

    @Value("${payfast.merchant-key}")
    private String merchantKey;

    @Value("${payfast.passphrase}")
    private String passphrase;

    // Constructor-based dependency injection
    public OrderService(OrderRepo orderRepo, UserRepo userRepo, ProductRepo productRepo, PayFastService payFastService) {
        this.orderRepo = orderRepo;
        this.userRepo = userRepo;
        this.productRepo = productRepo;
        this.payFastService = payFastService;
    }

    // Create an order for a user
    @Transactional
    public Order createOrderForUser(String firebaseUid,
                                    List<OrderItemDTO> orderItemDTOs,
                                    OrderStatus status) {
        // Fetch the user from your internal DB using Firebase UID
        User user = userRepo.findByFirebaseUid(firebaseUid)
                .orElseThrow(() -> new NotFoundException("User not found"));

        // Create a new Order
        Order order = new Order();
        order.setUser(user); // Associate the order with the user
        order.setStatus(status);

        // Set the customer email from the currently logged-in user's email
        order.setCustomerEmail(user.getEmail()); // Assuming the 'User' entity has an email field

        // Add items to the order based on the provided DTOs
        for (OrderItemDTO dto : orderItemDTOs) {
            Product product = productRepo.findById(dto.getProductId())
                    .orElseThrow(() -> new NotFoundException("Product not found"));

            OrderItem item = new OrderItem();
            item.setProduct(product);
            item.setQuantity(dto.getQuantity());

            // Set the unit price of the item
            item.setUnitPrice(BigDecimal.valueOf(product.getPrice())); // Assuming price is double

            // Add the item to the order
            order.addItem(item);
        }

        // Recalculate the total amount for the order after adding all items
        order.recalculateTotal();

        // Save and return the order
        return orderRepo.save(order);
    }

    // Get orders for a user by their Firebase UID
    public List<Order> getOrdersByFirebaseUid(String firebaseUid) {
        log.info("Fetching orders for Firebase UID: {}", firebaseUid);

        User user = userRepo.findByFirebaseUid(firebaseUid)
                .orElseThrow(() -> {
                    log.error("User not found for Firebase UID: {}", firebaseUid);
                    return new NotFoundException("User not found");
                });

        List<Order> orders = orderRepo.findByUser(user);
        log.info("Found {} orders for user ID: {}", orders.size(), user.getId());

        return orders;
    }

    // Get order details by orderId and Firebase UID
    public Order getOrderByIdAndUser(Long orderId, String firebaseUid) throws NotFoundException {
        log.info("Fetching order ID: {} for Firebase UID: {}", orderId, firebaseUid);

        User user = userRepo.findByFirebaseUid(firebaseUid)
                .orElseThrow(() -> {
                    log.error("User not found for Firebase UID: {}", firebaseUid);
                    return new NotFoundException("User not found");
                });

        return orderRepo.findByIdAndUser(orderId, user)
                .orElseThrow(() -> {
                    log.error("Order not found with ID: {} for user ID: {}", orderId, user.getId());
                    return new NotFoundException("Order not found for user");
                });
    }
    public PaymentInitiationResponse initiatePayment(Order order, String baseUrl) throws OrderProcessingException {
        try {
            // Check if the order is in a payable state (PENDING)
            if (order.getStatus() != OrderStatus.PENDING) {
                // If not, throw an OrderProcessingException with a meaningful message
                String errorMessage = String.format("Order with ID %d is not in a payable state. Current status: %s", order.getId(), order.getStatus());
                log.error(errorMessage); // Log the error for debugging purposes
                throw new OrderProcessingException(errorMessage);
            }

            // Delegate the payment initiation process to PayFastService
            log.info("Initiating payment for order ID: {} to PayFast", order.getId());
            return payFastService.initiatePaymentForm(order, baseUrl);

        } catch (OrderProcessingException e) {
            // Handle known exception for order status issue (log and rethrow)
            log.error("Order processing failed for order ID: {}: {}", order.getId(), e.getMessage());
            throw e;  // Rethrow the custom exception as it’s application-specific

        } catch (Exception e) {
            // Catch any other unexpected exceptions, log them and throw a runtime exception
            log.error("Unexpected error during payment initiation for order ID: {}: {}", order.getId(), e.getMessage(), e);
            throw new RuntimeException("Error occurred while initiating payment for order ID: " + order.getId(), e);
        }
    }




    // Additional helper method to get order by ID
    public Order getOrderById(Long orderId) throws NotFoundException {
        return orderRepo.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found"));
    }

    // Method to verify payment notification (this logic can be moved here)
    public String handlePaymentNotification(Map<String, String> params) {
        try {
            // Verify the PayFast notification signature
            if (!verifySignature(params)) {
                throw new OrderProcessingException("Invalid signature received from PayFast");
            }

            // Extract the orderId and paymentStatus from the notification parameters
            String orderId = params.get("m_payment_id");
            String paymentStatus = params.get("payment_status");

            if (orderId == null || paymentStatus == null) {
                throw new IllegalArgumentException("Missing order ID or payment status in the notification");
            }

            // Fetch the order from the database using the order ID
            Order order = orderRepo.findById(Long.parseLong(orderId))
                    .orElseThrow(() -> new NotFoundException("Order with ID " + orderId + " not found"));

            // Based on the payment status, update the order status accordingly
            if ("COMPLETE".equals(paymentStatus)) {
                order.setStatus(OrderStatus.PAID);
                log.info("Payment for order ID {} completed successfully", orderId);
            } else {
                order.setStatus(OrderStatus.FAILED);
                log.warn("Payment for order ID {} failed", orderId);
            }

            // Save the updated order
            orderRepo.save(order);

            // Respond with "OK" as PayFast expects this string for a successful notification
            return "OK";
        } catch (OrderProcessingException e) {
            log.error("Payment notification failed due to order processing error: {}", e.getMessage());
            return "ERROR";  // Return error status in case of invalid signature or other processing issues
        } catch (IllegalArgumentException e) {
            log.error("Invalid notification data: {}", e.getMessage());
            return "ERROR";
        } catch (NotFoundException e) {
            log.error("Order not found: {}", e.getMessage());
            return "ERROR";
        } catch (Exception e) {
            log.error("Unexpected error occurred while processing payment notification: {}", e.getMessage(), e);
            return "ERROR";
        }
    }

    // Signature verification logic
    private boolean verifySignature(Map<String, String> params) {
        try {
            String receivedSignature = params.get("signature");
            String calculatedSignature = PayFastUtil.generateSignature(params, passphrase);
            return calculatedSignature.equals(receivedSignature);
        } catch (Exception e) {
            return false;
        }
    }
}
