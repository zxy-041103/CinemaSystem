package com.cinema.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cinema.common.Result;
import com.cinema.entity.User;
import com.cinema.mapper.UserMapper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class LoginController {

    @Resource
    private UserMapper userMapper;

    @PostMapping("/login")
    public Result<?> login(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");
        if (username == null || password == null) {
            return Result.error("用户名和密码不能为空");
        }
        User user = userMapper.selectOne(new QueryWrapper<User>()
                .eq("username", username)
                .eq("password", password));
        if (user == null) {
            return Result.error("用户名或密码错误");
        }
        if (user.getStatus() == 0) {
            return Result.error("该账号已被禁用");
        }
        Map<String, Object> result = new HashMap<>();
        result.put("user", user);
        result.put("role", user.getRole());
        return Result.success(result);
    }

    @PostMapping("/register")
    public Result<?> register(@RequestBody User user) {
        User exist = userMapper.selectOne(new QueryWrapper<User>().eq("username", user.getUsername()));
        if (exist != null) {
            return Result.error("用户名已存在");
        }
        user.setRole("USER");
        user.setPoints(0);
        user.setStatus(1);
        userMapper.insert(user);
        return Result.success();
    }

    @PutMapping("/updatePassword")
    public Result<?> updatePassword(@RequestBody Map<String, String> params) {
        Integer userId = Integer.parseInt(params.get("userId"));
        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");
        User user = userMapper.selectById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        if (!user.getPassword().equals(oldPassword)) {
            return Result.error("原密码错误");
        }
        user.setPassword(newPassword);
        userMapper.updateById(user);
        return Result.success();
    }
}
