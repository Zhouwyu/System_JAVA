package com.geekzhou.crm.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class ProductInfoVo implements Serializable {
    private Integer productId;
    private String productName;
    private BigDecimal price;
    private Integer stockQuantity;
}
