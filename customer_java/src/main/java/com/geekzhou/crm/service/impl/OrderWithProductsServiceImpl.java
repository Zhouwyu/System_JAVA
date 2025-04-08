package com.geekzhou.crm.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.geekzhou.crm.entity.OrderWithProducts;
import com.geekzhou.crm.mapper.mybatis.OrderWithProductsMapper;
import com.geekzhou.crm.service.OrderWithProductsService;
import org.springframework.stereotype.Service;

@Service
public class OrderWithProductsServiceImpl extends ServiceImpl<OrderWithProductsMapper, OrderWithProducts> implements OrderWithProductsService {
}
