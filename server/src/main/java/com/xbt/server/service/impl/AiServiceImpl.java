package com.xbt.server.service.impl;

import com.xbt.server.mapper.AiMapper;
import com.xbt.server.pojo.dto.AnswerPayload;
import com.xbt.server.pojo.dto.SubmitAiAnswersRequest;
import com.xbt.server.pojo.entity.AiQuestion;
import com.xbt.server.service.AiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AiServiceImpl implements AiService {

    @Autowired
    private AiMapper aiMapper;

    @Override
    @Transactional
    public void submitAnswers(SubmitAiAnswersRequest request, Long studentId) {
        for (AnswerPayload answer : request.getAnswers()) {
            AiQuestion question = aiMapper.selectQuestionById(answer.getQuestionId());
            if (question != null && question.getStudentId().equals(studentId)) {
                question.setStudentAnswer(answer.getStudentAnswer());
                boolean isCorrect = question.getCorrectAnswer().equalsIgnoreCase(answer.getStudentAnswer());
                question.setIsCorrect(isCorrect);
                aiMapper.updateQuestionAnswer(question);
            }
        }
    }
}