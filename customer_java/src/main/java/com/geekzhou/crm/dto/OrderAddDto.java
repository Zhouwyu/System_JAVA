package com.geekzhou.crm.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderAddDto implements Serializable {
    private Integer customerId;
    List<OrderWithProductsDto> productsDtos;
    private BigDecimal totalPrice;
    private String remark;
    private String shipmentDate;
    private String operator;
}
