package com.reddish.adonis.websocket;

import com.alibaba.fastjson2.JSON;
import com.reddish.adonis.exception.ExceptionCode;
import com.reddish.adonis.exception.MessageException;
import com.reddish.adonis.exception.UserInfoException;
import com.reddish.adonis.service.DialogueInfoService;
import com.reddish.adonis.service.UserInfoService;
import com.reddish.adonis.service.entity.Message;
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
    private static UserInfoService userInfoService;
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

    @Autowired
    public void initialize(UserInfoService _userInfoService, DialogueInfoService _dialogueInfoService) {
        userInfoService = _userInfoService;
        dialogueInfoService = _dialogueInfoService;
    }

    /**
     * 分派收到的消息并视情况回复
     */
    public static void dispatch(String messageString, Session session) {
        Message message = JSON.parseObject(messageString, Message.class);

        try {
            if(message.getMessageId() == null) {
                throw new MessageException(ExceptionCode._100);
            }
            // 暂不处理客户端的任何回复
            // 默认只有服务端回复客户端，没有客户端回复服务端
            // 客户端的回复视作保活信息
            if (!message.isReply()) {
                switch (message.getType()) {
                    case "userMessage" -> {
                        if (message.getUserInfoMessage() == null) {
                            throw new MessageException(ExceptionCode._102);
                        } else {
                            userInfoService.handle(message.getUserInfoMessage(),session);
                        }
                    }
                    case "dialogueMessage" -> {
                        if (message.getDialogueInfoMessage() == null) {
                            throw new MessageException(ExceptionCode._102);
                        } else {
                            dialogueInfoService.handle(message.getDialogueInfoMessage());
                        }
                    }
                    default -> throw new MessageException(ExceptionCode._101);
                }
            }
        } catch (MessageException e) {
            logger.info(e.getMessage());
            sendMesageForReply(session, message, e.code.getCodeId());
        } catch (UserInfoException e) {
            logger.info(e.getMessage());
            sendMesageForReply(session, message, e.code.getCodeId());
        }
        // 此回复表示成功
        sendMesageForReply(session, message, 0);
    }

    public static void sendMessage(Session session, Message message) {
        try {
            session.getBasicRemote().sendText(JSON.toJSONString(message));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void sendMesageForReply(Session session, Message messageToReply, int code) {
        Message message = new Message();
        message.setReply(true);
        message.setReplyCode(0);
        message.setMessageId(messageToReply.getMessageId());
        sendMessage(session, message);
    }

}
