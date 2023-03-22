package com.reddish.adonis.mapper.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@TableName("friend")
public class Friend {
    @TableField("subject")
    private String subject;

    @TableField("object")
    private String object;

}
