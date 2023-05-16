package com.reddish.adonis.Manager;

import com.reddish.adonis.AO.*;
import com.reddish.adonis.DO.User;
import com.reddish.adonis.Websocket.Dispatcher;
import org.springframework.stereotype.Component;

import javax.websocket.Session;

@Component
public class SendManager {

    final DAOManager daoManager;

    public SendManager(DAOManager daoManager) {
        this.daoManager = daoManager;
    }


    public void sendUserInfoForUserOp(MessageCode messageCode, Session session) {
        Dispatcher.sendMessage(session, new Message(new UserInfoMessage(messageCode.getId())));
    }

    public void sendFriendInfoForOp(FriendOpMessage friendOpMessage, MessageCode messageCode, String infoId, String send2Id) {
        FriendInfoMessage friendInfoMessage = new FriendInfoMessage();

        // 消息代码
        friendInfoMessage.setCode(messageCode.getId());

        // 此消息要说明的用户
        friendInfoMessage.setId(infoId);

        // friendOpMessage指定要操作的用户
        User user = daoManager.selectUserById(friendOpMessage.getObjectId());
        if (user != null) {
            // 好友昵称
            friendInfoMessage.setNickname(user.getNickname());
        }

        // 对好友的备注昵称
        friendInfoMessage.setCustomNickname(friendOpMessage.getCustomNickname());

        // 备注
        friendInfoMessage.setMemo(friendOpMessage.getMemo());

        Message message = new Message(friendInfoMessage);
        Session session = Dispatcher.userId2SessionMap.get(send2Id);
        Dispatcher.sendMessage(session, message);
    }


    public void sendDialogue(String senderId, String receiverId, String content, long occurredTime, long lastedTime) {
        // 对方刚好在线,直接转发
        if (Dispatcher.isOnline(receiverId)) {
            DialogueMessage dialogueMessage = new DialogueMessage(senderId, receiverId, content, occurredTime, lastedTime);
            Message message = new Message(dialogueMessage);
            Session session = Dispatcher.userId2SessionMap.get(receiverId);
            Dispatcher.sendMessage(session, message);
        }
        // 否则先存在数据库里
        else {
            daoManager.saveDialogue(senderId, receiverId, content, occurredTime, lastedTime);
        }
    }
}