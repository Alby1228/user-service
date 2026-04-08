package org.alby.userservice.controller;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.alby.userservice.dto.request.LoginRequest;
import org.alby.userservice.dto.request.RegisterRequest;
import org.alby.userservice.dto.response.LoginResponse;
import org.alby.userservice.dto.response.UserResponse;
import org.alby.userservice.service.AuthService;
import org.alby.userservice.util.RespUtil;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Resource
    private AuthService authService;

    @PostMapping("/register")
    public RespUtil<UserResponse> register(@RequestBody @Valid RegisterRequest request) {
        return RespUtil.success(authService.register(request));
    }

    @PostMapping("/login")
    public RespUtil<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        return RespUtil.success(authService.login(request));
    }

    @PostMapping("/refresh")
    public RespUtil<LoginResponse> refreshToken(@RequestParam String refreshToken) {
        return RespUtil.success(authService.refreshToken(refreshToken));
    }

    @PostMapping("/logout")
    public RespUtil<Void> logout(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        authService.logout(token);
        return RespUtil.success(null);
    }
}
