package com.cinema.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("movie")
public class Movie {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    private String poster;
    private String director;
    private String actors;
    private String genre;
    private Integer duration;
    private LocalDate releaseDate;
    private LocalDate endDate;
    private String description;
    private BigDecimal rating;
    private String country;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
