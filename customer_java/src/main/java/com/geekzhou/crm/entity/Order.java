package com.geekzhou.crm.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("orders")
public class Order {

    @TableId(value = "order_id", type = IdType.AUTO)
    private Integer orderId;

    @NotNull(message = "订单编号不能为空")
    @TableField("order_num")
    private String orderNo;

    @NotNull(message = "客户ID不能为空")
    @Positive(message = "客户ID必须为正整数")
    @TableField("customer_id")
    private Integer customerId;

    @NotNull(message = "商品ID不能为空")
    @Positive(message = "商品ID必须为正整数")
    @TableField(value = "products_id", exist = false)
    private Integer productId;

    @NotNull(message = "商品数量不能为空")
    @Min(value = 1, message = "商品数量至少为1")
    @TableField(value = "products_num", exist = false)
    private Integer productQuantity; // 暂时不需要了

    @NotNull(message = "总价不能为空")
    @DecimalMin(value = "0.00", message = "总价不能为负数")
    @Digits(integer = 10, fraction = 2, message = "总价格式不正确")
    @TableField("total_price")
    private BigDecimal totalPrice;

    @NotNull(message = "出货时间不能为空")
    @Future(message = "出货时间必须是将来时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "shipment_date", fill = FieldFill.INSERT)
    private LocalDateTime shipmentDate;

    @TableField(value = "is_deleted", fill = FieldFill.INSERT)
    private Integer isDeleted;

    // 自动填充处理器（需要配套实现）
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "update_time", fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;

    @TableField("remark")
    private String remark; // 备注

    @TableField("status")
    private Integer status; // 状态，1：未出货，2：已出货

    @TableField("operator")
    private String operator; // 操作员

    @Version
    @TableField("version")
    private Integer version; // 版本控制，不为0表示订单被修改过

    @TableField("discount_type")
    private Integer discountType; // 打折类型，0为不打折，1为百分比，2为固定金额

    @TableField("discount_rate")
    private Integer discountRate; // 百分比

    @TableField("discount_amount")
    private BigDecimal discountAmount; // 固定金额

    // 关联字段示例（非数据库字段）
    @TableField(exist = false)
    private Customer customer;

    @TableField(exist = false)
    private Product product;

    // 自定义业务校验
    public void validateTotalPrice() {
        if (productQuantity > 100 && totalPrice.compareTo(new BigDecimal("10000")) < 0) {
            throw new IllegalArgumentException("大宗订单金额异常");
        }
    }
}