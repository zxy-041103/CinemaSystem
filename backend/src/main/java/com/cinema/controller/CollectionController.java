package com.cinema.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cinema.common.Result;
import com.cinema.entity.Collection;
import com.cinema.mapper.CollectionMapper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/collection")
public class CollectionController {

    @Resource
    private CollectionMapper collectionMapper;

    // 查询用户收藏列表
    @GetMapping("/user/{userId}")
    public Result<?> getByUserId(@PathVariable Integer userId) {
        List<Collection> list = collectionMapper.selectByUserId(userId);
        return Result.success(list);
    }

    // 检查是否收藏
    @GetMapping("/check")
    public Result<?> check(@RequestParam Integer userId, @RequestParam Integer movieId) {
        Collection collection = collectionMapper.selectOne(new QueryWrapper<Collection>()
                .eq("user_id", userId)
                .eq("movie_id", movieId));
        return Result.success(collection != null);
    }

    // 添加收藏
    @PostMapping
    public Result<?> add(@RequestBody Collection collection) {
        Collection exist = collectionMapper.selectOne(new QueryWrapper<Collection>()
                .eq("user_id", collection.getUserId())
                .eq("movie_id", collection.getMovieId()));
        if (exist != null) {
            return Result.error("已收藏过该影片");
        }
        collectionMapper.insert(collection);
        return Result.success();
    }

    // 取消收藏
    @DeleteMapping
    public Result<?> remove(@RequestParam Integer userId, @RequestParam Integer movieId) {
        collectionMapper.delete(new QueryWrapper<Collection>()
                .eq("user_id", userId)
                .eq("movie_id", movieId));
        return Result.success();
    }
}
