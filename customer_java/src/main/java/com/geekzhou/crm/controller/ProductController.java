package com.geekzhou.crm.controller;

import cn.hutool.log.StaticLog;
import com.geekzhou.crm.common.Result;
import com.geekzhou.crm.dto.ProductQueryDto;
import com.geekzhou.crm.entity.Product;
import com.geekzhou.crm.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/product")
public class ProductController {
    @Autowired
    private ProductService productService;

    @GetMapping("/page")
    public Result getPageInfo(ProductQueryDto queryDto) {
        return Result.success(productService.getProductByPage(queryDto));
    }

    @PostMapping("/add")
    public Result addProduct(@RequestBody Product product) {
        StaticLog.info("INFO:{}", product.getProductImage());
        return Result.success(productService.addProduct(product));
    }

    @PostMapping("/delete/batch")
    public Result deleteEmployee(@RequestBody Map<String, List<Long>> request) {
        List<Long> ids = request.get("ids");
        return Result.success(productService.batchDelProduct(ids));
    }

    @PostMapping("/update")
    public Result updateEmployee(@RequestBody Product product) {
        return Result.success(productService.updateProduct(product));
    }

    @GetMapping("/list")
    public Result getProductList() {
        return Result.success(productService.getAllProduct());
    }
}
