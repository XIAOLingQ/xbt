import request from '@/utils/request';

// 模拟课程相关的API请求
// 在真实应用中，这里会使用axios等库向后端发送网络请求

/**
 * 获取学生所学课程列表
 * @param {object} params - 分页参数 { page, size }
 */
export const getStudentCourses = (params) => {
  return request({
    url: '/course/student',
    method: 'get',
  });
}

/**
 * 获取教师所教课程列表
 * @param {object} params - 分页参数 { page, size }
 */
export const getTeacherCourses = (params) => {
  return request({
    url: '/course/teacher',
    method: 'get',
    params
  });
}

/**
 * 教师创建新课程
 * @param {object} data - { title, description, coverImage }
 * @returns 
 */
export const createCourse = (data) => {
  return request({
    url: '/course/create',
    method: 'post',
    data
  });
};

/**
 * 学生使用课程码加入课程
 * @param {object} data - { courseCode }
 */
export const joinCourse = (data) => {
  return request({
    url: '/course/join',
    method: 'post',
    data
  });
}

/**
 * 更新课程信息
 * @param {object} data - 课程信息对象，必须包含id
 */
export const updateCourse = (data) => {
  // 从课程对象中提取后端需要的字段
  const { id, title, description, coverImage } = data;
  return request({
    url: `/course/${id}`,
    method: 'put',
    data: { title, description, coverImage }
  });
};

/**
 * 获取学生学习页面的所有数据
 * @param {number} courseId - 课程ID
 */
export const getLearnPageData = (courseId) => {
  return request({
    url: `/course/${courseId}/learn`,
    method: 'get'
  });
};

/**
 * 获取课程的学生列表
 * @param {number} courseId - 课程ID
 */
export const getCourseStudents = (courseId) => {
  return request({
    url: `/course/${courseId}/students`,
    method: 'get'
  });
};

/**
 * 邀请学生加入课程
 * @param {number} courseId - 课程ID
 * @param {string} username - 学生学号
 */
export const inviteStudent = (courseId, username) => {
  return request({
    url: `/course/${courseId}/invite`,
    method: 'post',
    data: { username }
  });
};

/**
 * 从课程中移除学生
 * @param {number} courseId - 课程ID
 * @param {number} studentId - 学生ID
 */
export const removeStudent = (courseId, studentId) => {
  return request({
    url: `/course/${courseId}/students/${studentId}`,
    method: 'delete'
  });
};

// 删除课程
export function deleteCourse(courseId) {
  return request({
    url: `/course/${courseId}`,
    method: 'delete'
  })
}

export function getCourseList(params) {
  return request({
    url: '/course/all',
    method: 'get',
    params
  })
}