package com.reddish.adonis.service.entity;

import java.util.List;

public class UserOnlineMessage {
    private List<FriendInfoMessage> friendInfoMessageList;
    private List<DialogueInfoMessage> dialogueInfoMessageList;

    public List<FriendInfoMessage> getFriendInfoMessageList() {
        return friendInfoMessageList;
    }

    public void setFriendInfoMessageList(List<FriendInfoMessage> friendInfoMessageList) {
        this.friendInfoMessageList = friendInfoMessageList;
    }

    public List<DialogueInfoMessage> getDialogueInfoMessageList() {
        return dialogueInfoMessageList;
    }

    public void setDialogueInfoMessageList(List<DialogueInfoMessage> dialogueInfoMessageList) {
        this.dialogueInfoMessageList = dialogueInfoMessageList;
    }

    @Override
    public String toString() {
        return "UserOnlineMessage{" +
                "friendInfoMessageList=" + friendInfoMessageList +
                ", dialogueInfoMessageList=" + dialogueInfoMessageList +
                '}';
    }

    public UserOnlineMessage() {
    }

    public UserOnlineMessage(List<FriendInfoMessage> friendInfoMessageList, List<DialogueInfoMessage> dialogueInfoMessageList) {
        this.friendInfoMessageList = friendInfoMessageList;
        this.dialogueInfoMessageList = dialogueInfoMessageList;
    }
}
