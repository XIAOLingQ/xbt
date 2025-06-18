package com.xbt.server.pojo.vo;

import com.xbt.server.pojo.entity.HomeworkQuestion;
import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class SubmissionDetailVO {
    private String homeworkTitle;
    private String studentName;
    private List<HomeworkQuestion> questions;
    private Map<String, Object> studentAnswers; // QuestionId -> Answer
}