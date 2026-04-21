import request from './index'

// 创建新会话
export function createSession(userId) {
  return request({
    url: '/ai/session',
    method: 'post',
    data: { userId }
  })
}

// 获取会话信息
export function getSession(sessionId) {
  return request({
    url: `/ai/session/${sessionId}`,
    method: 'get'
  })
}

// 获取历史消息
export function getHistory(sessionId) {
  return request({
    url: `/ai/history/${sessionId}`,
    method: 'get'
  })
}

// 获取用户的所有会话列表
export function getUserSessions(userId) {
  return request({
    url: '/ai/sessions',
    method: 'get',
    params: { userId }
  })
}

// 结束会话
export function endSession(sessionId) {
  return request({
    url: '/ai/session/end',
    method: 'post',
    data: { sessionId }
  })
}

// 智能推荐图书
export function recommendBooks(preference, userId, sessionId) {
  return request({
    url: '/ai/recommend',
    method: 'post',
    data: { preference, userId, sessionId }
  })
}

// 图书咨询
export function chatWithAI(question, userId, sessionId) {
  return request({
    url: '/ai/chat',
    method: 'post',
    data: { question, userId, sessionId }
  })
}
