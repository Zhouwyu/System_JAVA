package com.geekzhou.crm.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.log.StaticLog;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.geekzhou.crm.dto.CustomerQueryDto;
import com.geekzhou.crm.entity.Customer;
import com.geekzhou.crm.exception.CustomException;
import com.geekzhou.crm.mapper.mybatis.CustomerMapper;
import com.geekzhou.crm.mapper.mapstruct.CustomerToVoMapper;
import com.geekzhou.crm.service.CustomerService;
import com.geekzhou.crm.vo.CustomerInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class CustomerServiceImpl extends ServiceImpl<CustomerMapper, Customer> implements CustomerService {
    @Autowired
    private CustomerMapper customerMapper;
    @Autowired
    private CustomerToVoMapper VoMapper; // Spring 自动注入

    @Override
    public Page<Customer> getCustomerByPage(CustomerQueryDto queryDTO) {
        // 构建分页参数
        Page<Customer> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());

        // 构建动态查询条件
        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<>();

        // isDeleted 是0 的才会显示
        wrapper.eq(Customer::getIsDeleted, 0);

        // 名称模糊查询（如果传了name参数）
        wrapper.like(StrUtil.isNotBlank(queryDTO.getName()),
                Customer::getCustomerName,
                queryDTO.getName());

        // 行业模糊匹配
        wrapper.like(StrUtil.isNotBlank(queryDTO.getIndustry()),
                Customer::getBusinessIndustry,
                queryDTO.getIndustry());

        // 性别精确匹配
        wrapper.eq(StrUtil.isNotBlank(queryDTO.getGender()),
                Customer::getSex,
                queryDTO.getGender());

        // 手机号模糊查询
        wrapper.like(StrUtil.isNotBlank(queryDTO.getPhone()),
                Customer::getPhoneNum,
                queryDTO.getPhone());

        // 微信号模糊查询
        wrapper.like(StrUtil.isNotBlank(queryDTO.getWechatNum()),
                Customer::getWechatNum,
                queryDTO.getWechatNum());

        // 按创建时间倒序
        wrapper.orderByDesc(Customer::getCreateTime);

        // 执行分页查询
        return baseMapper.selectPage(page, wrapper);
    }

    @Override
    public boolean checkWeChatNoExist(String wechatNum, Long excludeId) {
        LambdaQueryWrapper<Customer> queryWrapper = new LambdaQueryWrapper<>();
        // 精准匹配微信号
        queryWrapper.eq(Customer::getWechatNum, wechatNum);

        if (excludeId != null) {
            queryWrapper.ne(Customer::getCustomerId, excludeId); // 编辑模式下排除指定ID
        }
        return count(queryWrapper) > 0;
    }

    @Override
    public boolean checkPhoneNoExist(String phone, Long excludeId) {
        LambdaQueryWrapper<Customer> queryWrapper = new LambdaQueryWrapper<>();
        // 精准匹配手机号
        queryWrapper.eq(Customer::getPhoneNum, phone);

        if (excludeId != null) {
            queryWrapper.ne(Customer::getCustomerId, excludeId); // 编辑模式下排除指定ID
        }
        return count(queryWrapper) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int addCustomer(Customer customer) {
        if (customer==null) {
            throw new CustomException("3003", "客户信息为null");
        }
//        Long count = lambdaQuery()
//                .eq(Customer::getWechatNum, customer.getWechatNum())
//                .eq(Customer::getPhoneNum, customer.getPhoneNum())
//                .count();
//        if (count > 0) {
//            throw new CustomException("3001", "微信号/手机号已存在，请更换！");
//        }
        // 软删除标记设置
        customer.setIsDeleted(0);
        return customerMapper.insert(customer);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchDelCustomer(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            StaticLog.warn("批量删除传入空ID列表");
            return 0;
        }

        return customerMapper.update(new Customer(),
                Wrappers.<Customer>lambdaUpdate()
                        .set(Customer::getIsDeleted, 1)
                        .in(Customer::getCustomerId, ids)
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateCustomer(Customer customer) {
        StaticLog.info("看看Customer:{}", customer);
        return customerMapper.updateById(customer);
    }

    /**
     * 获取所有客户信息：客户ID，客户名称
     */
    @Override
    public List<CustomerInfoVo> getAllCustomer() {
        List<Customer> customers = customerMapper.selectList(
                Wrappers.<Customer>lambdaQuery()
                        .select(Customer::getCustomerId, Customer::getCustomerName, Customer::getPhoneNum) // 只返回必要字段
                        .eq(Customer::getIsDeleted, 0)
                        .orderByDesc(Customer::getCreateTime)       // 按创建时间倒序
        );
        return VoMapper.toVoList(customers);
    }

    /**
     * 根据客户ID获取客户ID，客户名称，客户手机号，客户地址，客户所在业务行业
     */
    @Override
    public CustomerInfoVo getCustomerById(Integer customerId) {
        // 1. 执行查询（仅查询必要字段）
        Customer customer = customerMapper.selectOne(
                Wrappers.<Customer>lambdaQuery()
                        .select(
                                Customer::getCustomerId,
                                Customer::getCustomerName,
                                Customer::getPhoneNum,
                                Customer::getAddress,
                                Customer::getBusinessIndustry
                        )
                        .eq(Customer::getCustomerId, customerId)
                        .eq(Customer::getIsDeleted, 0)
        );

        // 2. 校验查询结果
        if (customer == null) {
            throw new CustomException("4001", "不存在该客户！");
        }

        // 3. 执行类型转换
        return VoMapper.toVo(customer);
    }
}
