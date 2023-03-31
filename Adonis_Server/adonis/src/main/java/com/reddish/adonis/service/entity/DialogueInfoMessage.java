package com.reddish.adonis.service.entity;

public class DialogueInfoMessage {
    /**
     * 发送方账号
     */
    private String senderId;
    /**
     * 接收方账号
     */
    private String receiverId;
    /**
     * 消息内容
     */
    private String content;
    /**
     * 消息存在时间
     */
    private double lastedTime;

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public double getLastedTime() {
        return lastedTime;
    }

    public void setLastedTime(double lastedTime) {
        this.lastedTime = lastedTime;
    }

    @Override
    public String toString() {
        return "DialogueInfoMessage{" +
                "senderId='" + senderId + '\'' +
                ", receiverId='" + receiverId + '\'' +
                ", content='" + content + '\'' +
                ", lastedTime=" + lastedTime +
                '}';
    }

    public DialogueInfoMessage() {
    }

    public DialogueInfoMessage(String senderId, String receiverId, String content, double lastedTime) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.lastedTime = lastedTime;
    }
}
