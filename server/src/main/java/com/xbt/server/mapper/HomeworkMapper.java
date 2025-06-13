package com.xbt.server.mapper;

import com.xbt.server.pojo.entity.Homework;
import com.xbt.server.pojo.vo.HomeworkStatusVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface HomeworkMapper {

    /**
     * 根据ID查询作业
     * 
     * @param id
     * @return
     */
    Homework findById(@Param("id") Long id);

    /**
     * 根据课程ID查询作业列表
     * 
     * @param courseId
     * @return
     */
    List<Homework> findByCourseId(@Param("courseId") Long courseId);

    /**
     * 插入作业
     * 
     * @param homework
     */
    void insert(Homework homework);

    /**
     * 更新作业
     * 
     * @param homework
     */
    void update(Homework homework);

    /**
     * 根据ID删除作业
     * 
     * @param id
     */
    void deleteById(@Param("id") Long id);

    /**
     * 根据课程ID删除作业
     * 
     * @param courseId
     */
    void deleteByCourseId(@Param("courseId") Long courseId);

    Homework selectById(@Param("id") Long id);

    /**
     * 根据课程ID和学生ID获取作业列表及状态
     * 
     * @param courseId
     * @param studentId
     * @return
     */
    List<HomeworkStatusVO> findHomeworksWithStatusByCourseId(@Param("courseId") Long courseId,
            @Param("studentId") Long studentId);

    /**
     * 统计一个学生的所有作业总数
     * 
     * @param studentId
     * @return
     */
    int countTotalHomeworksByStudentId(@Param("studentId") Long studentId);
}