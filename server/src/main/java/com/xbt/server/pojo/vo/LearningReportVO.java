package com.xbt.server.pojo.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class LearningReportVO {
    // 核心指标
    private long totalStudyDurationMinutes; // 总学习时长（分钟）
    private int completedCoursesCount; // 完成的课程数
    private int totalCoursesCount; // 加入的总课程数
    private int completedHomeworkCount; // 完成的作业数
    private int totalHomeworkCount; // 总作业数
    private double averageScore; // 平均分

    // 学习趋势 (例如，最近7天的学习时长)
    private Map<String, Long> dailyStudyDuration; // key: "YYYY-MM-DD", value: minutes

    // 各课程进度
    private List<CourseProgressVO> courseProgress;

    @Data
    @Builder
    public static class CourseProgressVO {
        private Long courseId;
        private String courseTitle;
        private int progress; // 学习进度百分比
    }
} 