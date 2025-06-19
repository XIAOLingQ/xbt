package com.xbt.server.service.impl;

import com.xbt.server.mapper.HomeworkMapper;
import com.xbt.server.mapper.HomeworkQuestionMapper;
import com.xbt.server.mapper.HomeworkSubmissionMapper;
import com.xbt.server.mapper.UserMapper;
import com.xbt.server.pojo.dto.*;
import com.xbt.server.pojo.entity.Homework;
import com.xbt.server.pojo.entity.HomeworkQuestion;
import com.xbt.server.pojo.entity.HomeworkSubmission;
import com.xbt.server.pojo.entity.User;
import com.xbt.server.pojo.vo.HomeworkStatusVO;
import com.xbt.server.pojo.vo.HomeworkSubmissionSummaryVO;
import com.xbt.server.pojo.vo.SubmissionDetailVO;
import com.xbt.server.service.HomeworkService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class HomeworkServiceImpl implements HomeworkService {

    @Resource
    private HomeworkMapper homeworkMapper;

    @Resource
    private HomeworkQuestionMapper homeworkQuestionMapper;

    @Autowired
    private HomeworkSubmissionMapper homeworkSubmissionMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public List<HomeworkStatusVO> getHomeworkListByCourseId(Long courseId, Long studentId) {
        return homeworkMapper.findHomeworksWithStatusByCourseId(courseId, studentId);
    }

    @Override
    @Transactional
    public Homework saveHomework(HomeworkDTO request) {
        Homework homework = new Homework();
        BeanUtils.copyProperties(request, homework);

        if (homework.getId() == null) {
            homeworkMapper.insert(homework);
        } else {
            homeworkMapper.update(homework);
            homeworkQuestionMapper.deleteByHomeworkId(homework.getId());
        }

        List<HomeworkQuestion> questions = request.getQuestions();
        if (questions != null && !questions.isEmpty()) {
            Long homeworkId = homework.getId();
            questions.forEach(q -> q.setHomeworkId(homeworkId));
            homeworkQuestionMapper.batchInsert(questions);
        }

        homework.setQuestions(questions);
        return homework;
    }

    @Override
    @Transactional
    public void deleteHomework(Long homeworkId) {
        homeworkSubmissionMapper.deleteByHomeworkId(homeworkId);
        homeworkQuestionMapper.deleteByHomeworkId(homeworkId);
        homeworkMapper.deleteById(homeworkId);
    }

    @Override
    public HomeworkDetailDTO getHomeworkDetail(Long id, Long studentId) {
        Homework homework = homeworkMapper.selectById(id);
        if (homework == null) {
            return null;
        }
        List<HomeworkQuestion> questions = homeworkQuestionMapper.selectByHomeworkId(id);

        // 查询学生的提交记录
        HomeworkSubmission submission = homeworkSubmissionMapper.findByHomeworkAndStudent(id, studentId);

        HomeworkDetailDTO dto = new HomeworkDetailDTO();
        dto.setHomework(homework);
        dto.setQuestions(questions);
        dto.setSubmission(submission);
        return dto;
    }

    @Override
    @Transactional
    public void submitHomework(Long studentId, SubmitHomeworkRequest request) {
        // 1. 获取作业的所有问题
        List<HomeworkQuestion> questions = homeworkQuestionMapper.selectByHomeworkId(request.getHomeworkId());
        if (questions == null || questions.isEmpty()) {
            throw new RuntimeException("作业不存在或没有题目");
        }

        // 2. 自动批改选择题并计算总分
        int totalScore = 0;
        boolean allChoiceQuestions = true;
        for (HomeworkQuestion question : questions) {
            if ("choice".equals(question.getQuestionType())) {
                String correctAnswer = question.getAnswer();
                Object studentAnswerObj = request.getAnswers().get(question.getId());
                // Only grade if the answer is a simple string
                if (studentAnswerObj instanceof String) {
                    String studentAnswer = (String) studentAnswerObj;
                    if (correctAnswer != null && correctAnswer.equalsIgnoreCase(studentAnswer)) {
                        totalScore += Integer.parseInt(question.getScore());
                    }
                }
            } else {
                allChoiceQuestions = false; // 存在非选择题
            }
        }

        // 3. 准备并保存提交记录
        HomeworkSubmission submission = new HomeworkSubmission();
        submission.setHomeworkId(request.getHomeworkId());
        submission.setStudentId(studentId);
        submission.setSubmitTime(LocalDateTime.now());
        submission.setScore(totalScore);

        // 如果所有题目都是选择题，则状态为"已批改"，否则为"已提交"
        submission.setStatus(allChoiceQuestions ? 3 : 2);

        // 序列化答案为JSON
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String answersJson = objectMapper.writeValueAsString(request.getAnswers());
            submission.setAnswers(answersJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("序列化答案失败", e);
        }

        homeworkSubmissionMapper.upsert(submission);
    }

    @Override
    public List<HomeworkSubmissionSummaryVO> getSubmissionsByHomeworkId(Long homeworkId) {
        return homeworkSubmissionMapper.findSubmissionsByHomeworkId(homeworkId);
    }

    @Override
    public SubmissionDetailVO getSubmissionDetails(Long submissionId) {
        // This is a simplified implementation. A real one might need more details.
        HomeworkSubmission submission = homeworkSubmissionMapper.findById(submissionId);
        if (submission == null)
            return null;

        Homework homework = homeworkMapper.selectById(submission.getHomeworkId());
        User student = userMapper.selectById(submission.getStudentId());
        List<HomeworkQuestion> questions = homeworkQuestionMapper.selectByHomeworkId(submission.getHomeworkId());

        SubmissionDetailVO vo = new SubmissionDetailVO();
        vo.setHomeworkTitle(homework.getTitle());
        vo.setStudentName(student.getRealName());
        vo.setQuestions(questions);
        try {
            vo.setStudentAnswers(new ObjectMapper().readValue(submission.getAnswers(), Map.class));
        } catch (Exception e) {
            vo.setStudentAnswers(Map.of());
        }
        return vo;
    }

    @Override
    public void gradeSubmission(Long submissionId, GradeSubmissionRequest request) {
        HomeworkSubmission submission = homeworkSubmissionMapper.findById(submissionId);
        if (submission != null) {
            submission.setScore(request.getFinalScore());
            submission.setStatus(3); // 3 = Graded
            homeworkSubmissionMapper.update(submission);
        }
    }
}