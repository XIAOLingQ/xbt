package com.xbt.server.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 启用一个简单的消息代理，目的地前缀为 /topic
        config.enableSimpleBroker("/topic");
        // 为发往 /app 前缀的消息设置应用目的地前缀
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 注册 /ws 端点，允许任何来源的连接
        registry.addEndpoint("/ws")
                .addInterceptors(new HttpHandshakeInterceptor())
                .setAllowedOriginPatterns("*");
    }
}