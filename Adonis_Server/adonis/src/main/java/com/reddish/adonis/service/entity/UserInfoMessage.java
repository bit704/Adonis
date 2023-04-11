package com.reddish.adonis.service.entity;

public class UserInfoMessage {
    /**
     * 见MessageCode下编码
     */
    int code;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "UserInfoMessage{" +
                "code=" + code +
                '}';
    }

    public UserInfoMessage() {
    }

    public UserInfoMessage(int code) {
        this.code = code;
    }
}
