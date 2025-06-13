package com.xbt.server.service;

import com.xbt.server.pojo.vo.CourseChapterVO;
import java.util.List;
import java.util.Map;

public interface CourseChapterService {

    /**
     * 获取课程的完整目录树
     * 
     * @param courseId 课程ID
     * @return
     */
    List<CourseChapterVO> getCourseChapterTree(Long courseId);

    /**
     * 保存或更新章节信息
     * 
     * @param chapterVO
     * @return
     */
    CourseChapterVO saveOrUpdateChapter(CourseChapterVO chapterVO);

    /**
     * 删除章节
     * 
     * @param chapterId
     */
    void deleteChapter(Long chapterId);

    /**
     * 批量更新章节顺序
     * 
     * @param chapters
     */
    void reorderChapters(List<CourseChapterVO> chapters);

    String getUploadToken();

    void markVideoAsCompleted(Long studentId, Long courseId, Long videoId);
}