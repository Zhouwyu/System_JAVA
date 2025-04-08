package com.geekzhou.crm.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

@Data
@TableName("permissions")
public class Permission {

    @TableId(value = "permission_id", type = IdType.AUTO)
    private Integer permissionId;

    @NotBlank(message = "权限代码不能为空")
    @Size(max = 50, message = "权限代码最长50个字符")
    @Pattern(regexp = "^[a-zA-Z0-9:_]+$", message = "权限代码只能包含字母、数字、冒号和下划线")
    @TableField(value = "permission_code", condition = SqlCondition.LIKE)
    private String permissionCode;

    @NotBlank(message = "权限名称不能为空")
    @Size(max = 100, message = "权限名称最长100个字符")
    @TableField("permission_name")
    private String permissionName;

    @NotBlank(message = "资源类型不能为空")
    @Size(max = 50, message = "资源类型最长50个字符")
    @TableField("resource_type")
    private String resourceType;

    @NotBlank(message = "操作类型不能为空")
    @Size(max = 20, message = "操作类型最长20个字符")
    @TableField("action_type")
    private String actionType;

    // 自动维护字段（根据需求添加）
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    // 格式校验示例方法
    public void validateCodeFormat() {
        if (!permissionCode.contains(":")) {
            throw new IllegalArgumentException("权限代码格式必须包含冒号分隔符");
        }
    }
}
