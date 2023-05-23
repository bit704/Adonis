package com.reddish.adonisbase.Message;

public class UserOpMessage {
    /**
     * 见MessageCode下编码
     */
    private int code;
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

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
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
        return "UserOpMessage{" +
                "code=" + code +
                ", id='" + id + '\'' +
                ", nickname='" + nickname + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    public UserOpMessage() {
    }

    public UserOpMessage(int code, String id, String nickname, String password) {
        this.code = code;
        this.id = id;
        this.nickname = nickname;
        this.password = password;
    }
}
