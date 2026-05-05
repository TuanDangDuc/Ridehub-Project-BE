package com.tuan.ridehub.dto.response;

import lombok.Builder;
import lombok.Data;
import java.util.Map;

@Data
@Builder
public class SePayCheckoutResponse {
    private String checkoutUrl;
    private String merchant;
    private String operation;
    private String orderInvoiceNumber;
    private String orderAmount;
    private String currency;
    private String orderDescription;
    private String successUrl;
    private String errorUrl;
    private String cancelUrl;
    private String signature;

    /// Chuyển thành map để App dễ dàng đẩy vào form hoặc query params
    public Map<String, String> toMap() {
        return Map.ofEntries(
            Map.entry("merchant", merchant),
            Map.entry("operation", operation),
            Map.entry("order_invoice_number", orderInvoiceNumber),
            Map.entry("order_amount", orderAmount),
            Map.entry("currency", currency),
            Map.entry("order_description", orderDescription != null ? orderDescription : ""),
            Map.entry("success_url", successUrl != null ? successUrl : ""),
            Map.entry("error_url", errorUrl != null ? errorUrl : ""),
            Map.entry("cancel_url", cancelUrl != null ? cancelUrl : ""),
            Map.entry("signature", signature)
        );
    }
}
