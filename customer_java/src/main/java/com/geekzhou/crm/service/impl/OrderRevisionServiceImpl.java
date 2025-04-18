package com.geekzhou.crm.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.geekzhou.crm.entity.OrderRevision;
import com.geekzhou.crm.mapper.mybatis.OrderRevisionMapper;
import com.geekzhou.crm.service.OrderRevisionService;
import org.springframework.stereotype.Service;

@Service
public class OrderRevisionServiceImpl extends ServiceImpl<OrderRevisionMapper, OrderRevision> implements OrderRevisionService {
}
