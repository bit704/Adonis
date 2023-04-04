package com.reddish.adonis.service;

import com.reddish.adonis.exception.DialogueInfoException;
import com.reddish.adonis.exception.ExceptionCode;
import com.reddish.adonis.exception.MessageException;
import com.reddish.adonis.mapper.DialogueMapper;
import com.reddish.adonis.mapper.UserMapper;
import com.reddish.adonis.mapper.entity.Dialogue;
import com.reddish.adonis.mapper.entity.User;
import com.reddish.adonis.service.entity.DialogueInfoMessage;
import com.reddish.adonis.service.entity.Message;
import com.reddish.adonis.websocket.Dispatcher;
import org.springframework.stereotype.Service;

import javax.websocket.Session;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class DialogueInfoService {
    private final DialogueMapper dialogueMapper;
    private final UserMapper userMapper;

    public DialogueInfoService(DialogueMapper dialogueMapper, UserMapper userMapper) {
        this.dialogueMapper = dialogueMapper;
        this.userMapper = userMapper;
    }

    public static void sendDialogueInfoMessage(DialogueInfoMessage dialogueInfoMessage, String receiverId) {
        Message message = new Message();
        message.setType("DialogueInfoMessage");
        message.setDialogueInfoMessageList(Collections.singletonList(dialogueInfoMessage));
        Session session = Dispatcher.userId2SessionMap.get(receiverId);
        Dispatcher.sendMessage(session, message);
    }

    public static void sendDialogueInfoMessage(List<DialogueInfoMessage> dialogueInfoMessageList, Session session) {
        Message message = new Message();
        message.setType("DialogueInfoMessage");
        message.setDialogueInfoMessageList(dialogueInfoMessageList);
        Dispatcher.sendMessage(session, message);
    }

    public void handle(List<DialogueInfoMessage> dialogueInfoMessageList, Session session) throws MessageException, DialogueInfoException {
        for (DialogueInfoMessage dialogueInfoMessage : dialogueInfoMessageList) {
            String senderId = dialogueInfoMessage.getSenderId();
            String receiverId = dialogueInfoMessage.getReceiverId();
            User user1 = userMapper.selectById(senderId);
            User user2 = userMapper.selectById(receiverId);

            if (user1 == null || user2 == null) {
                throw new DialogueInfoException(ExceptionCode._401);
            }
            // 可以自己给自己发消息，user1和user2可以相同
            // 消息到达服务端的时间
            dialogueInfoMessage.setOccurredTime(System.currentTimeMillis());
            // TODO: 判断还是不是双向好友
            // 对方刚好在线,直接转发
            if (Dispatcher.onlineMap.get(receiverId)) {
                sendDialogueInfoMessage(dialogueInfoMessage, receiverId);
            }
            // 否则先存在数据库里
            else {
                dialogueMapper.insert(new Dialogue(senderId, receiverId,
                        dialogueInfoMessage.getContent(),
                        dialogueInfoMessage.getLastedTime(),
                        dialogueInfoMessage.getOccurredTime()));
            }
        }
    }
}
