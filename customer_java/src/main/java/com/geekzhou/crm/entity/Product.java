package com.geekzhou.crm.entity;

import com.baomidou.mybatisplus.annotation.*;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("products")
public class Product {

    @TableId(value = "products_id", type = IdType.AUTO)
    private Integer productId;

    @NotBlank(message = "商品名称不能为空")
    @Size(max = 255, message = "商品名称最长255个字符")
    @TableField(value = "products_name", condition = SqlCondition.LIKE)
    private String productName;

    @NotNull(message = "单价不能为空")
    @DecimalMin(value = "0.00", message = "单价不能为负数")
    @Digits(integer = 10, fraction = 2, message = "单价格式不正确")
    @TableField("price")
    private BigDecimal price;

    @NotNull(message = "库存数量不能为空")
    @Min(value = 0, message = "库存数量不能小于0")
    @TableField("stock_quantity")
    private Integer stockQuantity;

    @NotNull(message = "进货日期不能为空")
    @PastOrPresent(message = "进货日期不能是未来时间")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @TableField("purcase_date")
    private LocalDate purchaseDate;

    @NotBlank(message = "供应商渠道不能为空")
    @TableField("supplier_channel")
    private String supplierChannel;

    @NotBlank(message = "联系方式不能为空")
    @Pattern(regexp = "^(1[3-9]\\d{9}|[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6})$",
            message = "联系方式格式错误")
    @TableField("supplier_contact")
    private String supplierContact;

    // 自动填充字段（根据业务需求添加）
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @TableField("product_image")
    @Size(max = 1048576, message = "图片大小不能超过1MB") // 限制1MB
    private String productImage;

    @TableField("image_type")
    private String imageType;

    @TableField("image_size")
    private Integer imageSize;

    @Version
    @TableField("version")
    private Integer version; //乐观锁版本号，之后可能要用到，现在暂时注释

    // 逻辑删除（根据业务需求添加）
    @TableField(value = "is_deleted", fill = FieldFill.INSERT)
    private Integer isDeleted;

    // 库存校验方法
    public void checkStock(int required) {
        if (stockQuantity < required) {
            throw new RuntimeException("库存不足，当前库存：" + stockQuantity);
        }
    }
}