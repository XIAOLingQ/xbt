package com.xbt.server.pojo.vo;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class StudentReportVO {
    // 总学习时长（分钟）
    private Double totalStudyDurationMinutes;
    // 总加入课程数
    private Integer totalCoursesCount;
    // 已完成课程数
    private Integer completedCoursesCount;
    // 作业总数
    private Integer totalHomeworkCount;
    // 已完成作业数
    private Integer completedHomeworkCount;
    // 作业平均分
    private Double averageScore;
    // 学习趋势数据
    private List<Map<String, Object>> studyTrend;
}