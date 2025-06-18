package com.xbt.server.mapper;

import com.xbt.server.pojo.entity.Course;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CourseStudentMapper {

    /**
     * 根据课程ID删除所有学生选课记录
     * 
     * @param courseId
     */
    void deleteByCourseId(@Param("courseId") Long courseId);

    @Select("SELECT count(*) FROM course_student WHERE student_id = #{studentId}")
    Integer countTotalCoursesByStudentId(Long studentId);

    @Select("SELECT count(*) FROM course_student WHERE student_id = #{studentId} AND progress = 100")
    Integer countCompletedCoursesByStudentId(Long studentId);

    @Select("SELECT course_id FROM course_student WHERE student_id = #{studentId}")
    List<Long> getCourseIdsByStudentId(Long studentId);
}