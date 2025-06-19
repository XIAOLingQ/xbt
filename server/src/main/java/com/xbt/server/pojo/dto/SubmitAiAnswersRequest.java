package com.xbt.server.pojo.dto;

import lombok.Data;
import java.util.List;

@Data
public class SubmitAiAnswersRequest {
    private String batchId;
    private List<AnswerPayload> answers;
}