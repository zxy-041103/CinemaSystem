package com.cinema.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("ticket_order")
public class TicketOrder {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String orderNo;
    private Integer userId;
    private Integer screeningId;
    private String seats;
    private Integer seatCount;
    private BigDecimal totalPrice;
    private String status;
    private LocalDateTime payTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // 关联字段
    @TableField(exist = false)
    private String userName;
    @TableField(exist = false)
    private String movieName;
    @TableField(exist = false)
    private String moviePoster;
    @TableField(exist = false)
    private String hallName;
    @TableField(exist = false)
    private LocalDateTime startTime;
    @TableField(exist = false)
    private LocalDateTime endTime;
}
