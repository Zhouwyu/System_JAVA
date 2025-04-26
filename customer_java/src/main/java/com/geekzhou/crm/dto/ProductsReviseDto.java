package com.geekzhou.crm.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class ProductsReviseDto implements Serializable {
    private Integer productId;
    private String productName;
    private Integer quantity; // 新商品数量
    private BigDecimal salePrice;
    private BigDecimal itemDiscount; // 单品扣减金额
    private String productRemark;
}
