package com.geekzhou.crm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.geekzhou.crm.entity.Role;

import java.util.List;

public interface RoleService extends IService<Role> {

    List<Role> getAllRoles();
}
