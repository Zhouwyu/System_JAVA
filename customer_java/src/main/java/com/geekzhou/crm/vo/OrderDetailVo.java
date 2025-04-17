package com.geekzhou.crm.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDetailVo implements Serializable {
    private Integer orderId;          // 订单ID
    private String orderNo;        // 订单编号
    private LocalDateTime createTime; // 创建时间
    private LocalDateTime shipmentDate; // 出货时间
    private BigDecimal totalPrice;    // 总金额
    private Integer status;           // 订单状态 (1:待出货, 2:已出货...)
    private String remark;            // 备注
    private LocalDateTime updateTime; // 最后修改时间
    private Integer version;
    private Integer discountType; // 打折类型
    private Integer discountRate; // 打折比例
    private BigDecimal discountAmount; // 打折金额

    private CustomerDetailVo customer;      // 客户信息
    private OperatorDetailVo operator;     // 操作员信息
    private List<ProductItemDetailVo> products; // 商品清单
}
