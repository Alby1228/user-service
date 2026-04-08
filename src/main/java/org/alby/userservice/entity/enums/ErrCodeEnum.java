package org.alby.userservice.entity.enums;

import lombok.Getter;

/**
 * @author Alby
 */

@Getter
public enum ErrCodeEnum {
    SUCCESS(200, "OK"),
    NULL_PARAM_ERR(400, "请求参数不能为空"),
    RUNNING_ERR(400, "服务繁忙，请稍后重试"),
    TOKEN_ERR(400, "token错误"),
    TOKEN_NULL(400, "token is null"),
    UNAUTHORIZED(401, "未登录或登录已过期"),
    FORBIDDEN(403, "无权限"),
    PASSWORD_ERR(400, "密码错误"),
    OLD_PASSWORD_ERR(400, "原密码错误"),
    USER_NOT_EXIST(400, "用户不存在"),
    USER_ALREADY_EXIST(400, "用户名已存在"),
    PHONE_ALREADY_EXIST(400, "手机号已注册"),
    ACCOUNT_DISABLED(400, "账号已被禁用"),
    REFRESH_TOKEN_INVALID(400, "刷新令牌无效或已过期"),
    INTERNAL_ERROR(500, "系统内部错误");


    private int code;
    private String msg;

    ErrCodeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

}