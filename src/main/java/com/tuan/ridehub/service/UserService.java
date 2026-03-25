package com.tuan.ridehub.service;

import com.tuan.ridehub.dto.request.LoginDtoRequest;
import com.tuan.ridehub.dto.request.RegisterDtoRequest;
import com.tuan.ridehub.model.UserPrincipal;
import com.tuan.ridehub.model.Users;
import com.tuan.ridehub.repository.UserRepository;
import com.tuan.ridehub.service.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public String register(RegisterDtoRequest request) {
        Users check = userRepository.findUsersByUsername(request.getUsername(), request.getEmail());
        if (check != null)
            return "Username or Email already exists";

        userRepository.save(userMapper.toUser(request));
        return "Register successful";
    }

    public String login(LoginDtoRequest request) {
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
