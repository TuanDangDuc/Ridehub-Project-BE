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

        // 1. Kiểm tra trạng thái đơn hàng từ SePay
        if (!"CAPTURED".equals(order.getOrderStatus())) {
            log.warn("Order status is not CAPTURED: {}", order.getOrderStatus());
            return;
        }

        Double amount = order.getOrderAmount();
        if (amount == null || amount <= 0) {
            log.error("Invalid order amount: {}", amount);
            return;
        }

        // 2. Trích xuất User ID từ mô tả đơn hàng (format: NAP CREDIT <UUID>)
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

        // 3. Chống trùng giao dịch (Idempotency)
        boolean exists = paymentRepository.existsByResponseDataContaining("sepay_tx=" + transaction.getId());
        if (exists) {
            log.warn("Transaction already processed: {}", transaction.getId());
            return;
        }

        // 4. Lưu thông tin thanh toán và cộng tiền
        Payment payment = Payment.builder()
                .amount(amount)
                .paymentMethod("SEPAY_GATEWAY")
                .paymentStatus(PaymentStatus.PAID)
                .user(user)
                .responseData("sepay_tx=" + transaction.getId() + ", order_id=" + order.getOrderId() + ", invoice=" + order.getOrderInvoiceNumber())
                .build();

        paymentRepository.save(payment);
        userService.addCredit(userId, amount);
        
        log.info("Successfully added {} credit to user {} ({}) from SePay Gateway", amount, userId, user.getUsername());
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
