package com.geekzhou.crm.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDate;

@Data
public class OrderQueryDto implements Serializable {
    // 订单编号
    private String orderNo;

    // 客户
    private Integer customerId;
    // 商品
    private Integer productId;

    // 出货日期范围查询
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate beginShipmentDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endShipmentDate;

    // 分页参数（需单独处理）
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}
