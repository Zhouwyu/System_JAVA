package com.geekzhou.crm.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("role_permissions")
public class RolePermission {

    @NotNull(message = "角色ID不能为空")
    @TableField("role_id")
    private Integer roleId;

    @NotNull(message = "权限ID不能为空")
    @TableField("permission_id")
    private Integer permissionId;

    // 自动审计字段（根据业务需求添加）
    @TableField(value = "granted_time", fill = FieldFill.INSERT, exist = false)
    private LocalDateTime grantedTime;

    @TableField(value = "granted_by", exist = false)
    private String grantedBy;

    // 复合主键配置（需要自定义SQL）
    @TableField(exist = false)
    private CompositeKey compositeKey;

    @Data
    public static class CompositeKey implements Serializable {
        private Integer roleId;
        private Integer permissionId;
    }

    // 批量授权构造方法
    public RolePermission(Integer roleId, Integer permissionId) {
        this.roleId = roleId;
        this.permissionId = permissionId;
    }

    // 权限有效性校验
    public void validate() {
        if (roleId <= 0 || permissionId <= 0) {
            throw new IllegalArgumentException("无效的ID参数");
        }
    }
}
