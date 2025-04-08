package com.geekzhou.crm.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.geekzhou.crm.dto.ProductQueryDto;
import com.geekzhou.crm.entity.Product;
import com.geekzhou.crm.vo.ProductInfoVo;

import java.util.List;

public interface ProductService extends IService<Product> {
    Page<Product> getProductByPage(ProductQueryDto queryDto);

    int addProduct(Product product);

    int updateProduct(Product product);

    int batchDelProduct(List<Long> ids);

    List<ProductInfoVo> getAllProduct();

    Product selectById(Integer productId);
}
