import request from '@/utils/request';

/**
 * 获取课程的目录树
 * @param {number} courseId
 */
export const getCourseChapterTree = (courseId) => {
  return request({
    url: `/course-chapter/tree/${courseId}`,
    method: 'get'
  });
};

/**
 * 保存或更新一个章节/课时
 * @param {object} data - CourseChapterVO
 */
export const saveOrUpdateChapter = (data) => {
  return request({
    url: '/course-chapter/save',
    method: 'post',
    data
  });
};

/**
 * 删除一个章节/课时
 * @param {number} chapterId
 */
export const deleteChapter = (chapterId) => {
  return request({
    url: `/course-chapter/${chapterId}`,
    method: 'delete'
  });
};

/**
 * 获取七牛云上传凭证
 */
export const getUploadToken = () => {
  return request({
    url: '/course-chapter/upload-token',
    method: 'get'
  });
};

/**
 * 批量更新章节的顺序
 * @param {Array<object>} data - [{id, orderNum}, ...]
 */
export const reorderChapters = (data) => {
  return request({
    url: '/course-chapter/reorder',
    method: 'post',
    data
  });
};

/**
 * 标记视频课时为已完成
 * @param {number} courseId 课程ID
 * @param {number} videoId 视频ID
 */
export const markVideoAsCompleted = (courseId, videoId) => {
  return request({
    url: `/course-chapter/mark-completed`,
    method: 'post',
    data: { courseId, videoId }
  });
}; 