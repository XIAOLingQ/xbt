import request from '@/utils/request';

/**
 * 获取当前登录学生的学习报告摘要
 */
export const getMyReportSummary = () => {
  return request({
    url: '/report/student',
    method: 'get'
  });
}; 