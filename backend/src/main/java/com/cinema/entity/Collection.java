package com.cinema.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("collection")
public class Collection {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer userId;
    private Integer movieId;
    private LocalDateTime createTime;

    @TableField(exist = false)
    private String movieName;
    @TableField(exist = false)
    private String moviePoster;
    @TableField(exist = false)
    private String genre;
    @TableField(exist = false)
    private String rating;
}
