package com.geekzhou.crm.config;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.geekzhou.crm.entity.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 枚举处理增强
 */
@Configuration
public class EnumConfig {

    @Bean
    public SimpleModule enumModule() {
        SimpleModule module = new SimpleModule();
        module.addSerializer(User.AccountStatus.class, new EnumSerializer());
        module.addDeserializer(User.AccountStatus.class, new EnumDeserializer());
        return module;
    }
}