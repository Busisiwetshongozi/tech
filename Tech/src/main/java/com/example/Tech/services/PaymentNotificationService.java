package com.example.Tech.services;

import com.example.Tech.entities.Order;
import com.example.Tech.enums.OrderStatus;
import com.example.Tech.exceptions.PaymentVerificationException;
import com.example.Tech.repos.OrderRepo;
import com.example.Tech.utils.PayFastUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentNotificationService {

    private final OrderRepo orderRepo;

    @Value("${payfast.passphrase}")
    private String passphrase;

    public String processNotification(Map<String, String> params) {
        try {
            log.info("Received PayFast notification: {}", params);

            if (!verifySignature(params)) {
                log.warn("Invalid signature for PayFast notification");
                throw new PaymentVerificationException("Invalid signature");
            }

            String orderId = params.get("m_payment_id");
            String paymentStatus = params.get("payment_status");
            String amountGross = params.get("amount_gross");

            Order order = orderRepo.findById(Long.parseLong(orderId))
                    .orElseThrow(() -> new PaymentVerificationException("Order not found"));

            if (!order.getTotalAmount().toPlainString().equals(amountGross)) {
                log.warn("Amount mismatch. Expected: {}, Received: {}", order.getTotalAmount(), amountGross);
                throw new PaymentVerificationException("Amount mismatch");
            }

            if ("COMPLETE".equalsIgnoreCase(paymentStatus)) {
                order.setStatus(OrderStatus.PAID);
            } else {
                order.setStatus(OrderStatus.FAILED);
            }

            order.setUpdatedAt(java.time.LocalDateTime.now());
            orderRepo.save(order);

            return "OK";

        } catch (Exception e) {
            log.error("Error processing PayFast notification", e);
            return "ERROR";
        }
    }

    private boolean verifySignature(Map<String, String> params) {
        try {
            String receivedSignature = params.get("signature");
            String calculatedSignature = PayFastUtil.generateSignature(params, passphrase);
            return receivedSignature != null && receivedSignature.equals(calculatedSignature);
        } catch (Exception e) {
            log.error("Signature verification failed", e);
            return false;
        }
    }
}
