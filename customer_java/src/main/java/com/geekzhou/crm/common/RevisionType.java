package com.geekzhou.crm.common;

import com.baomidou.mybatisplus.annotation.EnumValue;

public enum RevisionType {
    PRICE("价格调整"),
    TIME("时间修改"),
    PRODUCT("商品变更");

    RevisionType(String description) {
        this.description = description;
    }

    @EnumValue
    private final String description;
}
