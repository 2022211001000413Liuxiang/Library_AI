<template>
  <div id="app">
    <el-container style="height: 100vh">
      <!-- 左侧导航 -->
      <el-aside
        :width="isCollapse ? '64px' : '200px'"
        class="sidebar"
        :class="{ 'sidebar--collapse': isCollapse }"
      >
        <!-- Logo 区域 -->
        <div class="logo" :class="{ 'logo--collapse': isCollapse }">
          <i class="el-icon-collection"></i>
          <transition name="fade">
            <span v-if="!isCollapse" class="logo-text">图书馆管理系统</span>
          </transition>
        </div>

        <!-- 菜单 -->
        <el-menu
          :default-active="currentActive"
          background-color="transparent"
          text-color="#8c92a4"
          active-text-color="#fff"
          :router="true"
          :unique-opened="true"
          :collapse="isCollapse"
          :collapse-transition="false"
          class="sidebar-menu"
        >
          <el-menu-item index="/home">
            <i class="el-icon-s-home"></i>
            <span slot="title">首页</span>
          </el-menu-item>

          <el-menu-item index="/books" v-if="isLibrarian">
            <i class="el-icon-reading"></i>
            <span slot="title">图书管理</span>
          </el-menu-item>

          <el-menu-item v-if="isLibrarian" index="/borrow">
            <i class="el-icon-s-order"></i>
            <span slot="title">借阅管理</span>
          </el-menu-item>
          <el-menu-item v-else-if="isReader" index="/my-borrow">
            <i class="el-icon-s-order"></i>
            <span slot="title">我的借阅</span>
          </el-menu-item>

          <el-menu-item index="/ai-robot">
            <i class="el-icon-microphone"></i>
            <span slot="title">AI 图书助手</span>
          </el-menu-item>

          <el-menu-item index="/users" v-if="hasPermission('user:manage')">
            <i class="el-icon-user"></i>
            <span slot="title">用户管理</span>
          </el-menu-item>

          <el-menu-item index="/profile">
            <i class="el-icon-user-solid"></i>
            <span slot="title">个人中心</span>
          </el-menu-item>

          <el-menu-item index="/announcements" v-if="isAdmin">
            <i class="el-icon-bell"></i>
            <span slot="title">公告管理</span>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <!-- 右侧内容 -->
      <el-container class="main-wrapper">
        <!-- Header -->
        <el-header class="header">
          <div class="header-left">
            <div class="toggle-btn" @click="toggleSidebar">
              <i :class="isCollapse ? 'el-icon-s-unfold' : 'el-icon-s-fold'"></i>
            </div>
            <el-breadcrumb separator="/" class="breadcrumb">
              <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
              <el-breadcrumb-item v-if="$route.path !== '/home'">{{ getPageTitle }}</el-breadcrumb-item>
            </el-breadcrumb>
          </div>
          <div class="header-right">
            <el-dropdown trigger="click" class="user-dropdown" @command="handleUserCommand">
              <div class="user-info">
                <el-avatar
                  :size="32"
                  :src="userAvatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'"
                ></el-avatar>
                <div class="user-detail">
                  <span class="username">{{ userName }}</span>
                  <span class="role-tag" :class="`role-tag--${userRole}`">{{ roleName }}</span>
                </div>
                <i class="el-icon-arrow-down el-icon--right"></i>
              </div>
              <el-dropdown-menu slot="dropdown" class="user-dropdown-menu">
                <el-dropdown-item command="profile">
                  <i class="el-icon-user"></i> 个人中心
                </el-dropdown-item>
                <el-dropdown-item divided command="logout">
                  <i class="el-icon-switch-button"></i> 退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </div>
        </el-header>

        <!-- 主内容区 -->
        <el-main class="main-content">
          <transition name="fade-transform" mode="out-in">
            <router-view />
          </transition>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script>
import { logout } from '@/api/auth'

export default {
  name: 'Layout',
  data() {
    return {
      isCollapse: false,
      userInfo: null,
      userType: '',
      userRole: '',
      userName: '',
      userAvatar: ''
    }
  },
  computed: {
    currentActive() {
      return this.$route.path
    },
    isReader() {
      return this.userType === 'reader'
    },
    isStaff() {
      return this.userType === 'staff'
    },
    isAdmin() {
      return this.userRole === 'admin'
    },
    isLibrarian() {
      return this.userRole === 'librarian'
    },
    roleName() {
      if (this.userRole === 'admin') return '系统管理员'
      if (this.userRole === 'librarian') return '图书管理员'
      return '读者'
    },
    getPageTitle() {
      const routeMap = {
        '/home': '首页',
        '/books': '图书管理',
        '/borrow': '借阅管理',
        '/my-borrow': '我的借阅',
        '/users': '用户管理',
        '/profile': '个人中心',
        '/ai-robot': 'AI 图书助手',
        '/announcements': '公告管理'
      }
      return routeMap[this.$route.path] || this.$route.name
    }
  },
  created() {
    this.loadUserInfo()
  },
  methods: {
    hasPermission(permKey) {
      const permissions = JSON.parse(localStorage.getItem('permissions') || '[]')
      return permissions.includes(permKey)
    },
    loadUserInfo() {
      const userInfoStr = localStorage.getItem('userInfo')
      this.userType = localStorage.getItem('userType') || ''
      this.userRole = localStorage.getItem('userRole') || ''
      if (userInfoStr) {
        this.userInfo = JSON.parse(userInfoStr)
        this.userName = this.userInfo.name || this.userInfo.username
        this.userAvatar = this.userInfo.avatar || ''
      }
    },
    toggleSidebar() {
      this.isCollapse = !this.isCollapse
    },
    async handleUserCommand(command) {
      if (command === 'profile') {
        this.$router.push('/profile')
      } else if (command === 'logout') {
        try {
          await this.$confirm('确定要退出登录吗？', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          })
          await logout()
          localStorage.removeItem('token')
          localStorage.removeItem('userInfo')
          localStorage.removeItem('userType')
          localStorage.removeItem('userRole')
          localStorage.removeItem('permissions')
          localStorage.removeItem('ai_session_id')
          localStorage.removeItem('ai_user_id')
          this.$message.success('退出登录成功')
          this.$router.push('/login')
        } catch (error) {
          // 用户取消
        }
      }
    }
  }
}
</script>

<style scoped>
#app {
  height: 100vh;
}

/* ========== 侧边栏 ========== */
.sidebar {
  background: var(--color-bg-sidebar);
  transition: width var(--transition-normal);
  overflow: hidden;
}

.sidebar-menu {
  border-right: none;
  padding-top: 10px;
}

.sidebar-menu:not(.el-menu--collapse) {
  width: 100%;
}

.sidebar-menu .el-menu-item {
  height: 50px;
  line-height: 50px;
  margin: 4px 10px;
  padding-left: 20px !important;
  border-radius: var(--radius-small);
  transition: all var(--transition-fast);
  font-size: var(--font-size-base);
  position: relative;
}

.sidebar-menu .el-menu-item::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 3px;
  height: 0;
  background: var(--color-primary);
  border-radius: 0 3px 3px 0;
  transition: height var(--transition-fast);
}

.sidebar-menu .el-menu-item:hover {
  background: rgba(255, 255, 255, 0.08) !important;
}

.sidebar-menu .el-menu-item:hover::before {
  height: 20px;
}

.sidebar-menu .el-menu-item.is-active {
  background: linear-gradient(135deg, var(--color-primary) 0%, #D2691E 100%) !important;
  box-shadow: 0 4px 12px rgba(139, 90, 43, 0.4);
}

.sidebar-menu .el-menu-item.is-active::before {
  height: 24px;
}

.sidebar-menu .el-menu-item i {
  margin-right: 10px;
  font-size: 18px;
  width: 20px;
}

.sidebar-menu .el-menu-item span {
  font-weight: 500;
}

/* 折叠状态 */
.sidebar--collapse .el-menu-item {
  padding-left: 22px !important;
  justify-content: center;
}

.sidebar--collapse .el-menu-item i {
  margin-right: 0;
}

/* ========== Logo ========== */
.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  color: #fff;
  font-size: 16px;
  font-weight: 600;
  background: linear-gradient(135deg, var(--color-primary) 0%, #D2691E 100%);
  box-shadow: 0 2px 12px rgba(139, 90, 43, 0.3);
  transition: all var(--transition-normal);
  overflow: hidden;
}

.logo i {
  font-size: 22px;
  flex-shrink: 0;
}

.logo--collapse {
  justify-content: center;
  padding: 0;
}

.logo-text {
  white-space: nowrap;
}

/* ========== Header ========== */
.header {
  background: var(--color-bg-header);
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 var(--spacing-lg);
  height: var(--header-height);
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-left {
  display: flex;
  align-items: center;
  gap: var(--spacing-base);
}

.toggle-btn {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: var(--radius-small);
  cursor: pointer;
  transition: all var(--transition-fast);
}

.toggle-btn:hover {
  background: rgba(139, 90, 43, 0.1);
}

.toggle-btn:hover i {
  color: var(--color-primary);
}

.toggle-btn i {
  font-size: 20px;
  color: var(--color-text-regular);
  transition: color var(--transition-fast);
}

.breadcrumb {
  font-size: var(--font-size-sm);
}

.breadcrumb >>> .el-breadcrumb__inner {
  color: var(--color-text-secondary);
}

.breadcrumb >>> .el-breadcrumb__inner a {
  color: var(--color-text-secondary);
  font-weight: normal;
}

.breadcrumb >>> .el-breadcrumb__inner a:hover {
  color: var(--color-primary);
}

.breadcrumb >>> .el-breadcrumb__separator {
  color: var(--color-text-placeholder);
}

/* ========== 用户信息 ========== */
.user-dropdown {
  cursor: pointer;
}

.user-info {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  padding: 6px 12px;
  border-radius: var(--radius-small);
  transition: all var(--transition-fast);
}

.user-info:hover {
  background: rgba(139, 90, 43, 0.08);
}

.user-detail {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}

.username {
  color: var(--color-text-primary);
  font-size: var(--font-size-sm);
  font-weight: 500;
  line-height: 1.3;
}

.role-tag {
  font-size: 10px;
  padding: 1px 6px;
  border-radius: 10px;
  line-height: 1.4;
}

.role-tag--admin {
  background: rgba(139, 90, 43, 0.15);
  color: var(--color-primary);
}

.role-tag--librarian {
  background: rgba(210, 105, 30, 0.15);
  color: #D2691E;
}

.role-tag--reader {
  background: rgba(160, 82, 45, 0.15);
  color: #A0522D;
}

.user-dropdown-menu {
  margin-top: 8px;
}

.user-dropdown-menu i {
  margin-right: 8px;
  color: var(--color-primary);
}

/* ========== 主内容区 ========== */
.main-wrapper {
  flex-direction: column;
  min-height: 100vh;
}

.main-content {
  background: var(--color-bg-page);
  padding: var(--spacing-lg);
  min-height: calc(100vh - var(--header-height));
}

/* ========== 过渡动画 ========== */
.fade-enter-active,
.fade-leave-active {
  transition: opacity var(--transition-fast);
}

.fade-enter,
.fade-leave-to {
  opacity: 0;
}

/* 路由过渡 */
.fade-transform-leave-active,
.fade-transform-enter-active {
  transition: all var(--transition-normal);
}

.fade-transform-enter {
  opacity: 0;
  transform: translateX(20px);
}

.fade-transform-leave-to {
  opacity: 0;
  transform: translateX(-20px);
}
</style>
