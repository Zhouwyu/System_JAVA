package com.geekzhou.crm.utils;

import cn.hutool.core.date.DateUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import java.util.Date;

public class TokenUtils {
    public static String createToken(String data, String sign) {
        return JWT.create().withAudience(data) // 以 user_id-role_code 作为载荷
                .withExpiresAt(DateUtil.offsetDay(new Date(), 1))// 1天后过期
                .sign(Algorithm.HMAC256(sign)); // 以password作为token密钥，HMAC256算法加密
    }
}
