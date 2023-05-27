package com.reddish.adoniswebsocket.Service;

import com.reddish.adoniswebsocket.Utils.Exception.DialogueInfoException;
import com.reddish.adoniswebsocket.Utils.Exception.ExceptionCode;
import com.reddish.adoniswebsocket.Utils.Exception.MessageException;
import com.reddish.adoniswebsocket.FeignClient.DAOFeignClient;
import com.reddish.adoniswebsocket.Manager.SendManager;
import com.reddish.adoniswebsocket.Utils.Message.DialogueMessage;
import org.springframework.stereotype.Service;

@Service
public class DialogueService {

    private final DAOFeignClient daoFeignClient;

    private final SendManager sendManager;

    public DialogueService(DAOFeignClient daoFeignClient, SendManager sendManager) {
        this.daoFeignClient = daoFeignClient;
        this.sendManager = sendManager;
    }


    public void handle(DialogueMessage dialogueMessage) throws MessageException, DialogueInfoException {

        String senderId = dialogueMessage.getSenderId();
        String receiverId = dialogueMessage.getReceiverId();

        // 聊天双方是否都存在
        if (daoFeignClient.selectUserById(senderId) == null ||
                daoFeignClient.selectUserById(receiverId) == null) {
            throw new DialogueInfoException(ExceptionCode.SHADOW_MAN);
        }

        // 可以自己给自己发消息，user1和user2可以相同
        // 判断还是不是双向好友
        if (daoFeignClient.judgeFriendshipOneWay(senderId, receiverId) ||
                daoFeignClient.judgeFriendshipOneWay(receiverId, senderId)) {
            throw new DialogueInfoException(ExceptionCode.STRANGER);
        }
        sendManager.sendDialogue(senderId, receiverId,
                // cccurredTime是消息到达服务端的时间，由服务端填写
                dialogueMessage.getContent(), System.currentTimeMillis(), dialogueMessage.getLastedTime());
    }
}
