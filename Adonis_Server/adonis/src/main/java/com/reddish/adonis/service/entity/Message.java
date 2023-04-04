package com.reddish.adonis.service.entity;

import java.util.List;

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
     * DialogueInfoMessage 对话相关消息(用List存)
     */
    private String type;
    private ReplyMessage replyMessage;
    private UserInfoMessage userInfoMessage;
    private FriendInfoMessage friendInfoMessage;
    private List<DialogueInfoMessage> dialogueInfoMessageList;

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

    public UserInfoMessage getUserInfoMessage() {
        return userInfoMessage;
    }

    public void setUserInfoMessage(UserInfoMessage userInfoMessage) {
        this.userInfoMessage = userInfoMessage;
    }

    public FriendInfoMessage getFriendInfoMessage() {
        return friendInfoMessage;
    }

    public void setFriendInfoMessage(FriendInfoMessage friendInfoMessage) {
        this.friendInfoMessage = friendInfoMessage;
    }

    public List<DialogueInfoMessage> getDialogueInfoMessageList() {
        return dialogueInfoMessageList;
    }

    public void setDialogueInfoMessageList(List<DialogueInfoMessage> dialogueInfoMessageList) {
        this.dialogueInfoMessageList = dialogueInfoMessageList;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", replyMessage=" + replyMessage +
                ", userInfoMessage=" + userInfoMessage +
                ", friendInfoMessage=" + friendInfoMessage +
                ", dialogueInfoMessageList=" + dialogueInfoMessageList +
                '}';
    }

    public Message() {
    }

    public Message(String id, String type, ReplyMessage replyMessage, UserInfoMessage userInfoMessage, FriendInfoMessage friendInfoMessage, List<DialogueInfoMessage> dialogueInfoMessageList) {
        this.id = id;
        this.type = type;
        this.replyMessage = replyMessage;
        this.userInfoMessage = userInfoMessage;
        this.friendInfoMessage = friendInfoMessage;
        this.dialogueInfoMessageList = dialogueInfoMessageList;
    }
}
