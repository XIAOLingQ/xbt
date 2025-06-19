package com.xbt.server.pojo.dto;

import lombok.Data;
import java.util.Map;

@Data
public class SubmitHomeworkRequest {
    private Long homeworkId;
    private Map<Long, Object> answers;
}