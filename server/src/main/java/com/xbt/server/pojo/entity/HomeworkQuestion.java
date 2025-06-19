package com.xbt.server.pojo.entity;

import lombok.Data;

@Data
public class HomeworkQuestion {
    private Long id;
    private Long homeworkId;
    private String questionText;
    private String questionType;
    private String options;
    private String answer;
    private String score;
}