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
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public String login(LoginDtoRequest request) {
        var c = userRepository.findUsersByUsername(request.getUsername());
        if (c == null || c.getStatus() == AccountStatus.INACTIVE) {
            throw new RuntimeException("Login failed: User not found or account is banned");
        }

        Authentication auth = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        try {
            if (auth.isAuthenticated()) {
                return jwtService.generateToken((UserPrincipal) Objects.requireNonNull(auth.getPrincipal()));
            }
        } catch (Exception e) {
            return "Login failed: " + e.getMessage() ;
        }
        return "Login failed";
    }
}
