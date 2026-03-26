package com.tuan.ridehub.service.mapper;

import com.tuan.ridehub.dto.request.ChangePasswordDtoRequest;
import com.tuan.ridehub.dto.request.RegisterDtoRequest;
import com.tuan.ridehub.dto.request.UpdateUserDtoRequest;
import com.tuan.ridehub.dto.response.UserDtopResponse;
import com.tuan.ridehub.enums.AccountStatus;
import com.tuan.ridehub.enums.Role;
import com.tuan.ridehub.model.Users;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

    public Users RegisterDtoRequestToUser(
            RegisterDtoRequest request
    ) {
        return Users.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .role(Role.USER)
                .status(AccountStatus.ACTIVE)
                .build();
    }

    public Users UpdateUserDtoRequestToUser(
            UpdateUserDtoRequest request
    ) {
        return Users.builder()
                .id(request.getId())
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .sex(request.getSex())
                .dateOfBirth(request.getDateOfBirth())
                .identityNumber(request.getIdentityNumber())
                .avatarUrl(request.getAvatarUrl())
                .phoneNumber(request.getPhoneNumber())
                .build();
    }

    public Users ChangePasswordDtoRequestToUser(
            ChangePasswordDtoRequest request
    ) {
        return Users.builder()
                .id(request.getId())
                .password(passwordEncoder.encode(request.getNewPassword()))
                .build();
    }

    public UserDtopResponse UserToUserDtoResponse(
            Users users
    ) {
        return UserDtopResponse.builder()
                .id(users.getId())
                .username(users.getUsername())
                .email(users.getEmail())
                .firstname(users.getFirstname())
                .lastname(users.getLastname())
                .sex(users.getSex())
                .dateOfBirth(users.getDateOfBirth())
                .identityNumber(users.getIdentityNumber())
                .avatarUrl(users.getAvatarUrl())
                .phoneNumber(users.getPhoneNumber())
                .status(users.getStatus())
                .createdAt(users.getCreatedAt())
                .build();
    }
}

