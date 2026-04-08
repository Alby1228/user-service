package org.alby.userservice.service;

import org.alby.userservice.dto.request.LoginRequest;
import org.alby.userservice.dto.request.RegisterRequest;
import org.alby.userservice.dto.response.LoginResponse;
import org.alby.userservice.dto.response.UserResponse;

public interface AuthService {

    UserResponse register(RegisterRequest request);

    LoginResponse login(LoginRequest request);

    LoginResponse refreshToken(String refreshToken);

    void logout(String accessToken);
}
