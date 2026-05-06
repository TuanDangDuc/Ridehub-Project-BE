package com.tuan.ridehub.service;

import org.springframework.stereotype.Service;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OtpService {
    
    // Store email as key, OtpData as value
    private final Map<String, OtpData> otpCache = new ConcurrentHashMap<>();
    private final SecureRandom secureRandom = new SecureRandom();
    private static final int OTP_EXPIRATION_MINUTES = 5;

    public String generateAndStoreOtp(String email) {
        int otpValue = 100000 + secureRandom.nextInt(900000);
        String otp = String.valueOf(otpValue);
        
        OtpData otpData = new OtpData(otp, LocalDateTime.now().plusMinutes(OTP_EXPIRATION_MINUTES));
        otpCache.put(email, otpData);
        
        return otp;
    }

    public boolean verifyOtp(String email, String otp) {
        OtpData otpData = otpCache.get(email);
        if (otpData == null) {
            return false;
        }
        
        if (LocalDateTime.now().isAfter(otpData.getExpirationTime())) {
            otpCache.remove(email);
            return false;
        }
        
        return otpData.getOtp().equals(otp);
    }
    
    public void clearOtp(String email) {
        otpCache.remove(email);
    }

    private static class OtpData {
        private final String otp;
        private final LocalDateTime expirationTime;

        public OtpData(String otp, LocalDateTime expirationTime) {
            this.otp = otp;
            this.expirationTime = expirationTime;
        }

        public String getOtp() {
            return otp;
        }

        public LocalDateTime getExpirationTime() {
            return expirationTime;
        }
    }
}
