package com.xbt.server.service.impl;

import com.xbt.server.mapper.CourseMapper;
import com.xbt.server.mapper.HomeworkMapper;
import com.xbt.server.mapper.HomeworkSubmissionMapper;
import com.xbt.server.mapper.StudentVideoProgressMapper;
import com.xbt.server.mapper.CourseVideoMapper;
import com.xbt.server.pojo.vo.LearningReportVO;
import com.xbt.server.pojo.vo.StudentCourseVO;
import com.xbt.server.service.ReportService;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {

    @Resource
    private CourseMapper courseMapper;

    @Resource
    private HomeworkSubmissionMapper homeworkSubmissionMapper;

    @Resource
    private HomeworkMapper homeworkMapper;

    @Resource
    private StudentVideoProgressMapper studentVideoProgressMapper;

    @Resource
    private CourseVideoMapper courseVideoMapper;

    // TODO: 后面还需要注入 CourseVideoMapper 和 StudentVideoProgressMapper

    @Override
    public LearningReportVO generateStudentReport(Long studentId) {

        // --- 核心指标计算 ---

        // 1. 获取学生加入的总课程数
        int totalCoursesCount = courseMapper.countCoursesByStudentId(studentId);

        // 2. 获取学生已提交的作业数和总作业数
        int completedHomeworkCount = homeworkSubmissionMapper.countSubmittedByStudentId(studentId);
        int totalHomeworkCount = homeworkMapper.countTotalHomeworksByStudentId(studentId);

        // 3. 计算平均分
        Double averageScore = homeworkSubmissionMapper.getAverageScoreByStudentId(studentId);

        // 4. 获取总学习时长
        Long totalStudyDurationSeconds = studentVideoProgressMapper.sumTotalWatchTimeByStudentId(studentId);
        long totalStudyDurationMinutes = (totalStudyDurationSeconds != null) ? totalStudyDurationSeconds / 60 : 0;

        // 5. 计算完成的课程数
        List<StudentCourseVO> courses = courseMapper.getCoursesByStudentId(studentId);
        int completedCoursesCount = 0;
        final double COMPLETION_THRESHOLD = 0.9;

        for (StudentCourseVO course : courses) {
            Long courseTotalDuration = courseVideoMapper.sumTotalDurationByCourseId(course.getId());
            Long studentWatchDuration = studentVideoProgressMapper.sumWatchTimeByStudentAndCourse(studentId,
                    course.getId());

            if (courseTotalDuration != null && courseTotalDuration > 0 && studentWatchDuration != null) {
                double progress = (double) studentWatchDuration / courseTotalDuration;
                if (progress >= COMPLETION_THRESHOLD) {
                    completedCoursesCount++;
                }
            }
        }

        // --- 学习趋势计算 ---
        // TODO: 待实现

        // --- 各课程进度计算 ---
        // TODO: 待实现

        // 使用 Builder 模式构建返回对象
        return LearningReportVO.builder()
                .totalCoursesCount(totalCoursesCount)
                .completedCoursesCount(completedCoursesCount)
                .totalHomeworkCount(totalHomeworkCount)
                .completedHomeworkCount(completedHomeworkCount)
                .averageScore(averageScore != null ? averageScore : 0.0)
                .totalStudyDurationMinutes(totalStudyDurationMinutes)
                .build();
    }
}