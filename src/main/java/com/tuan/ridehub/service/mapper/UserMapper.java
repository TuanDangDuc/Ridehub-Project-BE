package com.tuan.ridehub.service.mapper;

import com.tuan.ridehub.dto.request.RegisterDtoRequest;
import com.tuan.ridehub.model.Users;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

    public Users toUser(
            RegisterDtoRequest request
    ) {
        return Users.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .build();
    }
}
