package com.xbt.server.mapper;

import com.xbt.server.pojo.entity.Course;
import com.xbt.server.pojo.entity.CourseStudent;
import com.xbt.server.pojo.entity.CourseVideo;
import com.xbt.server.pojo.vo.StudentCourseVO;
import com.xbt.server.pojo.vo.TeacherCourseVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Set;

@Mapper
public interface CourseMapper {
    // 插入课程
    void insert(Course course);

    List<Course> findAll();

    // 根据ID查询课程
    Course findById(@Param("id") Long id);

    // 根据课程码查询课程
    Course findByCourseCode(@Param("courseCode") String courseCode);

    // 查询教师的所有课程
    List<Course> findByTeacherId(@Param("teacherId") Long teacherId);

    // 查询学生加入的所有课程
    List<Course> findByStudentId(@Param("studentId") Long studentId);

    // 更新课程信息
    void update(Course course);

    // 删除课程
    void deleteById(@Param("id") Long id);

    /**
     * 根据课程码查询课程
     * 
     * @param courseCode
     * @return
     */
    Course getByCourseCode(String courseCode);

    /**
     * 学生加入课程
     * 
     * @param courseStudent
     */
    void addStudentToCourse(CourseStudent courseStudent);

    /**
     * 根据教师ID获取课程列表
     * 
     * @param teacherId
     * @return
     */
    List<TeacherCourseVO> getCoursesByTeacherId(@Param("teacherId") Long teacherId);

    /**
     * 根据学生ID获取课程列表
     * 
     * @param studentId
     * @return
     */
    List<StudentCourseVO> getCoursesByStudentId(@Param("studentId") Long studentId);

    /**
     * 查询学生加入的课程总数
     * 
     * @param studentId
     * @return
     */
    int countCoursesByStudentId(@Param("studentId") Long studentId);

    /**
     * 查询学生是否已加入某课程
     * 
     * @param courseId  课程ID
     * @param studentId 学生ID
     * @return
     */
    CourseStudent findStudentInCourse(@Param("courseId") Long courseId, @Param("studentId") Long studentId);

    List<CourseVideo> getCourseChaptersAndSections(@Param("courseId") Long courseId);

    Set<Long> getCompletedVideoIdsByStudent(@Param("studentId") Long studentId, @Param("courseId") Long courseId);

    /**
     * 获取课程的学生列表
     * @param courseId 课程ID
     * @return 学生列表
     */
    List<StudentCourseVO> getCourseStudents(@Param("courseId") Long courseId);
}