package com.tuan.ridehub.controller;

import com.tuan.ridehub.dto.request.PaymentDtoRequest;
import com.tuan.ridehub.dto.response.PaymentDtoResponse;
import com.tuan.ridehub.dto.request.SePayWebhookDto;
import com.tuan.ridehub.service.PaymentService;
import com.tuan.ridehub.service.SePayService;
import com.tuan.ridehub.service.SePayGatewayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payment")
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;
    private final SePayService sePayService;
    private final SePayGatewayService sePayGatewayService;

    @org.springframework.beans.factory.annotation.Value("${sepay.merchant-id}")
    private String merchantId;

    @PostMapping("/sepay-webhook")
    public ResponseEntity<?> sePayWebhook(@RequestBody String rawPayload) {
        log.info("=== [WEBHOOK] RAW PAYLOAD RECEIVED ===");
        log.info("Payload: {}", rawPayload);
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            SePayWebhookDto payload = mapper.readValue(rawPayload, SePayWebhookDto.class);
            sePayService.processWebhook(payload);
            return ResponseEntity.ok().body("{\"success\": true}");
        } catch (Exception e) {
            log.error("Error processing SePay webhook: {}", e.getMessage(), e);
            return ResponseEntity.ok().body("{\"success\": false}");
        }
    }

    @GetMapping("/checkout-redirect")
    public ResponseEntity<String> checkoutRedirect(
            @RequestParam Double amount,
            @RequestParam UUID userId) {

        log.info("=== [GATEWAY] Initiate Redirect ===");
        log.info("Amount: {}, UserId: {}", amount, userId);

        String invoiceNumber = "TOPUP-" + System.currentTimeMillis();
        String description = userId.toString();
        
        java.util.Map<String, String> fields = new java.util.HashMap<>();
        fields.put("merchant", merchantId);
        fields.put("operation", "PURCHASE");
        fields.put("order_invoice_number", invoiceNumber);
        fields.put("order_amount", String.valueOf(amount.intValue()));
        fields.put("currency", "VND");
        fields.put("order_description", description);
        fields.put("success_url", "https://api.anhchuno.id.vn/api/payment/success?u=" + userId);
        fields.put("error_url", "https://api.anhchuno.id.vn/api/payment/error");
        fields.put("cancel_url", "https://api.anhchuno.id.vn/api/payment/error");
        fields.put("webhook_url", "https://api.anhchuno.id.vn/api/payment/sepay-webhook");
        fields.put("ipn_url", "https://api.anhchuno.id.vn/api/payment/sepay-webhook");
        fields.put("return_url", "https://api.anhchuno.id.vn/api/payment/success?u=" + userId);

        String signature = sePayGatewayService.generateSignature(fields);
        log.info("Generated Signature: {}", signature);

        StringBuilder html = new StringBuilder();
        html.append("<html><head><title>Redirecting to SePay...</title></head>");
        html.append("<body onload='document.forms[0].submit()'>");
        html.append("<h3>Đang chuyển hướng tới cổng thanh toán SePay...</h3>");
        html.append("<form action='https://pay.sepay.vn/v1/checkout/init' method='POST'>");
        fields.forEach((k, v) -> {
            html.append("<input type='hidden' name='").append(k).append("' value='").append(v).append("'>");
        });
        html.append("<input type='hidden' name='signature' value='").append(signature).append("'>");
        html.append("</form></body></html>");

        return ResponseEntity.ok()
                .header("Content-Type", "text/html; charset=UTF-8")
                .body(html.toString());
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/pay-with-balance/{tripId}")
    public ResponseEntity<PaymentDtoResponse> payWithBalance(@PathVariable UUID tripId,
            @RequestParam UUID userId) {
        return ResponseEntity.ok(paymentService.payTripWithBalance(tripId, userId));
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/create")
    public ResponseEntity<PaymentDtoResponse> createPayment(@RequestBody PaymentDtoRequest request) {
        return ResponseEntity.ok(paymentService.createPayment(request));
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/{id}/process")
    public ResponseEntity<PaymentDtoResponse> processPayment(@PathVariable UUID id) {
        return ResponseEntity.ok(paymentService.processPayment(id));
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/{id}/fail")
    public ResponseEntity<PaymentDtoResponse> failPayment(@PathVariable UUID id) {
        return ResponseEntity.ok(paymentService.failPayment(id));
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{id}")
    public ResponseEntity<PaymentDtoResponse> getPaymentById(@PathVariable UUID id) {
        return ResponseEntity.ok(paymentService.getPaymentById(id));
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PaymentDtoResponse>> getPaymentsByUserId(@PathVariable UUID userId) {
        return ResponseEntity.ok(paymentService.getPaymentsByUserId(userId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<PaymentDtoResponse>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePayment(@PathVariable UUID id) {
        paymentService.deletePayment(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/success")
    @ResponseBody
    public String paymentSuccess(@RequestParam(value = "u", required = false) String userId,
                                @RequestParam(value = "order_id", required = false) String sepayOrderId) {
        log.info("=== [SUCCESS REDIRECT] Mapping User to SePay Order ===");
        log.info("User: {}, SePay Order: {}", userId, sepayOrderId);
        
        if (userId != null && sepayOrderId != null) {
            sePayService.saveMapping(sepayOrderId, UUID.fromString(userId));
        }

        return "<html>" +
                "<head><meta name='viewport' content='width=device-width, initial-scale=1'><title>Thanh toán thành công</title>" +
                "<style>body{font-family:sans-serif;text-align:center;padding-top:50px;background:#f4f7f6;}" +
                ".card{background:white;padding:40px;border-radius:15px;display:inline-block;box-shadow:0 4px 15px rgba(0,0,0,0.1);}" +
                "h1{color:#2ecc71;}p{color:#7f8c8d;}.btn{display:inline-block;margin-top:20px;padding:12px 25px;background:#3498db;color:white;text-decoration:none;border-radius:5px;font-weight:bold;}</style></head>" +
                "<body><div class='card'><h1>✔ Thành công!</h1><p>Giao dịch của bạn đang được xử lý.<br>Vui lòng đợi trong giây lát để hệ thống cộng tiền.</p>" +
                "<a href='#' onclick='window.close();' class='btn'>Đóng trình duyệt</a></div>" +
                "<script>setTimeout(function(){ window.location.href='ridehub://home'; }, 3000);</script></body></html>";
    }

    @GetMapping("/error")
    @ResponseBody
    public String paymentError() {
        return "<html>" +
                "<head><meta name='viewport' content='width=device-width, initial-scale=1'><title>Lỗi thanh toán</title>" +
                "<style>body{font-family:sans-serif;text-align:center;padding-top:50px;background:#f4f7f6;}" +
                ".card{background:white;padding:40px;border-radius:15px;display:inline-block;box-shadow:0 4px 15px rgba(0,0,0,0.1);}" +
                "h1{color:#e74c3c;}p{color:#7f8c8d;}.btn{display:inline-block;margin-top:20px;padding:12px 25px;background:#3498db;color:white;text-decoration:none;border-radius:5px;font-weight:bold;}</style></head>" +
                "<body><div class='card'><h1>✘ Thất bại</h1><p>Đã có lỗi xảy ra trong quá trình thanh toán.<br>Vui lòng thử lại sau.</p>" +
                "<a href='#' onclick='window.close();' class='btn'>Quay lại</a></div>" +
                "</body></html>";
    }
}
