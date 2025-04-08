package com.geekzhou.crm.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class CustomerInfoVo implements Serializable {
    private Integer customerId;
    private String customerName;
    private String phoneNum;
    private String address;
    private String businessIndustry;
}
