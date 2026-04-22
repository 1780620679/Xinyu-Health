import request from '@/utils/request';


// 获取情绪日志分页列表
export const getEmotionalPageAPI = (params) => {
  return request({
    url: '/emotion-diary/admin/page',
    method: 'get',
    params
  });
}
// 删除情绪日志
export const deleteEmotionalAPI = (id) => {
  return request({
    url: `/emotion-diary/admin/${id}`,
    method: 'delete'
  });
}
