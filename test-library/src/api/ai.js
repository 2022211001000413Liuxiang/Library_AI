import request from './index'

// 智能推荐图书
export function recommendBooks(preference) {
  return request({
    url: '/ai/recommend',
    method: 'post',
    data: { preference }
  })
}

// 图书咨询
export function chatWithAI(question) {
  return request({
    url: '/ai/chat',
    method: 'post',
    data: { question }
  })
}
