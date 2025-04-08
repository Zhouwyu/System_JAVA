package com.geekzhou.crm.mapper.mybatis;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.geekzhou.crm.entity.Customer;
import com.geekzhou.crm.entity.Order;
import com.geekzhou.crm.vo.OrderShowInfoVo;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {
    /**
     * 订单管理分页页面
     */
    @Select("SELECT o.*, c.customer_name, CONCAT(c.customer_name, '的订单') AS order_name  " +
            "FROM orders o " +
            "LEFT JOIN customer c ON o.customer_id = c.customer_id " +
            "${ew.customSqlSegment}")
    @Results(id = "orderWithCustomerMap", value = {
            @Result(column = "order_id", property = "orderId"),
            @Result(column = "order_num", property = "orderNum"),
            @Result(column = "customer_id", property = "customerId"),
            @Result(column = "customer_name", property = "customerName"),
            @Result(column = "products_id", property = "productId"),
            @Result(column = "products_num", property = "productQuantity"),
            @Result(column = "total_price", property = "totalPrice"),
            @Result(column = "shipment_date", property = "shipmentDate"),
            @Result(column = "remark", property = "remark"),
            @Result(column = "create_time", property = "createTime"),
            @Result(column = "update_time", property = "updateTime"),
    })
    IPage<OrderShowInfoVo> selectWithCustomer(
            Page<OrderShowInfoVo> page,
            @Param(Constants.WRAPPER) Wrapper<Order> queryWrapper
    );

}
