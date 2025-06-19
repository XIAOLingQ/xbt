package com.xbt.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xbt.server.pojo.entity.AiQuestion;
import com.xbt.server.pojo.entity.AiSubmission;
import com.xbt.server.pojo.vo.AiQuestionHistoryVO;
import com.xbt.server.pojo.vo.AiTopicHistoryVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AiMapper extends BaseMapper<AiQuestion> {

    /**
     * 插入AI生成的问题
     * 
     * @param aiQuestion 问题对象
     * @return 影响行数
     */
    int insertAiQuestion(AiQuestion aiQuestion);

    /**
     * 插入学生的答案提交
     * 
     * @param aiSubmission 提交对象
     * @return 影响行数
     */
    int insertAiSubmission(AiSubmission aiSubmission);

    /**
     * 根据学生ID查询其AI题目的历史记录
     *
     * @param studentId 学生ID
     * @param batchId   批次ID
     * @return 题目历史列表
     */
    List<AiQuestionHistoryVO> selectHistoryByStudentId(@Param("studentId") Long studentId,
            @Param("batchId") String batchId);

    /**
     * 根据学生ID查询其AI练习的主题历史
     * 
     * @param studentId 学生ID
     * @return 主题历史列表
     */
    List<AiTopicHistoryVO> selectTopicHistoryByStudentId(@Param("studentId") Long studentId);

    /**
     * 根据题目ID查询题目详情
     * 
     * @param id
     * @return
     */
    AiQuestion selectQuestionById(Integer id);

    /**
     * 更新题目的学生答案和批改结果
     * 
     * @param question
     */
    void updateQuestionAnswer(AiQuestion question);

    /**
     * 更新主题历史的完成题目数
     * 
     * @param batchId
     * @param studentId
     * @param completedCount
     */
    void updateTopicHistoryCompletedCount(@Param("batchId") String batchId, @Param("studentId") Long studentId,
            @Param("count") int completedCount);
}
