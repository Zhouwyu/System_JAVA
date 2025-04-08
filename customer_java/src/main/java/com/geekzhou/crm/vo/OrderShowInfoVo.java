package com.geekzhou.crm.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 传给前端显示用的（订单管理主界面）
 */
@Data
public class OrderShowInfoVo implements Serializable {
    private String orderName;

    // 订单字段
    private Integer orderId;
    private String orderNum;
    private Integer customerId;
    private String customerName;  // 新增客户名称
    private Integer productId;
    private Integer productQuantity;
    private BigDecimal totalPrice;
    private LocalDateTime shipmentDate;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer status;

}
