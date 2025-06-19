package com.xbt.server.pojo.vo;

import lombok.Data;

@Data
public class TeacherCourseVO {
    private Long id;
    private String title;
    private String description;
    private String coverImage;
    private String courseCode;
    private Integer status;
    private Integer studentCount;
}