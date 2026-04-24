package com.cinema.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cinema.common.Result;
import com.cinema.entity.TicketOrder;
import com.cinema.entity.User;
import com.cinema.mapper.OrderMapper;
import com.cinema.mapper.UserMapper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private UserMapper userMapper;

    // 查询所有订单（管理端）
    @GetMapping("/list")
    public Result<?> list() {
        List<TicketOrder> list = orderMapper.selectAllWithInfo();
        return Result.success(list);
    }

    // 查询用户订单
    @GetMapping("/user/{userId}")
    public Result<?> getByUserId(@PathVariable Integer userId) {
        List<TicketOrder> list = orderMapper.selectByUserId(userId);
        return Result.success(list);
    }

    // 根据ID查询
    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Integer id) {
        TicketOrder order = orderMapper.selectByIdWithInfo(id);
        return Result.success(order);
    }

    // 创建订单
    @PostMapping
    public Result<?> add(@RequestBody TicketOrder order) {
        // 生成订单编号
        String orderNo = "ORD" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + String.format("%04d", new Random().nextInt(10000));
        order.setOrderNo(orderNo);
        order.setStatus("UNPAID");
        orderMapper.insert(order);
        return Result.success(order);
    }

    // 支付订单
    @PutMapping("/pay/{id}")
    public Result<?> pay(@PathVariable Integer id) {
        TicketOrder order = orderMapper.selectById(id);
        if (order == null) {
            return Result.error("订单不存在");
        }
        if (!"UNPAID".equals(order.getStatus())) {
            return Result.error("订单状态不正确");
        }
        order.setStatus("PAID");
        order.setPayTime(LocalDateTime.now());
        orderMapper.updateById(order);

        // 增加用户积分（每消费1元积1分）
        User user = userMapper.selectById(order.getUserId());
        if (user != null) {
            int addPoints = order.getTotalPrice().intValue();
            user.setPoints(user.getPoints() + addPoints);
            userMapper.updateById(user);
        }
        return Result.success();
    }

    // 取消订单
    @PutMapping("/cancel/{id}")
    public Result<?> cancel(@PathVariable Integer id) {
        TicketOrder order = orderMapper.selectById(id);
        if (order == null) {
            return Result.error("订单不存在");
        }
        order.setStatus("CANCELLED");
        orderMapper.updateById(order);
        return Result.success();
    }

    // 核销订单
    @PutMapping("/use/{id}")
    public Result<?> use(@PathVariable Integer id) {
        TicketOrder order = orderMapper.selectById(id);
        if (order == null) {
            return Result.error("订单不存在");
        }
        if (!"PAID".equals(order.getStatus())) {
            return Result.error("订单状态不正确，只有已支付的订单才能核销");
        }
        order.setStatus("USED");
        orderMapper.updateById(order);
        return Result.success();
    }

    // 退票
    @PutMapping("/refund/{id}")
    public Result<?> refund(@PathVariable Integer id) {
        TicketOrder order = orderMapper.selectById(id);
        if (order == null) {
            return Result.error("订单不存在");
        }
        if (!"PAID".equals(order.getStatus())) {
            return Result.error("只有已支付的订单才能退票");
        }
        order.setStatus("REFUNDED");
        orderMapper.updateById(order);

        // 扣减积分
        User user = userMapper.selectById(order.getUserId());
        if (user != null) {
            int subPoints = order.getTotalPrice().intValue();
            user.setPoints(Math.max(0, user.getPoints() - subPoints));
            userMapper.updateById(user);
        }
        return Result.success();
    }

    // 删除订单
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Integer id) {
        orderMapper.deleteById(id);
        return Result.success();
    }
}
