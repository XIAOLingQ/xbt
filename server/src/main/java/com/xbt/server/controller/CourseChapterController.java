package com.xbt.server.controller;

import com.xbt.server.pojo.vo.CourseChapterVO;
import com.xbt.server.service.CourseChapterService;
import com.xbt.server.service.QiniuService;
import com.xbt.server.util.AuthUtils;
import com.xbt.server.util.Result;
import org.springframework.web.bind.annotation.*;
import com.xbt.server.pojo.dto.MarkCompletedRequest;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/course-chapter")
public class CourseChapterController {

    @Resource
    private CourseChapterService courseChapterService;

    @Resource
    private QiniuService qiniuService;

    /**
     * 获取课程目录树
     */
    @GetMapping("/tree/{courseId}")
    public Result<List<CourseChapterVO>> getChapterTree(@PathVariable Long courseId) {
        // 此处可以加入权限校验，判断当前用户是否有权查看该课程目录
        return Result.success(courseChapterService.getCourseChapterTree(courseId));
    }

    /**
     * 新增或更新章节
     */
    @PostMapping("/save")
    public Result<CourseChapterVO> saveOrUpdateChapter(@RequestBody CourseChapterVO chapterVO) {
        if (!AuthUtils.isTeacher()) {
            return Result.error(403, "无权限操作");
        }
        // 此处应有更详细的权限校验，比如检查该教师是否是该课程的创建者

        CourseChapterVO result = courseChapterService.saveOrUpdateChapter(chapterVO);
        return Result.success(result);
    }

    /**
     * 删除章节
     */
    @DeleteMapping("/{chapterId}")
    public Result<Void> deleteChapter(@PathVariable Long chapterId) {
        if (!AuthUtils.isTeacher()) {
            return Result.error(403, "无权限操作");
        }
        courseChapterService.deleteChapter(chapterId);
        return Result.success();
    }

    /**
     * 批量更新章节顺序
     */
    @PostMapping("/reorder")
    public Result<Void> reorderChapters(@RequestBody List<CourseChapterVO> chapters) {
        if (!AuthUtils.isTeacher()) {
            return Result.error(403, "无权限操作");
        }
        // 此处应有更详细的权限校验

        courseChapterService.reorderChapters(chapters);
        return Result.success();
    }

    /**
     * 获取视频上传凭证
     */
    @GetMapping("/upload-token")
    public Result<String> getUploadToken() {
        String token = qiniuService.getUploadToken();
        return Result.success(token);
    }

    @PostMapping("/mark-completed")
    public Result markVideoAsCompleted(@RequestBody MarkCompletedRequest request) {
        Long studentId = AuthUtils.getCurrentUserId();
        courseChapterService.markVideoAsCompleted(studentId, request.getCourseId(), request.getVideoId());
        return Result.success();
    }

//    @GetMapping("/{courseId}")
//    public Result<List<CourseChapterVO>> getCourseChapterTree(@PathVariable Long courseId) {
//        List<CourseChapterVO> chapterTree = courseChapterService.getCourseChapterTree(courseId);
//        return Result.success(chapterTree);
//    }
}