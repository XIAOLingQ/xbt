package com.xbt.server.pojo.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CourseVideo {
    private Long id;
    private Long courseId;
    private Long parentId;
    private Integer type; // 1-章, 2-节(视频)
    private String title;
    private String url;
    private Integer duration;
    private Integer orderNum;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}