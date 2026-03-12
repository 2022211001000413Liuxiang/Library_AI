<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-header">
        <i class="el-icon-collection"></i>
        <h1>图书馆管理系统</h1>
      </div>
      <el-form :model="loginForm" :rules="rules" ref="loginForm" class="login-form">
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入用户名"
            prefix-icon="el-icon-user"
            clearable
          ></el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="el-icon-lock"
            clearable
            @keyup.enter.native="handleLogin"
          ></el-input>
        </el-form-item>
        <el-form-item prop="userType">
          <el-radio-group v-model="loginForm.userType">
            <el-radio label="admin">系统管理员</el-radio>
            <el-radio label="librarian">图书管理员</el-radio>
            <el-radio label="reader">读者</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            :loading="loading"
            class="login-button"
            @click="handleLogin"
          >
            {{ loading ? '登录中...' : '登 录' }}
          </el-button>
        </el-form-item>
      </el-form>
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
              // 保存用户信息到本地存储
              localStorage.setItem('userInfo', JSON.stringify(res.data))
              localStorage.setItem('userType', res.userType)
              localStorage.setItem('userRole', res.role)
              // 保存权限列表
              localStorage.setItem('permissions', JSON.stringify(res.data.permissions || []))
              // 保存 token
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
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-box {
  width: 420px;
  padding: 40px;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
}

.login-header {
  text-align: center;
  margin-bottom: 40px;
}

.login-header i {
  font-size: 48px;
  color: #667eea;
  margin-bottom: 16px;
}

.login-header h1 {
  font-size: 24px;
  color: #303133;
  font-weight: 600;
}

.login-form {
  margin-top: 20px;
}

.login-form .el-input {
  font-size: 15px;
}

.login-form .el-input input {
  height: 44px;
}

.login-button {
  width: 100%;
  height: 44px;
  font-size: 16px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
}

.login-button:hover {
  background: linear-gradient(135deg, #5a6fd6 0%, #6a4190 100%);
}

.login-form .el-radio-group {
  width: 100%;
  display: flex;
  justify-content: center;
  gap: 15px;
  flex-wrap: wrap;
}
</style>
