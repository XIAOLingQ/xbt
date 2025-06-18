package com.xbt.server.service.impl;

import com.xbt.server.mapper.*;
import com.xbt.server.pojo.vo.StudentReportVO;
import com.xbt.server.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final CourseStudentMapper courseStudentMapper;
    private final HomeworkMapper homeworkMapper;
    private final HomeworkSubmissionMapper homeworkSubmissionMapper;
    private final StudentVideoProgressMapper studentVideoProgressMapper;

    @Override
    public StudentReportVO getStudentReport(Long studentId) {
        StudentReportVO report = new StudentReportVO();

        // 1. 总学习时长
        Long totalDurationSeconds = Optional
                .ofNullable(studentVideoProgressMapper.sumTotalWatchTimeByStudentId(studentId)).orElse(0L);
        report.setTotalStudyDurationMinutes(totalDurationSeconds / 60.0);

        // 2. 课程完成情况
        report.setTotalCoursesCount(courseStudentMapper.countTotalCoursesByStudentId(studentId));
        report.setCompletedCoursesCount(courseStudentMapper.countCompletedCoursesByStudentId(studentId));

        // 3. 作业情况
        List<Long> courseIds = courseStudentMapper.getCourseIdsByStudentId(studentId);
        if (courseIds == null || courseIds.isEmpty()) {
            report.setTotalHomeworkCount(0);
        } else {
            report.setTotalHomeworkCount(homeworkMapper.countHomeworksByCourseIds(courseIds));
        }

        report.setCompletedHomeworkCount(homeworkSubmissionMapper.countCompletedSubmissionsByStudentId(studentId));
        Double avgScore = Optional.ofNullable(homeworkSubmissionMapper.getAverageScoreByStudentId(studentId))
                .orElse(0.0);
        report.setAverageScore(avgScore);

        // 4. 学习趋势 (最近30天)
        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);
        report.setStudyTrend(studentVideoProgressMapper.getStudyTrendByStudentId(studentId, thirtyDaysAgo.toString()));

        return report;
    }
}