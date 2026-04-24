import Vue from 'vue'
import VueRouter from 'vue-router'
import Login from '@/components/Login.vue'
import Home from '@/components/Home.vue'
import Books from '@/components/Books.vue'
import Borrow from '@/components/Borrow.vue'
import ReaderBorrow from '@/components/ReaderBorrow.vue'
import Users from '@/components/Users.vue'
import Profile from '@/components/Profile.vue'
import AiRobot from '@/components/AiRobot.vue'
import Announcements from '@/components/Announcements.vue'

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/home',
    name: 'Home',
    component: Home,
    meta: { requiresAuth: true }
  },
  {
    path: '/books',
    name: 'Books',
    component: Books,
    meta: { requiresAuth: true, requiresLibrarian: true }
  },
  {
    path: '/borrow',
    name: 'Borrow',
    component: Borrow,
    meta: { requiresAuth: true, requiresLibrarian: true }
  },
  {
    path: '/my-borrow',
    name: 'MyBorrow',
    component: ReaderBorrow,
    meta: { requiresAuth: true, requiresReader: true }
  },
  {
    path: '/users',
    name: 'Users',
    component: Users,
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/profile',
    name: 'Profile',
    component: Profile,
    meta: { requiresAuth: true }
  },
  {
    path: '/ai-robot',
    name: 'AiRobot',
    component: AiRobot,
    meta: { requiresAuth: true }
  },
  {
    path: '/announcements',
    name: 'Announcements',
    component: Announcements,
    meta: { requiresAuth: true, requiresAdmin: true }
  }
]

const router = new VueRouter({
  mode: 'hash',
  base: process.env.BASE_URL,
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const userInfo = localStorage.getItem('userInfo')
  const userType = localStorage.getItem('userType')
  const userRole = localStorage.getItem('userRole')

  // 访问根路径重定向到登录页
  if (to.path === '/') {
    next('/login')
    return
  }

  if (to.meta.requiresAuth) {
    if (!userInfo) {
      // 未登录，跳转到登录页
      next('/login')
    } else if (to.meta.requiresAdmin && userRole !== 'admin') {
      // 需要系统管理员权限
      Vue.prototype.$message.warning('您没有权限访问该页面')
      next('/home')
    } else if (to.meta.requiresLibrarian && userRole !== 'admin' && userRole !== 'librarian') {
      // 需要图书管理员权限
      Vue.prototype.$message.warning('您没有权限访问该页面')
      next('/home')
    } else if (to.meta.requiresReader && userRole !== 'reader') {
      // 需要读者权限
      Vue.prototype.$message.warning('您没有权限访问该页面')
      next('/home')
    } else {
      next()
    }
  } else if (to.path === '/login' && userInfo) {
    // 已登录访问登录页，跳转到首页
    next('/home')
  } else {
    next()
  }
})

export default router
