package com.cinema.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cinema.common.Result;
import com.cinema.entity.Seat;
import com.cinema.entity.TicketOrder;
import com.cinema.mapper.OrderMapper;
import com.cinema.mapper.SeatMapper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

@RestController
@RequestMapping("/api/seat")
public class SeatController {

    @Resource
    private SeatMapper seatMapper;

    @Resource
    private OrderMapper orderMapper;

    // 获取放映厅座位
    @GetMapping("/hall/{hallId}")
    public Result<?> getByHallId(@PathVariable Integer hallId) {
        List<Seat> seats = seatMapper.selectList(new QueryWrapper<Seat>()
                .eq("hall_id", hallId)
                .orderByAsc("row_num", "col_num"));
        return Result.success(seats);
    }

    // 获取场次已售座位
    @GetMapping("/sold/{screeningId}")
    public Result<?> getSoldSeats(@PathVariable Integer screeningId) {
        List<TicketOrder> orders = orderMapper.selectList(new QueryWrapper<TicketOrder>()
                .eq("screening_id", screeningId)
                .in("status", "PAID", "UNPAID"));
        List<Map<String, Integer>> soldSeats = new ArrayList<>();
        for (TicketOrder order : orders) {
            if (order.getSeats() != null && !order.getSeats().isEmpty()) {
                JSONArray arr = JSON.parseArray(order.getSeats());
                for (int i = 0; i < arr.size(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    Map<String, Integer> seat = new HashMap<>();
                    seat.put("row", obj.getInteger("row"));
                    seat.put("col", obj.getInteger("col"));
                    soldSeats.add(seat);
                }
            }
        }
        return Result.success(soldSeats);
    }

    // 更新座位状态
    @PutMapping
    public Result<?> update(@RequestBody Seat seat) {
        seatMapper.updateById(seat);
        return Result.success();
    }

    // 批量更新座位状态
    @PutMapping("/batch")
    public Result<?> batchUpdate(@RequestBody List<Seat> seats) {
        for (Seat seat : seats) {
            seatMapper.updateById(seat);
        }
        return Result.success();
    }
}
