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

/**
 * 获取当前登录学生的学习报告
 * @returns {Promise<axios.AxiosResponse<any>>}
 */
export const getMyReport = () => {
    return request.get('/reports/student/me');
}; 