package com.geekzhou.crm.controller;

import com.geekzhou.crm.common.Result;
import com.geekzhou.crm.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/role")
public class RoleController {
    @Autowired
    private RoleService roleService;

    @GetMapping("/load")
    public Result getAllRoles() {
        return Result.success(roleService.getAllRoles());
    }
}
