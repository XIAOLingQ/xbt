package com.xbt.server.pojo.vo;

import lombok.Data;

@Data
public class HomeworkVO {
    private Long id;
    private Long courseId;
    private String title;
    private String description;
    private String startTime;
    private String endTime;
}