package com.reddish.adonis.service.entity;

import java.util.UUID;

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
     * UserOpMessage 用户操作消息
     * FriendOpMessage 好友操作消息
     * FriendInfoMessage 对话信息消息
     * DialogueInfoMessage 对话信息消息
     * UserOnlineMessage 是用户离线期间存在服务器的消息，用户登录在线时发送给用户
     */
    private String type;
    private ReplyMessage replyMessage;
    private UserOpMessage userOpMessage;
    private FriendOpMessage friendOpMessage;
    private FriendInfoMessage friendInfoMessage;
    private DialogueInfoMessage dialogueInfoMessage;
    private UserOnlineMessage userOnlineMessage;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ReplyMessage getReplyMessage() {
        return replyMessage;
    }

    public void setReplyMessage(ReplyMessage replyMessage) {
        this.replyMessage = replyMessage;
    }

    public UserOpMessage getUserOpMessage() {
        return userOpMessage;
    }

    public void setUserOpMessage(UserOpMessage userOpMessage) {
        this.userOpMessage = userOpMessage;
    }

    public FriendOpMessage getFriendOpMessage() {
        return friendOpMessage;
    }

    public void setFriendOpMessage(FriendOpMessage friendOpMessage) {
        this.friendOpMessage = friendOpMessage;
    }

    public FriendInfoMessage getFriendInfoMessage() {
        return friendInfoMessage;
    }

    public void setFriendInfoMessage(FriendInfoMessage friendInfoMessage) {
        this.friendInfoMessage = friendInfoMessage;
    }

    public DialogueInfoMessage getDialogueInfoMessage() {
        return dialogueInfoMessage;
    }

    public void setDialogueInfoMessage(DialogueInfoMessage dialogueInfoMessage) {
        this.dialogueInfoMessage = dialogueInfoMessage;
    }

    public UserOnlineMessage getUserOnlineMessage() {
        return userOnlineMessage;
    }

    public void setUserOnlineMessage(UserOnlineMessage userOnlineMessage) {
        this.userOnlineMessage = userOnlineMessage;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", replyMessage=" + replyMessage +
                ", userOpMessage=" + userOpMessage +
                ", friendOpMessage=" + friendOpMessage +
                ", friendInfoMessage=" + friendInfoMessage +
                ", dialogueInfoMessage=" + dialogueInfoMessage +
                ", userOnlineMessage=" + userOnlineMessage +
                '}';
    }

    public Message() {
    }

    public Message(String id, String type, ReplyMessage replyMessage, UserOpMessage userOpMessage, FriendOpMessage friendOpMessage, DialogueInfoMessage dialogueInfoMessage, UserOnlineMessage userOnlineMessage) {
        this.id = id;
        this.type = type;
        this.replyMessage = replyMessage;
        this.userOpMessage = userOpMessage;
        this.friendOpMessage = friendOpMessage;
        this.dialogueInfoMessage = dialogueInfoMessage;
        this.userOnlineMessage = userOnlineMessage;
    }

    public Message(ReplyMessage replyMessage) {
        this.id = UUID.randomUUID().toString();
        this.type = "replyMessage";
        this.replyMessage = replyMessage;
    }

    public Message(UserOpMessage userOpMessage) {
        this.id = UUID.randomUUID().toString();
        this.type = "userOpMessage";
        this.userOpMessage = userOpMessage;
    }

    public Message(FriendOpMessage friendOpMessage) {
        this.id = UUID.randomUUID().toString();
        this.type = "friendOpMessage";
        this.friendOpMessage = friendOpMessage;
    }

    public Message(DialogueInfoMessage dialogueInfoMessage) {
        this.id = UUID.randomUUID().toString();
        this.type = "dialogueInfoMessage";
        this.dialogueInfoMessage = dialogueInfoMessage;
    }

    public Message(UserOnlineMessage userOnlineMessage) {
        this.id = UUID.randomUUID().toString();
        this.type = "userOnlineMessage";
        this.userOnlineMessage = userOnlineMessage;
    }

    public Message(FriendInfoMessage friendInfoMessage) {
        this.id = UUID.randomUUID().toString();
        this.type = "friendInfoMessage";
        this.friendInfoMessage = friendInfoMessage;
    }
}
