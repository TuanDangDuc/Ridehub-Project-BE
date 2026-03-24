package com.tuan.ridehub.service;

import com.tuan.ridehub.dto.request.RegisterDtoRequest;
import com.tuan.ridehub.model.Users;
import com.tuan.ridehub.repository.UserRepository;
import com.tuan.ridehub.service.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public String register(RegisterDtoRequest request) {
        Users checkUsername = userRepository.findUsersByUsername(request.getUsername());
        if (checkUsername != null)
            return "Username already exists";

        userRepository.save(userMapper.toUser(request));
        return "Register successful";
    }
}
