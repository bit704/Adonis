package com.reddish.adoniswebsocket.Websocket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

@Configuration
public class Config {
    @Bean
    public ServerEndpointExporter serverEndpointExporter(){
        return new ServerEndpointExporter();
    }

    @Bean
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        container.setMaxSessionIdleTimeout(3600000L);
        container.setAsyncSendTimeout(5000L);
        container.setMaxTextMessageBufferSize(200 * 1024);
        container.setMaxBinaryMessageBufferSize(200 * 1024);
        return container;
    }
}