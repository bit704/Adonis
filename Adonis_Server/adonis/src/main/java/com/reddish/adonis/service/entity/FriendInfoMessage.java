package com.reddish.adonis.service.entity;

public class FriendInfoMessage {
    /**
     * 好友id
     */
    private String id;
    /**
     * 好友nickname
     */
    private String nickname;

    /**
     * 好友状态
     * -2: 此好友是您的单向好友（您没删它，它删了您）
     * -1: 此好友已注销
     * 0: 此好友正申请将您加入好友列表
     * 1: 此好友已将您加入好友列表
     * 2: 此好友已将您拉黑
     * 3: 此好友拒绝您的好友申请
     * 4: 此用户存在
     * 5: 此用户不存在
     * 6: 此用户在线
     * 7： 此用户不在线
     */
    private int status;
    /**
     * 您对该好友的自定义备注名
     */
    private String customNickname;
    /**
     * 如果好友状态是0、2、3
     * 备注内容
     */
    private String memo;

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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
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
                "id='" + id + '\'' +
                ", nickname='" + nickname + '\'' +
                ", customname='" + customNickname + '\'' +
                ", status=" + status +
                ", memo='" + memo + '\'' +
                '}';
    }
}
