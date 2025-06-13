package com.xbt.server.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.xbt.server.pojo.entity.HomeworkSubmission;

@Mapper
public interface HomeworkSubmissionMapper {

    /**
     * 插入或更新作业提交记录
     * 
     * @param submission
     */
    void upsert(HomeworkSubmission submission);

    /**
     * 根据作业ID删除所有相关的提交记录
     * 
     * @param homeworkId 作业ID
     * @return 删除的行数
     */
    int deleteByHomeworkId(@Param("homeworkId") Long homeworkId);

    /**
     * 根据课程ID删除所有相关的提交记录
     * 
     * @param courseId 课程ID
     * @return 删除的行数
     */
    int deleteByCourseId(@Param("courseId") Long courseId);

    /**
     * 统计学生已提交的作业数
     * 
     * @param studentId
     * @return
     */
    int countSubmittedByStudentId(@Param("studentId") Long studentId);

    /**
     * 计算学生的作业平均分
     * 
     * @param studentId
     * @return
     */
    Double getAverageScoreByStudentId(@Param("studentId") Long studentId);

    /**
     * 根据作业ID和学生ID查找提交记录
     * 
     * @param homeworkId
     * @param studentId
     * @return
     */
    HomeworkSubmission findByHomeworkAndStudent(@Param("homeworkId") Long homeworkId,
            @Param("studentId") Long studentId);

}