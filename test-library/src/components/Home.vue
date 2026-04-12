<template>
  <div class="home-page page-container">
    <!-- 欢迎横幅 -->
    <div class="welcome-banner">
      <div class="welcome-content">
        <h1>欢迎回来，{{ userName }}</h1>
        <p>今天是 {{ currentDate }}，祝您工作顺利</p>
      </div>
      <div class="welcome-illustration">
        <i class="el-icon-reading"></i>
      </div>
      <div class="banner-decoration"></div>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stat-row">
      <el-col :xs="24" :sm="12" :lg="6" v-for="(item, index) in stats" :key="index">
        <div class="stat-card" :class="`stat-card--${item.type}`">
          <div class="stat-card__icon">
            <i :class="item.icon"></i>
          </div>
          <div class="stat-card__content">
            <div class="stat-card__value">{{ item.value }}</div>
            <div class="stat-card__label">{{ item.label }}</div>
          </div>
          <div class="stat-card__trend" v-if="item.trend">
            <i :class="item.trend > 0 ? 'el-icon-top' : 'el-icon-bottom'"></i>
            <span>{{ Math.abs(item.trend) }}%</span>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 内容区域 -->
    <el-row :gutter="20" class="content-row">
      <!-- 最近借阅 -->
      <el-col :xs="24" :lg="14">
        <el-card shadow="hover" class="custom-card">
          <div slot="header" class="card-header">
            <span>
              <i class="el-icon-s-order"></i>
              最近借阅
            </span>
            <el-button type="primary" text size="small" @click="$router.push(isReader ? '/my-borrow' : '/borrow')">
              查看更多 <i class="el-icon-arrow-right el-icon--right"></i>
            </el-button>
          </div>
          <el-table :data="recentBorrows" style="width: 100%" :header-cell-style="{ background: '#fafbfc' }">
            <el-table-column prop="bookName" label="书名" min-width="150"></el-table-column>
            <el-table-column prop="userName" label="借阅人" width="100"></el-table-column>
            <el-table-column prop="borrowDate" label="借阅日期" width="120"></el-table-column>
            <el-table-column prop="status" label="状态" width="100" align="center">
              <template slot-scope="scope">
                <el-tag :type="scope.row.status === 0 ? 'primary' : 'success'" size="small" effect="dark">
                  {{ scope.row.status === 0 ? '借阅中' : '已归还' }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <!-- 系统公告 -->
      <el-col :xs="24" :lg="10">
        <el-card shadow="hover" class="custom-card">
          <div slot="header" class="card-header">
            <span>
              <i class="el-icon-bell"></i>
              系统公告
            </span>
          </div>
          <div class="notice-list">
            <div
              class="notice-item"
              v-for="(item, index) in notices"
              :key="index"
              :class="{ 'notice-item--highlight': index === 0 }"
            >
              <div class="notice-item__icon" :class="`notice-item__icon--${index + 1}`">
                <i :class="index === 0 ? 'el-icon-magic-stick' : 'el-icon-bell'"></i>
              </div>
              <div class="notice-item__content">
                <div class="notice-item__title">{{ item.title }}</div>
                <div class="notice-item__desc">{{ item.desc }}</div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 快捷操作 -->
    <el-row :gutter="20" class="quick-action-row">
      <el-col :span="24">
        <el-card shadow="hover" class="custom-card">
          <div slot="header" class="card-header">
            <span>
              <i class="el-icon-s-grid"></i>
              快捷操作
            </span>
          </div>
          <div class="quick-actions">
            <div class="quick-action-item" @click="$router.push('/books')">
              <div class="quick-action-item__icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">
                <i class="el-icon-reading"></i>
              </div>
              <span class="quick-action-item__label">图书管理</span>
            </div>
            <div class="quick-action-item" @click="$router.push(isReader ? '/my-borrow' : '/borrow')">
              <div class="quick-action-item__icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);">
                <i class="el-icon-sold-out"></i>
              </div>
              <span class="quick-action-item__label">{{ isReader ? '我的借阅' : '借阅管理' }}</span>
            </div>
            <div class="quick-action-item" @click="$router.push('/ai-robot')">
              <div class="quick-action-item__icon" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);">
                <i class="el-icon-microphone"></i>
              </div>
              <span class="quick-action-item__label">AI 助手</span>
            </div>
            <div class="quick-action-item" @click="$router.push('/profile')">
              <div class="quick-action-item__icon" style="background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);">
                <i class="el-icon-user"></i>
              </div>
              <span class="quick-action-item__label">个人中心</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { getStats } from '@/api/stats'
import { getBorrows } from '@/api/borrow'

export default {
  name: 'Home',
  data() {
    return {
      userName: '',
      userType: '',
      currentDate: new Date().toLocaleDateString('zh-CN', {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        weekday: 'long'
      }),
      stats: [],
      recentBorrows: [],
      notices: [
        { title: '新书上架通知', desc: '本月新增热门图书，欢迎借阅' },
        { title: '逾期罚款标准调整', desc: '逾期罚款调整为每日0.5元' },
        { title: '春节期间开放时间', desc: '节日期间营业时间调整为9:00-17:00' }
      ]
    }
  },
  computed: {
    isReader() {
      return this.userType === 'reader'
    }
  },
  created() {
    this.loadUserInfo()
    this.loadStats()
    this.loadRecentBorrows()
  },
  methods: {
    loadUserInfo() {
      const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
      this.userName = userInfo.name || userInfo.username || '管理员'
      this.userType = localStorage.getItem('userType') || ''
    },
    async loadStats() {
      try {
        const res = await getStats()
        this.stats = [
          { icon: 'el-icon-reading', value: res.totalBooks || 0, label: '图书总数', type: 'primary', trend: 12 },
          { icon: 'el-icon-sold-out', value: res.borrowedBooks || 0, label: '已借出', type: 'warning', trend: -5 },
          { icon: 'el-icon-user', value: res.totalUsers || 0, label: '用户总数', type: 'info', trend: 8 },
          { icon: 'el-icon-warning', value: res.overdueBooks || 0, label: '逾期未还', type: 'danger', trend: -20 }
        ]
      } catch (error) {
        console.error('加载统计数据失败:', error)
        this.stats = [
          { icon: 'el-icon-reading', value: 0, label: '图书总数', type: 'primary', trend: 0 },
          { icon: 'el-icon-sold-out', value: 0, label: '已借出', type: 'warning', trend: 0 },
          { icon: 'el-icon-user', value: 0, label: '用户总数', type: 'info', trend: 0 },
          { icon: 'el-icon-warning', value: 0, label: '逾期未还', type: 'danger', trend: 0 }
        ]
      }
    },
    async loadRecentBorrows() {
      try {
        const res = await getBorrows({ current: 1, size: 5 })
        this.recentBorrows = (res.data || []).map(item => ({
          bookName: item.bookName,
          userName: item.userName,
          borrowDate: item.borrowDate,
          status: item.status
        }))
      } catch (error) {
        console.error('加载借阅记录失败:', error)
      }
    }
  }
}
</script>

<style scoped>
.home-page {
  max-width: 1400px;
  margin: 0 auto;
}

/* ========== 欢迎横幅 ========== */
.welcome-banner {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: var(--radius-large);
  padding: 32px 40px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-lg);
  box-shadow: 0 8px 24px rgba(102, 126, 234, 0.3);
  position: relative;
  overflow: hidden;
}

.welcome-banner::before {
  content: '';
  position: absolute;
  top: -80px;
  right: -80px;
  width: 300px;
  height: 300px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 50%;
}

.welcome-banner::after {
  content: '';
  position: absolute;
  bottom: -100px;
  right: 100px;
  width: 200px;
  height: 200px;
  background: rgba(255, 255, 255, 0.08);
  border-radius: 50%;
}

.welcome-content {
  position: relative;
  z-index: 1;
}

.welcome-content h1 {
  color: #fff;
  font-size: 28px;
  font-weight: 600;
  margin-bottom: 8px;
}

.welcome-content p {
  color: rgba(255, 255, 255, 0.85);
  font-size: var(--font-size-base);
}

.welcome-illustration {
  position: relative;
  z-index: 1;
}

.welcome-illustration i {
  font-size: 80px;
  color: rgba(255, 255, 255, 0.25);
}

.banner-decoration {
  position: absolute;
  right: 40%;
  top: 0;
  bottom: 0;
  width: 1px;
  background: linear-gradient(to bottom, transparent, rgba(255,255,255,0.2), transparent);
}

/* ========== 统计卡片 ========== */
.stat-row {
  margin-bottom: var(--spacing-lg);
}

.stat-card {
  display: flex;
  align-items: center;
  padding: var(--spacing-lg);
  background: var(--color-bg-card);
  border-radius: var(--radius-medium);
  box-shadow: var(--shadow-light);
  transition: all var(--transition-normal);
  position: relative;
  overflow: hidden;
  margin-bottom: var(--spacing-base);
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-medium);
}

.stat-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 4px;
  height: 100%;
}

.stat-card--primary::before { background: linear-gradient(180deg, var(--color-primary), #764ba2); }
.stat-card--warning::before { background: linear-gradient(180deg, #f093fb, #f5576c); }
.stat-card--info::before { background: linear-gradient(180deg, #4facfe, #00f2fe); }
.stat-card--danger::before { background: linear-gradient(180deg, var(--color-danger), #ff6b6b); }

.stat-card__icon {
  width: 56px;
  height: 56px;
  border-radius: var(--radius-medium);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: #fff;
  margin-right: var(--spacing-base);
  flex-shrink: 0;
}

.stat-card--primary .stat-card__icon { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
.stat-card--warning .stat-card__icon { background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%); }
.stat-card--info .stat-card__icon { background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%); }
.stat-card--danger .stat-card__icon { background: linear-gradient(135deg, #f5576c 0%, #ff6b6b 100%); }

.stat-card__content {
  flex: 1;
  min-width: 0;
}

.stat-card__value {
  font-size: 28px;
  font-weight: 700;
  color: var(--color-text-primary);
  line-height: 1.2;
}

.stat-card__label {
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
  margin-top: 4px;
}

.stat-card__trend {
  display: flex;
  align-items: center;
  gap: 2px;
  font-size: var(--font-size-xs);
  padding: 4px 10px;
  border-radius: 20px;
  background: rgba(67, 233, 123, 0.1);
  color: #2eb872;
}

.stat-card__trend i {
  font-size: 10px;
}

/* ========== 内容区域 ========== */
.content-row {
  margin-bottom: var(--spacing-lg);
}

/* ========== 卡片头部 ========== */
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--spacing-sm) 0;
}

.card-header span {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
  font-weight: 600;
  font-size: var(--font-size-base);
  color: var(--color-text-primary);
}

.card-header i {
  color: var(--color-primary);
  font-size: 16px;
}

/* ========== 公告列表 ========== */
.notice-list {
  padding: var(--spacing-xs) 0;
}

.notice-item {
  display: flex;
  align-items: flex-start;
  padding: var(--spacing-base) 0;
  border-bottom: 1px solid #f5f5f5;
  transition: all var(--transition-fast);
  cursor: pointer;
}

.notice-item:last-child {
  border-bottom: none;
}

.notice-item:hover {
  padding-left: var(--spacing-sm);
}

.notice-item--highlight {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.05) 0%, rgba(118, 75, 162, 0.05) 100%);
  margin: 0 calc(var(--spacing-sm) * -1);
  padding: var(--spacing-base);
  border-radius: var(--radius-small);
  border-bottom: none;
}

.notice-item__icon {
  width: 40px;
  height: 40px;
  border-radius: var(--radius-small);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  margin-right: var(--spacing-base);
  flex-shrink: 0;
  font-size: 16px;
}

.notice-item__icon--1 { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
.notice-item__icon--2 { background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%); }
.notice-item__icon--3 { background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%); }

.notice-item__content {
  flex: 1;
  min-width: 0;
}

.notice-item__title {
  font-size: var(--font-size-base);
  font-weight: 500;
  color: var(--color-text-primary);
  margin-bottom: 4px;
  line-height: 1.4;
}

.notice-item__desc {
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
  line-height: 1.5;
}

/* ========== 快捷操作 ========== */
.quick-actions {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: var(--spacing-base);
  padding: var(--spacing-sm) 0;
}

@media (max-width: 768px) {
  .quick-actions {
    grid-template-columns: repeat(2, 1fr);
  }
}

.quick-action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: var(--spacing-lg);
  border-radius: var(--radius-medium);
  cursor: pointer;
  transition: all var(--transition-normal);
  background: #fafbfc;
}

.quick-action-item:hover {
  background: #f0f2f5;
  transform: translateY(-4px);
}

.quick-action-item__icon {
  width: 56px;
  height: 56px;
  border-radius: var(--radius-medium);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: #fff;
  margin-bottom: var(--spacing-sm);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  transition: all var(--transition-normal);
}

.quick-action-item:hover .quick-action-item__icon {
  transform: scale(1.1);
}

.quick-action-item__label {
  font-size: var(--font-size-sm);
  color: var(--color-text-regular);
  font-weight: 500;
}
</style>
