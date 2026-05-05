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
        log.info("=== SePay Payment Gateway IPN Received ===");
        log.info("Notification Type: {}", webhook.getNotificationType());

        // Check notification type
        if (!"ORDER_PAID".equals(webhook.getNotificationType())) {
            log.warn("Ignoring notification type: {} (only 'ORDER_PAID' is processed)", webhook.getNotificationType());
            return;
        }

        SePayWebhookDto.SePayOrder order = webhook.getOrder();
        SePayWebhookDto.SePayTransaction transaction = webhook.getTransaction();

        if (order == null || transaction == null) {
            log.error("Missing order or transaction data in webhook!");
            return;
        }

        // Check order status
        if (!"CAPTURED".equals(order.getOrderStatus()) && !"APPROVED".equals(transaction.getTransactionStatus())) {
            log.warn("Order not captured or approved yet. Status: Order={}, Trans={}", 
                    order.getOrderStatus(), transaction.getTransactionStatus());
            return;
        }

        log.info("Order ID: {}, Amount: {}, Description: {}",
                order.getOrderId(), order.getOrderAmount(), order.getOrderDescription());

        Double amount = order.getOrderAmount();
        if (amount == null || amount <= 0) {
            log.error("Invalid order amount: {}", amount);
            return;
        }

        // Extract User ID from order_description or order_invoice_number
        // Expected format: "NAP CREDIT <UUID>"
        UUID userId = extractUserId(order.getOrderDescription());
        if (userId == null) {
            userId = extractUserId(order.getOrderInvoiceNumber());
        }

        if (userId == null) {
            log.error("Could not extract User ID from description: '{}' or invoice: '{}'",
                    order.getOrderDescription(), order.getOrderInvoiceNumber());
            return;
        }

        Users user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            log.error("User not found with ID: {}", userId);
            return;
        }

        // Check for duplicate transaction
        boolean exists = paymentRepository.existsByResponseDataContaining("sepay_tx=" + transaction.getId());
        if (exists) {
            log.warn("Transaction already processed: SePay Transaction ID {}", transaction.getId());
            return;
        }

        // Create Payment record
        Payment payment = Payment.builder()
                .amount(amount)
                .paymentMethod("SEPAY_" + (transaction.getPaymentMethod() != null ? transaction.getPaymentMethod() : "GATEWAY"))
                .paymentStatus(PaymentStatus.PAID)
                .user(user)
                .responseData("sepay_tx=" + transaction.getId() + ", order_id=" + order.getOrderId())
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
