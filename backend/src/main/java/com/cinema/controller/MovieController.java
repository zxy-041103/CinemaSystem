package com.cinema.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cinema.common.Result;
import com.cinema.entity.Movie;
import com.cinema.mapper.MovieMapper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/movie")
public class MovieController {

    @Resource
    private MovieMapper movieMapper;

    // 分页查询（管理端）
    @GetMapping("/page")
    public Result<?> page(@RequestParam(defaultValue = "1") Integer pageNum,
                          @RequestParam(defaultValue = "10") Integer pageSize,
                          @RequestParam(required = false) String name) {
        QueryWrapper<Movie> wrapper = new QueryWrapper<>();
        if (name != null && !name.isEmpty()) {
            wrapper.like("name", name);
        }
        wrapper.orderByDesc("create_time");
        Page<Movie> page = movieMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        return Result.success(page);
    }

    // 查询所有影片
    @GetMapping("/list")
    public Result<?> list() {
        List<Movie> list = movieMapper.selectList(new QueryWrapper<Movie>().orderByDesc("rating"));
        return Result.success(list);
    }

    // 查询正在热映的影片
    @GetMapping("/showing")
    public Result<?> showing() {
        List<Movie> list = movieMapper.selectList(new QueryWrapper<Movie>()
                .eq("status", 1)
                .orderByDesc("rating"));
        return Result.success(list);
    }

    // 查询即将上映的影片
    @GetMapping("/upcoming")
    public Result<?> upcoming() {
        List<Movie> list = movieMapper.selectList(new QueryWrapper<Movie>()
                .eq("status", 2)
                .orderByAsc("release_date"));
        return Result.success(list);
    }

    // 根据ID查询
    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Integer id) {
        return Result.success(movieMapper.selectById(id));
    }

    // 新增影片
    @PostMapping
    public Result<?> add(@RequestBody Movie movie) {
        movieMapper.insert(movie);
        return Result.success();
    }

    // 更新影片
    @PutMapping
    public Result<?> update(@RequestBody Movie movie) {
        movieMapper.updateById(movie);
        return Result.success();
    }

    // 删除影片
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Integer id) {
        movieMapper.deleteById(id);
        return Result.success();
    }
}
