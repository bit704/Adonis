package com.reddish.adonis.websocket;

import com.reddish.adonis.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ServerEndpoint("/ct")
public class Server {

    private static final Logger logger = LoggerFactory.getLogger(Server.class);

    private static ConcurrentHashMap<String, Session> map = new ConcurrentHashMap<>();

    private static UserService userService;

    @Autowired
    public void initialize(UserService _userService) {
        userService = _userService;
    }

    @OnOpen
    public void onOpen(Session session) {
        map.put(session.getId(), session);
        logger.info("[新连接]" + " session:" + session.getId() + " 当前连接数：" + map.size());
    }

    @OnClose
    public void onClose(Session session) {
        map.remove(session.getId());
        System.out.println("[关闭连接]" + " session:" + session.getId() + " 当前连接数：" + map.size());
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("[收到消息]" + message);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        System.out.println(error);
    }


}
