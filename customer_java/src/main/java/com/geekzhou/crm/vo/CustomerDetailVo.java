package com.geekzhou.crm.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class CustomerDetailVo implements Serializable {
    private Long customerId;        // 客户ID
    private String customerName;     // 客户名称
    private String phoneNum;         // 联系电话
    private String address;          // 联系地址
    private String businessIndustry; // 业务行业
}
