package com.geekzhou.crm.common;

public enum OrderShipmentStatus {
    PENDING(1, "pending", "待出货"),
    SHIPPED(2, "shipped", "已出货");

    private final int code;
    private final String name;
    private final String description;

    OrderShipmentStatus(int code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }

    // 根据状态码获取枚举
    public static OrderShipmentStatus fromCode(int code) {
        for (OrderShipmentStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("无效出货状态码: " + code);
    }

    // 根据状态名获取枚举
    public static OrderShipmentStatus fromName(String name) {
        for (OrderShipmentStatus status : values()) {
            if (status.name.equalsIgnoreCase(name)) {
                return status;
            }
        }
        throw new IllegalArgumentException("无效出货状态名称: " + name);
    }

    // 判断是否已出货（业务相关方法）
    public boolean isShipped() {
        return this == SHIPPED;
    }

    // Getter 方法
    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return name + " - " + description;
    }
}
