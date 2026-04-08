package com.menu.enums;

import lombok.Getter;

/**
 * 点餐记录状态枚举
 */
@Getter
public enum OrderStatus {
    PENDING(0, "待做"),
    COMPLETED(1, "已做"),
    CANCELLED(2, "取消");

    private final int code;
    private final String desc;

    OrderStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static OrderStatus fromCode(int code) {
        for (OrderStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("无效的订单状态码: " + code);
    }

    public static boolean isValid(Integer code) {
        if (code == null) return false;
        for (OrderStatus status : values()) {
            if (status.code == code) {
                return true;
            }
        }
        return false;
    }
}
