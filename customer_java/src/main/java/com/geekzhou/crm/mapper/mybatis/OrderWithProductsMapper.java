package com.geekzhou.crm.mapper.mybatis;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.geekzhou.crm.entity.OrderWithProducts;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderWithProductsMapper extends BaseMapper<OrderWithProducts>{
    // 自定义批量插入方法
    @Insert({
            "<script>",
            "INSERT INTO order_products (order_num, product_id, quantity, unit_price, sale_price)",
            "VALUES ",
            "<foreach collection='list' item='item' separator=','>",
            "   (#{item.orderNo}, #{item.productId}, #{item.quantity}, #{item.unitPrice}, #{item.salePrice})",
            "</foreach>",
            "</script>"
    })
    int insertBatch(@Param("list") List<OrderWithProducts> list);
}
