package com.xbt.server.pojo.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CourseVO {
    private Long id;
    private String title;
    private String description;
    private String coverImage;
    private String courseCode;
    private String teacherName;
    private Integer status;
    private LocalDateTime createdAt;
}