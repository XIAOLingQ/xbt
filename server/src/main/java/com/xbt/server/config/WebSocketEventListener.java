package com.xbt.server.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class WebSocketEventListener {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        Object userId = headerAccessor.getSessionAttributes().get("userId");
        logger.info(">>>>>> WebSocket a new web socket connection. Session ID: [{}], User ID: [{}]",
                headerAccessor.getSessionId(), userId);
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        Object userId = headerAccessor.getSessionAttributes().get("userId");
        logger.info("<<<<<< WebSocket connection closed. Session ID: [{}], User ID: [{}]",
                headerAccessor.getSessionId(), userId);
    }
}