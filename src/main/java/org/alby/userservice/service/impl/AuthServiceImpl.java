package org.alby.userservice.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.alby.userservice.dto.request.LoginRequest;
import org.alby.userservice.dto.request.RegisterRequest;
import org.alby.userservice.dto.response.LoginResponse;
import org.alby.userservice.dto.response.UserResponse;
import org.alby.userservice.entity.User;
import org.alby.userservice.entity.enums.ErrCodeEnum;
import org.alby.userservice.enums.UserStatus;
import org.alby.userservice.exception.BusinessException;
import org.alby.userservice.repository.UserRepository;
import org.alby.userservice.service.AuthService;
import org.alby.userservice.util.JwtUtil;
import org.alby.userservice.util.RedisUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    private static final String SESSION_KEY_PREFIX = "user:session:";
    private static final String REFRESH_TOKEN_KEY_PREFIX = "user:refresh:";

    @Resource
    private UserRepository userRepository;

    @Resource
    private PasswordEncoder passwordEncoder;

    @Resource
    private JwtUtil jwtUtil;

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private ObjectMapper objectMapper;

    @Override
    @Transactional
    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException(ErrCodeEnum.USER_ALREADY_EXIST);
        }

        if (request.getPhone() != null && userRepository.existsByPhone(request.getPhone())) {
            throw new BusinessException(ErrCodeEnum.PHONE_ALREADY_EXIST);
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setNickname(request.getNickname() != null ? request.getNickname() : request.getUsername());
        user.setStatus(UserStatus.ACTIVE.getCode());

        userRepository.save(user);
        log.info("User registered: username={}, id={}", user.getUsername(), user.getId());

        return UserResponse.fromEntity(user);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BusinessException(ErrCodeEnum.USER_NOT_EXIST));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(ErrCodeEnum.PASSWORD_ERR);
        }

        if (user.getStatus().equals(UserStatus.DISABLED.getCode())) {
            throw new BusinessException(ErrCodeEnum.ACCOUNT_DISABLED);
        }

        String sessionId = UUID.randomUUID().toString().replace("-", "");
        String accessToken = jwtUtil.generateAccessToken(user.getId(), sessionId);
        String refreshToken = jwtUtil.generateRefreshToken();

        storeSession(sessionId, user);
        storeRefreshToken(refreshToken, user.getId(), sessionId);

        log.info("User logged in: userId={}, sessionId={}", user.getId(), sessionId);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(jwtUtil.getAccessTokenExpiration() / 1000)
                .userInfo(UserResponse.fromEntity(user))
                .build();
    }

    @Override
    public LoginResponse refreshToken(String refreshToken) {
        String refreshKey = REFRESH_TOKEN_KEY_PREFIX + refreshToken;
        String refreshData = redisUtil.get(refreshKey);

        if (refreshData == null) {
            throw new BusinessException(ErrCodeEnum.REFRESH_TOKEN_INVALID);
        }

        RefreshTokenData tokenData;
        try {
            tokenData = objectMapper.readValue(refreshData, RefreshTokenData.class);
        } catch (JsonProcessingException e) {
            throw new BusinessException(ErrCodeEnum.REFRESH_TOKEN_INVALID);
        }

        redisUtil.delete(refreshKey);
        redisUtil.delete(SESSION_KEY_PREFIX + tokenData.sessionId);

        User user = userRepository.findById(tokenData.userId)
                .orElseThrow(() -> new BusinessException(ErrCodeEnum.USER_NOT_EXIST));

        if (user.getStatus().equals(UserStatus.DISABLED.getCode())) {
            throw new BusinessException(ErrCodeEnum.ACCOUNT_DISABLED);
        }

        String newSessionId = UUID.randomUUID().toString().replace("-", "");
        String newAccessToken = jwtUtil.generateAccessToken(user.getId(), newSessionId);
        String newRefreshToken = jwtUtil.generateRefreshToken();

        storeSession(newSessionId, user);
        storeRefreshToken(newRefreshToken, user.getId(), newSessionId);

        log.info("Token refreshed: userId={}, newSessionId={}", user.getId(), newSessionId);

        return LoginResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .expiresIn(jwtUtil.getAccessTokenExpiration() / 1000)
                .userInfo(UserResponse.fromEntity(user))
                .build();
    }

    @Override
    public void logout(String accessToken) {
        Long userId = jwtUtil.getUserIdFromToken(accessToken);
        String sessionId = jwtUtil.getSessionIdFromToken(accessToken);

        if (sessionId != null) {
            redisUtil.delete(SESSION_KEY_PREFIX + sessionId);
            log.info("User logged out: userId={}, sessionId={}", userId, sessionId);
        }
    }

    private void storeSession(String sessionId, User user) {
        try {
            SessionData sessionData = new SessionData(user.getId(), user.getUsername(), user.getStatus());
            String json = objectMapper.writeValueAsString(sessionData);
            redisUtil.set(SESSION_KEY_PREFIX + sessionId, json,
                    jwtUtil.getAccessTokenExpiration(), TimeUnit.MILLISECONDS);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize session data", e);
        }
    }

    private void storeRefreshToken(String refreshToken, Long userId, String sessionId) {
        try {
            RefreshTokenData data = new RefreshTokenData(userId, sessionId);
            String json = objectMapper.writeValueAsString(data);
            redisUtil.set(REFRESH_TOKEN_KEY_PREFIX + refreshToken, json,
                    jwtUtil.getRefreshTokenExpiration(), TimeUnit.MILLISECONDS);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize refresh token data", e);
        }
    }

    public record SessionData(Long userId, String username, Integer status) {
    }

    public record RefreshTokenData(Long userId, String sessionId) {
    }
}
