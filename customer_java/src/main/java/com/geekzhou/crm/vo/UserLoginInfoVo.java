package com.geekzhou.crm.vo;

import lombok.Data;

@Data
public class UserLoginInfoVo {
    private Long userId;
    private String userName;
    private String accessToken;
    private Integer roleId;
    private String status;
}
