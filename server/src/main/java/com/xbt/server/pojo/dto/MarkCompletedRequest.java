package com.xbt.server.pojo.dto;

import lombok.Data;

@Data
public class MarkCompletedRequest {
    private Long courseId;
    private Long videoId;
}