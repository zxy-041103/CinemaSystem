package com.cinema.controller;

import com.cinema.common.Result;
import com.cinema.entity.Screening;
import com.cinema.mapper.ScreeningMapper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/screening")
public class ScreeningController {

    @Resource
    private ScreeningMapper screeningMapper;

    // 查询所有场次（含影片名和厅名）
    @GetMapping("/list")
    public Result<?> list() {
        List<Screening> list = screeningMapper.selectAllWithInfo();
        return Result.success(list);
    }

    // 根据影片查询可用场次
    @GetMapping("/movie/{movieId}")
    public Result<?> getByMovieId(@PathVariable Integer movieId) {
        List<Screening> list = screeningMapper.selectByMovieId(movieId);
        return Result.success(list);
    }

    // 根据ID查询场次详情
    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Integer id) {
        Screening screening = screeningMapper.selectByIdWithInfo(id);
        return Result.success(screening);
    }

    // 新增场次
    @PostMapping
    public Result<?> add(@RequestBody Screening screening) {
        screeningMapper.insert(screening);
        return Result.success();
    }

    // 更新场次
    @PutMapping
    public Result<?> update(@RequestBody Screening screening) {
        screeningMapper.updateById(screening);
        return Result.success();
    }

    // 删除场次
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Integer id) {
        screeningMapper.deleteById(id);
        return Result.success();
    }
}
