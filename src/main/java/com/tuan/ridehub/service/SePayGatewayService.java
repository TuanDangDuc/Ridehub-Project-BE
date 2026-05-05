package com.tuan.ridehub.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SePayGatewayService {

    @Value("${sepay.merchant-id}")
    private String merchantId;

    @Value("${sepay.secret-key}")
    private String secretKey;

    private static final String HMAC_SHA256 = "HmacSHA256";

    /**
     * Tạo signature cho SePay Gateway
     * Thuật toán: Sắp xếp các key theo alphabet, nối lại dạng key=value&... rồi HMAC-SHA256 với secret_key
     */
    public String generateSignature(Map<String, String> fields) {
        // 1. Sắp xếp các tham số theo tên (alphabetical order)
        TreeMap<String, String> sortedFields = new TreeMap<>(fields);

        // 2. Nối chuỗi key=value&key=value
        String data = sortedFields.entrySet().stream()
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining("&"));

        log.debug("Data string for signature: {}", data);

        // 3. Tính toán HMAC-SHA256
        return calculateHmacSha256(data, secretKey);
    }

    private String calculateHmacSha256(String data, String key) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), HMAC_SHA256);
            Mac mac = Mac.getInstance(HMAC_SHA256);
            mac.init(secretKeySpec);
            byte[] rawHmac = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return toHexString(rawHmac);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("Error calculating HMAC-SHA256", e);
            throw new RuntimeException("Could not generate signature", e);
        }
    }

    private String toHexString(byte[] bytes) {
        Formatter formatter = new Formatter();
        for (byte b : bytes) {
            formatter.format("%02x", b);
        }
        return formatter.toString();
    }
}
