package com.reddish.adoniswebsocket.Utils.Message;

public class ReplyMessage {

    /**
     * 此回复消息对应的消息的messageId
     */
    private String messageToReplyId;

    /**
     * 回复代码
     * 回复已发送消息的执行情况
     */
    private int replyCode;

    /**
     * 消息发送时间
     * 自1970年1月1日 00:00:00 GMT到消息发送时刻经过的毫秒数
     * 由服务端填写，以回复消息发出服务端的时间为准
     */
    private long occurredTime;

    public String getMessageToReplyId() {
        return messageToReplyId;
    }

    public void setMessageToReplyId(String messageToReplyId) {
        this.messageToReplyId = messageToReplyId;
    }

    public int getReplyCode() {
        return replyCode;
    }

    public void setReplyCode(int replyCode) {
        this.replyCode = replyCode;
    }

    public long getOccurredTime() {
        return occurredTime;
    }

    public void setOccurredTime(long occurredTime) {
        this.occurredTime = occurredTime;
    }

    @Override
    public String toString() {
        return "ReplyMessage{" +
                "messageToReplyId='" + messageToReplyId + '\'' +
                ", replyCode=" + replyCode +
                '}';
    }

    public ReplyMessage() {
    }

    public ReplyMessage(String messageToReplyId, int replyCode) {
        this.messageToReplyId = messageToReplyId;
        this.replyCode = replyCode;
        this.occurredTime = System.currentTimeMillis();
    }
}
