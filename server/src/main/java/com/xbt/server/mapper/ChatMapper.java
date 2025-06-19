package com.xbt.server.mapper;

import com.xbt.server.pojo.dto.ChatMessageDTO;
import com.xbt.server.pojo.entity.ChatMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ChatMapper {
    /**
     * 插入一条新的聊天记录
     * 
     * @param chatMessage
     */
    void insert(ChatMessage chatMessage);

    /**
     * 根据课程ID查询聊天记录
     * 
     * @param courseId
     * @return
     */
    List<ChatMessageDTO> findByCourseId(@Param("courseId") Long courseId);
}