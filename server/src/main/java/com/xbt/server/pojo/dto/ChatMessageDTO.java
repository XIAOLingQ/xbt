package com.xbt.server.pojo.dto;

import lombok.Data;
import java.sql.Timestamp;

@Data
public class ChatMessageDTO {
    private Long id;
    private Long courseId;
    private Long senderId;
    private String senderName;
    private String senderAvatar;
    private String content;
    private Timestamp createdAt;
}