package com.geekzhou.crm.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.geekzhou.crm.dto.CustomerQueryDto;
import com.geekzhou.crm.entity.Customer;
import com.geekzhou.crm.vo.CustomerInfoVo;

import java.util.List;

public interface CustomerService extends IService<Customer> {
    Page<Customer> getCustomerByPage(CustomerQueryDto queryDTO);

    boolean checkWeChatNoExist(String wechatNum, Long excludeId);

    boolean checkPhoneNoExist(String phone, Long excludeId);

    int addCustomer(Customer customer);

    int batchDelCustomer(List<Long> ids);

    int updateCustomer(Customer customer);

    List<CustomerInfoVo>  getAllCustomer();

    CustomerInfoVo getCustomerById(Integer customerId);
}
