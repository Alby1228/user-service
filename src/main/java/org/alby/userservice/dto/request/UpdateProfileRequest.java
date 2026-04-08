package org.alby.userservice.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateProfileRequest {

    @Size(max = 64, message = "昵称最长64个字符")
    private String nickname;

    @Size(max = 512, message = "头像URL过长")
    private String avatar;

    @Email(message = "邮箱格式不正确")
    private String email;

    @Size(max = 20, message = "手机号格式不正确")
    private String phone;
}
