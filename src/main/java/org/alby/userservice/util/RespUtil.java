package org.alby.userservice.util;

import lombok.Data;
import org.alby.userservice.entity.enums.ErrCodeEnum;

import java.io.Serializable;

@Data
public class RespUtil<T> implements Serializable {
    private int ec;
    private String em;
    private T data;

    public RespUtil(int ec, String em, T data) {
        this.ec = ec;
        this.em = em;
        this.data = data;
    }

    public RespUtil(ErrCodeEnum ec, T data) {
        this.ec = ec.getCode();
        this.em = ec.getMsg();
        this.data = data;
    }

    public static <T> RespUtil<T> success(T data) {
        return new RespUtil<>(ErrCodeEnum.SUCCESS, data);
    }

    public static <T> RespUtil<T> error(ErrCodeEnum ec) {
        return new RespUtil<>(ec.getCode(), ec.getMsg(), null);
    }

    public static <T> RespUtil<T> error(Integer ec, String em) {
        return new RespUtil<>(ec, em, null);
    }
}
