package com.geekzhou.crm.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.log.StaticLog;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.geekzhou.crm.common.OrderShipmentStatus;
import com.geekzhou.crm.dto.OrderAddDto;
import com.geekzhou.crm.dto.OrderQueryDto;
import com.geekzhou.crm.dto.OrderWithProductsDto;
import com.geekzhou.crm.entity.*;
import com.geekzhou.crm.exception.CustomException;
import com.geekzhou.crm.mapper.mapstruct.OrderToShowInfoVoMapper;
import com.geekzhou.crm.mapper.mybatis.*;
import com.geekzhou.crm.service.OrderService;
import com.geekzhou.crm.utils.OrderUtils;
import com.geekzhou.crm.vo.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderWithProductsMapper orderWithProductsMapper;
    @Autowired
    private OrderToShowInfoVoMapper showInfoVoMapper;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private CustomerMapper customerMapper;
    @Autowired
    private UserMapper userMapper;

    /**
     * 暂时废弃不用
     */
    @Override
    public Page<OrderShowInfoVo> getOrderByPage(OrderQueryDto queryDto) {
        // 1. 构建分页参数（使用Order实体类接收）
        Page<Order> entityPage = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());

        // 2. 构建动态查询条件
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();

        // 基础条件：未删除
        queryWrapper.eq(Order::getIsDeleted, 0);

        // 3. 时间范围查询
        if (queryDto.getBeginShipmentDate() != null) {
            queryWrapper.ge(Order::getShipmentDate, queryDto.getBeginShipmentDate());
        }
        if (queryDto.getEndShipmentDate() != null) {
            // 处理结束时间范围（包含当天）
            LocalDate endDateTime = queryDateRangeEnd(queryDto.getEndShipmentDate());
            queryWrapper.le(Order::getShipmentDate, endDateTime);
        }
        // 4. 排序规则（示例按出货时间倒序）
        queryWrapper.orderByDesc(Order::getShipmentDate);

        // 5. 执行原始分页查询（获取Order实体分页）
        Page<Order> orderPage = baseMapper.selectPage(entityPage, queryWrapper);

        // 6. 转换为VO分页对象
        return convertToVoPage(orderPage);
    }

    // 处理结束日期的时间范围（包含当天）
    private LocalDate queryDateRangeEnd(LocalDate endDate) {
        return endDate != null ? endDate.plusDays(1) : null;
    }

    // 转换分页对象方法
    private Page<OrderShowInfoVo> convertToVoPage(Page<Order> entityPage) {
        // 创建VO分页对象（复制分页信息）
        Page<OrderShowInfoVo> voPage = new Page<>();
        BeanUtils.copyProperties(entityPage, voPage, "records");

        // 使用MapStruct转换记录列表
        List<OrderShowInfoVo> voRecords = showInfoVoMapper.entityToShowVoList(entityPage.getRecords());

        // 设置转换后的记录和分页信息
        voPage.setRecords(voRecords);
        return voPage;
    }

    /**
     * 订单管理分页页面
     */
    @Override
    public Page<OrderShowInfoVo> getOrdersWithCustomer(OrderQueryDto queryDto) {
        // 1. 构建分页参数
        Page<OrderShowInfoVo> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());

        // 2. 构建查询条件
        QueryWrapper<Order> wrapper = new QueryWrapper<>();
        wrapper.apply("o.is_deleted = {0}", 0);  // 关键修改：添加表别名
        // 动态条件（仅在值非空时添加）
        if (!StrUtil.isEmpty(queryDto.getOrderNo())) {
            wrapper.eq("o.order_num", queryDto.getOrderNo());
        }
        if (queryDto.getCustomerId() != null) {
            wrapper.eq("o.customer_id", queryDto.getCustomerId());
        }
        // 3. 时间范围查询
        if (queryDto.getBeginShipmentDate() != null) {
            wrapper.ge("o.shipment_date", queryDto.getBeginShipmentDate());
        }
        if (queryDto.getEndShipmentDate() != null) {
            // 处理结束时间范围（包含当天）
            LocalDate endDateTime = queryDateRangeEnd(queryDto.getEndShipmentDate());
            wrapper.le("o.shipment_date", endDateTime);
        }
        // 4. 排序规则（优先未出货的，出货时间早的）
        wrapper.orderByAsc("o.status","o.shipment_date");
        // 执行自定义查询
        return (Page<OrderShowInfoVo>) orderMapper.selectWithCustomer(page, wrapper);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public int addOrder(OrderAddDto orderDto) {
        StaticLog.info("看看商品列表有什么：{}", orderDto.getProductsDtos());
        // 1. 生成订单编号, 插入orders表需要的
        String orderNum = OrderUtils.generate("GZ");
        Order order = new Order();
        order.setOrderNo(orderNum);
        order.setCustomerId(orderDto.getCustomerId());
        // 定义匹配字符串的格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        order.setShipmentDate(LocalDateTime.parse(orderDto.getShipmentDate(), formatter));
        order.setTotalPrice(orderDto.getTotalPrice());
        order.setRemark(orderDto.getRemark());
        order.setIsDeleted(0);
        order.setTotalPrice(orderDto.getTotalPrice());
        order.setOperator(orderDto.getOperator());
        // 2. 存order表
        orderMapper.insert(order);
        // 3. 接着存order_products表（商品明细）
        // 获取前端选择的商品明细信息
        List<OrderWithProductsDto> productsDtos = orderDto.getProductsDtos();
        // 插入订单商品关联表
        List<OrderWithProducts> orderProducts = convertToOrderProducts(orderNum, productsDtos);
        if (!orderProducts.isEmpty()) {
            orderWithProductsMapper.insertBatch(orderProducts);
        }
        // 商品库存减少操作
        // 4. 扣减库存（必须在事务中）
        deductStocks(orderDto.getProductsDtos());
        // 成功则返回订单ID
        return order.getOrderId();
    }

    /**
     * 计算商品总价(前端计算了，这里暂时废弃)
     */
    private BigDecimal calculateTotalPrice(List<OrderWithProductsDto> products) {
        return products.stream()
                .map(p -> p.getPrice().multiply(BigDecimal.valueOf(p.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * 把DTO转换成entity之后批量插入订单商品关联表
     */
    private List<OrderWithProducts> convertToOrderProducts(String orderNum, List<OrderWithProductsDto> dtos) {
        return dtos.stream().map(dto -> {
            OrderWithProducts op = new OrderWithProducts();
            op.setOrderNo(orderNum);
            op.setProductId(dto.getProductId());
            op.setQuantity(dto.getQuantity());
            op.setUnitPrice(dto.getPrice());
            op.setSalePrice(dto.getSalePrice());
            return op;
        }).collect(Collectors.toList());
    }

    /**
     * 批量扣减库存
     */
    private void deductStocks(List<OrderWithProductsDto> products) {
        products.forEach(item -> {
            int rows = productMapper.deductStock(item.getProductId(), item.getQuantity());
            // 更新返回行数为0，说明库存不足或版本冲突
            if (rows == 0) {
                throw new RuntimeException("商品ID: " + item.getProductId() + " 库存不足或不存在");
            }
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchDelOrder(List<Integer> ids) {
        StaticLog.info("看看前端传过来的订单编号集合：{}", ids);
        // 1. 查询有效的未删除订单
        List<Order> validOrders = orderMapper.selectList(
                Wrappers.<Order>lambdaQuery()
                        .select(Order::getOrderId, Order::getOrderNo) // 使用实体类get方法
                        .in(Order::getOrderId, ids)
                        .eq(Order::getIsDeleted, 0)
        );
        StaticLog.info("看看需要删除的订单：{}", validOrders);
        if (validOrders.isEmpty()) {
            return 0;
        }

        // 2. 获取订单编号集合
        List<String> orderNos = validOrders.stream()
                .map(Order::getOrderNo)
                .collect(Collectors.toList());
        StaticLog.info("看看订单编号集合：{}", orderNos);

        // 3. 查询订单商品关联记录
        List<OrderWithProducts> orderProducts = orderWithProductsMapper.selectList(
                new QueryWrapper<OrderWithProducts>()
                        .select("product_id", "quantity")
                        .in("order_num", orderNos)
        );
        StaticLog.info("看看商品关联记录：{}", orderProducts);

        // 4. 按商品ID聚合需要恢复的数量
        Map<Integer, Integer> productStockRecovery = orderProducts.stream()
                .collect(Collectors.groupingBy(
                        OrderWithProducts::getProductId,
                        Collectors.summingInt(OrderWithProducts::getQuantity)
                ));

        StaticLog.info("看看需要恢复的数据：{}", productStockRecovery);
        // 5. 批量恢复商品库存（使用SQL原子操作）
        productStockRecovery.forEach((productId, quantity) -> {
            // 1. 先查询当前版本号
            Product product = productMapper.selectOne(
                    new QueryWrapper<Product>()
                            .select("version")
                            .eq("products_id", productId)
            );

            // 2. 带版本号的更新
            int rows = productMapper.update(null,
                    new UpdateWrapper<Product>()
                            .setSql("stock_quantity = stock_quantity + " + quantity)
                            .setSql("version = version + 1")
                            .eq("products_id", productId)
                            .eq("version", product.getVersion())
            );

            // 3. 处理并发冲突
            if (rows == 0) {
                throw new OptimisticLockingFailureException("商品ID：" + productId + " 库存恢复失败，版本冲突");
            }
        });

        // 6. 执行批量软删除
        List<Integer> validOrderIds = validOrders.stream()
                .map(Order::getOrderId)
                .collect(Collectors.toList());

        int updateCount = orderMapper.update(null,
                new UpdateWrapper<Order>()
                        .set("is_deleted", 1)
                        .in("order_id", validOrderIds)
        );

        return updateCount;
    }

    @Override
    public int updateOrder(Order order) {
        return 0;
    }

    /**
     * 获取订单详情
     */
    @Override
    public OrderDetailVo getOrderDetail(Integer orderId) {
        // 先获取订单自身的相关信息
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new CustomException("408", "订单不存在！");
        }
        OrderDetailVo orderDetailVo = new OrderDetailVo();
        orderDetailVo.setOrderId(orderId);
        orderDetailVo.setOrderNo(order.getOrderNo());
        orderDetailVo.setCreateTime(order.getCreateTime());
        orderDetailVo.setUpdateTime(order.getUpdateTime());
        orderDetailVo.setTotalPrice(order.getTotalPrice());
        orderDetailVo.setRemark(order.getRemark());
        orderDetailVo.setShipmentDate(order.getShipmentDate());
        orderDetailVo.setStatus(order.getStatus());
        // 获取客户信息
        Customer customer = customerMapper.selectById(order.getCustomerId());
        if (customer != null) {
            CustomerDetailVo customerDetailVo = new CustomerDetailVo();
            customerDetailVo.setCustomerName(customer.getCustomerName());
            customerDetailVo.setAddress(customer.getAddress());
            customerDetailVo.setPhoneNum(customer.getPhoneNum());
            orderDetailVo.setCustomer(customerDetailVo);
        }
        // 获取商品信息
        List<OrderWithProducts> orderWithProductsList = orderWithProductsMapper.selectList(new QueryWrapper<OrderWithProducts>().eq("order_num", order.getOrderNo()));
        StaticLog.info("看看所选的商品信息{}", orderWithProductsList);
        // 使用并行流提高处理效率（当商品数量较多时）
        List<ProductItemDetailVo> productItemList = orderWithProductsList.parallelStream()
                .map(owp -> {
                    Product product = productMapper.selectById(owp.getProductId());
                    StaticLog.info("看看这个ID：{}的商品信息{}", owp.getProductId(), product);
                    return product != null ?
                            new ProductItemDetailVo(
                                    owp.getProductId(),
                                    product.getProductName(),
                                    owp.getUnitPrice(),
                                    owp.getQuantity(),
                                    owp.getSalePrice()
                            ) : null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        orderDetailVo.setProducts(productItemList);
        // 操作用户信息
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, order.getOperator()));
        if (user != null) {
            OperatorDetailVo operatorDetailVo = new OperatorDetailVo();
            operatorDetailVo.setName(user.getUsername());
            operatorDetailVo.setUserId(user.getUserId());
            orderDetailVo.setOperator(operatorDetailVo);
        }
        return orderDetailVo;
    }

    /**
     * 更改出货状态,
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer setOrderShip(Integer orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            StaticLog.warn("订单不存在, orderId={}", orderId);
            return 0;
        }

        if (order.getStatus() == OrderShipmentStatus.SHIPPED.getCode()) {
            StaticLog.warn("订单已是出货状态, orderId={}", orderId);
            return 0;
        }

        StaticLog.info("更新订单状态为出货, orderId={}", orderId);
        order.setStatus(OrderShipmentStatus.SHIPPED.getCode());
        orderMapper.updateById(order);
        return 1;
    }
}
