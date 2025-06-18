import request from '@/utils/request';

/**
 * 获取指定课程的聊天历史记录
 * @param {number} courseId
 */
export const getChatHistory = (courseId) => {
  return request({
    url: `/chat/${courseId}/history`,
    method: 'get'
  });
}; 