package com.geekzhou.crm.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.geekzhou.crm.entity.User;

import java.io.IOException;

public class EnumSerializer extends StdSerializer<User.AccountStatus> {

    public EnumSerializer() {
        super(User.AccountStatus.class);
    }

    @Override
    public void serialize(User.AccountStatus value, JsonGenerator gen, SerializerProvider provider)
            throws IOException {
        // 序列化枚举的 code 值（对应数据库存储值）
        gen.writeString(value.getCode());
    }
}