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

//    | 代码 | 含义                                  |
//            | ---- | ------------------------------------- |
//            | 0    | 成功执行                              |
//            | 100  | 不合法的消息                          |
//            | 101  | 不存在的消息类型                      |
//            | 102  | 消息类型与消息内容不符                |
//            | 200  | 不存在的用户消息类型                  |
//            | 201  | 用户ID已存在，无法注册                |
//            | 202  | 用户ID不存在，无法线上操作            |
//            | 203  | 密码不正确                            |
//            | 204  | 消息内用户ID与session拥有用户ID不一致 |
//            | 205  | 消息中未传入新昵称，无法更新          |
//            | 206  | 消息中未传入新密码，无法更新          |
//            | 207  | 注册时未传入完整信息                  |
//            | 208  | 用户不在线                            |
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
