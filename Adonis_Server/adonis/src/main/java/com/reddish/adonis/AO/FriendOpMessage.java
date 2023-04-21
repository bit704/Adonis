package com.reddish.adonis.AO;

public class FriendOpMessage {

    /**
     * 见MessageCode下编码
     */
    private int code;
    /**
     * 请求方账号，即发出此message的用户账号，简称为s
     */
    private String subjectId;
    /**
     * 被请求方账号，简称为o
     */
    private String objectId;
    /**
     * s对o的自定义备注名
     */
    private String customNickname;
    /**
     * 添加好友时的申请备注
     * 如：你好！我是XXX。
     */
    private String memo;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
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
        return "FriendOpMessage{" +
                "code=" + code +
                ", subjectId='" + subjectId + '\'' +
                ", objectId='" + objectId + '\'' +
                ", customNickname='" + customNickname + '\'' +
                ", memo='" + memo + '\'' +
                '}';
    }

    public FriendOpMessage() {
    }

    public FriendOpMessage(int code, String subjectId, String objectId, String customNickname, String memo) {
        this.code = code;
        this.subjectId = subjectId;
        this.objectId = objectId;
        this.customNickname = customNickname;
        this.memo = memo;
    }
}
