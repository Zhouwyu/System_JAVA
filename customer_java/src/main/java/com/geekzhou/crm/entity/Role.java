package com.geekzhou.crm.entity;

import com.baomidou.mybatisplus.annotation.*;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("roles")
public class Role {

    @TableId(value = "role_id", type = IdType.AUTO)
    private Integer roleId;

    @NotBlank(message = "角色代码不能为空")
    @Size(min = 2, max = 30, message = "角色代码长度2-30个字符")
    @Pattern(regexp = "^[A-Z_]+$", message = "角色代码只能包含大写字母和下划线")
    @TableField(value = "role_code", condition = SqlCondition.NOT_EQUAL)
    private String roleCode;

    @NotBlank(message = "角色名称不能为空")
    @Size(max = 50, message = "角色名称最长50个字符")
    @TableField("role_name")
    private String roleName;

    @Size(max = 255, message = "描述最长255个字符")
    @TableField("description")
    private String description;

    // 自动审计字段（根据需求添加）
    @TableField(value = "create_time", fill = FieldFill.INSERT, exist = false)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE, exist = false)
    private LocalDateTime updateTime;

    // 状态管理字段（扩展功能）
    @TableField(value = "is_active", exist = false)
    private Boolean active = true;

    // 角色代码格式校验方法
    public void validateRoleCode() {
        if (!roleCode.matches("^[A-Z][A-Z0-9_]*$")) {
            throw new IllegalArgumentException("角色代码必须以大写字母开头，只能包含字母、数字和下划线");
        }
    }
}