package com.xbt.server.mapper;

import com.xbt.server.pojo.entity.CourseVideo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface CourseChapterMapper {

    /**
     * 根据课程ID查询所有章节
     * 
     * @param courseId
     * @return
     */
    List<CourseVideo> findByCourseId(@Param("courseId") Long courseId);

    /**
     * 插入章节
     * 
     * @param courseVideo
     */
    void insert(CourseVideo courseVideo);

    /**
     * 更新章节
     * 
     * @param courseVideo
     */
    void update(CourseVideo courseVideo);

    /**
     * 根据ID删除章节
     * 
     * @param id
     */
    void deleteById(@Param("id") Long id);

    /**
     * 根据父ID删除所有子章节
     * 
     * @param parentId
     */
    void deleteByParentId(@Param("parentId") Long parentId);

    void markVideoAsCompleted(@Param("studentId") Long studentId, @Param("videoId") Long videoId);

    int countTotalSectionsByCourse(@Param("courseId") Long courseId);

    int countCompletedSectionsByStudent(@Param("studentId") Long studentId, @Param("courseId") Long courseId);

    void updateStudentProgress(@Param("studentId") Long studentId, @Param("courseId") Long courseId,
            @Param("progress") int progress);
}