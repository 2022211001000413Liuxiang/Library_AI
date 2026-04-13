import request from './index'

// 获取公告列表（分页）
export function getAnnouncements(params) {
  return request({
    url: '/announcements',
    method: 'get',
    params
  })
}

// 获取首页公告展示
export function getHomeAnnouncements(limit = 5) {
  return request({
    url: '/announcements/home',
    method: 'get',
    params: { limit }
  })
}

// 获取公告详情
export function getAnnouncement(id) {
  return request({
    url: `/announcements/${id}`,
    method: 'get'
  })
}

// 创建公告
export function addAnnouncement(data) {
  return request({
    url: '/announcements',
    method: 'post',
    data
  })
}

// 更新公告
export function updateAnnouncement(id, data) {
  return request({
    url: `/announcements/${id}`,
    method: 'put',
    data
  })
}

// 删除公告
export function deleteAnnouncement(id) {
  return request({
    url: `/announcements/${id}`,
    method: 'delete'
  })
}
