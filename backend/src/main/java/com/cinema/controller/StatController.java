package com.cinema.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cinema.common.Result;
import com.cinema.entity.*;
import com.cinema.mapper.*;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;

@RestController
@RequestMapping("/api/stat")
public class StatController {

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private MovieMapper movieMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private ScreeningMapper screeningMapper;

    // 总览数据
    @GetMapping("/overview")
    public Result<?> overview() {
        Map<String, Object> data = new HashMap<>();

        // 总用户数
        long userCount = userMapper.selectCount(new QueryWrapper<User>().eq("role", "USER"));
        data.put("userCount", userCount);

        // 总影片数
        long movieCount = movieMapper.selectCount(null);
        data.put("movieCount", movieCount);

        // 正在上映的影片数
        long showingCount = movieMapper.selectCount(new QueryWrapper<Movie>().eq("status", 1));
        data.put("showingCount", showingCount);

        // 总订单数
        long orderCount = orderMapper.selectCount(null);
        data.put("orderCount", orderCount);

        // 已支付订单数
        long paidCount = orderMapper.selectCount(new QueryWrapper<TicketOrder>().in("status", "PAID", "USED"));
        data.put("paidCount", paidCount);

        // 总收入
        List<TicketOrder> paidOrders = orderMapper.selectList(new QueryWrapper<TicketOrder>().in("status", "PAID", "USED"));
        double totalRevenue = paidOrders.stream()
                .mapToDouble(o -> o.getTotalPrice().doubleValue())
                .sum();
        data.put("totalRevenue", totalRevenue);

        // 今日场次数
        long todayScreening = screeningMapper.selectCount(new QueryWrapper<Screening>()
                .apply("DATE(start_time) = CURDATE()"));
        data.put("todayScreening", todayScreening);

        return Result.success(data);
    }

    // 近7天票房趋势
    @GetMapping("/revenue/daily")
    public Result<?> dailyRevenue() {
        List<Map<String, Object>> list = orderMapper.selectRevenueByDay();
        return Result.success(list);
    }

    // 影片票房排行
    @GetMapping("/revenue/movie")
    public Result<?> movieRevenue() {
        List<Map<String, Object>> list = orderMapper.selectRevenueByMovie();
        return Result.success(list);
    }

    // 影片类型分布
    @GetMapping("/genre")
    public Result<?> genreDistribution() {
        List<Movie> movies = movieMapper.selectList(null);
        Map<String, Integer> genreMap = new HashMap<>();
        for (Movie movie : movies) {
            if (movie.getGenre() != null) {
                String[] genres = movie.getGenre().split("/");
                for (String genre : genres) {
                    genreMap.merge(genre.trim(), 1, Integer::sum);
                }
            }
        }
        List<Map<String, Object>> result = new ArrayList<>();
        genreMap.forEach((k, v) -> {
            Map<String, Object> item = new HashMap<>();
            item.put("name", k);
            item.put("value", v);
            result.add(item);
        });
        return Result.success(result);
    }

    // 订单状态分布
    @GetMapping("/order/status")
    public Result<?> orderStatus() {
        List<TicketOrder> orders = orderMapper.selectList(null);
        Map<String, Integer> statusMap = new HashMap<>();
        for (TicketOrder order : orders) {
            statusMap.merge(order.getStatus(), 1, Integer::sum);
        }
        List<Map<String, Object>> result = new ArrayList<>();
        Map<String, String> statusNames = new HashMap<>();
        statusNames.put("UNPAID", "未支付");
        statusNames.put("PAID", "已支付");
        statusNames.put("CANCELLED", "已取消");
        statusNames.put("USED", "已使用");
        statusNames.put("REFUNDED", "已退票");
        statusMap.forEach((k, v) -> {
            Map<String, Object> item = new HashMap<>();
            item.put("name", statusNames.getOrDefault(k, k));
            item.put("value", v);
            result.add(item);
        });
        return Result.success(result);
    }
}
