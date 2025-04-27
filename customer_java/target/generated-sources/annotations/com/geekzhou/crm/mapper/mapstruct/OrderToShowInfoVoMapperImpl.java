package com.geekzhou.crm.mapper.mapstruct;

import com.geekzhou.crm.entity.Order;
import com.geekzhou.crm.vo.OrderShowInfoVo;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-04-26T21:06:20+0800",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17 (Oracle Corporation)"
)
@Component
public class OrderToShowInfoVoMapperImpl implements OrderToShowInfoVoMapper {

    @Override
    public OrderShowInfoVo entityToShowVo(Order entity) {
        if ( entity == null ) {
            return null;
        }

        OrderShowInfoVo orderShowInfoVo = new OrderShowInfoVo();

        if ( entity.getOrderNo() != null ) {
            orderShowInfoVo.setOrderNum( entity.getOrderNo() );
        }
        if ( entity.getCreateTime() != null ) {
            orderShowInfoVo.setCreateTime( entity.getCreateTime() );
        }
        if ( entity.getShipmentDate() != null ) {
            orderShowInfoVo.setShipmentDate( entity.getShipmentDate() );
        }
        if ( entity.getUpdateTime() != null ) {
            orderShowInfoVo.setUpdateTime( entity.getUpdateTime() );
        }
        if ( entity.getStatus() != null ) {
            orderShowInfoVo.setStatus( entity.getStatus() );
        }
        if ( entity.getOrderId() != null ) {
            orderShowInfoVo.setOrderId( entity.getOrderId() );
        }
        if ( entity.getCustomerId() != null ) {
            orderShowInfoVo.setCustomerId( entity.getCustomerId() );
        }
        if ( entity.getProductId() != null ) {
            orderShowInfoVo.setProductId( entity.getProductId() );
        }
        if ( entity.getProductQuantity() != null ) {
            orderShowInfoVo.setProductQuantity( entity.getProductQuantity() );
        }
        if ( entity.getTotalPrice() != null ) {
            orderShowInfoVo.setTotalPrice( entity.getTotalPrice() );
        }
        if ( entity.getRemark() != null ) {
            orderShowInfoVo.setRemark( entity.getRemark() );
        }

        return orderShowInfoVo;
    }

    @Override
    public List<OrderShowInfoVo> entityToShowVoList(List<Order> orders) {
        if ( orders == null ) {
            return null;
        }

        List<OrderShowInfoVo> list = new ArrayList<OrderShowInfoVo>( orders.size() );
        for ( Order order : orders ) {
            list.add( entityToShowVo( order ) );
        }

        return list;
    }
}
