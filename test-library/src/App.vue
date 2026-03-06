<template>
  <div id="app">
    <el-container style="height: 100vh">
      <!-- 左侧导航 -->
      <el-aside width="220px" style="background: linear-gradient(180deg, #1a1f2e 0%, #263145 100%)">
        <div class="logo">
          <i class="el-icon-collection"></i>
          <span>图书馆管理系统</span>
        </div>
        <el-menu
          :default-active="$route.path"
          background-color="transparent"
          text-color="#8c92a4"
          active-text-color="#fff"
          :router="true"
          :unique-opened="true"
          class="sidebar-menu"
        >
          <!-- 首页：所有用户可见 -->
          <el-menu-item index="/home">
            <i class="el-icon-s-home"></i>
            <span slot="title">首页</span>
          </el-menu-item>

          <!-- 图书管理：根据权限显示 -->
          <el-menu-item index="/books" v-if="hasPermission('book:manage')">
            <i class="el-icon-reading"></i>
            <span slot="title">图书管理</span>
          </el-menu-item>

          <!-- 借阅管理/我的借阅：根据角色显示 -->
          <el-menu-item index="/borrow">
            <i class="el-icon-s-order"></i>
            <span slot="title">{{ isReader ? '我的借阅' : '借阅管理' }}</span>
          </el-menu-item>

          <!-- 用户管理：根据权限显示 -->
          <el-menu-item index="/users" v-if="hasPermission('user:manage')">
            <i class="el-icon-user"></i>
            <span slot="title">用户管理</span>
          </el-menu-item>

          <!-- 个人中心：所有用户可见 -->
          <el-menu-item index="/profile">
            <i class="el-icon-user-solid"></i>
            <span slot="title">个人中心</span>
          </el-menu-item>

          <!-- 系统设置：根据权限显示 -->
          <el-menu-item index="/settings" v-if="hasPermission('settings:manage')">
            <i class="el-icon-s-tools"></i>
            <span slot="title">系统设置</span>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <!-- 右侧内容 -->
      <el-container>
        <el-header class="header">
          <div class="header-left">
            <i class="el-icon-s-fold toggle-btn" @click="toggleSidebar"></i>
            <el-breadcrumb separator="/">
              <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
              <el-breadcrumb-item v-if="$route.path !== '/home'">{{ $route.name }}</el-breadcrumb-item>
            </el-breadcrumb>
          </div>
          <div class="header-right">
            <el-dropdown trigger="click" class="user-dropdown" @command="handleUserCommand">
              <div class="user-info">
                <el-avatar :size="32" src="https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png"></el-avatar>
                <span class="username">{{ userName }} ({{ roleName }})</span>
                <i class="el-icon-arrow-down"></i>
              </div>
              <el-dropdown-menu slot="dropdown" class="user-dropdown-menu">
                <el-dropdown-item command="profile"><i class="el-icon-user"></i> 个人中心</el-dropdown-item>
                <el-dropdown-item command="settings" v-if="hasPermission('settings:manage')"><i class="el-icon-setting"></i> 系统设置</el-dropdown-item>
                <el-dropdown-item divided command="logout"><i class="el-icon-switch-button"></i> 退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </div>
        </el-header>
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
  name: 'App',
  data() {
    return {
      isCollapse: false,
      userInfo: null,
      userType: '',
      userRole: '',
      userName: ''
    }
  },
  computed: {
    // 判断是否为读者
    isReader() {
      return this.userType === 'reader'
    },
    // 判断是否为系统管理员
    isAdmin() {
      return this.userRole === 'admin'
    },
    // 判断是否为图书管理员
    isLibrarian() {
      return this.userRole === 'librarian'
    },
    // 角色名称
    roleName() {
      if (this.userRole === 'admin') return '系统管理员'
      if (this.userRole === 'librarian') return '图书管理员'
      return '读者'
    }
  },
  created() {
    this.loadUserInfo()
  },
  updated() {
    // 每次组件更新后都重新加载用户信息，确保显示正确的登录用户
    this.loadUserInfo()
  },
  methods: {
    // 判断是否有指定权限
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
      }
    },
    toggleSidebar() {
      this.isCollapse = !this.isCollapse
    },
    async handleUserCommand(command) {
      if (command === 'profile') {
        this.$router.push('/profile')
      } else if (command === 'settings') {
        this.$router.push('/settings')
      } else if (command === 'logout') {
        try {
          await this.$confirm('确定要退出登录吗？', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          })
          await logout()
          localStorage.removeItem('userInfo')
          localStorage.removeItem('userType')
          localStorage.removeItem('userRole')
          localStorage.removeItem('permissions')
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

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}
#app {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', 'Helvetica Neue', Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  font-size: 15px;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  color: #fff;
  font-size: 20px;
  font-weight: 600;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  box-shadow: 0 2px 12px rgba(102, 126, 234, 0.3);
}
.logo i {
  font-size: 24px;
}

.sidebar-menu {
  border-right: none;
  padding-top: 10px;
}
.sidebar-menu .el-menu-item {
  height: 56px;
  line-height: 56px;
  margin: 4px 12px;
  border-radius: 8px;
  transition: all 0.3s;
  font-size: 15px;
}
.sidebar-menu .el-menu-item:hover {
  background: rgba(255, 255, 255, 0.08) !important;
}
.sidebar-menu .el-menu-item.is-active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}
.sidebar-menu .el-menu-item i {
  margin-right: 10px;
  font-size: 20px;
}

.header {
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 28px;
  z-index: 100;
  height: 64px;
}
.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}
.toggle-btn {
  font-size: 20px;
  cursor: pointer;
  color: #606266;
  transition: color 0.3s;
}
.toggle-btn:hover {
  color: #409EFF;
}
.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}
.header-right .el-badge__content {
  top: 10px;
  right: 8px;
}
.header-right .el-icon-bell {
  font-size: 20px;
  color: #606266;
  cursor: pointer;
  transition: color 0.3s;
}
.header-right .el-icon-bell:hover {
  color: #409EFF;
}
.user-dropdown {
  cursor: pointer;
}
.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
}
.username {
  color: #303133;
  font-size: 15px;
}
.user-dropdown-menu {
  margin-top: 10px;
}
.user-dropdown-menu i {
  margin-right: 8px;
}

.main-content {
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e7ed 100%);
  padding: 24px;
  min-height: calc(100vh - 64px);
}

.page-title {
  font-size: 22px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  gap: 10px;
}
.page-title::before {
  content: '';
  width: 4px;
  height: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 2px;
}

.custom-card {
  border-radius: 12px;
  overflow: hidden;
}
.custom-card .el-card__header {
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e7ed 100%);
  padding: 16px 20px;
  border-bottom: 1px solid #ebeef5;
}
.custom-card .el-card__header span {
  font-weight: 600;
  color: #303133;
}

.el-button--primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}
.el-button--primary:hover {
  background: linear-gradient(135deg, #5a6fd6 0%, #6a4190 100%);
}

.el-table {
  border-radius: 8px;
  overflow: hidden;
  font-size: 15px;
}
.el-table th {
  background: #f5f7fa !important;
  color: #606266;
  font-weight: 600;
  font-size: 15px;
}
.el-table td {
  font-size: 15px;
}
.el-table--border::after,
.el-table--border::before {
  background: #ebeef5;
}

.el-pagination {
  margin-top: 20px;
  text-align: right;
}
.el-pagination .el-pager li.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.fade-transform-leave-active,
.fade-transform-enter-active {
  transition: all 0.3s;
}
.fade-transform-enter {
  opacity: 0;
  transform: translateX(20px);
}
.fade-transform-leave-to {
  opacity: 0;
  transform: translateX(-20px);
}

::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}
::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 4px;
}
::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 4px;
}
::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}
</style>
