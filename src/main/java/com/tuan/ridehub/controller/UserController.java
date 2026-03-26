package com.tuan.ridehub.controller;

import com.tuan.ridehub.dto.request.ChangePasswordDtoRequest;
import com.tuan.ridehub.dto.request.LoginDtoRequest;
import com.tuan.ridehub.dto.request.RegisterDtoRequest;
import com.tuan.ridehub.dto.request.UpdateUserDtoRequest;
import com.tuan.ridehub.dto.response.UserDtopResponse;
import com.tuan.ridehub.enums.AccountStatus;
import com.tuan.ridehub.enums.Role;
import com.tuan.ridehub.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public String register(
            @RequestBody RegisterDtoRequest request
    ) {
        return userService.register(request);
    }


    @PostMapping("/login")
    public String login(
            @RequestBody LoginDtoRequest request
    ) {
        return userService.login(request);
    }

    @PutMapping
    public ResponseEntity<?> updateUserInfo(
        @RequestBody UpdateUserDtoRequest request
    ) {
        userService.updateUserInfo(request);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> changeStatus(
            @PathVariable UUID id,
            @RequestParam AccountStatus status
    ) {
        userService.changeStatus(id, status);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/role")
    public ResponseEntity<?> changeRole(
            @PathVariable UUID id,
            @RequestParam Role role
    ) {
        userService.changeRole(id, role);
        return ResponseEntity.ok().build();
    }

    @PatchMapping
    public ResponseEntity<?> changePassword(
            @RequestBody ChangePasswordDtoRequest request
    ) {
        userService.changePassword(request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?>  delete(
            @PathVariable UUID id
    ) {
        userService.delete(id);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<UserDtopResponse>  getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public UserDtopResponse getUserById(
            @PathVariable UUID id
    ) {
        return userService.getUserById(id);
    }
}
