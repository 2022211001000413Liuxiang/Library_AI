import request from './index'

// 获取个人信息
export function getProfile(userId) {
  return request({
    url: '/profile',
    method: 'get',
    params: { userId }
  })
}

// 更新个人信息
export function updateProfile(data) {
  return request({
    url: '/profile',
    method: 'put',
    data
  })
}

// 修改密码
export function changePassword(data) {
  return request({
    url: '/password',
    method: 'put',
    data
  })
}

// 上传头像到 OSS
export function uploadAvatar(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/avatar/upload',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 更新头像 URL
export function updateAvatar(userId, avatarUrl) {
  return request({
    url: '/avatar',
    method: 'put',
    data: { userId, avatarUrl }
  })
}
