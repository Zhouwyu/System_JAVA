package com.geekzhou.crm.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.log.StaticLog;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.geekzhou.crm.dto.ProductQueryDto;
import com.geekzhou.crm.entity.Product;
import com.geekzhou.crm.exception.CustomException;
import com.geekzhou.crm.mapper.mapstruct.ProductToVoMapper;
import com.geekzhou.crm.mapper.mybatis.ProductMapper;
import com.geekzhou.crm.service.ProductService;
import com.geekzhou.crm.vo.ProductInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ProductToVoMapper productToVoMapper;

    @Override
    public Page<Product> getProductByPage(ProductQueryDto queryDTO) {
        // 构建分页参数
        Page<Product> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());

        // 构建动态查询条件
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();

        // isDeleted 是0 的才会显示
        wrapper.eq(Product::getIsDeleted, 0);

        // 名称模糊查询（如果传了name参数）
        wrapper.like(StrUtil.isNotBlank(queryDTO.getName()),
                Product::getProductName,
                queryDTO.getName());

        // 供应商模糊查询
        wrapper.like(StrUtil.isNotBlank(queryDTO.getSupplier()),
                Product::getSupplierChannel,
                queryDTO.getSupplier());

        // 联系方式模糊查询
        wrapper.like(StrUtil.isNotBlank(queryDTO.getContact()),
                Product::getSupplierContact,
                queryDTO.getContact());

        // 进货日期范围查询
        if (queryDTO.getBeginPurchaseDate() != null) {
            wrapper.ge(Product::getPurchaseDate, queryDTO.getBeginPurchaseDate());
        }
        if (queryDTO.getEndPurchaseDate() != null) {
            wrapper.le(Product::getPurchaseDate, queryDateRangeEnd(queryDTO.getEndPurchaseDate()));
        }

        // 按进货日期升序，早进货的排前面
        wrapper.orderByAsc(Product::getPurchaseDate);

        // 执行分页查询
        return baseMapper.selectPage(page, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int addProduct(Product product) {
        Long count = lambdaQuery()
                .eq(Product::getProductName, product.getProductName())
                .count();
        if (count > 0) {
            throw new CustomException("3001", "商品已存在！");
        }
        // 软删除标记设置
        product.setIsDeleted(0);
        return productMapper.insert(product);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateProduct(Product product) {
        return productMapper.updateById(product);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchDelProduct(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            StaticLog.warn("批量删除传入空ID列表");
            return 0;
        }

        return productMapper.update(new Product(),
                Wrappers.<Product>lambdaUpdate()
                        .set(Product::getIsDeleted, 1)
                        .in(Product::getProductId, ids)
        );
    }

    @Override
    public List<ProductInfoVo> getAllProduct() {
        List<Product> products = productMapper.selectList(
                Wrappers.<Product>lambdaQuery()
                        .select(Product::getProductId, Product::getProductName, Product::getPrice, Product::getStockQuantity) // 只返回必要字段
                        .eq(Product::getIsDeleted, 0) // 未删除
                        .gt(Product::getStockQuantity, 0) // 库存大于0
                        .orderByDesc(Product::getCreateTime)       // 按创建时间倒序
        );
        return productToVoMapper.toVoList(products);
    }

    @Override
    public Product selectById(Integer productId) {
        return productMapper.selectById(productId);
    }

    // 处理结束日期的时间范围（包含当天）
    private LocalDate queryDateRangeEnd(LocalDate endDate) {
        return endDate != null ? endDate.plusDays(1) : null;
    }
}
