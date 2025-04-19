package com.geekzhou.crm.mapper.mapstruct;

import com.geekzhou.crm.entity.Product;
import com.geekzhou.crm.vo.ProductInfoVo;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-04-19T10:26:32+0800",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17 (Oracle Corporation)"
)
@Component
public class ProductToVoMapperImpl implements ProductToVoMapper {

    @Override
    public ProductInfoVo toVo(Product product) {
        if ( product == null ) {
            return null;
        }

        ProductInfoVo productInfoVo = new ProductInfoVo();

        productInfoVo.setProductId( product.getProductId() );
        productInfoVo.setProductName( product.getProductName() );
        productInfoVo.setPrice( product.getPrice() );
        productInfoVo.setStockQuantity( product.getStockQuantity() );

        return productInfoVo;
    }

    @Override
    public List<ProductInfoVo> toVoList(List<Product> products) {
        if ( products == null ) {
            return null;
        }

        List<ProductInfoVo> list = new ArrayList<ProductInfoVo>( products.size() );
        for ( Product product : products ) {
            list.add( toVo( product ) );
        }

        return list;
    }
}
