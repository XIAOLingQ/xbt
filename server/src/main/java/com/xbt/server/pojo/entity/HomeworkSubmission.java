package com.xbt.server.pojo.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class HomeworkSubmission {
    private Long id;
    private Long homeworkId;
    private Long studentId;
    private Integer status;
    private Integer score;
    private String answers; // Stored as JSON string
    private LocalDateTime submitTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}