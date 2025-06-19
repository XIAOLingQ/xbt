package com.xbt.server.pojo.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class StudentCourseVO {
    private Long id;
    private String title;
    private String description;
    private String coverImage;
    private String teacherName;
    private Integer progress;

    // for student list in course manage
    private String username;
    private String realName;
    private String email;
    private LocalDateTime joinTime;
}