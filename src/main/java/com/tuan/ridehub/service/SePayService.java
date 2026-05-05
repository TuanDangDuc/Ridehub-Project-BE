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
        if (webhook.isBankWebhook()) {
            processBankWebhook(webhook);
        } else {
            processGatewayIPN(webhook);
        }
    }

    /// Xử lý webhook từ SePay Bank Monitoring (flat JSON)
    private void processBankWebhook(SePayWebhookDto webhook) {
        log.info("=== SePay Bank Webhook Received ===");
        log.info("Gateway: {}, Amount: {}, Content: {}", webhook.getGateway(), webhook.getTransferAmount(), webhook.getContent());

        if (!"in".equalsIgnoreCase(webhook.getTransferType())) {
            log.warn("Ignoring transfer type: {}", webhook.getTransferType());
            return;
        }

        if (webhook.getTransferAmount() == null || webhook.getTransferAmount() <= 0) {
            log.error("Invalid transfer amount: {}", webhook.getTransferAmount());
            return;
        }

        Double amount = webhook.getTransferAmount().doubleValue();

        UUID userId = extractUserId(webhook.getContent());
        if (userId == null) {
            userId = extractUserId(webhook.getDescription());
        }
        if (userId == null) {
            log.error("Could not extract User ID from content: '{}' or description: '{}'", webhook.getContent(), webhook.getDescription());
            return;
        }

        Users user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            log.error("User not found with ID: {}", userId);
            return;
        }

        // Chống trùng
        boolean exists = paymentRepository.existsByResponseDataContaining("sepay_id=" + webhook.getId());
        if (exists) {
            log.warn("Transaction already processed: SePay ID {}", webhook.getId());
            return;
        }

        Payment payment = Payment.builder()
                .amount(amount)
                .paymentMethod("SEPAY_" + (webhook.getGateway() != null ? webhook.getGateway() : "BANK"))
                .paymentStatus(PaymentStatus.PAID)
                .user(user)
                .responseData("sepay_id=" + webhook.getId() + ", ref=" + webhook.getReferenceCode())
                .build();

        paymentRepository.save(payment);
        userService.addCredit(userId, amount);
        log.info("Successfully added {} credit to user {} ({})", amount, userId, user.getUsername());
    }

    /// Xử lý IPN từ SePay Payment Gateway (nested JSON)
    private void processGatewayIPN(SePayWebhookDto webhook) {
        log.info("=== SePay Payment Gateway IPN Received ===");
        log.info("Notification Type: {}", webhook.getNotificationType());

        if (!"ORDER_PAID".equals(webhook.getNotificationType())) {
            log.warn("Ignoring notification type: {}", webhook.getNotificationType());
            return;
        }

        SePayWebhookDto.SePayOrder order = webhook.getOrder();
        SePayWebhookDto.SePayTransaction transaction = webhook.getTransaction();

        if (order == null || transaction == null) {
            log.error("Missing order or transaction data!");
            return;
        }

        Double amount = order.getOrderAmount();
        if (amount == null || amount <= 0) {
            log.error("Invalid order amount: {}", amount);
            return;
        }

        UUID userId = extractUserId(order.getOrderDescription());
        if (userId == null) {
            userId = extractUserId(order.getOrderInvoiceNumber());
        }
        if (userId == null) {
            log.error("Could not extract User ID from description: '{}' or invoice: '{}'", order.getOrderDescription(), order.getOrderInvoiceNumber());
            return;
        }

        Users user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            log.error("User not found with ID: {}", userId);
            return;
        }

        boolean exists = paymentRepository.existsByResponseDataContaining("sepay_tx=" + transaction.getId());
        if (exists) {
            log.warn("Transaction already processed: {}", transaction.getId());
            return;
        }

        Payment payment = Payment.builder()
                .amount(amount)
                .paymentMethod("SEPAY_" + (transaction.getPaymentMethod() != null ? transaction.getPaymentMethod() : "GATEWAY"))
                .paymentStatus(PaymentStatus.PAID)
                .user(user)
                .responseData("sepay_tx=" + transaction.getId() + ", order_id=" + order.getOrderId())
                .build();

        paymentRepository.save(payment);
        userService.addCredit(userId, amount);
        log.info("Successfully added {} credit to user {} ({})", amount, userId, user.getUsername());
    }

    private UUID extractUserId(String text) {
        if (text == null) return null;
        Pattern pattern = Pattern.compile("([a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12})", Pattern.CASE_INSENSITIVE);
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
