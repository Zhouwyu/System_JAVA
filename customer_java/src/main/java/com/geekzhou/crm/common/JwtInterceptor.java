package com.geekzhou.crm.common;

import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.geekzhou.crm.entity.User;
import com.geekzhou.crm.exception.CustomException;
import com.geekzhou.crm.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 拦截器具体实现
 */
@Component
public class JwtInterceptor implements HandlerInterceptor {
    @Autowired
    UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从请求头取一次
        String token = request.getHeader("Accesstoken");
        if(StrUtil.isBlank(token)){
            // 没拿到再从请求参数拿一次
            token = request.getParameter("accessToken");
        }
        if(StrUtil.isBlank(token)){
            throw new CustomException("402", "无权限");
        }

        User dbUserInfo = null;
        // 拿到token的载荷数据
        String audience = JWT.decode(token).getAudience().get(0);
        String[] split = audience.split("-");
        String userId = split[0];
        String roleCode = split[1];
        // 验证用户信息
        if ("SUPER_ADMIN".equals(roleCode)) {
            dbUserInfo = userService.getUserInfoByUserId(Integer.parseInt(userId));
        } else if ("USER_ADMIN".equals(roleCode)) {
            dbUserInfo = userService.getUserInfoByUserId(Integer.parseInt(userId));
        }
        if (dbUserInfo == null) {
            throw new CustomException("402", "无权限");
        }
        // 签名验证
        try {
            JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(dbUserInfo.getPassword())).build();
            jwtVerifier.verify(token);
        } catch (Exception e) {
            throw new CustomException("402", "无权限");
        }
        return true;
    }

}
