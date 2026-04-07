package com.tuan.ridehub.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SePayWebhookDto {
    private Long id;
    private String gateway;
    private Double amount;
    private String content;
    private String transferDate;
    private String transferType;
    private Double accumulated;
    private String subAccount;
    private String referenceNumber;
    private String code;
}
