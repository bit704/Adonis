package com.reddish.adonis.websocket;

import com.alibaba.fastjson2.JSON;
import com.reddish.adonis.exception.*;
import com.reddish.adonis.service.DialogueService;
import com.reddish.adonis.service.FriendService;
import com.reddish.adonis.service.UserService;
import com.reddish.adonis.AO.Message;
import com.reddish.adonis.AO.ReplyMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.Session;
import java.util.concurrent.ConcurrentHashMap;

import static com.reddish.adonis.exception.ExceptionCode.*;

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
    private static final ConcurrentHashMap<String, Boolean> onlineMap = new ConcurrentHashMap<>();

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
                throw new MessageException(illegal_type);
            }

            switch (messageType) {
                // 首字母小写
                case "userOpMessage" -> {
                    // 先发reply表示收到
                    sendMesageForReply(session, message, 0);
                    if (message.getUserOpMessage() == null) {
                        throw new MessageException(unconformity);
                    } else {
                        userService.handle(message.getUserOpMessage(), session);
                    }
                }
                case "friendOpMessage" -> {
                    // 先发reply表示收到
                    sendMesageForReply(session, message, 0);
                    if (message.getFriendOpMessage() == null) {
                        throw new MessageException(unconformity);
                    } else {
                        friendService.handle(message.getFriendOpMessage(), session);
                    }
                }
                case "dialogueInfoMessage" -> {
                    // 先发reply表示收到
                    sendMesageForReply(session, message, 0);
                    if (message.getDialogueInfoMessage() == null) {
                        throw new MessageException(unconformity);
                    } else {
                        dialogueService.handle(message.getDialogueInfoMessage(), session);
                    }
                }
                case "replyMessage" -> {
                    return;
                }
                default -> throw new MessageException(illegal_type);
            }
        } catch (MessageException e) {
            logger.info(e.getMessage());
            sendMesageForReply(session, message, e.code.getId());
        } catch (UserInfoException e) {
            logger.info(e.getMessage());
            sendMesageForReply(session, message, e.code.getId());
        } catch (FriendInfoException e) {
            logger.info(e.getMessage());
            sendMesageForReply(session, message, e.code.getId());
        } catch (DialogueInfoException e) {
            logger.info(e.getMessage());
            sendMesageForReply(session, message, e.code.getId());
        }
    }

    // 发送消息
    public static void sendMessage(Session session, Message message) {
        try {
            session.getAsyncRemote().sendText(JSON.toJSONString(message));
        } catch (Exception e) {
            logger.info(e.getMessage());
            logger.info("异步发送消息失败！");
        }
    }

    // 回复消息
    public static void sendMesageForReply(Session session, Message messageToReply, int code) {
        Message message = new Message(new ReplyMessage(messageToReply.getId(), code));
        sendMessage(session, message);
    }

    // 心跳机制保活消息
    public static void sendMessageForAlive(Session session) {
        Message message = new Message();
        sendMessage(session, message);
    }

    public static boolean isOnline(String userId) {
        return onlineMap.get(userId) != null && onlineMap.get(userId);
    }

    public static void setOnline(String userId) {
        Dispatcher.onlineMap.put(userId, true);
    }

    public static void setOffline(String userId) {
        Dispatcher.onlineMap.put(userId, false);
    }

}
