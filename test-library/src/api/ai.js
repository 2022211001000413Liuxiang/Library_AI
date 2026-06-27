import request from './index'

// 创建新会话
export function createSession() {
  return request({
    url: '/ai/session',
    method: 'post'
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
export function getUserSessions() {
  return request({
    url: '/ai/sessions',
    method: 'get'
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
export function recommendBooks(preference, sessionId) {
  return request({
    url: '/ai/recommend',
    method: 'post',
    data: { preference, sessionId }
  })
}

// 图书咨询
export function chatWithAI(question, sessionId) {
  return request({
    url: '/ai/chat',
    method: 'post',
    data: { question, sessionId }
  })
}

// 图书摘要
export function summarizeBook(bookName, sessionId) {
  return request({
    url: '/ai/summarize',
    method: 'post',
    data: { bookName, sessionId }
  })
}

// 相似图书推荐
export function recommendSimilar(bookName, sessionId) {
  return request({
    url: '/ai/similar',
    method: 'post',
    data: { bookName, sessionId }
  })
}
