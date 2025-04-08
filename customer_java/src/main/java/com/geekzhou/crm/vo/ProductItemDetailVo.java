package com.geekzhou.crm.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class ProductItemDetailVo implements Serializable {
    private Integer productId;         // 商品ID
    private String productName;     // 商品名称
    private BigDecimal price;       // 商品单价（下单时价格）
    private Integer quantity;       // 购买数量
    private BigDecimal unitPrice;   // 购买时单价（可能与当前价格不同）
}
