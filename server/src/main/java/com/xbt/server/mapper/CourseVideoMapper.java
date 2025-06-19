package com.xbt.server.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CourseVideoMapper {

    /**
     * 获取一门课程所有视频的总时长（秒）
     * 
     * @param courseId 课程ID
     * @return 视频总时长（秒）
     */
    Long sumTotalDurationByCourseId(@Param("courseId") Long courseId);

    /**
     * 根据课程ID删除所有视频和章节
     * 
     * @param courseId
     */
    void deleteByCourseId(@Param("courseId") Long courseId);

}