package com.geekzhou.crm.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class OrderWithProductsDto implements Serializable {
    private String orderNo;
    private Integer productId;
    private String productName;
    private BigDecimal price; // 商品原价
    private BigDecimal salePrice; // 商品成交价
    private Integer quantity;
    private Integer stock;
    private String productRemark; // 商品备注
}
