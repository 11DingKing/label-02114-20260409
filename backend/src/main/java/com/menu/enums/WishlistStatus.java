package com.menu.enums;

import lombok.Getter;

/**
 * 心愿单状态枚举
 */
@Getter
public enum WishlistStatus {
    PENDING(0, "待添加"),
    ADDED(1, "已添加");

    private final int code;
    private final String desc;

    WishlistStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static WishlistStatus fromCode(int code) {
        for (WishlistStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("无效的心愿单状态码: " + code);
    }

    public static boolean isValid(Integer code) {
        if (code == null) return false;
        for (WishlistStatus status : values()) {
            if (status.code == code) {
                return true;
            }
        }
        return false;
    }
}
