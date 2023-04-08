package com.reddish.adonis.websocket;

import com.alibaba.fastjson2.JSON;
import com.reddish.adonis.exception.*;
import com.reddish.adonis.service.DialogueService;
import com.reddish.adonis.service.FriendService;
import com.reddish.adonis.service.UserService;
import com.reddish.adonis.service.entity.Message;
import com.reddish.adonis.service.entity.ReplyMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.Session;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class Dispatcher {
    private static final Logger logger = LoggerFactory.getLogger(Dispatcher.class);
    private static UserService userService;
    private static FriendService friendService;
    private static DialogueService dialogueService;
    /**
     * 统计当前连接数
     * Session id, Session
     */
    public static final ConcurrentHashMap<String, Session> selfId2SessionMap = new ConcurrentHashMap<>();
    /**
     * userId, Session
     */
    public static final ConcurrentHashMap<String, Session> userId2SessionMap = new ConcurrentHashMap<>();
    /**
     * Session, userId
     */
    public static final ConcurrentHashMap<Session, String> session2UserIdMap = new ConcurrentHashMap<>();
    /**
     * senderId, 在线状态
     */
    public static final ConcurrentHashMap<String, Boolean> onlineMap = new ConcurrentHashMap<>();

    @Autowired
    public void initialize(UserService _userService, FriendService _friendService, DialogueService _dialogueService) {
        userService = _userService;
        friendService = _friendService;
        dialogueService = _dialogueService;
    }

    /**
     * 分派收到的消息并视情况回复
     */
    public static void dispatch(String messageString, Session session) {
        Message message = JSON.parseObject(messageString, Message.class);
        String messageType = message.getType();
        try {
            // id = null 的消息视作保活消息，对其回复同样的保活消息
            if (message.getId() == null) {
                sendMessageForAlive(session);
                return;
            }

            if (messageType == null) {
                throw new MessageException(ExceptionCode._101);
            }

            switch (messageType) {
                case "UserOpMessage" -> {
                    if (message.getUserOpMessage() == null) {
                        throw new MessageException(ExceptionCode._102);
                    } else {
                        userService.handle(message.getUserOpMessage(), session);
                    }
                }
                case "FriendOpMessage" -> {
                    if (message.getFriendOpMessage() == null) {
                        throw new MessageException(ExceptionCode._102);
                    } else {
                        friendService.handle(message.getFriendOpMessage(), session);
                    }
                }
                case "DialogueInfoMessage" -> {
                    if (message.getDialogueInfoMessage() == null) {
                        throw new MessageException(ExceptionCode._102);
                    } else {
                        dialogueService.handle(message.getDialogueInfoMessage(), session);
                    }
                }
                default -> throw new MessageException(ExceptionCode._101);

            }
        } catch (MessageException e) {
            logger.info(e.getMessage());
            sendMesageForReply(session, message, e.code.getCodeId());
            return;
        } catch (UserInfoException e) {
            logger.info(e.getMessage());
            sendMesageForReply(session, message, e.code.getCodeId());
            return;
        } catch (FriendInfoException e) {
            logger.info(e.getMessage());
            sendMesageForReply(session, message, e.code.getCodeId());
            return;
        } catch (DialogueInfoException e) {
            logger.info(e.getMessage());
            sendMesageForReply(session, message, e.code.getCodeId());
            return;
        }
        // 代码0表示成功
        sendMesageForReply(session, message, 0);

    }

    // 发送消息
    public static void sendMessage(Session session, Message message) {
        try {
            session.getBasicRemote().sendText(JSON.toJSONString(message));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // 回复消息
    public static void sendMesageForReply(Session session, Message messageToReply, int code) {
        Message message = new Message(new ReplyMessage(messageToReply.getId(), code));
        sendMessage(session, message);
    }

    //保活消息
    public static void sendMessageForAlive(Session session) {
        Message message = new Message();
        sendMessage(session, message);
    }

}
