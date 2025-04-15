package com.geekzhou.crm.dto;

import com.geekzhou.crm.entity.Product;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDate;

@Data
public class ProductQueryDto extends Product implements Serializable {
    // 产品名称
    private String name;
    // 供应商
    private String supplier;
    // 联系方式
    private String contact;
    // 进货日期范围查询
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate beginPurchaseDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endPurchaseDate;
    // 库存阈值查询
    private Integer stockThreshold;
    // 分页参数（需单独处理）
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}
