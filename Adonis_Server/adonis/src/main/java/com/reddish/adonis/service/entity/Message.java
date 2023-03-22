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
     *
     * 默认只有服务端回复客户端，没有客户端回复服务端
     *
     * messageId = null 的消息视作保活消息，对其回复同样的保活消息
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
