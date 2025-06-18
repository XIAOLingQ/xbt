package com.xbt.server.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.servlet.http.HttpSession;
import java.util.Map;

public class HttpHandshakeInterceptor implements HandshakeInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(HttpHandshakeInterceptor.class);

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
            Map<String, Object> attributes) throws Exception {
        logger.info("====== WebSocket Handshake Start ======");
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
            logger.info("Request URI: {}", servletRequest.getURI());
            logger.info("Request Headers: {}", servletRequest.getHeaders());
            HttpSession session = servletRequest.getServletRequest().getSession(false);
            if (session != null) {
                Long userId = (Long) session.getAttribute("userId");
                logger.info("Found HTTP Session: {}. Found userId: {}", session.getId(), userId);
                // 将HTTP session中的userId存入WebSocket的session attributes
                attributes.put("userId", userId);
            } else {
                logger.warn("No HTTP Session found for WebSocket handshake.");
            }
        }
        logger.info("Attributes to be passed to WebSocket session: {}", attributes);
        logger.info("====== WebSocket Handshake End ======");
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
            Exception exception) {
        if (exception != null) {
            logger.error("Handshake failed with exception", exception);
        }
    }
}