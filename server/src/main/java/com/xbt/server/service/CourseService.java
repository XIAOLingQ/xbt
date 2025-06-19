package com.xbt.server.service;

import com.xbt.server.pojo.vo.CourseVO;
import com.xbt.server.pojo.vo.CreateCourseRequest;
import com.xbt.server.pojo.vo.StudentCourseVO;
import com.xbt.server.pojo.vo.TeacherCourseVO;
import com.xbt.server.pojo.vo.PageVO;
import com.xbt.server.pojo.vo.LearnPageVO;
import java.util.List;
import com.xbt.server.pojo.PageBean;

public interface CourseService {
    // 创建课程
    CourseVO createCourse(Long teacherId, CreateCourseRequest request);

    // 加入课程
    void joinCourse(Long studentId, String courseCode);

    // 获取课程信息
    CourseVO getCourseInfo(Long courseId);

    /**
     * 根据教师ID获取课程列表
     * 
     * @param teacherId
     * @return
     */
    PageVO<TeacherCourseVO> getCoursesByTeacherId(Long teacherId, int page, int size);

    /**
     * 根据学生ID获取课程列表
     * 
     * @param studentId
     * @return
     */
    PageVO<StudentCourseVO> getCoursesByStudentId(Long studentId, int page, int size);

    PageBean<CourseVO> getAllCourses(Integer page, Integer pageSize);

    // 更新课程信息
    CourseVO updateCourse(Long courseId, CreateCourseRequest request);

    // 删除课程
    void deleteCourse(Long courseId, Long teacherId);

    LearnPageVO getLearnPageData(Long courseId, Long studentId);

    /**
     * 获取课程的学生列表
     * @param courseId 课程ID
     * @return 学生列表
     */
    List<StudentCourseVO> getCourseStudents(Long courseId);

    /**
     * 邀请学生加入课程
     * @param courseId 课程ID
     * @param username 学生学号
     */
    void inviteStudent(Long courseId, String username);

    /**
     * 将学生从课程中移除
     * @param courseId 课程ID
     * @param studentId 学生ID
     */
    void removeStudent(Long courseId, Long studentId);
}