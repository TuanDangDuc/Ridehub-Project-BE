package com.tuan.ridehub.controller;

import com.tuan.ridehub.dto.request.RegisterDtoRequest;
import com.tuan.ridehub.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
