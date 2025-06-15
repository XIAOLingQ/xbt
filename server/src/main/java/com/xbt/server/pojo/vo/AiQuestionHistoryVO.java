package com.xbt.server.pojo.vo;

import lombok.Data;

@Data
public class AiQuestionHistoryVO {
    //
    // From ai_question table
    private Long id; // Corresponds to ai_question.id
    private String topic;
    private String batchId;
    private String questionType;
    private String questionText;
    private String options; // JSON string
    private String correctAnswer;
    private String analysis;

    // From ai_submission table
    private String studentAnswer;
    private Boolean isCorrect;
}