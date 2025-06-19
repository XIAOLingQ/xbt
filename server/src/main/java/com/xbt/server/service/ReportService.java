package com.xbt.server.service;

import com.xbt.server.pojo.vo.StudentReportVO;

/**
 * 学生学习报告服务接口
 */
public interface ReportService {

    /**
     * 获取指定学生的学习报告
     * 
     * @param studentId 学生ID
     * @return 学习报告视图对象
     */
    StudentReportVO getStudentReport(Long studentId);

}