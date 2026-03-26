package com.tuan.ridehub.service;

import com.tuan.ridehub.dto.request.ChangePasswordDtoRequest;
import com.tuan.ridehub.dto.request.LoginDtoRequest;
import com.tuan.ridehub.dto.request.RegisterDtoRequest;
import com.tuan.ridehub.dto.request.UpdateUserDtoRequest;
import com.tuan.ridehub.dto.response.UserDtopResponse;
import com.tuan.ridehub.enums.AccountStatus;
import com.tuan.ridehub.enums.Role;
import com.tuan.ridehub.model.UserPrincipal;
import com.tuan.ridehub.model.Users;
import com.tuan.ridehub.repository.UserRepository;
import com.tuan.ridehub.service.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
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
        Users check = userRepository.findUsersByUsername(request.getUsername(), request.getPassword());
        if (check != null)
            return "Username or Email already exists";

        userRepository.save(userMapper.RegisterDtoRequestToUser(request));
        return "Register successful";
    }

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

    public void delete(UUID id) {
        userRepository.deleteById(id);
    }

    public void updateUserInfo(UpdateUserDtoRequest request) {
        userRepository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        userRepository.updateUserInfo(userMapper.UpdateUserDtoRequestToUser(request));
    }

    public void changeStatus(UUID id, AccountStatus status) {
        userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        userRepository.setStatus(id, status);
    }

    public void changeRole(UUID id, Role role) {
        userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        userRepository.setRole(id, role);
    }

    public void changePassword(ChangePasswordDtoRequest request) {
        userRepository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        userRepository.changePassword(userMapper.ChangePasswordDtoRequestToUser(request));
    }

    public List<UserDtopResponse> getAllUsers() {
        return userRepository.getAllUser()
                .stream()
                .map(userMapper::UserToUserDtoResponse)
                .toList();
    }

    public UserDtopResponse getUserById(UUID id) {
        return userRepository.findFirstById(id);
    }
}
