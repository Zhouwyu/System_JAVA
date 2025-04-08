package com.geekzhou.crm.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserQueryDto implements Serializable {
    private String userName;

    // 分页参数（需单独处理）
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}
