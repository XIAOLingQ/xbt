package com.xbt.server.service;

import com.xbt.server.pojo.dto.SubmitAiAnswersRequest;

public interface AiService {
    void submitAnswers(SubmitAiAnswersRequest request, Long studentId);
}