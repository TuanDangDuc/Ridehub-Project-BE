package com.tuan.ridehub.controller;

import com.tuan.ridehub.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/oauth2")
public class OAuth2Controller {
    private final JwtService jwtService;

    @GetMapping("/login/google")
    public void loginGoogle(
            HttpServletResponse httpServletResponse,
            HttpServletRequest request
    ) throws IOException {
        System.out.println("Header: " + request.getHeader("X-Forwarded-Proto"));
        System.out.println("Scheme: " + request.getScheme());
        httpServletResponse.sendRedirect("/oauth2/authorization/google");
    }

    @GetMapping("/login/github")
    public void loginGithub(
            HttpServletResponse httpServletResponse
    ) throws IOException {
        httpServletResponse.sendRedirect("/oauth2/authorization/github");
    }
}
