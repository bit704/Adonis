package com.reddish.adonisbase.Service;

import com.reddish.adonisbase.Message.DialogueMessage;
import com.reddish.adonisbase.Manager.DAOManager;
import com.reddish.adonisbase.Manager.Exception.DialogueInfoException;
import com.reddish.adonisbase.Manager.Exception.ExceptionCode;
import com.reddish.adonisbase.Manager.Exception.MessageException;
import com.reddish.adonisbase.Manager.SendManager;
import org.springframework.stereotype.Service;

import javax.websocket.Session;

@Service
public class DialogueService {

    private final DAOManager DAOManager;

    private final SendManager sendManager;

    public DialogueService(DAOManager DAOManager, SendManager sendManager) {
        this.DAOManager = DAOManager;
        this.sendManager = sendManager;
    }


    public void handle(DialogueMessage dialogueMessage, Session session) throws MessageException, DialogueInfoException {

        String senderId = dialogueMessage.getSenderId();
        String receiverId = dialogueMessage.getReceiverId();

        // 聊天双方是否都存在
        if (DAOManager.selectUserById(senderId) == null ||
                DAOManager.selectUserById(receiverId) == null) {
            throw new DialogueInfoException(ExceptionCode.SHADOW_MAN);
        }

        // 可以自己给自己发消息，user1和user2可以相同
        // 判断还是不是双向好友
        if (DAOManager.judgeFriendshipOneWay(senderId, receiverId) ||
                DAOManager.judgeFriendshipOneWay(receiverId, senderId)) {
            throw new DialogueInfoException(ExceptionCode.STRANGER);
        }
        sendManager.sendDialogue(senderId, receiverId,
                // cccurredTime是消息到达服务端的时间，由服务端填写
                dialogueMessage.getContent(), System.currentTimeMillis(), dialogueMessage.getLastedTime());
    }
}
