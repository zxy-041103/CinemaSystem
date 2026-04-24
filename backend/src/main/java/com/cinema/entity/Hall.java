package com.cinema.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("hall")
public class Hall {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    private String type;
    private Integer seatRows;
    private Integer seatCols;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
