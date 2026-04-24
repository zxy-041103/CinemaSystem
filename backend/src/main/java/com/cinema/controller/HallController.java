package com.cinema.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cinema.common.Result;
import com.cinema.entity.Hall;
import com.cinema.entity.Seat;
import com.cinema.mapper.HallMapper;
import com.cinema.mapper.SeatMapper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/hall")
public class HallController {

    @Resource
    private HallMapper hallMapper;

    @Resource
    private SeatMapper seatMapper;

    @GetMapping("/page")
    public Result<?> page(@RequestParam(defaultValue = "1") Integer pageNum,
                          @RequestParam(defaultValue = "10") Integer pageSize,
                          @RequestParam(required = false) String name) {
        QueryWrapper<Hall> wrapper = new QueryWrapper<>();
        if (name != null && !name.isEmpty()) {
            wrapper.like("name", name);
        }
        wrapper.orderByAsc("id");
        Page<Hall> page = hallMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        return Result.success(page);
    }

    @GetMapping("/list")
    public Result<?> list() {
        return Result.success(hallMapper.selectList(new QueryWrapper<Hall>().eq("status", 1)));
    }

    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Integer id) {
        return Result.success(hallMapper.selectById(id));
    }

    @PostMapping
    public Result<?> add(@RequestBody Hall hall) {
        hallMapper.insert(hall);
        // 自动生成座位
        generateSeats(hall.getId(), hall.getSeatRows(), hall.getSeatCols());
        return Result.success();
    }

    @PutMapping
    public Result<?> update(@RequestBody Hall hall) {
        Hall old = hallMapper.selectById(hall.getId());
        hallMapper.updateById(hall);
        // 如果行列数变化，重新生成座位
        if (!old.getSeatRows().equals(hall.getSeatRows()) || !old.getSeatCols().equals(hall.getSeatCols())) {
            seatMapper.delete(new QueryWrapper<Seat>().eq("hall_id", hall.getId()));
            generateSeats(hall.getId(), hall.getSeatRows(), hall.getSeatCols());
        }
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Integer id) {
        hallMapper.deleteById(id);
        seatMapper.delete(new QueryWrapper<Seat>().eq("hall_id", id));
        return Result.success();
    }

    private void generateSeats(Integer hallId, Integer rows, Integer cols) {
        for (int r = 1; r <= rows; r++) {
            for (int c = 1; c <= cols; c++) {
                Seat seat = new Seat();
                seat.setHallId(hallId);
                seat.setRowNum(r);
                seat.setColNum(c);
                seat.setStatus(1);
                seatMapper.insert(seat);
            }
        }
    }
}
