package com.xbt.server.service.impl;

import com.xbt.server.exception.BusinessException;
import com.xbt.server.mapper.CourseMapper;
import com.xbt.server.mapper.UserMapper;
import com.xbt.server.pojo.entity.Course;
import com.xbt.server.pojo.entity.CourseStudent;
import com.xbt.server.pojo.entity.CourseVideo;
import com.xbt.server.pojo.entity.User;
import com.xbt.server.pojo.vo.CourseVO;
import com.xbt.server.pojo.vo.CreateCourseRequest;
import com.xbt.server.pojo.vo.StudentCourseVO;
import com.xbt.server.pojo.vo.TeacherCourseVO;
import com.xbt.server.pojo.vo.PageVO;
import com.xbt.server.pojo.vo.LearnPageVO;
import com.xbt.server.pojo.vo.ChapterVO;
import com.xbt.server.pojo.vo.SectionVO;
import com.xbt.server.service.CourseService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.xbt.server.exception.BizException;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.xbt.server.mapper.HomeworkSubmissionMapper;
import com.xbt.server.mapper.HomeworkMapper;
import com.xbt.server.mapper.StudentVideoProgressMapper;
import com.xbt.server.mapper.CourseVideoMapper;
import com.xbt.server.mapper.CourseStudentMapper;
import com.xbt.server.pojo.PageBean;

@Service
public class CourseServiceImpl implements CourseService {

    private static final Logger log = LoggerFactory.getLogger(CourseServiceImpl.class);

    @Autowired
    private CourseMapper courseMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private HomeworkSubmissionMapper homeworkSubmissionMapper;

    @Resource
    private HomeworkMapper homeworkMapper;

    @Resource
    private StudentVideoProgressMapper studentVideoProgressMapper;

    @Resource
    private CourseVideoMapper courseVideoMapper;

    @Resource
    private CourseStudentMapper courseStudentMapper;

    @Override
    @Transactional
    public CourseVO createCourse(Long teacherId, CreateCourseRequest request) {
        // 实际项目中应该从 SecurityContext 获取用户角色，这里暂时信任 AuthUtils
        // User teacher = userMapper.findById(teacherId);
        // if (teacher == null || teacher.getRole() != 2) {
        // throw new BusinessException("无权创建课程");
        // }

        // 创建课程
        Course course = new Course();
        BeanUtils.copyProperties(request, course);
        course.setTeacherId(teacherId);
        course.setCourseCode(generateCourseCode());
        course.setStatus(1); // 1-进行中
        // course.setCreatedAt(LocalDateTime.now()); // 由数据库自动生成
        // course.setUpdatedAt(LocalDateTime.now()); // 由数据库自动生成

        courseMapper.insert(course);

        // 为了返回teacherName，我们还是需要查询用户信息
        User teacher = userMapper.findById(teacherId);
        String teacherName = (teacher != null) ? teacher.getRealName() : "未知教师";

        return convertToCourseVO(course, teacherName);
    }

    @Transactional
    @Override
    public void joinCourse(Long studentId, String courseCode) {
        Course course = courseMapper.getByCourseCode(courseCode);
        if (course == null) {
            throw new BizException("课程码不存在或不正确");
        }

        // 检查学生是否已加入该课程
        CourseStudent existing = courseMapper.findStudentInCourse(course.getId(), studentId);
        if (existing != null) {
            throw new BizException("您已经加入该课程");
        }

        CourseStudent courseStudent = new CourseStudent();
        courseStudent.setCourseId(course.getId());
        courseStudent.setStudentId(studentId);
        // courseStudent.setJoinTime(LocalDateTime.now()); // 由数据库自动生成
        courseStudent.setStatus(1); // 1-正常
        // courseStudent.setCreatedAt(LocalDateTime.now()); // 由数据库自动生成
        // courseStudent.setUpdatedAt(LocalDateTime.now()); // 由数据库自动生成
        courseMapper.addStudentToCourse(courseStudent);
    }

    @Override
    public CourseVO getCourseInfo(Long courseId) {
        Course course = courseMapper.findById(courseId);
        if (course == null) {
            throw new BusinessException("课程不存在");
        }

        User teacher = userMapper.findById(course.getTeacherId());
        return convertToCourseVO(course, teacher.getRealName());
    }

    @Override
    public PageVO<TeacherCourseVO> getCoursesByTeacherId(Long teacherId, int page, int size) {
        PageHelper.startPage(page, size);
        List<TeacherCourseVO> list = courseMapper.getCoursesByTeacherId(teacherId);
        PageInfo<TeacherCourseVO> pageInfo = new PageInfo<>(list);
        return new PageVO<>(pageInfo.getTotal(), pageInfo.getList());
    }

    @Override
    public PageVO<StudentCourseVO> getCoursesByStudentId(Long studentId, int page, int size) {
        PageHelper.startPage(page, size);
        List<StudentCourseVO> list = courseMapper.getCoursesByStudentId(studentId);
        PageInfo<StudentCourseVO> pageInfo = new PageInfo<>(list);
        return new PageVO<>(pageInfo.getTotal(), pageInfo.getList());
    }

    @Override
    public PageBean<CourseVO> getAllCourses(Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        List<Course> courseList = courseMapper.findAll();
        PageInfo<Course> pageInfo = new PageInfo<>(courseList);

        List<CourseVO> courseVOList = pageInfo.getList().stream().map(course -> {
            User teacher = userMapper.findById(course.getTeacherId());
            String teacherName = (teacher != null) ? teacher.getRealName() : "未知教师";
            return convertToCourseVO(course, teacherName);
        }).collect(Collectors.toList());

        return new PageBean<>(pageInfo.getTotal(), courseVOList);
    }

    @Override
    @Transactional
    public CourseVO updateCourse(Long courseId, CreateCourseRequest request) {
        Course course = courseMapper.findById(courseId);
        if (course == null) {
            throw new BusinessException("课程不存在");
        }

        BeanUtils.copyProperties(request, course);
        // course.setUpdatedAt(LocalDateTime.now()); // 由数据库自动更新
        courseMapper.update(course);

        // 修复：增加对teacher是否为null的判断
        User teacher = userMapper.findById(course.getTeacherId());
        String teacherName = (teacher != null) ? teacher.getRealName() : "未知教师";

        return convertToCourseVO(course, teacherName);
    }

    @Override
    @Transactional
    public void deleteCourse(Long courseId, Long teacherId) {
        Course course = courseMapper.findById(courseId);
        if (course == null) {
            // 如果课程不存在，可以直接返回，或者抛出异常，这里选择静默处理
            return;
        }

        // 权限校验：确保只有课程的创建者才能删除
        if (!course.getTeacherId().equals(teacherId)) {
            throw new BusinessException("无权删除该课程");
        }

        // 1. 删除作业提交
        homeworkSubmissionMapper.deleteByCourseId(courseId);

        // 2. 删除作业
        homeworkMapper.deleteByCourseId(courseId);

        // 3. 删除学生观看进度
        studentVideoProgressMapper.deleteByCourseId(courseId);

        // 4. 删除课程视频和章节
        courseVideoMapper.deleteByCourseId(courseId);

        // 5. 删除学生选课记录
        courseStudentMapper.deleteByCourseId(courseId);

        // 6. 最后删除课程本身
        courseMapper.deleteById(courseId);
    }

    @Override
    public LearnPageVO getLearnPageData(Long courseId, Long studentId) {
        Course course = courseMapper.findById(courseId);
        if (course == null) {
            throw new BusinessException("课程不存在");
        }

        // 1. 获取课程所有章节和课时
        List<CourseVideo> allItems = courseMapper.getCourseChaptersAndSections(courseId);
        log.info("【Debug】获取到的所有课程项 ({}条): {}", allItems.size(), allItems);

        // 2. 获取学生已完成的视频ID
        Set<Long> completedVideoIds = courseMapper.getCompletedVideoIdsByStudent(studentId, courseId);

        // 3. 组装数据
        LearnPageVO learnPageVO = new LearnPageVO();
        BeanUtils.copyProperties(course, learnPageVO);

        List<ChapterVO> chapterVOs = allItems.stream()
                .filter(item -> item.getType() == 1) // 筛选出章
                .map(chapterEntity -> {
                    log.info("【Debug】正在处理章: id={}, title={}", chapterEntity.getId(), chapterEntity.getTitle());

                    ChapterVO chapterVO = new ChapterVO();
                    // 手动赋值，避免BeanUtils可能带来的问题
                    chapterVO.setId(chapterEntity.getId());
                    chapterVO.setTitle(chapterEntity.getTitle());
                    chapterVO.setOrderNum(chapterEntity.getOrderNum());

                    List<SectionVO> sectionVOs = allItems.stream()
                            .filter(sectionEntity -> {
                                boolean isSection = sectionEntity.getType() == 2;
                                boolean parentMatch = chapterEntity.getId().equals(sectionEntity.getParentId());
                                if (isSection) {
                                    log.info("【Debug】检查节: id={}, parentId={}, 所属章id={}, 是否匹配={}",
                                            sectionEntity.getId(), sectionEntity.getParentId(), chapterEntity.getId(),
                                            parentMatch);
                                }
                                return isSection && parentMatch;
                            }) // 筛选出本章下的节
                            .map(sectionEntity -> {
                                log.info("【Debug】成功匹配并添加节: id={}", sectionEntity.getId());
                                SectionVO sectionVO = new SectionVO();
                                // 手动赋值
                                sectionVO.setId(sectionEntity.getId());
                                sectionVO.setTitle(sectionEntity.getTitle());
                                sectionVO.setUrl(sectionEntity.getUrl());
                                sectionVO.setOrderNum(sectionEntity.getOrderNum());
                                sectionVO.setCompleted(completedVideoIds.contains(sectionEntity.getId()));
                                return sectionVO;
                            })
                            .collect(Collectors.toList());
                    chapterVO.setSections(sectionVOs);
                    // TODO: 作业状态获取逻辑
                    // chapterVO.setHomeworks(...);

                    return chapterVO;
                })
                .collect(Collectors.toList());

        learnPageVO.setChapters(chapterVOs);

        return learnPageVO;
    }

    private String generateCourseCode() {
        // 生成6位随机课程码
        return UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }

    private CourseVO convertToCourseVO(Course course, String teacherName) {
        CourseVO vo = new CourseVO();
        BeanUtils.copyProperties(course, vo);
        vo.setTeacherName(teacherName);
        return vo;
    }
}