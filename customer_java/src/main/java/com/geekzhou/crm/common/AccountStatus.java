package com.geekzhou.crm.common;

public enum AccountStatus {
    // 定义三种状态（推荐全大写命名规范）
    ACTIVE(1, "active", "正常状态"),
    LOCKED(2, "locked", "已锁定"),
    INACTIVE(0, "inactive", "未激活");

    // 枚举属性
    private final int code;        // 状态码（可用于数据库存储）
    private final String name;     // 状态名称
    private final String desc;     // 状态描述

    // 构造函数
    AccountStatus(int code, String name, String desc) {
        this.code = code;
        this.name = name;
        this.desc = desc;
    }

    // 根据状态码获取枚举（常用于从数据库数值转换）
    public static AccountStatus fromCode(int code) {
        for (AccountStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("无效状态码: " + code);
    }

    // 根据名称获取枚举（不区分大小写）
    public static AccountStatus fromName(String name) {
        for (AccountStatus status : values()) {
            if (status.name.equalsIgnoreCase(name)) {
                return status;
            }
        }
        throw new IllegalArgumentException("无效状态名称: " + name);
    }

    // Getter 方法
    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    // 重写 toString 显示友好信息
    @Override
    public String toString() {
        return name + " - " + desc;
    }
}
