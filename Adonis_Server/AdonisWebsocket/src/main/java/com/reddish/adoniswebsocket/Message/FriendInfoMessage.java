package com.reddish.adoniswebsocket.Message;

public class FriendInfoMessage {
    /**
     * 见MessageCode下编码
     */
    private int code;

    /**
     * 涉及此消息的用户id
     */
    private String id;
    /**
     * 涉及此消息的用户nickname
     */
    private String nickname;

    /**
     * 您对该用户的自定义备注名
     */
    private String customNickname;
    /**
     * 涉及此消息的备注内容
     */
    private String memo;

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

    public String getCustomNickname() {
        return customNickname;
    }

    public void setCustomNickname(String customNickname) {
        this.customNickname = customNickname;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    @Override
    public String toString() {
        return "FriendInfoMessage{" +
                "code=" + code +
                ", id='" + id + '\'' +
                ", nickname='" + nickname + '\'' +
                ", customNickname='" + customNickname + '\'' +
                ", memo='" + memo + '\'' +
                '}';
    }

    public FriendInfoMessage() {
    }

    public FriendInfoMessage(int code, String id, String nickname, String customNickname, String memo) {
        this.code = code;
        this.id = id;
        this.nickname = nickname;
        this.customNickname = customNickname;
        this.memo = memo;
    }
}
