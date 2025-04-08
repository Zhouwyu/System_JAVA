package com.geekzhou.crm.controller;

import cn.hutool.log.StaticLog;
import com.geekzhou.crm.common.Result;
import com.geekzhou.crm.dto.UserQueryDto;
import com.geekzhou.crm.entity.User;
import com.geekzhou.crm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/page")
    public Result getPageInfo(UserQueryDto queryDto) {
        return Result.success(userService.getUserByPage(queryDto));
    }

    @PostMapping("/create")
    public Result createNewUser(@RequestBody User user) {
        return Result.success(userService.createNewUser(user));
    }

    @PostMapping("/update")
    public Result updateUser(@RequestBody User user) {
        return Result.success(userService.updateUser(user));
    }

    @PostMapping("/delete/batch")
    public Result deleteUser(@RequestBody Map<String, List<Integer>> request) {
        List<Integer> ids = request.get("ids");
        return Result.success(userService.batchDelUser(ids));
    }

    @GetMapping("/checkUsername")
    public Result checkUsername(@RequestParam("username") String username, @RequestParam(required = false) Long userId) {
        boolean available = userService.checkUsernameAvailability(username, userId);
        return Result.success(Collections.singletonMap("exists", !available));
    }
}
