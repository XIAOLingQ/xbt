package com.xbt.server.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CourseStudentMapper {

    /**
     * 根据课程ID删除所有学生选课记录
     * 
     * @param courseId
     */
    void deleteByCourseId(@Param("courseId") Long courseId);
}