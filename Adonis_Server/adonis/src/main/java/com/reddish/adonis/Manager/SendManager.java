package com.reddish.adonis.Manager;

import com.reddish.adonis.AO.DialogueMessage;
import com.reddish.adonis.AO.Message;
import com.reddish.adonis.Websocket.Dispatcher;
import org.springframework.stereotype.Component;

import javax.websocket.Session;

@Component
public class SendManager {

    final DAOManager daoManager;

    public SendManager(DAOManager daoManager) {
        this.daoManager = daoManager;
    }


    public void sendDialogue(String senderId, String receiverId, String content, long occurredTime, long lastedTime) {
        // 对方刚好在线,直接转发
        if (Dispatcher.isOnline(receiverId)) {
            DialogueMessage dialogueMessage = new DialogueMessage(senderId, receiverId, content, occurredTime, lastedTime);
            sendDialogueInfoMessage(dialogueMessage, receiverId);
        }
        // 否则先存在数据库里
        else {
            daoManager.saveDialogue(senderId, receiverId, content, occurredTime, lastedTime);
        }
    }

    private void sendDialogueInfoMessage(DialogueMessage dialogueMessage, String receiverId) {
        Message message = new Message(dialogueMessage);
        Session session = Dispatcher.userId2SessionMap.get(receiverId);
        Dispatcher.sendMessage(session, message);
    }
}
