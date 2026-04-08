package org.alby.userservice.controller;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.alby.userservice.context.UserContext;
import org.alby.userservice.dto.request.ChangePasswordRequest;
import org.alby.userservice.dto.request.UpdateProfileRequest;
import org.alby.userservice.dto.response.UserResponse;
import org.alby.userservice.service.UserService;
import org.alby.userservice.util.RespUtil;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Resource
    private UserService userService;

    @GetMapping("/me")
    public RespUtil<UserResponse> getCurrentUser() {
        Long userId = UserContext.getUserId();
        return RespUtil.success(userService.getUserById(userId));
    }

    @PutMapping("/me")
    public RespUtil<UserResponse> updateProfile(@RequestBody @Valid UpdateProfileRequest request) {
        Long userId = UserContext.getUserId();
        return RespUtil.success(userService.updateProfile(userId, request));
    }

    @PutMapping("/me/password")
    public RespUtil<Void> changePassword(@RequestBody @Valid ChangePasswordRequest request) {
        Long userId = UserContext.getUserId();
        userService.changePassword(userId, request);
        return RespUtil.success(null);
    }
}
