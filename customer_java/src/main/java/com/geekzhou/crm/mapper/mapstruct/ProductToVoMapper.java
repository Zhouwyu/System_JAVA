package com.geekzhou.crm.mapper.mapstruct;

import com.geekzhou.crm.entity.Product;
import com.geekzhou.crm.vo.ProductInfoVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductToVoMapper {
    @Mapping(target = "productId", source = "productId")
    @Mapping(target = "productName", source = "productName")
    @Mapping(target = "price", source = "price")
    @Mapping(target = "stockQuantity", source = "stockQuantity")
    @Mapping(target = "unit", source = "unit")
    ProductInfoVo toVo(Product product);
    // 集合转换（MapStruct自动生成实现）
    List<ProductInfoVo> toVoList(List<Product> products);
}
