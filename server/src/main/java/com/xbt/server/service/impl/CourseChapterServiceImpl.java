package com.xbt.server.service.impl;

import com.xbt.server.mapper.CourseChapterMapper;
import com.xbt.server.pojo.entity.CourseVideo;
import com.xbt.server.pojo.vo.CourseChapterVO;
import com.xbt.server.service.CourseChapterService;
import com.xbt.server.service.QiniuService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CourseChapterServiceImpl implements CourseChapterService {

    @Resource
    private CourseChapterMapper courseChapterMapper;

    @Resource
    private QiniuService qiniuService;

    @Override
    public List<CourseChapterVO> getCourseChapterTree(Long courseId) {
        // 1. 获取课程的所有章节/课时
        List<CourseVideo> flatList = courseChapterMapper.findByCourseId(courseId);

        // 2. 手动转换为VO列表，以避免BeanUtils的潜在问题
        List<CourseChapterVO> voList = new java.util.ArrayList<>();
        for (CourseVideo item : flatList) {
            CourseChapterVO vo = new CourseChapterVO();
            vo.setId(item.getId());
            vo.setCourseId(item.getCourseId());
            vo.setParentId(item.getParentId()); // 直接赋值 Long
            vo.setType(item.getType());
            vo.setTitle(item.getTitle());
            vo.setUrl(item.getUrl());
            vo.setDuration(item.getDuration());
            vo.setOrderNum(item.getOrderNum());
            voList.add(vo);
        }

        // 3. 构建树形结构
        Map<Long, CourseChapterVO> map = voList.stream()
                .collect(Collectors.toMap(CourseChapterVO::getId, vo -> vo, (k1, k2) -> k1)); // 防止key重复

        List<CourseChapterVO> tree = new java.util.ArrayList<>();
        for (CourseChapterVO vo : voList) {
            Long parentId = vo.getParentId();
            if (parentId != null && parentId != 0) {
                CourseChapterVO parent = map.get(parentId);
                if (parent != null) {
                    if (parent.getChildren() == null) {
                        parent.setChildren(new java.util.ArrayList<>());
                    }
                    parent.getChildren().add(vo);
                } else {
                    // 如果找不到父节点，也将其视为根节点（容错处理）
                    tree.add(vo);
                }
            } else {
                // parentId为0或null的，是根节点
                tree.add(vo);
            }
        }

        return tree;
    }

    @Override
    public CourseChapterVO saveOrUpdateChapter(CourseChapterVO chapterVO) {
        CourseVideo courseVideo = new CourseVideo();
        BeanUtils.copyProperties(chapterVO, courseVideo);

        if (courseVideo.getId() == null) { // 新增
            courseChapterMapper.insert(courseVideo);
        } else { // 更新
            courseChapterMapper.update(courseVideo);
        }

        // 返回带有新ID的VO
        chapterVO.setId(courseVideo.getId());
        return chapterVO;
    }

    @Override
    @Transactional
    public void deleteChapter(Long chapterId) {
        // 删除子节点（如果是章）
        courseChapterMapper.deleteByParentId(chapterId);
        // 删除自身
        courseChapterMapper.deleteById(chapterId);
    }

    @Override
    @Transactional
    public void reorderChapters(List<CourseChapterVO> chapters) {
        for (CourseChapterVO chapter : chapters) {
            CourseVideo courseVideo = new CourseVideo();
            courseVideo.setId(chapter.getId());
            courseVideo.setOrderNum(chapter.getOrderNum());
            courseChapterMapper.update(courseVideo);
        }
    }

    @Override
    @Transactional
    public void markVideoAsCompleted(Long studentId, Long courseId, Long videoId) {
        // 1. 插入完成记录，如果已存在则忽略
        courseChapterMapper.markVideoAsCompleted(studentId, videoId);

        // 2. 重新计算该课程的进度
        int totalSections = courseChapterMapper.countTotalSectionsByCourse(courseId);
        int completedSections = courseChapterMapper.countCompletedSectionsByStudent(studentId, courseId);

        int progress = 0;
        if (totalSections > 0) {
            progress = (int) (((double) completedSections / totalSections) * 100);
        }

        // 3. 更新course_student表中的总进度
        courseChapterMapper.updateStudentProgress(studentId, courseId, progress);
    }

    @Override
    public String getUploadToken() {
        return qiniuService.getUploadToken();
    }
}