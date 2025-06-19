package com.xbt.server.pojo.dto;

import com.xbt.server.pojo.entity.HomeworkQuestion;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class HomeworkDTO {
    private Long id;
    private Long courseId;
    private String title;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private List<HomeworkQuestion> questions;
}