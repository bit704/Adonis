package com.reddish.adonis.service.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserMessage {
    /**
     * sign_in: 登录
     * sign_out: 登出
     * sign_up: 注册
     * delete: 注销
     * change_nickname: 修改昵称
     * change_password: 修改密码
     */
    private String type;
    /**
     * 账号
     * 长度20以内
     */
    private String id;
    /**
     * 昵称
     * 长度20以内
     */
    private String nickname;
    /**
     * 密码
     * 长度20以内
     */
    private String password;
}
