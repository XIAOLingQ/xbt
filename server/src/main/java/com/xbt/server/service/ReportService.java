package com.xbt.server.service;

import com.xbt.server.pojo.vo.LearningReportVO;

public interface ReportService {

    /**
     * 生成并返回指定学生的学习报告
     * @param studentId 学生ID
     * @return 学习报告数据
     */
    LearningReportVO generateStudentReport(Long studentId);

} 