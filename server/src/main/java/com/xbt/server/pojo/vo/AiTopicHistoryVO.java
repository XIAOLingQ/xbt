package com.xbt.server.pojo.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AiTopicHistoryVO {
    private String topic;
    private String batchId;
    private LocalDateTime createdAt;
    private int totalCount;
    private int completedCount;
}