package com.reddish.adonis.DO;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

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

    @TableField("occurredTime")
    private long occurredTime;

    @TableField("lastedTime")
    private long lastedTime;
}