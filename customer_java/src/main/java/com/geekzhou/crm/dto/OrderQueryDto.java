package com.geekzhou.crm.dto;

import com.geekzhou.crm.entity.Customer;
import com.geekzhou.crm.entity.Order;
import com.geekzhou.crm.entity.Product;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class OrderQueryDto extends Order implements Serializable {
    // 订单编号
    private String orderNo;

    // 客户
    private Integer customerId;
    // 商品
    private Integer productId;

    // 出货日期范围查询
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime beginShipmentDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endShipmentDate;

    // 分页参数（需单独处理）
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}
