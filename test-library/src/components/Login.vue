<template>
  <div class="login-page">
    <!-- 背景装饰 -->
    <div class="bg-decoration">
      <div class="circle circle-1"></div>
      <div class="circle circle-2"></div>
      <div class="circle circle-3"></div>
    </div>

    <div class="login-container">
      <!-- 左侧品牌区域 -->
      <div class="brand-section">
        <div class="brand-content">
          <div class="brand-icon">
            <i class="el-icon-collection"></i>
          </div>
          <h1 class="brand-title">图书馆管理系统</h1>
          <p class="brand-desc">智能、高效、便捷的图书馆管理解决方案</p>

          <div class="feature-list">
            <div class="feature-item">
              <i class="el-icon-reading"></i>
              <span>图书管理</span>
            </div>
            <div class="feature-item">
              <i class="el-icon-s-order"></i>
              <span>借阅管理</span>
            </div>
            <div class="feature-item">
              <i class="el-icon-microphone"></i>
              <span>AI智能助手</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧登录表单区域 -->
      <div class="form-section">
        <div class="login-box">
          <div class="login-header">
            <h2>欢迎回来</h2>
            <p>请登录您的账户</p>
          </div>

          <el-form
            :model="loginForm"
            :rules="rules"
            ref="loginForm"
            class="login-form"
            @submit.native.prevent
          >
            <el-form-item prop="username">
              <el-input
                v-model="loginForm.username"
                placeholder="请输入用户名"
                prefix-icon="el-icon-user"
                clearable
                size="medium"
              ></el-input>
            </el-form-item>

            <el-form-item prop="password">
              <el-input
                v-model="loginForm.password"
                type="password"
                placeholder="请输入密码"
                prefix-icon="el-icon-lock"
                clearable
                size="medium"
                @keyup.enter.native="handleLogin"
              ></el-input>
            </el-form-item>

            <el-form-item prop="userType">
              <div class="role-selector">
                <div class="role-label">选择身份</div>
                <el-radio-group v-model="loginForm.userType" class="role-group">
                  <el-radio-button label="admin">
                    <i class="el-icon-s-tools"></i> 系统管理员
                  </el-radio-button>
                  <el-radio-button label="librarian">
                    <i class="el-icon-reading"></i> 图书管理员
                  </el-radio-button>
                  <el-radio-button label="reader">
                    <i class="el-icon-user"></i> 读者
                  </el-radio-button>
                </el-radio-group>
              </div>
            </el-form-item>

            <el-form-item>
              <el-button
                type="primary"
                :loading="loading"
                class="login-button"
                size="medium"
                @click="handleLogin"
              >
                {{ loading ? '登录中...' : '登 录' }}
              </el-button>
            </el-form-item>
          </el-form>

          <div class="login-tip">
            <i class="el-icon-info"></i>
            <span>默认账号：admin / 123456</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { login } from '@/api/auth'

export default {
  name: 'Login',
  data() {
    return {
      loginForm: {
        username: '',
        password: '',
        userType: 'admin'
      },
      rules: {
        username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
      },
      loading: false
    }
  },
  methods: {
    handleLogin() {
      this.$refs.loginForm.validate(async valid => {
        if (valid) {
          this.loading = true
          try {
            const res = await login(this.loginForm)
            if (res.success) {
              localStorage.setItem('userInfo', JSON.stringify(res.data))
              localStorage.setItem('userType', res.userType)
              localStorage.setItem('userRole', res.role)
              localStorage.setItem('permissions', JSON.stringify(res.data.permissions || []))
              localStorage.setItem('token', res.token)

              this.$message.success(res.message || '登录成功')
              this.$router.push('/home')
            } else {
              this.$message.error(res.message || '登录失败')
            }
          } catch (error) {
            this.$message.error('登录失败，请检查网络')
          } finally {
            this.loading = false
          }
        }
      })
    }
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  position: relative;
  overflow: hidden;
}

/* 背景装饰圆形 */
.bg-decoration {
  position: absolute;
  width: 100%;
  height: 100%;
  overflow: hidden;
}

.circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  animation: float 20s infinite ease-in-out;
}

.circle-1 {
  width: 400px;
  height: 400px;
  top: -100px;
  right: -100px;
  animation-delay: 0s;
}

.circle-2 {
  width: 300px;
  height: 300px;
  bottom: -50px;
  left: -50px;
  animation-delay: -5s;
}

.circle-3 {
  width: 200px;
  height: 200px;
  top: 50%;
  left: 30%;
  animation-delay: -10s;
}

@keyframes float {
  0%, 100% {
    transform: translate(0, 0) scale(1);
  }
  25% {
    transform: translate(20px, -20px) scale(1.05);
  }
  50% {
    transform: translate(0, -40px) scale(1);
  }
  75% {
    transform: translate(-20px, -20px) scale(0.95);
  }
}

/* 主容器 */
.login-container {
  display: flex;
  min-height: 100vh;
  position: relative;
  z-index: 1;
}

/* 左侧品牌区域 */
.brand-section {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: var(--spacing-xl);
}

.brand-content {
  max-width: 480px;
  color: #fff;
}

.brand-icon {
  width: 100px;
  height: 100px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: var(--spacing-xl);
  backdrop-filter: blur(10px);
  animation: pulse 3s infinite ease-in-out;
}

.brand-icon i {
  font-size: 48px;
  color: #fff;
}

@keyframes pulse {
  0%, 100% {
    transform: scale(1);
    box-shadow: 0 0 0 0 rgba(255, 255, 255, 0.4);
  }
  50% {
    transform: scale(1.05);
    box-shadow: 0 0 30px 10px rgba(255, 255, 255, 0.2);
  }
}

.brand-title {
  font-size: 42px;
  font-weight: 700;
  margin-bottom: var(--spacing-base);
  line-height: 1.2;
}

.brand-desc {
  font-size: var(--font-size-lg);
  opacity: 0.9;
  margin-bottom: var(--spacing-xl);
  line-height: 1.6;
}

.feature-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-base);
}

.feature-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  font-size: var(--font-size-base);
  opacity: 0.9;
}

.feature-item i {
  width: 36px;
  height: 36px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
}

/* 右侧表单区域 */
.form-section {
  width: 480px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: var(--spacing-xl);
}

.login-box {
  width: 100%;
  max-width: 380px;
  padding: var(--spacing-xl);
  background: #fff;
  border-radius: var(--radius-large);
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  animation: slideUp 0.5s ease-out;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.login-header {
  text-align: center;
  margin-bottom: var(--spacing-xl);
}

.login-header h2 {
  font-size: 26px;
  font-weight: 600;
  color: var(--color-text-primary);
  margin-bottom: var(--spacing-xs);
}

.login-header p {
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
}

/* 表单样式 */
.login-form >>> .el-form-item {
  margin-bottom: var(--spacing-lg);
}

.login-form >>> .el-input__inner {
  border-radius: var(--radius-small);
  height: 44px;
  font-size: var(--font-size-base);
  border: 1px solid var(--color-border);
  transition: all var(--transition-fast);
}

.login-form >>> .el-input__inner:focus {
  border-color: var(--color-primary);
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.login-form >>> .el-input__prefix {
  color: var(--color-text-placeholder);
}

/* 角色选择器 */
.role-selector {
  text-align: center;
}

.role-label {
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
  margin-bottom: var(--spacing-sm);
}

.role-group {
  display: flex;
  width: 100%;
}

.role-group >>> .el-radio-button {
  flex: 1;
}

.role-group >>> .el-radio-button__inner {
  width: 100%;
  padding: 10px 8px;
  font-size: 12px;
  border-radius: 0;
  border-left: 1px solid #dcdfe6;
  white-space: normal;
  line-height: 1.4;
}

.role-group >>> .el-radio-button:first-child .el-radio-button__inner {
  border-radius: var(--radius-small) 0 0 var(--radius-small);
}

.role-group >>> .el-radio-button:last-child .el-radio-button__inner {
  border-radius: 0 var(--radius-small) var(--radius-small) 0;
}

.role-group >>> .el-radio-button__orig-radio:checked + .el-radio-button__inner {
  background: linear-gradient(135deg, var(--color-primary) 0%, #764ba2 100%);
  border-color: var(--color-primary);
}

.role-group >>> .el-radio-button__inner i {
  display: block;
  font-size: 16px;
  margin-bottom: 4px;
}

/* 登录按钮 */
.login-button {
  width: 100%;
  height: 48px;
  font-size: 16px;
  background: linear-gradient(135deg, var(--color-primary) 0%, #764ba2 100%);
  border: none;
  border-radius: var(--radius-small);
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
  transition: all var(--transition-normal);
}

.login-button:hover {
  background: linear-gradient(135deg, var(--color-primary-dark) 0%, #6a4190 100%);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.5);
  transform: translateY(-2px);
}

.login-button:active {
  transform: translateY(0);
}

/* 登录提示 */
.login-tip {
  margin-top: var(--spacing-lg);
  padding: var(--spacing-sm) var(--spacing-base);
  background: #f5f7fa;
  border-radius: var(--radius-small);
  font-size: var(--font-size-xs);
  color: var(--color-text-secondary);
  text-align: center;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
}

.login-tip i {
  color: var(--color-info);
}

/* 响应式 */
@media (max-width: 1024px) {
  .brand-section {
    display: none;
  }

  .form-section {
    width: 100%;
  }
}

@media (max-width: 480px) {
  .login-box {
    padding: var(--spacing-lg);
  }

  .role-group {
    flex-direction: column;
  }

  .role-group >>> .el-radio-button {
    width: 100%;
  }

  .role-group >>> .el-radio-button__inner {
    border-radius: var(--radius-small) !important;
    border-left: 1px solid #dcdfe6;
    margin-bottom: 4px;
  }
}
</style>
