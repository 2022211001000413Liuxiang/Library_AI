import request from './index'

// 获取借阅列表
export function getBorrows(params) {
  return request({
    url: '/borrows',
    method: 'get',
    params
  })
}

// 获取借阅详情
export function getBorrow(id) {
  return request({
    url: `/borrows/${id}`,
    method: 'get'
  })
}

// 借阅图书
export function borrowBook(data) {
  return request({
    url: '/borrows',
    method: 'post',
    data
  })
}

// 归还图书
export function returnBook(id) {
  return request({
    url: `/borrows/${id}/return`,
    method: 'put'
  })
}

// 获取当前用户的借阅记录
export function getMyBorrows(params) {
  return request({
    url: '/borrows/my',
    method: 'get',
    params
  })
}
