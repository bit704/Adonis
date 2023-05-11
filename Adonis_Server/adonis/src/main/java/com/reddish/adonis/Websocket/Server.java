package com.reddish.adonis.Websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;

@Component
@ServerEndpoint("/ws")
public class Server {

    private static final Logger logger = LoggerFactory.getLogger(Server.class);

    @OnOpen
    public void onOpen(Session session) {
        Dispatcher.selfId2SessionMap.put(session.getId(), session);
        logger.info("[新连接]" + " session:" + session.getId() + " 当前连接数：" + Dispatcher.selfId2SessionMap.size());
    }

    @OnClose
    public void onClose(Session session) {
        Dispatcher.selfId2SessionMap.remove(session.getId());
        String userId = Dispatcher.session2UserIdMap.get(session);
        if (userId != null) {
            // 标记为不在线
            Dispatcher.setOffline(userId);
            // 删除userId和Session的对应关系
            Dispatcher.session2UserIdMap.remove(session);
            Dispatcher.userId2SessionMap.remove(userId);
        }
        logger.info("[关闭连接]" + " session:" + session.getId() + " 当前连接数：" + Dispatcher.selfId2SessionMap.size());
    }

    @OnMessage
    public void onMessage(String messageString, Session session) {
        logger.info("[收到消息]" + messageString);
        Dispatcher.dispatch(messageString, session);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        System.out.println(error);
    }


}
