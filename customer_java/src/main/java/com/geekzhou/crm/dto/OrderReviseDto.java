package com.geekzhou.crm.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderReviseDto implements Serializable {
    private Integer originalOrderId;
    private String originalOrderNo;
    private Integer version;
    List<ProductsReviseDto> productsRevise;
    private String newShipmentDate;
    private String revisionRemark;
    private Boolean isShipmentChanged;
    private BigDecimal totalPrice;
    private String operator;
    private Integer discountType;
    private Integer discountRate;
    private BigDecimal discountAmount;
}
