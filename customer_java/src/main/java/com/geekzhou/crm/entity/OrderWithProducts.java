package com.geekzhou.crm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("order_products")
public class OrderWithProducts {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("order_num")
    private String orderNo;   // 订单编号

    @TableField("product_id")
    private Integer productId; // 商品ID

    @TableField("quantity")
    private Integer quantity;   // 购买数量

    @TableField("unit_price")
    private BigDecimal unitPrice; // 购买时单价

    // 可选：关联对象（非数据库字段）
    @TableField(exist = false)
    private Product product; // 商品详情
}