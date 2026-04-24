package com.cinema.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cinema.common.Result;
import com.cinema.entity.User;
import com.cinema.mapper.UserMapper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Resource
    private UserMapper userMapper;

    // 分页查询用户（管理端）
    @GetMapping("/page")
    public Result<?> page(@RequestParam(defaultValue = "1") Integer pageNum,
                          @RequestParam(defaultValue = "10") Integer pageSize,
                          @RequestParam(required = false) String username) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        if (username != null && !username.isEmpty()) {
            wrapper.like("username", username);
        }
        wrapper.orderByDesc("create_time");
        Page<User> page = userMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        return Result.success(page);
    }

    // 根据ID查询
    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Integer id) {
        return Result.success(userMapper.selectById(id));
    }

    // 新增用户
    @PostMapping
    public Result<?> add(@RequestBody User user) {
        User exist = userMapper.selectOne(new QueryWrapper<User>().eq("username", user.getUsername()));
        if (exist != null) {
            return Result.error("用户名已存在");
        }
        if (user.getRole() == null) user.setRole("USER");
        if (user.getPoints() == null) user.setPoints(0);
        if (user.getStatus() == null) user.setStatus(1);
        userMapper.insert(user);
        return Result.success();
    }

    // 更新用户
    @PutMapping
    public Result<?> update(@RequestBody User user) {
        userMapper.updateById(user);
        return Result.success();
    }

    // 删除用户
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Integer id) {
        userMapper.deleteById(id);
        return Result.success();
    }
}
