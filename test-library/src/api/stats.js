import request from './index'

// 获取统计数据
export function getStats() {
  return request({
    url: '/stats',
    method: 'get'
  })
}
