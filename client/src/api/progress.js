import request from '@/utils/request';

/**
 * 更新视频学习进度
 * @param {object} data 包含 videoId 和 watchDuration
 * @returns 
 */
export const updateVideoProgress = (data) => {
  return request({
    url: '/progress/video',
    method: 'post',
    data
  });
}; 