package org.alby.userservice.service.impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.alby.userservice.dto.request.ChangePasswordRequest;
import org.alby.userservice.dto.request.UpdateProfileRequest;
import org.alby.userservice.dto.response.UserResponse;
import org.alby.userservice.entity.User;
import org.alby.userservice.entity.enums.ErrCodeEnum;
import org.alby.userservice.enums.UserStatus;
import org.alby.userservice.exception.BusinessException;
import org.alby.userservice.repository.UserRepository;
import org.alby.userservice.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserRepository userRepository;

    @Resource
    private PasswordEncoder passwordEncoder;

    @Override
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrCodeEnum.USER_NOT_EXIST));
        return UserResponse.fromEntity(user);
    }

    @Override
    public UserResponse getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(ErrCodeEnum.USER_NOT_EXIST));
        return UserResponse.fromEntity(user);
    }

    @Override
    public List<UserResponse> getUsersByIds(List<Long> ids) {
        List<User> users = userRepository.findByIdIn(ids);
        return users.stream().map(UserResponse::fromEntity).toList();
    }

    @Override
    @Transactional
    public UserResponse updateProfile(Long userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrCodeEnum.USER_NOT_EXIST));

        if (request.getNickname() != null) {
            user.setNickname(request.getNickname());
        }
        if (request.getAvatar() != null) {
            user.setAvatar(request.getAvatar());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getPhone() != null) {
            if (!request.getPhone().equals(user.getPhone())
                    && userRepository.existsByPhone(request.getPhone())) {
                throw new BusinessException(ErrCodeEnum.PHONE_ALREADY_EXIST);
            }
            user.setPhone(request.getPhone());
        }

        userRepository.save(user);
        log.info("User profile updated: userId={}", userId);
        return UserResponse.fromEntity(user);
    }

    @Override
    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrCodeEnum.USER_NOT_EXIST));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BusinessException(ErrCodeEnum.OLD_PASSWORD_ERR);
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        log.info("Password changed: userId={}", userId);
    }

    @Override
    @Transactional
    public void updateUserStatus(Long userId, Integer status) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrCodeEnum.USER_NOT_EXIST));

        UserStatus.of(status);
        user.setStatus(status);
        userRepository.save(user);
        log.info("User status updated: userId={}, status={}", userId, status);
    }
}
