package com.geekzhou.crm.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("customer")
public class Customer {

    @TableId(value = "customer_id", type = IdType.AUTO)
    private Integer customerId;

    @NotBlank(message = "客户名称不能为空")
    @Size(max = 255, message = "客户名称最长255个字符")
    @TableField(value = "customer_name", condition = SqlCondition.LIKE)
    private String customerName;

    @TableField(value = "wechat_num")
    private String wechatNum;

    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @TableField("phone_num")
    private String phoneNum;

    @NotBlank(message = "地址不能为空")
    @Size(max = 255, message = "地址最长255个字符")
    @TableField(value = "address", whereStrategy = FieldStrategy.NOT_EMPTY)
    private String address;

    @NotNull(message = "性别不能为空")
    @TableField("sex")
    private String sex;

    @NotBlank(message = "经营行业不能为空")
    @Size(max = 255, message = "经营行业最长255个字符")
    @TableField("business_industry")
    private String businessIndustry;

    @TableField("remark")
    @Size(max = 255, message = "备注最长255个字符")
    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField(value = "is_deleted", fill = FieldFill.INSERT)
    private Integer isDeleted;

    // 扩展方法示例
    public void validateWechatFormat() {
        if (!this.wechatNum.matches("^[a-zA-Z][a-zA-Z0-9_-]{5,20}$")) {
            throw new IllegalArgumentException("微信号格式错误");
        }
    }
}
