import Vue from 'vue'
import VueRouter from 'vue-router'
import Layout from '@/components/Layout.vue'

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/components/Login.vue')
  },
  {
    path: '/',
    component: Layout,
    redirect: '/home',
    meta: { requiresAuth: true },
    children: [
      {
        path: 'home',
        name: 'Home',
        component: () => import('@/components/Home.vue')
      },
      {
        path: 'books',
        name: 'Books',
        component: () => import('@/components/Books.vue'),
        meta: { requiresLibrarianOnly: true }
      },
      {
        path: 'borrow',
        name: 'Borrow',
        component: () => import('@/components/Borrow.vue'),
        meta: { requiresLibrarianOnly: true }
      },
      {
        path: 'my-borrow',
        name: 'MyBorrow',
        component: () => import('@/components/ReaderBorrow.vue'),
        meta: { requiresReader: true }
      },
      {
        path: 'users',
        name: 'Users',
        component: () => import('@/components/Users.vue'),
        meta: { requiresAdmin: true }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/components/Profile.vue')
      },
      {
        path: 'ai-robot',
        name: 'AiRobot',
        component: () => import('@/components/AiRobot.vue')
      },
      {
        path: 'book-browse',
        name: 'BookBrowse',
        component: () => import('@/components/BookBrowse.vue')
      },
      {
        path: 'book/:id',
        name: 'BookDetail',
        component: () => import('@/components/BookDetail.vue')
      },
      {
        path: 'announcements',
        name: 'Announcements',
        component: () => import('@/components/Announcements.vue'),
        meta: { requiresAdmin: true }
      }
    ]
  }
]

const router = new VueRouter({
  mode: 'hash',
  base: process.env.BASE_URL,
  routes
})

// 防止重复导航的错误
const originalPush = VueRouter.prototype.push
VueRouter.prototype.push = function push(location) {
  return originalPush.call(this, location).catch(err => {
    if (err.name !== 'NavigationDuplicated') {
      throw err
    }
  })
}

// 路由守卫
router.beforeEach((to, from, next) => {
  const userInfo = localStorage.getItem('userInfo')
  const userRole = localStorage.getItem('userRole')

  // 访问根路径重定向到登录页
  if (to.path === '/') {
    next('/login')
    return
  }

  // 登录页：已登录则跳转首页
  if (to.path === '/login') {
    if (userInfo) {
      next({ path: '/home' })
    } else {
      next()
    }
    return
  }

  // 其他页面需要登录
  if (!userInfo) {
    next({ path: '/login' })
    return
  }

  // 权限检查
  if (to.meta.requiresAdmin && userRole !== 'admin') {
    Vue.prototype.$message.warning('您没有权限访问该页面')
    next({ path: '/home' })
    return
  }
  if (to.meta.requiresLibrarianOnly && userRole !== 'librarian' && userRole !== 'admin') {
    Vue.prototype.$message.warning('您没有权限访问该页面')
    next({ path: '/home' })
    return
  }
  if (to.meta.requiresReader && userRole !== 'reader') {
    Vue.prototype.$message.warning('您没有权限访问该页面')
    next({ path: '/home' })
    return
  }

  next()
})

export default router
