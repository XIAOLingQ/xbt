package com.xbt.server.pojo.entity;

import lombok.Data;
import java.time.LocalDateTime;
import java.sql.Timestamp;
import java.util.List;

@Data
public class Homework {
    private Long id;
    private Long courseId;
    private String title;
    private String description;
    private List<HomeworkQuestion> questions;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}