package com.xbt.server.controller;

import com.xbt.server.pojo.dto.UserDTO;
import com.xbt.server.util.AuthUtils;
import com.xbt.server.util.Result;
import com.xbt.server.pojo.vo.StudentReportVO;
import com.xbt.server.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/student/me")
    public Result<StudentReportVO> getMyStudentReport() {
        Long studentId = AuthUtils.getCurrentUserId();
        StudentReportVO report = reportService.getStudentReport(studentId);
        return Result.success(report);
    }
}