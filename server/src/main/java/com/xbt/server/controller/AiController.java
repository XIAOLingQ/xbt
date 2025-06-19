package com.xbt.server.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xbt.server.mapper.AiMapper;
import com.xbt.server.pojo.dto.SubmitAiAnswersRequest;
import com.xbt.server.pojo.entity.AiQuestion;
import com.xbt.server.pojo.vo.AiQuestionHistoryVO;
import com.xbt.server.service.AiService;
import com.xbt.server.service.CozeService;
import com.xbt.server.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/ai")
public class AiController {
    @Autowired
    private CozeService cozeService;

    @Autowired
    private AiService aiService;

    @Autowired
    private AiMapper aiMapper;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 根据用户输入生成AI题目
     * 
     * @param payload 包含userInput的Map
     * @return 包含AI生成问题的Result
     */
    @PostMapping("/generate-question")
    public Result<List<Map<String, Object>>> getQuestion(@RequestBody Map<String, String> payload,
            HttpServletRequest request) {
        String userInput = payload.get("userInput");

        Object userIdObj = request.getSession().getAttribute("userId");
        if (userIdObj == null) {
            return Result.error(401, "无法从会话中获取用户信息，请重新登录");
        }
        Long studentId = Long.valueOf(userIdObj.toString());

        if (userInput == null || userInput.isEmpty()) {
            return Result.error(400, "用户输入不能为空");
        }

        String jsonResponse = cozeService.Getquestion(userInput);

        try {
            // HOTFIX for malformed JSON from AI service which may be missing array brackets
            if (jsonResponse != null && !jsonResponse.trim().startsWith("[")) {
                jsonResponse = "[" + jsonResponse + "]";
            }

            List<Map<String, Object>> questionsFromAi = objectMapper.readValue(jsonResponse,
                    new TypeReference<List<Map<String, Object>>>() {
                    });

            List<Map<String, Object>> questionsForFrontend = new ArrayList<>();
            String batchId = UUID.randomUUID().toString();

            for (Map<String, Object> q : questionsFromAi) {
                AiQuestion questionToDb = new AiQuestion();
                questionToDb.setStudentId(studentId);
                questionToDb.setTopic(userInput);
                questionToDb.setBatchId(batchId);
                questionToDb.setQuestionText((String) q.get("description"));
                questionToDb.setQuestionType("single_choice");
                questionToDb.setCorrectAnswer((String) q.get("answer"));
                questionToDb.setAnalysis((String) q.get("analysis"));
                questionToDb.setOptions(objectMapper.writeValueAsString(q.get("options")));

                aiMapper.insertAiQuestion(questionToDb);

                q.put("id", questionToDb.getId()); // 用数据库ID覆盖AI返回的ID
                questionsForFrontend.add(q);
            }

            return Result.success(questionsForFrontend);

        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(500, "AI返回数据解析或存储失败");
        }
    }

    /**
     * 提交答案并进行批改
     * 
     * @param request
     * @return
     */
    @PostMapping("/submit")
    public Result submitAnswers(@RequestBody SubmitAiAnswersRequest request, HttpServletRequest httpServletRequest) {
        Object userIdObj = httpServletRequest.getSession().getAttribute("userId");
        if (userIdObj == null) {
            return Result.error(401, "无法从会话中获取用户信息，请重新登录");
        }
        Long studentId = Long.valueOf(userIdObj.toString());

        aiService.submitAnswers(request, studentId);
        return Result.success();
    }

    @GetMapping("/history/topics")
    public Result getTopicHistory(HttpServletRequest request) {
        Object userIdObj = request.getSession().getAttribute("userId");
        if (userIdObj == null) {
            return Result.error(401, "无法从会话中获取用户信息，请重新登录");
        }
        Long studentId = Long.valueOf(userIdObj.toString());

        return Result.success(aiMapper.selectTopicHistoryByStudentId(studentId));
    }

    @GetMapping("/history/questions")
    public Result<List<AiQuestionHistoryVO>> getQuestionHistoryByBatchId(@RequestParam String batchId,
            HttpServletRequest request) {
        Object userIdObj = request.getSession().getAttribute("userId");
        if (userIdObj == null) {
            return Result.error(401, "无法从会话中获取用户信息，请重新登录");
        }
        Long studentId = Long.valueOf(userIdObj.toString());

        List<AiQuestionHistoryVO> history = aiMapper.selectHistoryByStudentId(studentId, batchId);
        return Result.success(history);
    }
}