package com.geekzhou.crm.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.geekzhou.crm.entity.Role;
import com.geekzhou.crm.mapper.mybatis.RoleMapper;
import com.geekzhou.crm.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {
    @Autowired
    private RoleMapper roleMapper;


    @Override
    public List<Role> getAllRoles() {
        List<Role> roles = roleMapper.selectList(null);
        return roles;
    }
}
