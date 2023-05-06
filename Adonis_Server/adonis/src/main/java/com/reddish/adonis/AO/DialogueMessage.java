package com.reddish.adonis.AO;

public class DialogueMessage {
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
     * 消息发送时间
     * 自 1970 年 1 月 1 日 00:00:00 GMT 到 消息发送时刻 经过了多少毫秒
     * 由服务端填写，以消息到达服务端的时间为准
     */
    private long occurredTime;
    /**
     * 消息存活时间
     * 指示客户端将此消息展示给客户后应在多久后自动删除
     * 单位是毫秒
     * 由用户设置，由客户端填写
     * 若为0则代表消息不过期
     */
    private long lastedTime;

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

    public long getOccurredTime() {
        return occurredTime;
    }

    public void setOccurredTime(long occurredTime) {
        this.occurredTime = occurredTime;
    }

    public long getLastedTime() {
        return lastedTime;
    }

    public void setLastedTime(long lastedTime) {
        this.lastedTime = lastedTime;
    }

    @Override
    public String toString() {
        return "DialogueMessage{" +
                "senderId='" + senderId + '\'' +
                ", receiverId='" + receiverId + '\'' +
                ", content='" + content + '\'' +
                ", occurredTime=" + occurredTime +
                ", lastedTime=" + lastedTime +
                '}';
    }

    public DialogueMessage() {
    }

    public DialogueMessage(String senderId, String receiverId, String content, long occurredTime, long lastedTime) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.occurredTime = occurredTime;
        this.lastedTime = lastedTime;
    }

}
