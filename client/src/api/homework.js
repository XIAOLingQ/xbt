import request from '@/utils/request';

/**
 * 获取指定课程的作业列表
 * @param {number} courseId - 课程ID
 */
export const getHomeworkList = (courseId) => {
  return request({
    url: `/homework/list/${courseId}`,
    method: 'get'
  });
};

/**
 * 获取单个作业的详细信息
 * @param {number} homeworkId - 作业ID
 */
export const getHomeworkDetail = (homeworkId) => {
  return request({
    url: `/homework/${homeworkId}`,
    method: 'get'
  });
};

/**
 * 保存或更新一个作业
 * @param {object} data - 作业数据 (Homework entity)
 */
export const saveHomework = (data) => {
  return request({
    url: '/homework/save',
    method: 'post',
    data
  });
};

/**
 * 删除一个作业
 * @param {number} homeworkId - 作业ID
 */
export const deleteHomework = (homeworkId) => {
  return request({
    url: `/homework/${homeworkId}`,
    method: 'delete'
  });
};

/**
 * 学生提交作业
 * @param {object} data - { homeworkId, answers }
 */
export const submitHomework = (data) => {
  return request({
    url: '/homework/submit',
    method: 'post',
    data
  });
};

/**
 * 获取作业提交列表
 * @param {number} homeworkId - 作业ID
 */
export const getHomeworkSubmissions = (homeworkId) => {
  return request({
    url: `/homework/${homeworkId}/submissions`,
    method: 'get'
  });
};

/**
 * 获取作业提交详情
 * @param {number} submissionId - 提交ID
 */
export const getSubmissionDetail = (submissionId) => {
  return request({
    url: `/homework/submission/${submissionId}`,
    method: 'get'
  });
};

/**
 * 提交批改结果
 * @param {number} submissionId - 提交ID
 * @param {object} data - 批改数据
 */
export const gradeSubmission = (submissionId, data) => {
  return request({
    url: `/homework/submission/${submissionId}/grade`,
    method: 'post',
    data
  });
}; 