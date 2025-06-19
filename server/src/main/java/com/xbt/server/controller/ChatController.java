package com.xbt.server.controller;

import com.xbt.server.mapper.ChatMapper;
import com.xbt.server.mapper.UserMapper;
import com.xbt.server.pojo.dto.ChatMessageDTO;
import com.xbt.server.pojo.entity.ChatMessage;
import com.xbt.server.pojo.entity.User;
import com.xbt.server.util.AuthUtils;
import com.xbt.server.util.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.List;

@RestController
public class ChatController {

    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    @Resource
    private SimpMessagingTemplate messagingTemplate;

    @Resource
    private ChatMapper chatMapper;

    @Resource
    private UserMapper userMapper;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessageDTO chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        // 从WebSocket Session中获取用户信息
        Long senderId = (Long) headerAccessor.getSessionAttributes().get("userId");
        logger.debug("Processing chat message: [{}] from user [{}]. Headers: {}", chatMessage, senderId,
                headerAccessor.getMessageHeaders());

        if (senderId == null) {
            // 用户未认证，可以记录日志或进行其他处理
            logger.warn("Cannot send message. Unauthenticated user for session: {}", headerAccessor.getSessionId());
            return;
        }

        // 1. 完善消息信息
        User sender = userMapper.findById(senderId);
        chatMessage.setSenderId(senderId);
        chatMessage.setSenderName(sender.getRealName());
        chatMessage.setSenderAvatar(sender.getAvatar());
        chatMessage.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        // 2. 保存到数据库
        ChatMessage messageToDb = new ChatMessage();
        messageToDb.setCourseId(chatMessage.getCourseId());
        messageToDb.setSenderId(chatMessage.getSenderId());
        messageToDb.setContent(chatMessage.getContent());
        messageToDb.setCreatedAt(chatMessage.getCreatedAt());
        chatMapper.insert(messageToDb);

        chatMessage.setId(messageToDb.getId());

        // 3. 通过WebSocket广播给所有订阅者
        logger.info("Broadcasting message to /topic/course/{}", chatMessage.getCourseId());
        messagingTemplate.convertAndSend("/topic/course/" + chatMessage.getCourseId(), chatMessage);
    }

    @GetMapping("/api/chat/{courseId}/history")
    public Result<List<ChatMessageDTO>> getChatHistory(@PathVariable Long courseId) {
        List<ChatMessageDTO> history = chatMapper.findByCourseId(courseId);
        return Result.success(history);
    }
}