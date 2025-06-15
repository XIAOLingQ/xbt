package com.xbt.server.pojo.dto;

import lombok.Data;

@Data
public class AnswerPayload {
    private Integer questionId;
    private String studentAnswer;
}