package com.geekzhou.crm.vo;

import com.geekzhou.crm.entity.Role;
import lombok.Data;

/**
 * 用于前端展示用户管理页面
 */
@Data
public class UserShowInfoVo {
    private Integer userId;
    private String userName;
    private String fullName;
    private String phone;
    private String email;
    private String status;
    private Integer roleId;
    private String roleName;
}
