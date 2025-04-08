package com.geekzhou.crm.controller;

import com.geekzhou.crm.common.Result;
import com.geekzhou.crm.entity.User;
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
        return Result.success(dbUser);
    }
}
