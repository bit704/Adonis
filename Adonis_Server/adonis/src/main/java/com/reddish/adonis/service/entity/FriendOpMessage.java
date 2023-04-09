package com.reddish.adonis.service.entity;


public class FriendOpMessage {
    /**
     * add: 添加好友申请（可以选择是否添加申请备注，memo字段）
     * consent: 同意好友申请
     * refuse: 拒绝好友申请
     * delete: 删除好友（只能单向删除，自己还在对方好友列表中，但对方发给自己的消息会被服务器拦截）
     * exist: 查询好友是否存在
     * online: 查询好友是否在线
     * block: 拉黑好友
     * custom: 自定义对好友的备注名（customNickname字段）
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
     * s对o的自定义备注名
     */
    private String customname;
    /**
     * 添加好友时的申请备注
     * 如：你好！我是XXX，想添加你为好友
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

    public String getCustomname() {
        return customname;
    }

    public void setCustomname(String customname) {
        this.customname = customname;
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
                "type='" + type + '\'' +
                ", subjectId='" + subjectId + '\'' +
                ", objectId='" + objectId + '\'' +
                ", customname='" + customname + '\'' +
                ", memo='" + memo + '\'' +
                '}';
    }

    public FriendOpMessage() {
    }

    public FriendOpMessage(String type, String subjectId, String objectId, String memo) {
        this.type = type;
        this.subjectId = subjectId;
        this.objectId = objectId;
        this.memo = memo;
    }
}
