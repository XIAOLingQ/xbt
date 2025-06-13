package com.xbt.server.controller;

import com.xbt.server.pojo.vo.LearningReportVO;
import com.xbt.server.service.ReportService;
import com.xbt.server.util.AuthUtils;
import com.xbt.server.util.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/report")
public class ReportController {

    @Resource
    private ReportService reportService;

    @GetMapping("/student")
    public Result<LearningReportVO> getStudentReport() {
        try {
            Long studentId = AuthUtils.getCurrentUserId();
            LearningReportVO report = reportService.generateStudentReport(studentId);
            return Result.success(report);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            return Result.error(401, "用户未登录");
        }
    }
}