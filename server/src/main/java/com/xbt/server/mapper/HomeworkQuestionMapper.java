package com.xbt.server.mapper;

import com.xbt.server.pojo.entity.HomeworkQuestion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface HomeworkQuestionMapper {
    /**
     * 批量插入问题
     * 
     * @param questions
     */
    void batchInsert(@Param("questions") List<HomeworkQuestion> questions);

    /**
     * 根据作业ID查询所有问题
     * 
     * @param homeworkId 作业ID
     * @return 问题列表
     */
    List<HomeworkQuestion> selectByHomeworkId(@Param("homeworkId") Long homeworkId);

    /**
     * 根据作业ID删除所有问题
     * 
     * @param homeworkId 作业ID
     * @return 删除的行数
     */
    int deleteByHomeworkId(@Param("homeworkId") Long homeworkId);
}