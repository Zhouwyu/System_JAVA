package com.geekzhou.crm.controller;

import com.geekzhou.crm.common.Result;
import com.geekzhou.crm.entity.User;
import com.geekzhou.crm.exception.CustomException;
import com.geekzhou.crm.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class UserAuthController {
    @Autowired
    private UserServiceImpl userService;

    @PostMapping("/login")
    public Result login(@RequestBody User user) {
        User dbUser = userService.getUserInfoByUsername(user);
        dbUser.setPassword(null);
        return Result.success(dbUser);
    }

    // TODO: 忘记密码系列功能
    @PostMapping("/send-verify-code")
    public Result sendVerifyCode() {
        throw new CustomException("5001", "敬请期待");
    }
}
