package com.example.adonis.entity;

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
    }
}
