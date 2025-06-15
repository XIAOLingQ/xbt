package com.xbt.server.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("ai_submission")
public class AiSubmission {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private Long aiQuestionId;
    private Long studentId;
    private String studentAnswer;
    private Boolean isCorrect;
    private LocalDateTime submittedAt;
}