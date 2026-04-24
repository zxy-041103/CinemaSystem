package com.cinema.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("screening")
public class Screening {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer movieId;
    private Integer hallId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BigDecimal price;
    private BigDecimal discountPrice;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // 关联字段（非数据库字段）
    @TableField(exist = false)
    private String movieName;
    @TableField(exist = false)
    private String moviePoster;
    @TableField(exist = false)
    private String hallName;
    @TableField(exist = false)
    private String hallType;
    @TableField(exist = false)
    private Integer duration;
}
