package com.reddish.adonis.service.entity;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    /**
     * 消息唯一id
     * 使用UUID实现
     * 来自客户端的 id = null 的消息视作保活消息，对其发送同样的保活消息
     */
    private String id;
    /**
     * 消息类型
     * ReplyMessage 回复消息
     * UserInfoMessage 用户操作相关消息
     * FriendInfoMessage 好友操作相关消息
     * DialogueInfoMessage 对话相关消息
     */
    private String type;
    private ReplyMessage replyMessage;
    private UserInfoMessage userInfoMessage;
    private FriendInfoMessage friendInfoMessage;
    private DialogueInfoMessage dialogueInfoMessage;
}
