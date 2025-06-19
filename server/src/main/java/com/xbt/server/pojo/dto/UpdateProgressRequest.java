package com.xbt.server.pojo.dto;

import lombok.Data;

@Data
public class UpdateProgressRequest {
    private Long videoId;
    private Integer watchDuration; // 观看时长，单位秒
}