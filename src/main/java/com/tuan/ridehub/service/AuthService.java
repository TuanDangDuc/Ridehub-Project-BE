package com.tuan.ridehub.service;

import com.nimbusds.jose.JWEObject;
import com.tuan.ridehub.dto.request.LoginDtoRequest;
import com.tuan.ridehub.enums.AccountStatus;
import com.tuan.ridehub.model.UserPrincipal;
import com.tuan.ridehub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final OtpService otpService;
    private final MailService mailService;
    private final PasswordEncoder passwordEncoder;

    public String login(LoginDtoRequest request) {
        var c = userRepository.findUsersByUsername(request.getUsername());
        if (c == null || c.getStatus() == AccountStatus.INACTIVE) {
            return "Login failed: User not found or account is banned";
        }

        try {
            Authentication auth = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
            if (auth.isAuthenticated()) {
                return jwtService.generateToken((UserPrincipal) Objects.requireNonNull(auth.getPrincipal()));
            }
        } catch (Exception e) {
            return "Login failed: " + e.getMessage() ;
        }
        return "Login failed";
    }

    public String forgotPassword(String email) {
        var user = userRepository.findUsersByEmail(email);
        if (user == null) {
            return "Email không tồn tại";
        }
        String otp = otpService.generateAndStoreOtp(email);
        mailService.sendOtpEmail(email, otp);
        return "Mã OTP đã được gửi đến email của bạn";
    }

    public String verifyOtp(String email, String otp) {
        if (otpService.verifyOtp(email, otp)) {
            return "Xác thực OTP thành công";
        }
        return "Mã OTP không hợp lệ hoặc đã hết hạn";
    }

    public String resetPassword(String email, String newPassword) {
        var user = userRepository.findUsersByEmail(email);
        if (user == null) {
            return "Email không tồn tại";
        }
        userRepository.resetPassword(email, passwordEncoder.encode(newPassword));
        return "Đổi mật khẩu thành công";
    }
}
