package org.alby.userservice.exception;

import lombok.Getter;
import org.alby.userservice.entity.enums.ErrCodeEnum;

@Getter
public class BusinessException extends RuntimeException {

    private final int code;

    public BusinessException(ErrCodeEnum errCode) {
        super(errCode.getMsg());
        this.code = errCode.getCode();
    }

    public BusinessException(ErrCodeEnum errCode, String message) {
        super(message);
        this.code = errCode.getCode();
    }
}
