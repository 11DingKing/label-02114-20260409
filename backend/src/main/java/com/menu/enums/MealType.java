package com.menu.enums;

import lombok.Getter;

/**
 * 餐次类型枚举
 */
@Getter
public enum MealType {
    BREAKFAST("breakfast", "早餐"),
    LUNCH("lunch", "午餐"),
    DINNER("dinner", "晚餐"),
    SNACK("snack", "夜宵");

    private final String code;
    private final String desc;

    MealType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static MealType fromCode(String code) {
        for (MealType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("无效的餐次类型: " + code);
    }

    public static boolean isValid(String code) {
        if (code == null) return false;
        for (MealType type : values()) {
            if (type.code.equals(code)) {
                return true;
            }
        }
        return false;
    }
}
