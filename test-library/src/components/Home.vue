<template>
  <div class="home-page">
    <!-- 欢迎横幅 -->
    <div class="welcome-banner">
      <div class="welcome-content">
        <h1>欢迎回来，管理员</h1>
        <p>今天是 {{ currentDate }}，祝您工作顺利</p>
      </div>
      <div class="welcome-illustration">
        <i class="el-icon-reading"></i>
      </div>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stat-row">
      <el-col :span="6" v-for="(item, index) in stats" :key="index">
        <el-card shadow="hover" class="stat-card" :class="'stat-card-' + (index + 1)">
          <div class="stat-icon">
            <i :class="item.icon"></i>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ item.value }}</div>
            <div class="stat-label">{{ item.label }}</div>
            <div class="stat-trend" v-if="item.trend">
              <i :class="item.trend > 0 ? 'el-icon-top' : 'el-icon-bottom'" :style="{ color: item.trend > 0 ? '#67C23A' : '#F56C6C' }"></i>
              <span :style="{ color: item.trend > 0 ? '#67C23A' : '#F56C6C' }">{{ Math.abs(item.trend) }}%</span>
              <span class="trend-text">较上周</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 内容区域 -->
    <el-row :gutter="20">
      <!-- 最近借阅 -->
      <el-col :span="14">
        <el-card shadow="hover" class="custom-card">
          <div slot="header" class="card-header">
            <span><i class="el-icon-s-order"></i> 最近借阅</span>
            <el-button type="text" size="small">查看更多</el-button>
          </div>
          <el-table :data="recentBorrows" style="width: 100%" :header-cell-style="{ background: '#f5f7fa' }">
            <el-table-column prop="bookName" label="书名" min-width="150"></el-table-column>
            <el-table-column prop="userName" label="借阅人" width="100"></el-table-column>
            <el-table-column prop="borrowDate" label="借阅日期" width="120"></el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template slot-scope="scope">
                <el-tag :type="scope.row.status === 0 ? 'primary' : 'success'" size="small">
                  {{ scope.row.status === 0 ? '借阅中' : '已归还' }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <!-- 系统公告 -->
      <el-col :span="10">
        <el-card shadow="hover" class="custom-card">
          <div slot="header" class="card-header">
            <span><i class="el-icon-bell"></i> 系统公告</span>
          </div>
          <div class="notice-list">
            <div class="notice-item" v-for="(item, index) in notices" :key="index">
              <div class="notice-icon" :class="'notice-icon-' + (index + 1)">
                <i class="el-icon-bell"></i>
              </div>
              <div class="notice-content">
                <div class="notice-title">{{ item.title }}</div>
                <div class="notice-desc">{{ item.desc }}</div>
              </div>
              <div class="notice-date">{{ item.date }}</div>
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
            <span><i class="el-icon-s-tools"></i> 快捷操作</span>
          </div>
          <div class="quick-actions">
            <div class="quick-action-item" @click="$router.push('/books')">
              <div class="quick-action-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">
                <i class="el-icon-plus"></i>
              </div>
              <span>添加图书</span>
            </div>
            <div class="quick-action-item" @click="$router.push('/borrow')">
              <div class="quick-action-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);">
                <i class="el-icon-sold-out"></i>
              </div>
              <span>借阅图书</span>
            </div>
            <div class="quick-action-item" @click="$router.push('/users')">
              <div class="quick-action-icon" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);">
                <i class="el-icon-user-plus"></i>
              </div>
              <span>添加用户</span>
            </div>
            <div class="quick-action-item">
              <div class="quick-action-icon" style="background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);">
                <i class="el-icon-data-analysis"></i>
              </div>
              <span>数据统计</span>
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
      currentDate: new Date().toLocaleDateString('zh-CN', { year: 'numeric', month: 'long', day: 'numeric', weekday: 'long' }),
      stats: [],
      recentBorrows: [],
      notices: [
        { title: '春节期间图书馆开放时间调整', desc: '2月10日至2月17日营业时间调整为9:00-17:00', date: '01-10' },
        { title: '新书上架通知', desc: '本月新增图书200册，欢迎借阅', date: '01-08' },
        { title: '逾期罚款标准调整', desc: '逾期罚款调整为每日0.5元', date: '01-05' }
      ]
    }
  },
  created() {
    this.loadStats()
    this.loadRecentBorrows()
  },
  methods: {
    async loadStats() {
      try {
        const res = await getStats()
        this.stats = [
          { icon: 'el-icon-reading', value: res.totalBooks || 0, label: '图书总数', trend: 12 },
          { icon: 'el-icon-sold-out', value: res.borrowedBooks || 0, label: '已借出', trend: -5 },
          { icon: 'el-icon-user', value: res.totalUsers || 0, label: '用户总数', trend: 8 },
          { icon: 'el-icon-warning', value: res.overdueBooks || 0, label: '逾期未还', trend: -20 }
        ]
      } catch (error) {
        console.error('加载统计数据失败:', error)
      }
    },
    async loadRecentBorrows() {
      try {
        const res = await getBorrows({ current: 1, size: 4 })
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
  animation: fadeIn 0.5s ease;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

/* 欢迎横幅 */
.welcome-banner {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16px;
  padding: 30px 40px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  box-shadow: 0 8px 24px rgba(102, 126, 234, 0.3);
  position: relative;
  overflow: hidden;
}

.welcome-banner::before {
  content: '';
  position: absolute;
  top: -50%;
  right: -20%;
  width: 400px;
  height: 400px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 50%;
}

.welcome-content h1 {
  color: #fff;
  font-size: 32px;
  font-weight: 600;
  margin-bottom: 10px;
}

.welcome-content p {
  color: rgba(255, 255, 255, 0.8);
  font-size: 16px;
}

.welcome-illustration {
  font-size: 100px;
  color: rgba(255, 255, 255, 0.2);
  position: relative;
  z-index: 1;
}

/* 统计卡片 */
.stat-row {
  margin-bottom: 20px;
}

.stat-card {
  border-radius: 12px;
  border: none;
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1) !important;
}

.stat-card .stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: #fff;
  margin-bottom: 16px;
}

.stat-card-1 .stat-icon { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
.stat-card-2 .stat-icon { background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%); }
.stat-card-3 .stat-icon { background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%); }
.stat-card-4 .stat-icon { background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%); }

.stat-info .stat-value {
  font-size: 32px;
  font-weight: 700;
  color: #303133;
  line-height: 1.2;
}

.stat-info .stat-label {
  font-size: 15px;
  color: #909399;
  margin-top: 6px;
}

.stat-trend {
  margin-top: 8px;
  font-size: 12px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.trend-text {
  color: #909399 !important;
  margin-left: 4px;
}

/* 卡片头部 */
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header span {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
}

.card-header i {
  color: #409EFF;
}

/* 公告列表 */
.notice-list {
  padding: 0;
}

.notice-item {
  display: flex;
  align-items: center;
  padding: 16px 0;
  border-bottom: 1px solid #f0f0f0;
  transition: all 0.3s;
}

.notice-item:last-child {
  border-bottom: none;
}

.notice-item:hover {
  padding-left: 8px;
}

.notice-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  margin-right: 12px;
  flex-shrink: 0;
}

.notice-icon-1 { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
.notice-icon-2 { background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%); }
.notice-icon-3 { background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%); }

.notice-content {
  flex: 1;
  min-width: 0;
}

.notice-title {
  font-size: 15px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 6px;
}

.notice-desc {
  font-size: 13px;
  color: #909399;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.notice-date {
  font-size: 12px;
  color: #c0c4cc;
  flex-shrink: 0;
  margin-left: 12px;
}

/* 快捷操作 */
.quick-action-row {
  margin-top: 20px;
}

.quick-actions {
  display: flex;
  justify-content: space-around;
  padding: 20px 0;
}

.quick-action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
  transition: all 0.3s;
}

.quick-action-item:hover {
  transform: translateY(-5px);
}

.quick-action-icon {
  width: 64px;
  height: 64px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: #fff;
  margin-bottom: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.quick-action-item span {
  font-size: 15px;
  color: #606266;
}
</style>
