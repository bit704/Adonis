package com.reddish.adonis.mapper.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@TableName("user")
public class User {
    @TableField("id")
    private String id;
    @TableField("nickname")
    private String nickname;
    @TableField("password")
    private String password;
}
