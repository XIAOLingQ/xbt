import request from '@/utils/request';

/**
 * 获取七牛云上传凭证
 */
export const getUploadToken = () => {
  return request({
    url: 'file/upload-token',
    method: 'get'
  });
}; 