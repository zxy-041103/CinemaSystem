package com.cinema.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cinema.entity.Collection;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CollectionMapper extends BaseMapper<Collection> {

    @Select("SELECT c.*, m.name as movie_name, m.poster as movie_poster, m.genre, m.rating " +
            "FROM collection c " +
            "LEFT JOIN movie m ON c.movie_id = m.id " +
            "WHERE c.user_id = #{userId} " +
            "ORDER BY c.create_time DESC")
    List<Collection> selectByUserId(@Param("userId") Integer userId);
}
