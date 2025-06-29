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
import com.xbt.server.pojo.PageBean;
import java.util.Map;

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
     * 获取所有课程列表（用于启动页）
     */
    @GetMapping("/all")
    public Result<PageBean<CourseVO>> getAllCourses(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        PageBean<CourseVO> courses = courseService.getAllCourses(page, pageSize);
        return Result.success(courses);
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

    /**
     * 获取课程的学生列表
     */
    @GetMapping("/{courseId}/students")
    public Result getStudentList(@PathVariable Long courseId) {
        if (!AuthUtils.isTeacher()) {
            return Result.error(403, "无权限操作");
        }
        return Result.success(courseService.getCourseStudents(courseId));
    }

    /**
     * 通过学号邀请学生加入课程
     */
    @PostMapping("/{courseId}/invite")
    public Result inviteStudent(@PathVariable Long courseId, @RequestBody Map<String, String> request) {
        if (!AuthUtils.isTeacher()) {
            return Result.error(403, "无权限操作");
        }
        String username = request.get("username");
        courseService.inviteStudent(courseId, username);
        return Result.success();
    }

    /**
     * 将学生从课程中移除
     */
    @DeleteMapping("/{courseId}/students/{studentId}")
    public Result removeStudent(@PathVariable Long courseId, @PathVariable Long studentId) {
        if (!AuthUtils.isTeacher()) {
            return Result.error(403, "无权限操作");
        }
        courseService.removeStudent(courseId, studentId);
        return Result.success();
    }
}