package com.reddish.adonis.DO;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.util.Objects;

@Getter
@Setter
@ToString
@TableName("user")
public class User {
    @TableField("id")
    private String id;
    @TableField("nickname")
    private String nickname;
    @TableField("password")
    private String password;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User user = (User) o;
        return id.equals(user.id) && nickname.equals(user.nickname) && password.equals(user.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nickname, password);
    }

    public User() {
    }

    public User(String id, String nickname, String password) {
        this.id = id;
        this.nickname = nickname;
        this.password = password;
    }
}
