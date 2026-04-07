package com.tuan.ridehub.service;

import com.tuan.ridehub.dto.request.SePayWebhookDto;
import com.tuan.ridehub.enums.PaymentStatus;
import com.tuan.ridehub.model.Payment;
import com.tuan.ridehub.model.Users;
import com.tuan.ridehub.repository.PaymentRepository;
import com.tuan.ridehub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class SePayService {

    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final UserService userService;

    @Transactional
    public void processWebhook(SePayWebhookDto webhook) {
        log.info("=== SePay Gateway IPN Received ===");
        log.info("Notification Type: {}", webhook.getNotificationType());

        if (!"ORDER_PAID".equals(webhook.getNotificationType())) {
            log.warn("Ignoring notification type: {}", webhook.getNotificationType());
            return;
        }

        SePayWebhookDto.SePayOrder order = webhook.getOrder();
        SePayWebhookDto.SePayTransaction transaction = webhook.getTransaction();

        if (order == null || transaction == null) {
            log.error("Missing order or transaction data in webhook!");
            return;
        }

        log.info("Order ID: {}, Invoice: {}, Amount: {}, Status: {}",
                order.getOrderId(), order.getOrderInvoiceNumber(),
                order.getOrderAmount(), order.getOrderStatus());
        log.info("Transaction ID: {}, Status: {}, Method: {}",
                transaction.getTransactionId(), transaction.getTransactionStatus(),
                transaction.getPaymentMethod());

        // Parse amount
        Double amount;
        try {
            amount = Double.parseDouble(order.getOrderAmount());
        } catch (NumberFormatException e) {
            log.error("Invalid order amount: {}", order.getOrderAmount());
            return;
        }

        // Extract User ID from order_description or order_invoice_number
        // Expected format in description: "NAP CREDIT <UUID>"
        // Or invoice number contains UUID
        UUID userId = extractUserId(order.getOrderDescription());
        if (userId == null) {
            userId = extractUserId(order.getOrderInvoiceNumber());
        }

        if (userId == null) {
            log.error("Could not extract User ID from order description: '{}' or invoice: '{}'",
                    order.getOrderDescription(), order.getOrderInvoiceNumber());
            return;
        }

        Users user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            log.error("User not found with ID: {}", userId);
            return;
        }

        // Create Payment record
        Payment payment = Payment.builder()
                .amount(amount)
                .paymentMethod("SEPAY_" + (transaction.getPaymentMethod() != null ? transaction.getPaymentMethod() : "UNKNOWN"))
                .paymentStatus(PaymentStatus.PAID)
                .user(user)
                .responseData(webhook.toString())
                .build();

        paymentRepository.save(payment);

        // Add credit to user balance
        userService.addCredit(userId, amount);
        log.info("Successfully added {} credit to user {} ({})", amount, userId, user.getUsername());
    }

    private UUID extractUserId(String text) {
        if (text == null) return null;

        // Find UUID pattern in text
        Pattern pattern = Pattern.compile(
                "([a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12})",
                Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            try {
                return UUID.fromString(matcher.group(1));
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
        return null;
    }
}
