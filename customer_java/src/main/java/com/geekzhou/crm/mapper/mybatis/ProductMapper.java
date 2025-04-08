package com.geekzhou.crm.mapper.mybatis;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.geekzhou.crm.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {
    // 原子性扣减库存（带乐观锁）
    @Update("UPDATE products SET stock_quantity = stock_quantity - #{quantity}, version = version + 1 " +
            "WHERE products_id = #{productId} AND stock_quantity >= #{quantity}")
    int deductStock(@Param("productId") Integer productId,
                    @Param("quantity") Integer quantity);
}
