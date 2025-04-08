package com.geekzhou.crm.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class OperatorDetailVo implements Serializable {
    private Long userId;            // 用户ID
    private String name;            // 操作员姓名
}
