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
    private BigDecimal price;       // 商品单价
    private Integer quantity;       // 购买数量
    private BigDecimal salePrice;   // 成交价
    private Integer stockQuantity; // 库存
    private BigDecimal itemDiscount; // 单个折扣
    private Integer isDeleted; // 是否删除标记
    private String unit; // 单位
    private String productRemark; // 商品备注
}
