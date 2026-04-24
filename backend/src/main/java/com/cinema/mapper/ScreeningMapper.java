package com.cinema.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cinema.entity.Screening;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ScreeningMapper extends BaseMapper<Screening> {

    @Select("SELECT s.*, m.name as movie_name, m.poster as movie_poster, m.duration, " +
            "h.name as hall_name, h.type as hall_type " +
            "FROM screening s " +
            "LEFT JOIN movie m ON s.movie_id = m.id " +
            "LEFT JOIN hall h ON s.hall_id = h.id " +
            "ORDER BY s.start_time DESC")
    List<Screening> selectAllWithInfo();

    @Select("SELECT s.*, m.name as movie_name, m.poster as movie_poster, m.duration, " +
            "h.name as hall_name, h.type as hall_type " +
            "FROM screening s " +
            "LEFT JOIN movie m ON s.movie_id = m.id " +
            "LEFT JOIN hall h ON s.hall_id = h.id " +
            "WHERE s.movie_id = #{movieId} AND s.status = 1 AND s.start_time > NOW() " +
            "ORDER BY s.start_time ASC")
    List<Screening> selectByMovieId(@Param("movieId") Integer movieId);

    @Select("SELECT s.*, m.name as movie_name, m.poster as movie_poster, m.duration, " +
            "h.name as hall_name, h.type as hall_type " +
            "FROM screening s " +
            "LEFT JOIN movie m ON s.movie_id = m.id " +
            "LEFT JOIN hall h ON s.hall_id = h.id " +
            "WHERE s.id = #{id}")
    Screening selectByIdWithInfo(@Param("id") Integer id);
}
