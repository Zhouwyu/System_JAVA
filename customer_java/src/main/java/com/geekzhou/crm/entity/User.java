package com.geekzhou.crm.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("users") // 指定表名
public class User {

    @TableId(value = "user_id", type = IdType.AUTO)
    private Long userId;

    @NotBlank(message = "用户名不能为空")
    @Size(min = 4, max = 50, message = "用户名长度4-50位")
    @TableField(value = "username", condition = SqlCondition.EQUAL)
    private String username;

    @NotBlank(message = "密码不能为空")
    @TableField("password_hash")
    private String password;

    @NotBlank(message = "姓名不能为空")
    @Size(max = 100, message = "姓名最长100个字符")
    @TableField("full_name")
    private String fullName;

    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式错误")
    @TableField(value = "mobile", whereStrategy = FieldStrategy.NOT_EMPTY)
    private String mobile;

    @Email(message = "邮箱格式错误")
    @TableField("email")
    private String email;

    @TableField(value = "status")
    private AccountStatus status = AccountStatus.ACTIVE;

    public enum AccountStatus {
        ACTIVE("active"),
        LOCKED("locked"),
        INACTIVE("inactive");

        @EnumValue // 标记数据库存储值
        private final String code;

        AccountStatus(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }

        // 添加反向查找方法
        public static AccountStatus fromCode(String code) {
            for (AccountStatus status : AccountStatus.values()) {
                if (status.code.equals(code)) {
                    return status;
                }
            }
            throw new IllegalArgumentException("未知状态码: " + code);
        }
    }

    @TableField("failed_attempts")
    private Integer failedAttempts = 0;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("last_login")
    private LocalDateTime lastLogin;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    @TableField(value = "is_deleted", fill = FieldFill.INSERT)
    private Integer isDeleted;

    @TableField("deleted_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deletedAt;

    @TableField(exist = false)
    private String accessToken;

    @TableField("role_id")
    private Integer roleId;

    // 业务方法
    public void incrementFailedAttempts() {
        this.failedAttempts++;
        if (this.failedAttempts >= 5) {
            this.status = AccountStatus.LOCKED;
        }
    }
}