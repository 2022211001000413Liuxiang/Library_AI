import axios from 'axios'
import { Message } from 'element-ui'
import router from '../router'

const service = axios.create({
  baseURL: 'http://localhost:8081/api',
  timeout: 10000
})

// 请求拦截器->每次请求前都会执行这个函数
service.interceptors.request.use(
  config => {
    // 从 localStorage 获取 token
    const token = localStorage.getItem('token')
    if (token) {
      // 将 token 添加到请求头
      config.headers['Authorization'] = 'Bearer ' + token
    }
    return config
  },
  error => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器->每次收到响应后都会执行这个函数
service.interceptors.response.use(
  response => {
    const res = response.data
    if (res.success === false) {
      Message.error(res.message || '操作失败')
      return Promise.reject(new Error(res.message || '操作失败'))
    }
    return res
  },
  error => {
    // 处理 token 过期或未授权的情况
    if (error.response) {
      if (error.response.status === 401) {
        // 清除 localStorage 中的用户信息
        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')
        localStorage.removeItem('userType')
        localStorage.removeItem('userRole')
        localStorage.removeItem('permissions')
        Message.error('登录已过期，请重新登录')
        // 避免重复导航到当前路由
        if (router.currentRoute.path !== '/login') {
          router.push('/login')
        }
      } else {
        Message.error(error.response.data?.message || '网络错误')
      }
    } else {
      Message.error(error.message || '网络错误')
    }
    return Promise.reject(error)
  }
)

export default service
