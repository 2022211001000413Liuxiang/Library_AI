import Vue from 'vue'
import VueRouter from 'vue-router'
import Login from '@/components/Login.vue'
import Layout from '@/components/Layout.vue'
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
    path: '/',
    component: Layout,
    redirect: '/home',
    meta: { requiresAuth: true },
    children: [
      {
        path: 'home',
        name: 'Home',
        component: Home
      },
      {
        path: 'books',
        name: 'Books',
        component: Books,
        meta: { requiresLibrarian: true }
      },
      {
        path: 'borrow',
        name: 'Borrow',
        component: Borrow,
        meta: { requiresLibrarian: true }
      },
      {
        path: 'my-borrow',
        name: 'MyBorrow',
        component: ReaderBorrow,
        meta: { requiresReader: true }
      },
      {
        path: 'users',
        name: 'Users',
        component: Users,
        meta: { requiresAdmin: true }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: Profile
      },
      {
        path: 'ai-robot',
        name: 'AiRobot',
        component: AiRobot
      },
      {
        path: 'announcements',
        name: 'Announcements',
        component: Announcements,
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

// 路由守卫
router.beforeEach((to, from, next) => {
  const userInfo = localStorage.getItem('userInfo')
  const userRole = localStorage.getItem('userRole')

  // 访问根路径重定向到登录页
  if (to.path === '/') {
    next('/login')
    return
  }

  // 登录页直接放行
  if (to.path === '/login') {
    if (userInfo) {
      next('/home')
    } else {
      next()
    }
    return
  }

  // 其他页面需要登录
  if (to.meta.requiresAuth && !userInfo) {
    next('/login')
    return
  }

  // 权限检查
  if (to.meta.requiresAdmin && userRole !== 'admin') {
    Vue.prototype.$message.warning('您没有权限访问该页面')
    next('/home')
  } else if (to.meta.requiresLibrarian && userRole !== 'admin' && userRole !== 'librarian') {
    Vue.prototype.$message.warning('您没有权限访问该页面')
    next('/home')
  } else if (to.meta.requiresReader && userRole !== 'reader') {
    Vue.prototype.$message.warning('您没有权限访问该页面')
    next('/home')
  } else {
    next()
  }
})

export default router
