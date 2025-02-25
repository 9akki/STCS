package com.itheima.config;

import com.itheima.mapper.MessageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import javax.websocket.server.ServerEndpoint;

@Configuration
@EnableWebSocket
public class WebSocketConfig {

    @Bean
    public ServerEndpointExporter serverEndpointExporter() { return new ServerEndpointExporter(); }
}
