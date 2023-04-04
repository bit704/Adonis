package com.reddish.adonis.websocket;

import com.alibaba.fastjson2.JSON;
import com.reddish.adonis.exception.*;
import com.reddish.adonis.service.DialogueInfoService;
import com.reddish.adonis.service.FriendInfoService;
import com.reddish.adonis.service.UserInfoService;
import com.reddish.adonis.service.entity.Message;
import com.reddish.adonis.service.entity.ReplyMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.Session;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class Dispatcher {
    private static final Logger logger = LoggerFactory.getLogger(Dispatcher.class);
    private static UserInfoService userInfoService;
    private static FriendInfoService friendInfoService;
    private static DialogueInfoService dialogueInfoService;
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


    public void initialize(UserInfoService _userInfoService, FriendInfoService _friendInfoService, DialogueInfoService _dialogueInfoService) {
        userInfoService = _userInfoService;
        friendInfoService = _friendInfoService;
        dialogueInfoService = _dialogueInfoService;
    }

    /**
     * 分派收到的消息并视情况回复
     */
    public static void dispatch(String messageString, Session session) {
        Message message = JSON.parseObject(messageString, Message.class);

        try {
            // id = null 的消息视作保活消息，对其回复同样的保活消息
            if (message.getId() == null) {
                sendMessageForAlive(session);
                return;
            }

            switch (message.getType()) {
                case "UserInfoMessage" -> {
                    if (message.getUserInfoMessage() == null) {
                        throw new MessageException(ExceptionCode._102);
                    } else {
                        userInfoService.handle(message.getUserInfoMessage(), session);
                    }
                }
                case "FriendInfoMessage" -> {
                    if (message.getFriendInfoMessage() == null) {
                        throw new MessageException(ExceptionCode._102);
                    } else {
                        friendInfoService.handle(message.getFriendInfoMessage(), session);
                    }
                }
                case "DialogueInfoMessage" -> {
                    if (message.getDialogueInfoMessageList() == null) {
                        throw new MessageException(ExceptionCode._102);
                    } else {
                        dialogueInfoService.handle(message.getDialogueInfoMessageList(), session);
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
        }
        catch (FriendInfoException e) {
            logger.info(e.getMessage());
            sendMesageForReply(session, message, e.code.getCodeId());
            return;
        }
        catch (DialogueInfoException e) {
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
        Message message = new Message();
        message.setId(UUID.randomUUID().toString());
        message.setType("ReplyMessage");
        message.setReplyMessage(new ReplyMessage(messageToReply.getId(), code));
        sendMessage(session, message);
    }

    //保活消息
    public static void sendMessageForAlive(Session session) {
        Message message = new Message();
        sendMessage(session, message);
    }

}
