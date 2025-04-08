package com.geekzhou.crm.mapper.mapstruct;

import com.geekzhou.crm.entity.Order;
import com.geekzhou.crm.vo.OrderShowInfoVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 实体类映射为VO用于前端展示（订单管理页面）
 */
@Mapper(componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        imports = {LocalDateTime.class})
public interface OrderToShowInfoVoMapper {
    @Mapping(target = "orderNum", source = "orderNo")
    @Mapping(target = "createTime", source = "createTime")
    @Mapping(target = "shipmentDate", source = "shipmentDate")
    @Mapping(target = "updateTime", source = "updateTime")
    @Mapping(target = "status", source = "status")
    OrderShowInfoVo entityToShowVo(Order entity);
    List<OrderShowInfoVo> entityToShowVoList(List<Order> orders);
}
