package com.reddish.adonis.mapper.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import java.sql.Timestamp;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@TableName("dialogue")
public class Dialogue {
    @TableField("senderId")
    private String senderId;

    @TableField("receiverId")
    private String receiverId;

    @TableField("content")
    private String content;

    @TableField("lastedTime")
    private double lastedTime;

    @TableField("occuringTime")
    private Timestamp occuringTime;

}
