package com.geekzhou.crm.controller;

import cn.hutool.log.StaticLog;
import com.geekzhou.crm.common.Result;
import com.geekzhou.crm.dto.OrderAddDto;
import com.geekzhou.crm.dto.OrderQueryDto;
import com.geekzhou.crm.dto.OrderReviseDto;
import com.geekzhou.crm.dto.OrderWithProductsDto;
import com.geekzhou.crm.entity.Order;
import com.geekzhou.crm.entity.Product;
import com.geekzhou.crm.exception.CustomException;
import com.geekzhou.crm.service.OrderService;
import com.geekzhou.crm.service.ProductService;
import com.geekzhou.crm.vo.OrderDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private ProductService productService;

    @GetMapping("/page")
    public Result getPageInfo(OrderQueryDto queryDto) {
        return Result.success(orderService.getOrdersWithCustomer(queryDto));
    }

    @PostMapping("/add")
    public Result addOrder(@RequestBody OrderAddDto order) {
        return Result.success(orderService.addOrder(order));
    }

    @PostMapping("/delete/batch")
    public Result deleteOrder(@RequestBody Map<String, List<Integer>> request) {
        List<Integer> ids = request.get("ids");
        return Result.success(orderService.batchDelOrder(ids));
    }

    @PostMapping("/update")
    public Result updateOrder(@RequestBody Order order) {
        return Result.success(orderService.updateOrder(order));
    }


    @GetMapping("/detail/{orderId}")
    public Result getOrderDetail(@PathVariable Integer orderId) {
        OrderDetailVo orderDetail = orderService.getOrderDetail(orderId);
        return Result.success(orderDetail);
    }

    @PostMapping("/precheck")
    public Result preCheckStock(@RequestBody List<OrderWithProductsDto> products) {
        StaticLog.info("库存预检查，当前检查商品是：{}", products);
        products.forEach(item -> {
            Product product = productService.selectById(item.getProductId());
            if (product == null || product.getStockQuantity() < item.getQuantity()) {
                assert product != null;
                throw new CustomException("409", product.getProductName() + "库存不足，请补货！");
            }
        });
        return Result.success();
    }

    /**
     * 标记对应订单已出货
     */
    @GetMapping("/ship/{orderId}")
    public Result setOrderShip(@PathVariable Integer orderId) {
        Integer updateRows = orderService.setOrderShip(orderId);
        return Result.success(updateRows);
    }

    @PostMapping("/revise")
    public Result reviseOrder(@RequestBody OrderReviseDto orderReviseDto) {
        return Result.success(orderService.reviseOrder(orderReviseDto));
    }
}
