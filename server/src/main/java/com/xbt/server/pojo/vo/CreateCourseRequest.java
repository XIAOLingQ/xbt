package com.xbt.server.pojo.vo;

import lombok.Data;

@Data
public class CreateCourseRequest {
    private String title;
    private String description;
    private String coverImage;
}