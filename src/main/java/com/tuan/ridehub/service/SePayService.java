package com.tuan.ridehub.service;

import com.tuan.ridehub.dto.request.SePayWebhookDto;
import com.tuan.ridehub.enums.PaymentStatus;
import com.tuan.ridehub.model.Payment;
import com.tuan.ridehub.model.Users;
import com.tuan.ridehub.repository.PaymentRepository;
import com.tuan.ridehub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${sepay.webhook-secret}")
    private String webhookSecret;

    @Transactional
    public void processWebhook(SePayWebhookDto webhook) {
        log.info("=== SePay Bank Webhook Received ===");
        log.info("ID: {}, Gateway: {}, Amount: {}, Content: {}",
                webhook.getId(), webhook.getGateway(),
                webhook.getTransferAmount(), webhook.getContent());
        log.info("Transfer Type: {}, Ref: {}, Account: {}",
                webhook.getTransferType(), webhook.getReferenceCode(),
                webhook.getAccountNumber());

        // Only process incoming transfers
        if (!"in".equals(webhook.getTransferType())) {
            log.warn("Ignoring non-incoming transfer type: {}", webhook.getTransferType());
            return;
        }

        if (webhook.getTransferAmount() == null || webhook.getTransferAmount() <= 0) {
            log.error("Invalid transfer amount: {}", webhook.getTransferAmount());
            return;
        }

        Double amount = webhook.getTransferAmount().doubleValue();

        // Extract User UUID from the transfer content
        // Expected format: "NAP CREDIT <UUID>" or content containing a UUID
        UUID userId = extractUserId(webhook.getContent());

        if (userId == null) {
            log.error("Could not extract User ID from content: '{}'", webhook.getContent());
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
                .paymentMethod("SEPAY_" + (webhook.getGateway() != null ? webhook.getGateway() : "UNKNOWN"))
                .paymentStatus(PaymentStatus.PAID)
                .user(user)
                .responseData("ref=" + webhook.getReferenceCode() + ", txDate=" + webhook.getTransactionDate())
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
