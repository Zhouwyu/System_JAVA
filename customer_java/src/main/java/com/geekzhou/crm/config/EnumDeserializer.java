package com.geekzhou.crm.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.geekzhou.crm.entity.User;

import java.io.IOException;

public class EnumDeserializer extends StdDeserializer<User.AccountStatus> {

    public EnumDeserializer() {
        super(User.AccountStatus.class);
    }

    @Override
    public User.AccountStatus deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException {
        // 根据 code 值反序列化
        String code = p.getText();
        return User.AccountStatus.fromCode(code);
    }
}