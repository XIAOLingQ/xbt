package com.xbt.server.pojo.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class HomeworkSubmissionSummaryVO {
    private Long id; // submission id
    private Long studentId;
    private String studentName;
    private Integer status;
    private LocalDateTime submitTime;
    private Integer score;
}