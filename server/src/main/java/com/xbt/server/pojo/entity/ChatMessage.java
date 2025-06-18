package com.xbt.server.pojo.entity;

import lombok.Data;
import java.sql.Timestamp;

@Data
public class ChatMessage {
    private Long id;
    private Long courseId;
    private Long senderId;
    private String content;
    private Timestamp createdAt;
}