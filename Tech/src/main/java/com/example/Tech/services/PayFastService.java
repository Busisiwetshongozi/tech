package com.example.Tech.services;

import com.example.Tech.controllers.PayFastController.PaymentRequest;
import com.example.Tech.dtos.PaymentInitiationResponse;
import com.example.Tech.entities.Order;
import com.example.Tech.repos.OrderRepo;
import com.example.Tech.utils.PayFastUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PayFastService {

    private final OrderRepo orderRepo;

    @Value("${payfast.merchant-id}")
    private String merchantId;

    @Value("${payfast.merchant-key}")
    private String merchantKey;

    @Value("${payfast.sandbox-url}")
    private String payfastUrl;

    @Value("${payfast.passphrase:}")
    private String passphrase;

    @Value("${app.base-url}")
    private String baseUrl;

    public PaymentInitiationResponse initiatePaymentForm(Order order, String baseUrl) throws Exception {
        // Validate the order before processing
        validateOrder(order);

        // Prepare parameters for PayFast
        Map<String, String> parameters = new LinkedHashMap<>();
        parameters.put("merchant_id", merchantId);
        parameters.put("merchant_key", merchantKey);
        parameters.put("amount", order.getTotalAmount().toPlainString());
        parameters.put("item_name", order.getDescription() != null ? order.getDescription() : "No description");
        parameters.put("return_url", baseUrl + "/payment/success?orderId=" + order.getId());
        parameters.put("cancel_url", baseUrl + "/payment/cancel?orderId=" + order.getId());
        parameters.put("notify_url", baseUrl + "/api/payments/notify");
        parameters.put("email_address", order.getCustomerEmail());
        parameters.put("m_payment_id", order.getId().toString());

        // Generate PayFast signature
        String signature = PayFastUtil.generateSignature(parameters, passphrase);
        parameters.put("signature", signature);

        // Generate the payment URL for PayFast (this is where the user will be redirected)
        String paymentUrl = generateHtmlForm(parameters); // Assuming this generates the correct URL for PayFast.

        // Return a PaymentInitiationResponse containing the orderId, paymentUrl, and parameters
        return new PaymentInitiationResponse(order.getId(), paymentUrl, parameters);
    }


    private void validateOrder(Order order) {
        if (order == null || order.getTotalAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Invalid order");
        }
    }



    private void validateRequest(PaymentRequest request) {
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Invalid amount");
        }
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new IllegalArgumentException("Missing email");
        }
        if (request.getItemName() == null || request.getItemName().isBlank()) {
            throw new IllegalArgumentException("Missing item name");
        }
    }

    private Order createOrder(String email, String itemName, BigDecimal amount) {
        Order order = new Order();
        order.setCustomerEmail(email);
        order.setDescription(itemName);
        order.setTotalAmount(amount);
        return orderRepo.save(order);
    }

    private String generateHtmlForm(Map<String, String> parameters) {
        StringBuilder form = new StringBuilder();
        form.append("<html><body onload='document.forms[0].submit()'>")
                .append("<form action='").append(payfastUrl).append("' method='post'>");

        parameters.forEach((key, value) -> {
            String encodedValue = URLEncoder.encode(value, StandardCharsets.UTF_8);
            form.append("<input type='hidden' name='").append(key)
                    .append("' value='").append(encodedValue).append("'/>");
        });

        form.append("</form></body></html>");
        return form.toString();
    }
}
