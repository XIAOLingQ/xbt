package com.xbt.server.controller;

import com.xbt.server.pojo.vo.CourseVO;
import com.xbt.server.pojo.vo.CreateCourseRequest;
import com.xbt.server.pojo.vo.JoinCourseRequest;
import com.xbt.server.pojo.vo.LearnPageVO;
import com.xbt.server.util.Result;
import com.xbt.server.service.CourseService;
import com.xbt.server.util.AuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/api/course")
public class CourseController {

    @Resource
    private CourseService courseService;

    @PostMapping("/create")
    public Result<CourseVO> createCourse(@RequestBody CreateCourseRequest request) {
        if (!AuthUtils.isTeacher()) {
            return Result.error(403, "无权限操作，仅教师可以创建课程");
        }
        Long teacherId = AuthUtils.getCurrentUserId();
        return Result.success(courseService.createCourse(teacherId, request));
    }

    @PostMapping("/join")
    public Result joinCourse(@RequestBody JoinCourseRequest joinCourseRequest) {
        Long studentId = AuthUtils.getCurrentUserId();
        courseService.joinCourse(studentId, joinCourseRequest.getCourseCode());
        return Result.success();
    }

    @GetMapping("/{courseId}")
    public Result<CourseVO> getCourseInfo(@PathVariable Long courseId) {
        return Result.success(courseService.getCourseInfo(courseId));
    }

    /**
     * 获取教师的课程列表
     */
    @GetMapping("/teacher")
    public Result getTeacherCourses(@RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        if (!AuthUtils.isTeacher()) {
            return Result.error(403, "无权限操作");
        }
        Long teacherId = AuthUtils.getCurrentUserId();
        return Result.success(courseService.getCoursesByTeacherId(teacherId, page, size));
    }

    /**
     * 获取学生的课程列表
     */
    @GetMapping("/student")
    public Result getStudentCourses(@RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        if (AuthUtils.isTeacher()) {
            return Result.error(403, "无权限操作，仅学生可以查看'我的课程'");
        }
        Long studentId = AuthUtils.getCurrentUserId();
        return Result.success(courseService.getCoursesByStudentId(studentId, page, size));
    }

    @PutMapping("/{courseId}")
    public Result<CourseVO> updateCourse(@PathVariable Long courseId, @RequestBody CreateCourseRequest request) {
        if (!AuthUtils.isTeacher()) {
            return Result.error(403, "无权限操作");
        }
        Long currentUserId = AuthUtils.getCurrentUserId();
        return Result.success(courseService.updateCourse(courseId, request));
    }

    @DeleteMapping("/{courseId}")
    public Result<Void> deleteCourse(@PathVariable Long courseId) {
        if (!AuthUtils.isTeacher()) {
            return Result.error(403, "无权限操作");
        }
        Long teacherId = AuthUtils.getCurrentUserId();
        courseService.deleteCourse(courseId, teacherId);
        return Result.success();
    }

    @GetMapping("/{courseId}/learn")
    public Result<LearnPageVO> getLearnPageData(@PathVariable Long courseId) {
        Long studentId = AuthUtils.getCurrentUserId();
        LearnPageVO learnPageData = courseService.getLearnPageData(courseId, studentId);
        return Result.success(learnPageData);
    }
}