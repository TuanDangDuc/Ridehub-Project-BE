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
        fields.put("signature", signature);

        try {
            org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate();
            // Cấu hình để RestTemplate không tự động follow redirect (để mình lấy Header Location)
            org.springframework.http.client.SimpleClientHttpRequestFactory factory = new org.springframework.http.client.SimpleClientHttpRequestFactory() {
                @Override
                protected void prepareConnection(java.net.HttpURLConnection connection, String httpMethod) throws java.io.IOException {
                    super.prepareConnection(connection, httpMethod);
                    connection.setInstanceFollowRedirects(false); // Cực kỳ quan trọng để bắt được 302
                }
            };
            restTemplate.setRequestFactory(factory);
            
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED); // SePay yêu cầu form-urlencoded
            headers.set("Authorization", "Bearer 8EGFQ6TTGHVRYXBZHJKK8Y4STWZFK3XQ4AXP0CIKJOWOD3VNBG7NCVD1JLS1BFIO"); 
            // Cực kỳ quan trọng: Giả mạo User-Agent để bypass Cloudflare WAF trên Production
            headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
            headers.set("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");

            org.springframework.util.MultiValueMap<String, String> map = new org.springframework.util.LinkedMultiValueMap<>();
            for (java.util.Map.Entry<String, String> entry : fields.entrySet()) {
                map.add(entry.getKey(), entry.getValue());
            }

            org.springframework.http.HttpEntity<org.springframework.util.MultiValueMap<String, String>> requestEntity = new org.springframework.http.HttpEntity<>(map, headers);

            log.info("Calling SePay API (PROD) with Form-UrlEncoded data...");
            org.springframework.http.ResponseEntity<String> response;
            try {
                response = restTemplate.postForEntity("https://pay.sepay.vn/v1/checkout/init", requestEntity, String.class);
            } catch (org.springframework.web.client.HttpStatusCodeException e) {
                // Nếu RestTemplate ném lỗi 3xx, ta bắt ở đây
                if (e.getStatusCode().is3xxRedirection()) {
                    response = org.springframework.http.ResponseEntity.status(e.getStatusCode())
                                .headers(e.getResponseHeaders())
                                .body(e.getResponseBodyAsString());
                } else {
                    throw e;
                }
            }

            log.info("SePay Response Status: {}", response.getStatusCode());

            if (response.getStatusCode().is3xxRedirection()) {
                java.net.URI location = response.getHeaders().getLocation();
                if (location != null) {
                    String checkoutUrl = location.toString();
                    
                    // Trích xuất order_id (PAY...) từ URL
                    String sepayOrderId = null;
                    java.util.regex.Matcher m = java.util.regex.Pattern.compile("order_id=(PAY[A-Z0-9]+)").matcher(checkoutUrl);
                    if (m.find()) {
                        sepayOrderId = m.group(1);
                        log.info("Successfully initiated SePay Order via 302: {}. Mapping to user: {}", sepayOrderId, userId);
                        sePayService.saveMapping(sepayOrderId, userId);
                    } else {
                        log.warn("Could not find order_id (PAY...) in redirect URL: {}", checkoutUrl);
                    }

                    return ResponseEntity.status(org.springframework.http.HttpStatus.FOUND)
                            .location(location)
                            .build();
                }
            } else if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                // Nếu không bị Redirect mà SePay trả về luôn trang HTML (200 OK)
                String body = response.getBody();
                java.util.regex.Matcher m = java.util.regex.Pattern.compile("ORDER_ID\\s*=\\s*['\"`](PAY[A-Z0-9]+)['\"`]").matcher(body);
                if (m.find()) {
                    String sepayOrderId = m.group(1);
                    log.info("Successfully initiated SePay Order via HTML 200 OK: {}. Mapping to user: {}", sepayOrderId, userId);
                    sePayService.saveMapping(sepayOrderId, userId);
                } else {
                    log.warn("Could not find ORDER_ID in 200 OK HTML response.");
                }

                // Trả về luôn trang HTML của SePay cho Mobile App hiển thị
                return ResponseEntity.ok()
                        .header("Content-Type", "text/html; charset=UTF-8")
                        .body(body);
            }
            
            // Nếu không thỏa mãn cả 302 và 200, ném lỗi để chạy vào fallback
            throw new RuntimeException("Unexpected response from SePay: " + response.getStatusCode());

        } catch (Exception e) {
            log.error("SEPAY ERROR: {}", e.getMessage(), e);
            // Fallback: Nếu gọi API Server-to-Server bị lỗi (do Cloudflare chặn), dùng lại cơ chế Form gửi từ trình duyệt Client
            StringBuilder html = new StringBuilder();
            html.append("<html><head><meta name='viewport' content='width=device-width, initial-scale=1'><title>Redirecting to SePay...</title></head>");
            html.append("<body onload='document.forms[0].submit()' style='text-align:center; padding-top: 50px; font-family: sans-serif;'>");
            html.append("<h3>Đang chuyển hướng tới cổng thanh toán SePay...</h3>");
            html.append("<form action='https://pay.sepay.vn/v1/checkout/init' method='POST'>");
            for (java.util.Map.Entry<String, String> entry : fields.entrySet()) {
                html.append("<input type='hidden' name='").append(entry.getKey()).append("' value='").append(entry.getValue()).append("'>");
            }
            html.append("</form></body></html>");

            return ResponseEntity.ok()
                    .header("Content-Type", "text/html; charset=UTF-8")
                    .body(html.toString());
        }
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
                "<head><meta name='viewport' content='width=device-width, initial-scale=1'><title>Thanh toán thành công</title>"
                +
                "<style>body{font-family:sans-serif;text-align:center;padding-top:50px;background:#f4f7f6;}" +
                ".card{background:white;padding:40px;border-radius:15px;display:inline-block;box-shadow:0 4px 15px rgba(0,0,0,0.1);}"
                +
                "h1{color:#2ecc71;}p{color:#7f8c8d;}.btn{display:inline-block;margin-top:20px;padding:12px 25px;background:#3498db;color:white;text-decoration:none;border-radius:5px;font-weight:bold;}</style></head>"
                +
                "<body><div class='card'><h1>✔ Thành công!</h1><p>Giao dịch của bạn đang được xử lý.<br>Vui lòng đợi trong giây lát để hệ thống cộng tiền.</p>"
                +
                "<a href='#' onclick='window.close();' class='btn'>Đóng trình duyệt</a></div>" +
                "<script>setTimeout(function(){ window.location.href='ridehub://home'; }, 3000);</script></body></html>";
    }

    @GetMapping("/error")
    @ResponseBody
    public String paymentError() {
        return "<html>" +
                "<head><meta name='viewport' content='width=device-width, initial-scale=1'><title>Lỗi thanh toán</title>"
                +
                "<style>body{font-family:sans-serif;text-align:center;padding-top:50px;background:#f4f7f6;}" +
                ".card{background:white;padding:40px;border-radius:15px;display:inline-block;box-shadow:0 4px 15px rgba(0,0,0,0.1);}"
                +
                "h1{color:#e74c3c;}p{color:#7f8c8d;}.btn{display:inline-block;margin-top:20px;padding:12px 25px;background:#3498db;color:white;text-decoration:none;border-radius:5px;font-weight:bold;}</style></head>"
                +
                "<body><div class='card'><h1>✘ Thất bại</h1><p>Đã có lỗi xảy ra trong quá trình thanh toán.<br>Vui lòng thử lại sau.</p>"
                +
                "<a href='#' onclick='window.close();' class='btn'>Quay lại</a></div>" +
                "</body></html>";
    }
}
