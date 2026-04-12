<template>
  <div class="settings-page page-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-title"><i class="el-icon-s-tools"></i>系统设置</h2>
      <p class="page-desc">配置系统参数和个人偏好</p>
    </div>

    <div class="settings-container">
      <!-- 左侧设置菜单 -->
      <div class="settings-menu">
        <el-card shadow="hover" class="menu-card">
          <div
            v-for="item in menuItems"
            :key="item.key"
            :class="['menu-item', { 'menu-item--active': activeMenu === item.key }]"
            @click="activeMenu = item.key"
          >
            <i :class="item.icon"></i>
            <span>{{ item.label }}</span>
          </div>
        </el-card>
      </div>

      <!-- 右侧设置内容 -->
      <div class="settings-content">
        <!-- 基本设置 -->
        <el-card v-if="activeMenu === 'general'" shadow="hover" class="settings-card">
          <div slot="header" class="card-header">
            <span><i class="el-icon-s-home"></i>基本设置</span>
          </div>
          <el-form label-width="140px" class="settings-form">
            <el-form-item label="系统名称">
              <el-input v-model="settings.systemName" placeholder="图书馆管理系统" prefix-icon="el-icon-collection"></el-input>
            </el-form-item>
            <el-form-item label="系统描述">
              <el-input v-model="settings.systemDesc" type="textarea" :rows="3" placeholder="请输入系统描述"></el-input>
            </el-form-item>
            <el-form-item label="每页显示条数">
              <el-input-number v-model="settings.pageSize" :min="5" :max="100" :step="5"></el-input-number>
            </el-form-item>
            <el-form-item label="数据刷新间隔">
              <el-select v-model="settings.refreshInterval" placeholder="请选择" style="width: 100%;">
                <el-option label="不自动刷新" :value="0"></el-option>
                <el-option label="30秒" :value="30"></el-option>
                <el-option label="1分钟" :value="60"></el-option>
                <el-option label="5分钟" :value="300"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" icon="el-icon-check" @click="saveSettings">保存设置</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <!-- 外观设置 -->
        <el-card v-if="activeMenu === 'appearance'" shadow="hover" class="settings-card">
          <div slot="header" class="card-header">
            <span><i class="el-icon-picture-outline"></i>外观设置</span>
          </div>
          <el-form label-width="140px" class="settings-form">
            <el-form-item label="主题模式">
              <el-radio-group v-model="settings.theme">
                <el-radio label="light">
                  <span><i class="el-icon-sunny"></i> 明亮</span>
                </el-radio>
                <el-radio label="dark">
                  <span><i class="el-icon-moon"></i> 暗黑</span>
                </el-radio>
                <el-radio label="auto">
                  <span><i class="el-icon-monitor"></i> 跟随系统</span>
                </el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="主题色">
              <el-color-picker v-model="settings.primaryColor" show-alpha></el-color-picker>
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
              <el-button type="primary" icon="el-icon-check" @click="saveSettings">保存设置</el-button>
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
              <span class="unit-label">天</span>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" icon="el-icon-check" @click="saveSettings">保存设置</el-button>
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
              <span class="unit-label">次</span>
            </el-form-item>
            <el-form-item label="锁定时长" v-if="settings.lockOnFail">
              <el-input-number v-model="settings.lockDuration" :min="5" :max="60" :step="5"></el-input-number>
              <span class="unit-label">分钟</span>
            </el-form-item>
            <el-form-item label="会话超时">
              <el-select v-model="settings.sessionTimeout" placeholder="请选择" style="width: 100%;">
                <el-option label="30分钟" :value="30"></el-option>
                <el-option label="1小时" :value="60"></el-option>
                <el-option label="2小时" :value="120"></el-option>
                <el-option label="4小时" :value="240"></el-option>
                <el-option label="8小时" :value="480"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" icon="el-icon-check" @click="saveSettings">保存设置</el-button>
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
            <h3 class="about-title">图书馆管理系统</h3>
            <p class="about-version">版本号：v1.0.0</p>
            <p class="about-desc">一款功能完善的图书馆管理系统，提供图书管理、借阅管理、用户管理、AI智能助手等功能。</p>

            <el-divider></el-divider>

            <div class="about-grid">
              <div class="about-item">
                <i class="el-icon-monitor"></i>
                <div class="about-item__content">
                  <div class="about-item__label">前端技术</div>
                  <div class="about-item__value">Vue 2 + Element UI</div>
                </div>
              </div>
              <div class="about-item">
                <i class="el-icon-cpu"></i>
                <div class="about-item__content">
                  <div class="about-item__label">后端技术</div>
                  <div class="about-item__value">Spring Boot 2</div>
                </div>
              </div>
              <div class="about-item">
                <i class="el-icon-coin"></i>
                <div class="about-item__content">
                  <div class="about-item__label">数据库</div>
                  <div class="about-item__value">MySQL</div>
                </div>
              </div>
              <div class="about-item">
                <i class="el-icon-microphone"></i>
                <div class="about-item__content">
                  <div class="about-item__label">AI 服务</div>
                  <div class="about-item__value">阿里云通义千问</div>
                </div>
              </div>
            </div>
          </div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script>
import { getSettings, saveSettings } from '@/api/settings'

export default {
  name: 'Settings',
  data() {
    return {
      activeMenu: 'general',
      menuItems: [
        { key: 'general', label: '基本设置', icon: 'el-icon-s-home' },
        { key: 'appearance', label: '外观设置', icon: 'el-icon-picture-outline' },
        { key: 'notification', label: '通知设置', icon: 'el-icon-bell' },
        { key: 'security', label: '安全设置', icon: 'el-icon-lock' },
        { key: 'about', label: '关于系统', icon: 'el-icon-info' }
      ],
      settings: {
        systemName: '图书馆管理系统',
        systemDesc: '一款功能完善的图书馆管理系统',
        pageSize: 10,
        refreshInterval: 0,
        theme: 'light',
        primaryColor: '#667eea',
        sidebarCollapsed: false,
        showBreadcrumb: true,
        tableStripe: true,
        notifyAnnouncement: true,
        notifyDueDate: true,
        notifyOverdue: true,
        notifyNewBook: false,
        remindDays: 3,
        verifyCode: false,
        lockOnFail: true,
        lockThreshold: 5,
        lockDuration: 30,
        sessionTimeout: 60
      }
    }
  },
  methods: {
    async saveSettings() {
      try {
        await saveSettings(this.settings)
        this.$message.success('设置保存成功')
      } catch (error) {
        this.$message.error('保存设置失败')
      }
    },
    async loadSettings() {
      try {
        const res = await getSettings()
        if (res.data) {
          this.settings = { ...this.settings, ...res.data }
        }
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
.settings-page {
  max-width: 1200px;
  margin: 0 auto;
}

/* ========== 页面标题 ========== */
.page-header {
  margin-bottom: var(--spacing-lg);
}

.page-title {
  font-size: var(--font-size-xl);
  font-weight: 600;
  color: var(--color-text-primary);
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  margin: 0 0 var(--spacing-xs) 0;
}

.page-title i {
  color: var(--color-primary);
}

.page-desc {
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
  margin: 0;
  padding-left: 30px;
}

/* ========== 设置容器 ========== */
.settings-container {
  display: flex;
  gap: var(--spacing-lg);
}

.settings-menu {
  width: 220px;
  flex-shrink: 0;
}

.menu-card {
  border-radius: var(--radius-medium);
  position: sticky;
  top: 80px;
}

.menu-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  padding: var(--spacing-base);
  border-radius: var(--radius-small);
  cursor: pointer;
  transition: all var(--transition-fast);
  margin-bottom: var(--spacing-xs);
  color: var(--color-text-regular);
  font-size: var(--font-size-base);
}

.menu-item:hover {
  background: #f5f7fa;
  color: var(--color-primary);
}

.menu-item--active {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
  color: var(--color-primary);
  font-weight: 500;
}

.menu-item i {
  font-size: 18px;
}

/* ========== 设置内容 ========== */
.settings-content {
  flex: 1;
  min-width: 0;
}

.settings-card {
  border-radius: var(--radius-medium);
}

.card-header {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
  font-weight: 600;
}

.card-header i {
  color: var(--color-primary);
}

.settings-form {
  padding: var(--spacing-base) 0;
}

.settings-form >>> .el-form-item__label {
  font-weight: 500;
}

.settings-form >>> .el-input__inner,
.settings-form >>> .el-textarea__inner {
  border-radius: var(--radius-small);
}

.settings-form >>> .el-radio-group {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.settings-form >>> .el-radio {
  display: flex;
  align-items: center;
  gap: 4px;
}

.unit-label {
  margin-left: var(--spacing-sm);
  color: var(--color-text-secondary);
}

/* ========== 关于页面 ========== */
.about-content {
  text-align: center;
  padding: var(--spacing-xl);
}

.about-logo {
  width: 90px;
  height: 90px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto var(--spacing-base);
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.3);
}

.about-logo i {
  font-size: 42px;
  color: #fff;
}

.about-title {
  font-size: 24px;
  font-weight: 600;
  color: var(--color-text-primary);
  margin: 0 0 var(--spacing-xs) 0;
}

.about-version {
  color: var(--color-text-secondary);
  font-size: var(--font-size-sm);
  margin: 0 0 var(--spacing-base) 0;
}

.about-desc {
  color: var(--color-text-regular);
  font-size: var(--font-size-base);
  line-height: 1.6;
  max-width: 450px;
  margin: 0 auto;
}

.about-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: var(--spacing-base);
  margin-top: var(--spacing-lg);
}

.about-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-base);
  padding: var(--spacing-base);
  background: #f5f7fa;
  border-radius: var(--radius-small);
  text-align: left;
}

.about-item i {
  font-size: 24px;
  color: var(--color-primary);
}

.about-item__label {
  font-size: var(--font-size-xs);
  color: var(--color-text-secondary);
}

.about-item__value {
  font-size: var(--font-size-sm);
  font-weight: 500;
  color: var(--color-text-primary);
}

/* ========== 响应式 ========== */
@media (max-width: 900px) {
  .settings-container {
    flex-direction: column;
  }

  .settings-menu {
    width: 100%;
  }

  .menu-card {
    position: static;
  }

  .menu-item {
    display: inline-flex;
    margin-right: var(--spacing-xs);
  }
}
</style>
