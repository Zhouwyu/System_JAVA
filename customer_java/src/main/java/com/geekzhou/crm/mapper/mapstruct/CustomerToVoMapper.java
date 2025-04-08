package com.geekzhou.crm.mapper.mapstruct;

import com.geekzhou.crm.entity.Customer;
import com.geekzhou.crm.vo.CustomerInfoVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CustomerToVoMapper {
    @Mapping(target = "customerId", source = "customerId")
    @Mapping(target = "customerName", source = "customerName")
    @Mapping(target = "phoneNum", source = "phoneNum")
    @Mapping(target = "address", source = "address")
    @Mapping(target = "businessIndustry", source = "businessIndustry")
    CustomerInfoVo toVo(Customer customer);
    // 集合转换（MapStruct自动生成实现）
    List<CustomerInfoVo> toVoList(List<Customer> customers);
}
