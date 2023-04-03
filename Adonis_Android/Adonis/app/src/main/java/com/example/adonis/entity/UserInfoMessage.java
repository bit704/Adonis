package com.example.adonis.entity;

public class UserInfoMessage {
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "UserInfoMessage{" +
                "type='" + type + '\'' +
                ", id='" + id + '\'' +
                ", nickname='" + nickname + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    public UserInfoMessage() {
    }

    public UserInfoMessage(String type, String id, String nickname, String password) {
        this.type = type;
        this.id = id;
        this.nickname = nickname;
        this.password = password;
    }
}
