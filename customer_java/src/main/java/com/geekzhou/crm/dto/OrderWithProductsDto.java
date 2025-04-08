package com.geekzhou.crm.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class OrderWithProductsDto implements Serializable {
    private String orderNo;
    private Integer productId;
    private String productName;
    private BigDecimal price;
    private Integer quantity;
    private Integer stock;
}
