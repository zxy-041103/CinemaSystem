package com.cinema.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("seat")
public class Seat {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer hallId;
    private Integer rowNum;
    private Integer colNum;
    private Integer status;
}
