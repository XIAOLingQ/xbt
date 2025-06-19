package com.xbt.server.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("ai_question")
public class AiQuestion {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private Long studentId;
    private String topic;
    private String batchId;
    private String questionType;
    private String questionText;
    private String options; // JSON string
    private String correctAnswer;
    private String analysis;
    private LocalDateTime createdAt;
    private String studentAnswer; // 学生答案
    private Boolean isCorrect; // 是否正确
}