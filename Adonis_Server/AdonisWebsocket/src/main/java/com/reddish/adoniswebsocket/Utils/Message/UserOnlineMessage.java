package com.reddish.adoniswebsocket.Utils.Message;

import java.util.List;

public class UserOnlineMessage {
    private List<FriendInfoMessage> friendInfoMessageList;
    /**
     * 服务端按从旧到新排序后发
     */
    private List<DialogueMessage> dialogueMessageList;

    public List<FriendInfoMessage> getFriendInfoMessageList() {
        return friendInfoMessageList;
    }

    public void setFriendInfoMessageList(List<FriendInfoMessage> friendInfoMessageList) {
        this.friendInfoMessageList = friendInfoMessageList;
    }

    public List<DialogueMessage> getDialogueInfoMessageList() {
        return dialogueMessageList;
    }

    public void setDialogueInfoMessageList(List<DialogueMessage> dialogueMessageList) {
        this.dialogueMessageList = dialogueMessageList;
    }

    @Override
    public String toString() {
        return "UserOnlineMessage{" +
                "friendInfoMessageList=" + friendInfoMessageList +
                ", dialogueMessageList=" + dialogueMessageList +
                '}';
    }

    public UserOnlineMessage() {
    }

    public UserOnlineMessage(List<FriendInfoMessage> friendInfoMessageList, List<DialogueMessage> dialogueMessageList) {
        this.friendInfoMessageList = friendInfoMessageList;
        this.dialogueMessageList = dialogueMessageList;
    }
}
