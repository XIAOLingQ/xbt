package com.xbt.server.mapper;

import com.xbt.server.pojo.dto.UpdateVideoProgressDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface StudentVideoProgressMapper {

        /**
         * 获取一个学生所有视频的总观看时长（秒）
         * 
         * @param studentId 学生ID
         * @return 总观看时长（秒）
         */
        Long sumTotalWatchTimeByStudentId(@Param("studentId") Long studentId);

        /**
         * 获取学生在特定课程下的总观看时长（秒）
         * 
         * @param studentId 学生ID
         * @param courseId  课程ID
         * @return 观看时长（秒）
         */
        Long sumWatchTimeByStudentAndCourse(@Param("studentId") Long studentId, @Param("courseId") Long courseId);

        /**
         * 插入或更新学生的视频观看进度
         * 
         * @param progressDTO
         */
        void upsertProgress(UpdateVideoProgressDTO progressDTO);

        /**
         * 根据课程ID删除所有相关的观看进度
         * 
         * @param courseId 课程ID
         */
        void deleteByCourseId(@Param("courseId") Long courseId);

        @Select("SELECT DATE(updated_at) as date, SUM(watch_duration) / 60.0 as duration " +
                        "FROM student_video_progress " +
                        "WHERE student_id = #{studentId} AND updated_at >= #{startDate} " +
                        "GROUP BY DATE(updated_at) " +
                        "ORDER BY date ASC")
        List<Map<String, Object>> getStudyTrendByStudentId(@Param("studentId") Long studentId,
                        @Param("startDate") String startDate);

}