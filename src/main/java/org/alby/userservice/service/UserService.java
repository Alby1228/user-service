package org.alby.userservice.service;

import org.alby.userservice.dto.request.ChangePasswordRequest;
import org.alby.userservice.dto.request.UpdateProfileRequest;
import org.alby.userservice.dto.response.UserResponse;

import java.util.List;

public interface UserService {

    UserResponse getUserById(Long id);

    UserResponse getUserByUsername(String username);

    List<UserResponse> getUsersByIds(List<Long> ids);

    UserResponse updateProfile(Long userId, UpdateProfileRequest request);

    void changePassword(Long userId, ChangePasswordRequest request);

    void updateUserStatus(Long userId, Integer status);
}
