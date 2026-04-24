package com.cinema.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cinema.entity.TicketOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper extends BaseMapper<TicketOrder> {

    @Select("SELECT o.*, u.nickname as user_name, m.name as movie_name, m.poster as movie_poster, " +
            "h.name as hall_name, s.start_time, s.end_time " +
            "FROM ticket_order o " +
            "LEFT JOIN user u ON o.user_id = u.id " +
            "LEFT JOIN screening s ON o.screening_id = s.id " +
            "LEFT JOIN movie m ON s.movie_id = m.id " +
            "LEFT JOIN hall h ON s.hall_id = h.id " +
            "ORDER BY o.create_time DESC")
    List<TicketOrder> selectAllWithInfo();

    @Select("SELECT o.*, u.nickname as user_name, m.name as movie_name, m.poster as movie_poster, " +
            "h.name as hall_name, s.start_time, s.end_time " +
            "FROM ticket_order o " +
            "LEFT JOIN user u ON o.user_id = u.id " +
            "LEFT JOIN screening s ON o.screening_id = s.id " +
            "LEFT JOIN movie m ON s.movie_id = m.id " +
            "LEFT JOIN hall h ON s.hall_id = h.id " +
            "WHERE o.user_id = #{userId} " +
            "ORDER BY o.create_time DESC")
    List<TicketOrder> selectByUserId(@Param("userId") Integer userId);

    @Select("SELECT DATE(o.create_time) as date, SUM(o.total_price) as revenue, COUNT(*) as count " +
            "FROM ticket_order o WHERE o.status IN ('PAID','USED') " +
            "AND o.create_time >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) " +
            "GROUP BY DATE(o.create_time) ORDER BY date")
    List<Map<String, Object>> selectRevenueByDay();

    @Select("SELECT m.name as movie_name, SUM(o.total_price) as revenue, COUNT(*) as count " +
            "FROM ticket_order o " +
            "LEFT JOIN screening s ON o.screening_id = s.id " +
            "LEFT JOIN movie m ON s.movie_id = m.id " +
            "WHERE o.status IN ('PAID','USED') " +
            "GROUP BY m.name ORDER BY revenue DESC LIMIT 10")
    List<Map<String, Object>> selectRevenueByMovie();

    @Select("SELECT o.*, u.nickname as user_name, m.name as movie_name, m.poster as movie_poster, " +
            "h.name as hall_name, s.start_time, s.end_time " +
            "FROM ticket_order o " +
            "LEFT JOIN user u ON o.user_id = u.id " +
            "LEFT JOIN screening s ON o.screening_id = s.id " +
            "LEFT JOIN movie m ON s.movie_id = m.id " +
            "LEFT JOIN hall h ON s.hall_id = h.id " +
            "WHERE o.id = #{id}")
    TicketOrder selectByIdWithInfo(@Param("id") Integer id);
}
