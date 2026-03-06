<template>
  <div class="page-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-title"><i class="el-icon-s-tools"></i>系统设置</h2>
      <div class="page-desc">配置系统参数和个人偏好</div>
    </div>

    <el-row :gutter="20">
      <!-- 左侧设置菜单 -->
      <el-col :span="6">
        <el-card shadow="hover" class="settings-menu-card">
          <el-menu :default-active="activeMenu" @select="handleMenuSelect">
            <el-menu-item index="general">
              <i class="el-icon-s-home"></i>
              <span slot="title">基本设置</span>
            </el-menu-item>
            <el-menu-item index="appearance">
              <i class="el-icon-picture-outline"></i>
              <span slot="title">外观设置</span>
            </el-menu-item>
            <el-menu-item index="notification">
              <i class="el-icon-bell"></i>
              <span slot="title">通知设置</span>
            </el-menu-item>
            <el-menu-item index="security">
              <i class="el-icon-lock"></i>
              <span slot="title">安全设置</span>
            </el-menu-item>
            <el-menu-item index="about">
              <i class="el-icon-info"></i>
              <span slot="title">关于系统</span>
            </el-menu-item>
          </el-menu>
        </el-card>
      </el-col>

      <!-- 右侧设置内容 -->
      <el-col :span="18">
        <!-- 基本设置 -->
        <el-card v-if="activeMenu === 'general'" shadow="hover" class="settings-card">
          <div slot="header" class="card-header">
            <span><i class="el-icon-s-home"></i>基本设置</span>
          </div>
          <el-form label-width="120px" class="settings-form">
            <el-form-item label="系统名称">
              <el-input v-model="settings.systemName" placeholder="图书馆管理系统"></el-input>
            </el-form-item>
            <el-form-item label="系统描述">
              <el-input v-model="settings.systemDesc" type="textarea" :rows="3" placeholder="请输入系统描述"></el-input>
            </el-form-item>
            <el-form-item label="每页显示条数">
              <el-input-number v-model="settings.pageSize" :min="5" :max="100" :step="5"></el-input-number>
            </el-form-item>
            <el-form-item label="数据刷新间隔">
              <el-select v-model="settings.refreshInterval" placeholder="请选择">
                <el-option label="不自动刷新" :value="0"></el-option>
                <el-option label="30秒" :value="30"></el-option>
                <el-option label="1分钟" :value="60"></el-option>
                <el-option label="5分钟" :value="300"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="saveSettings" icon="el-icon-check">保存设置</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <!-- 外观设置 -->
        <el-card v-if="activeMenu === 'appearance'" shadow="hover" class="settings-card">
          <div slot="header" class="card-header">
            <span><i class="el-icon-picture-outline"></i>外观设置</span>
          </div>
          <el-form label-width="120px" class="settings-form">
            <el-form-item label="主题模式">
              <el-radio-group v-model="settings.theme">
                <el-radio label="light">明亮模式</el-radio>
                <el-radio label="dark">暗黑模式</el-radio>
                <el-radio label="auto">跟随系统</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="主题色">
              <el-color-picker v-model="settings.primaryColor"></el-color-picker>
            </el-form-item>
            <el-form-item label="侧边栏折叠">
              <el-switch v-model="settings.sidebarCollapsed"></el-switch>
            </el-form-item>
            <el-form-item label="显示面包屑">
              <el-switch v-model="settings.showBreadcrumb"></el-switch>
            </el-form-item>
            <el-form-item label="表格斑马纹">
              <el-switch v-model="settings.tableStripe"></el-switch>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="saveSettings" icon="el-icon-check">保存设置</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <!-- 通知设置 -->
        <el-card v-if="activeMenu === 'notification'" shadow="hover" class="settings-card">
          <div slot="header" class="card-header">
            <span><i class="el-icon-bell"></i>通知设置</span>
          </div>
          <el-form label-width="150px" class="settings-form">
            <el-form-item label="系统公告通知">
              <el-switch v-model="settings.notifyAnnouncement"></el-switch>
            </el-form-item>
            <el-form-item label="借阅到期提醒">
              <el-switch v-model="settings.notifyDueDate"></el-switch>
            </el-form-item>
            <el-form-item label="逾期通知">
              <el-switch v-model="settings.notifyOverdue"></el-switch>
            </el-form-item>
            <el-form-item label="新书上架通知">
              <el-switch v-model="settings.notifyNewBook"></el-switch>
            </el-form-item>
            <el-form-item label="提醒提前天数" v-if="settings.notifyDueDate || settings.notifyOverdue">
              <el-input-number v-model="settings.remindDays" :min="1" :max="30"></el-input-number>
              <span style="margin-left: 10px; color: #909399;">天</span>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="saveSettings" icon="el-icon-check">保存设置</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <!-- 安全设置 -->
        <el-card v-if="activeMenu === 'security'" shadow="hover" class="settings-card">
          <div slot="header" class="card-header">
            <span><i class="el-icon-lock"></i>安全设置</span>
          </div>
          <el-form label-width="150px" class="settings-form">
            <el-form-item label="登录验证码">
              <el-switch v-model="settings.verifyCode"></el-switch>
            </el-form-item>
            <el-form-item label="登录失败锁定">
              <el-switch v-model="settings.lockOnFail"></el-switch>
            </el-form-item>
            <el-form-item label="锁定阈值" v-if="settings.lockOnFail">
              <el-input-number v-model="settings.lockThreshold" :min="3" :max="10"></el-input-number>
              <span style="margin-left: 10px; color: #909399;">次</span>
            </el-form-item>
            <el-form-item label="锁定时长" v-if="settings.lockOnFail">
              <el-input-number v-model="settings.lockDuration" :min="5" :max="60" :step="5"></el-input-number>
              <span style="margin-left: 10px; color: #909399;">分钟</span>
            </el-form-item>
            <el-form-item label="会话超时">
              <el-select v-model="settings.sessionTimeout" placeholder="请选择">
                <el-option label="30分钟" :value="30"></el-option>
                <el-option label="1小时" :value="60"></el-option>
                <el-option label="2小时" :value="120"></el-option>
                <el-option label="4小时" :value="240"></el-option>
                <el-option label="8小时" :value="480"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="saveSettings" icon="el-icon-check">保存设置</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <!-- 关于系统 -->
        <el-card v-if="activeMenu === 'about'" shadow="hover" class="settings-card">
          <div slot="header" class="card-header">
            <span><i class="el-icon-info"></i>关于系统</span>
          </div>
          <div class="about-content">
            <div class="about-logo">
              <i class="el-icon-collection"></i>
            </div>
            <h3>图书馆管理系统</h3>
            <p class="version">版本号：v1.0.0</p>
            <p class="description">一款功能完善的图书馆管理系统，提供图书管理、借阅管理、用户管理等功能。</p>
            <el-divider></el-divider>
            <div class="about-info">
              <div class="info-row">
                <span class="label">开发团队：</span>
                <span class="value">图书馆开发团队</span>
              </div>
              <div class="info-row">
                <span class="label">技术支持：</span>
                <span class="value">Vue + ElementUI + Spring Boot</span>
              </div>
              <div class="info-row">
                <span class="label">发布日期：</span>
                <span class="value">2024年1月</span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { getSettings, saveSettings } from '@/api/settings'

export default {
  name: 'Settings',
  data() {
    return {
      activeMenu: 'general',
      settings: {
        // 基本设置
        systemName: '图书馆管理系统',
        systemDesc: '一款功能完善的图书馆管理系统',
        pageSize: 10,
        refreshInterval: 0,
        // 外观设置
        theme: 'light',
        primaryColor: '#409EFF',
        sidebarCollapsed: false,
        showBreadcrumb: true,
        tableStripe: true,
        // 通知设置
        notifyAnnouncement: true,
        notifyDueDate: true,
        notifyOverdue: true,
        notifyNewBook: false,
        remindDays: 3,
        // 安全设置
        verifyCode: false,
        lockOnFail: true,
        lockThreshold: 5,
        lockDuration: 30,
        sessionTimeout: 60
      }
    }
  },
  methods: {
    handleMenuSelect(index) {
      this.activeMenu = index
    },
    async saveSettings() {
      try {
        await saveSettings(this.settings)
        this.$message.success('设置保存成功')
      } catch (error) {
        console.error('保存设置失败:', error)
      }
    },
    async loadSettings() {
      try {
        const res = await getSettings()
        this.settings = { ...this.settings, ...res.data }
      } catch (error) {
        console.error('加载设置失败:', error)
      }
    }
  },
  created() {
    this.loadSettings()
  }
}
</script>

<style scoped>
.page-container {
  animation: fadeIn 0.4s ease;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

.page-header {
  margin-bottom: 20px;
}

.page-title {
  font-size: 22px;
  font-weight: 600;
  color: #303133;
  display: flex;
  align-items: center;
  gap: 10px;
  margin: 0 0 10px 0;
}

.page-title i {
  color: #667eea;
}

.page-desc {
  font-size: 15px;
  color: #909399;
}

.settings-menu-card {
  border-radius: 12px;
}

.settings-menu-card .el-menu-item {
  border-radius: 8px;
  margin: 4px 8px;
}

.settings-card {
  border-radius: 12px;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  font-size: 16px;
}

.card-header i {
  color: #667eea;
}

.settings-form {
  padding: 20px 20px 0 0;
}

.settings-form .el-switch {
  margin-right: 10px;
}

.about-content {
  text-align: center;
  padding: 40px 20px;
}

.about-logo {
  width: 100px;
  height: 100px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 20px;
}

.about-logo i {
  font-size: 50px;
  color: #fff;
}

.about-content h3 {
  font-size: 24px;
  color: #303133;
  margin-bottom: 10px;
}

.about-content .version {
  color: #909399;
  font-size: 14px;
  margin-bottom: 20px;
}

.about-content .description {
  color: #606266;
  font-size: 14px;
  line-height: 1.8;
  max-width: 500px;
  margin: 0 auto 30px;
}

.about-info {
  text-align: left;
  max-width: 300px;
  margin: 0 auto;
}

.info-row {
  display: flex;
  padding: 10px 0;
  border-bottom: 1px solid #f0f0f0;
}

.info-row:last-child {
  border-bottom: none;
}

.info-row .label {
  color: #909399;
  width: 100px;
}

.info-row .value {
  color: #303133;
  flex: 1;
}
</style>
