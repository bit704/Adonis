package com.reddish.adonis.service.entity;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    /**
     * 表示此消息是不是对已发送消息的回复
     * 如果是，messageId表示此消息对应的已发送消息的id,除了isReply、replyCode、id的其它字段都应为null
     */
    private boolean isReply;
    /**
     * 回复代码
     * 回复已发送消息的执行情况
     */
    private int replyCode;

    /**
     * 唯一id
     * 使用UUID实现
     */
    private String messageId;

    /**
     * 消息类型
     * userMessage 用户操作相关消息
     * friendInfoMessage 好友操作相关消息
     * dialogueMessage 对话相关消息
     */
    private String type;

    private UserInfoMessage userInfoMessage;
    private FriendInfoMessage friendInfoMessage;
    private DialogueInfoMessage dialogueInfoMessage;
}
