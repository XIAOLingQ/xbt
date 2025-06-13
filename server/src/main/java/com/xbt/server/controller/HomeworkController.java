package com.xbt.server.controller;

import com.xbt.server.pojo.dto.HomeworkDetailDTO;
import com.xbt.server.pojo.dto.SaveHomeworkRequest;
import com.xbt.server.pojo.dto.SubmitHomeworkRequest;
import com.xbt.server.pojo.entity.Homework;
import com.xbt.server.pojo.vo.HomeworkStatusVO;
import com.xbt.server.service.HomeworkService;
import com.xbt.server.util.AuthUtils;
import com.xbt.server.util.Result;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/homework")
public class HomeworkController {

    @Resource
    private HomeworkService homeworkService;

    @GetMapping("/list/{courseId}")
    public Result<List<HomeworkStatusVO>> getHomeworkList(@PathVariable Long courseId) {
        Long userId = AuthUtils.getCurrentUserId();
        List<HomeworkStatusVO> list = homeworkService.getHomeworkListByCourseId(courseId, userId);
        return Result.success(list);
    }

    @PostMapping("/save")
    public Result<Homework> saveHomework(@RequestBody SaveHomeworkRequest request) {
        if (!AuthUtils.isTeacher()) {
            return Result.error(403, "无权限操作");
        }
        Homework savedHomework = homeworkService.saveHomework(request);
        return Result.success(savedHomework);
    }

    @DeleteMapping("/{homeworkId}")
    public Result<?> deleteHomework(@PathVariable Long homeworkId) {
        if (!AuthUtils.isTeacher()) {
            return Result.error(403, "无权限操作");
        }
        homeworkService.deleteHomework(homeworkId);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<HomeworkDetailDTO> getHomeworkDetail(@PathVariable Long id) {
        Long studentId = AuthUtils.getCurrentUserId();
        HomeworkDetailDTO homeworkDetail = homeworkService.getHomeworkDetail(id, studentId);
        if (homeworkDetail == null) {
            return Result.error(404, "作业未找到");
        }
        return Result.success(homeworkDetail);
    }

    @PostMapping("/submit")
    public Result<?> submitHomework(@RequestBody SubmitHomeworkRequest request) {
        // 学生权限在Service层或拦截器中校验更佳，这里简化处理
        Long studentId = AuthUtils.getCurrentUserId();
        homeworkService.submitHomework(studentId, request);
        return Result.success();
    }
}