package com.xbt.server.pojo.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Course {
    private Long id;
    private String title;
    private String description;
    private String coverImage;
    private String courseCode;
    private Long teacherId;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}