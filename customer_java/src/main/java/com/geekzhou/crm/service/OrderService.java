package com.geekzhou.crm.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.geekzhou.crm.dto.OrderAddDto;
import com.geekzhou.crm.dto.OrderQueryDto;
import com.geekzhou.crm.dto.OrderReviseDto;
import com.geekzhou.crm.entity.Order;
import com.geekzhou.crm.entity.OrderRevision;
import com.geekzhou.crm.vo.OrderDetailVo;
import com.geekzhou.crm.vo.OrderRevisionHistoryVo;
import com.geekzhou.crm.vo.OrderShowInfoVo;

import java.util.List;

public interface OrderService extends IService<Order> {
    Page<OrderShowInfoVo> getOrderByPage(OrderQueryDto queryDto);

    Page<OrderShowInfoVo> getOrdersWithCustomer(OrderQueryDto queryDto);

    int addOrder(OrderAddDto order);

    int batchDelOrder(List<Integer> ids);

    OrderDetailVo getOrderDetail(Integer orderId);

    Integer setOrderShip(Integer orderId);

    Integer reviseOrder(OrderReviseDto orderReviseDto);

    OrderRevisionHistoryVo getOrderRevisionHistory(Integer orderId);
}
