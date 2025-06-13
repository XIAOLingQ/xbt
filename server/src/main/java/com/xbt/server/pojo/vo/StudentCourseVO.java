package com.xbt.server.pojo.vo;

import lombok.Data;

@Data
public class StudentCourseVO {
    private Long id;
    private String title;
    private String description;
    private String coverImage;
    private String teacherName;
    private Integer progress;
}