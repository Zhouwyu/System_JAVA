package com.geekzhou.crm.controller;

import com.geekzhou.crm.common.Result;
import com.geekzhou.crm.dto.CustomerQueryDto;
import com.geekzhou.crm.entity.Customer;
import com.geekzhou.crm.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/customer")
public class CustomerController {
    @Autowired
    CustomerService customerService;

    @GetMapping("/page")
    public Result getPageInfo(CustomerQueryDto queryDto) {
        return Result.success(customerService.getCustomerByPage(queryDto));
    }

    @GetMapping("/checkWechat")
    public Result checkWechat(@RequestParam String wechatNum, @RequestParam(required = false) Long excludeId) {
        boolean exist = customerService.checkWeChatNoExist(wechatNum, excludeId);
        return Result.success(Collections.singletonMap("exist", exist));
    }

    @GetMapping("/checkPhone")
    public Result checkPhone(@RequestParam String phoneNum, @RequestParam(required = false) Long excludeId) {
        boolean exist = customerService.checkPhoneNoExist(phoneNum, excludeId);
        return Result.success(Collections.singletonMap("exist", exist));
    }

    @PostMapping("/add")
    public Result addEmployee(@RequestBody Customer customer) {
        return Result.success(customerService.addCustomer(customer));
    }

    @PostMapping("/delete/batch")
    public Result deleteEmployee(@RequestBody Map<String, List<Long>> request) {
        List<Long> ids = request.get("ids");
        return Result.success(customerService.batchDelCustomer(ids));
    }

    @PostMapping("/update")
    public Result updateEmployee(@RequestBody Customer customer) {
        return Result.success(customerService.updateCustomer(customer));
    }

    @GetMapping("/list")
    public Result getAllCustomer() {
        return Result.success(customerService.getAllCustomer());
    }

    @GetMapping("/{customerId}")
    public Result getCustomerById(@PathVariable Integer customerId) {
        return Result.success(customerService.getCustomerById(customerId));
    }

}
