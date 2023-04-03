package com.example.adonis.entity;


public class FriendInfoMessage {
    /**
     * add: 添加好友申请
     * consent: 同意好友申请
     * refuse: 拒绝好友申请
     * delete: 删除好友
     */
    private String type;
    /**
     * 请求方账号
     */
    private String subjectId;
    /**
     * 被请求方账号
     */
    private String objectId;
    /**
     * 备注内容
     */
    private String memo;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    @Override
    public String toString() {
        return "FriendInfoMessage{" +
                "type='" + type + '\'' +
                ", subjectId='" + subjectId + '\'' +
                ", objectId='" + objectId + '\'' +
                ", memo='" + memo + '\'' +
                '}';
    }

    public FriendInfoMessage() {
    }

    public FriendInfoMessage(String type, String subjectId, String objectId, String memo) {
        this.type = type;
        this.subjectId = subjectId;
        this.objectId = objectId;
        this.memo = memo;
    }
}
