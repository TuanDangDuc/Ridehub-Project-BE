package com.tuan.ridehub.configuration;

import com.tuan.ridehub.dto.request.RegisterDtoRequest;
import com.tuan.ridehub.model.UserPrincipal;
import com.tuan.ridehub.model.Users;
import com.tuan.ridehub.service.JwtService;
import com.tuan.ridehub.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final UserService userService;
    private final JwtService jwtService;

    @Override
    public void onAuthenticationSuccess(
        HttpServletRequest request,
        HttpServletResponse response,
        Authentication authentication
    ) throws IOException {
        OAuth2User user = (OAuth2User) authentication.getPrincipal();
        assert user != null;
        var check = userService.findUserByEmail(user.getAttribute("email"));

        if (check == null)
            userService.register(
                    RegisterDtoRequest.builder()
                            .username(user.getAttribute("given_name"))
                            .email(user.getAttribute("email"))
                            .password("oauth2user")
                            .build()
            );
        Users us = userService.findUserByEmail(user.getAttribute("email"));
        UserPrincipal userPrincipal =  new UserPrincipal(us);
        String token = jwtService.generateToken(userPrincipal);

        //FE sua link redirect kem jwt(token) o day
        response.sendRedirect("https://anhchuno.id.vn/oauth2/success?token=" + token);
    }
}
