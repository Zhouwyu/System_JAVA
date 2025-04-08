package com.geekzhou.crm.dto;

import com.geekzhou.crm.entity.Customer;
import lombok.Data;

import java.io.Serializable;

@Data
public class CustomerQueryDto extends Customer implements Serializable {
    // 客户名称（模糊查询）
    private String name;

    // 行业（精确匹配）
    private String industry;

    // 性别（精确匹配）
    private String gender;

    // 手机号（模糊查询）
    private String phone;

    // 微信号（模糊查询）
    private String wechatNum;

    // 分页参数（需单独处理）
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}
