package com.xbt.server.pojo.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CourseStudent {
    private Long id;
    private Long courseId;
    private Long studentId;
    private LocalDateTime joinTime;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}