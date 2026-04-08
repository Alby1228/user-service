package org.alby.userservice.dto.response;

import lombok.Builder;
import lombok.Data;
import org.alby.userservice.entity.User;

import java.time.LocalDateTime;

@Data
@Builder
public class UserResponse {

    private Long id;
    private String username;
    private String phone;
    private String email;
    private String nickname;
    private String avatar;
    private Integer status;
    private LocalDateTime createTime;

    public static UserResponse fromEntity(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .phone(user.getPhone())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .status(user.getStatus())
                .createTime(user.getCreateTime())
                .build();
    }
}
