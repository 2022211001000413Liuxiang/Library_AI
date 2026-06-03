<template>
  <div class="profile-page page-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-title"><i class="el-icon-user"></i>个人中心</h2>
      <p class="page-desc">查看和修改个人信息</p>
    </div>

    <el-row :gutter="24">
      <!-- 左侧个人信息卡片 -->
      <el-col :xs="24" :sm="8">
        <el-card shadow="hover" class="profile-card">
          <div class="profile-card__header">
            <div class="avatar-section">
              <el-avatar :size="90" :src="userInfo.avatar || defaultAvatar" class="user-avatar">
                {{ userInfo.name && userInfo.name[0] }}
              </el-avatar>
              <el-upload
                class="avatar-upload"
                action="#"
                :show-file-list="false"
                :before-upload="beforeAvatarUpload"
                :http-request="handleAvatarUpload"
              >
                <el-button size="small" type="primary" icon="el-icon-upload2">更换头像</el-button>
              </el-upload>
            </div>
            <div class="user-status">
              <span class="user-name">{{ userInfo.name }}</span>
              <el-tag :type="getRoleTagType()" size="small" effect="dark" round>
                {{ roleName }}
              </el-tag>
            </div>
          </div>

          <div class="user-info-list">
            <div class="info-item">
              <span class="info-item__label"><i class="el-icon-user"></i> 用户名</span>
              <span class="info-item__value">{{ userInfo.username }}</span>
            </div>
            <div class="info-item">
              <span class="info-item__label"><i class="el-icon-phone"></i> 电话</span>
              <span class="info-item__value">{{ userInfo.phone || '-' }}</span>
            </div>
            <div class="info-item">
              <span class="info-item__label"><i class="el-icon-message"></i> 邮箱</span>
              <span class="info-item__value">{{ userInfo.email || '-' }}</span>
            </div>
            <div class="info-item">
              <span class="info-item__label"><i class="el-icon-calendar"></i> 注册时间</span>
              <span class="info-item__value">{{ formatDate(userInfo.createTime) }}</span>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 右侧编辑表单 -->
      <el-col :xs="24" :sm="16">
        <!-- 编辑信息表单 -->
        <el-card shadow="hover" class="edit-card">
          <div slot="header" class="card-header">
            <span><i class="el-icon-edit-outline"></i>编辑个人信息</span>
          </div>
          <el-form :model="form" :rules="rules" ref="form" label-width="100px" class="profile-form">
            <el-form-item label="姓名" prop="name">
              <el-input v-model="form.name" placeholder="请输入姓名" prefix-icon="el-icon-postcard"></el-input>
            </el-form-item>
            <el-form-item label="性别" prop="gender">
              <el-radio-group v-model="form.gender">
                <el-radio :label="0"><i class="el-icon-male"></i> 男</el-radio>
                <el-radio :label="1"><i class="el-icon-female"></i> 女</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="电话" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入电话" prefix-icon="el-icon-phone"></el-input>
            </el-form-item>
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="form.email" placeholder="请输入邮箱" prefix-icon="el-icon-message"></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" icon="el-icon-check" @click="handleSubmit">保存修改</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <!-- 修改密码 -->
        <el-card shadow="hover" class="password-card">
          <div slot="header" class="card-header">
            <span><i class="el-icon-lock"></i>修改密码</span>
          </div>
          <el-form :model="passwordForm" :rules="passwordRules" ref="passwordForm" label-width="100px" class="profile-form">
            <el-form-item label="当前密码" prop="oldPassword">
              <el-input v-model="passwordForm.oldPassword" type="password" placeholder="请输入当前密码" show-password prefix-icon="el-icon-key"></el-input>
            </el-form-item>
            <el-form-item label="新密码" prop="newPassword">
              <el-input v-model="passwordForm.newPassword" type="password" placeholder="请输入新密码" show-password prefix-icon="el-icon-key"></el-input>
            </el-form-item>
            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input v-model="passwordForm.confirmPassword" type="password" placeholder="请再次输入新密码" show-password prefix-icon="el-icon-key"></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="warning" icon="el-icon-refresh" @click="handlePasswordSubmit">修改密码</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { getProfile, updateProfile, changePassword, uploadAvatar, updateAvatar } from '@/api/profile'
import { logout } from '@/api/auth'

export default {
  name: 'Profile',
  data() {
    const validatePass = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请输入新密码'))
      } else if (value.length < 6) {
        callback(new Error('密码长度不能少于6位'))
      } else {
        if (this.passwordForm.confirmPassword !== '') {
          this.$refs.passwordForm.validateField('confirmPassword')
        }
        callback()
      }
    }
    const validatePass2 = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请再次输入密码'))
      } else if (value !== this.passwordForm.newPassword) {
        callback(new Error('两次输入密码不一致'))
      } else {
        callback()
      }
    }
    return {
      defaultAvatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png',
      userInfo: {
        id: null,
        username: '',
        name: '',
        gender: 0,
        phone: '',
        email: '',
        avatar: '',
        createTime: ''
      },
      form: {
        name: '',
        gender: 0,
        phone: '',
        email: ''
      },
      rules: {
        name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
        phone: [{ required: true, message: '请输入电话', trigger: 'blur' }],
        email: [
          { required: true, message: '请输入邮箱', trigger: 'blur' },
          { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
        ]
      },
      passwordForm: {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
      },
      passwordRules: {
        oldPassword: [{ required: true, message: '请输入当前密码', trigger: 'blur' }],
        newPassword: [{ required: true, validator: validatePass, trigger: 'blur' }],
        confirmPassword: [{ required: true, validator: validatePass2, trigger: 'blur' }]
      }
    }
  },
  computed: {
    roleName() {
      const userRole = localStorage.getItem('userRole')
      if (userRole === 'admin') return '系统管理员'
      if (userRole === 'librarian') return '图书管理员'
      return '读者'
    }
  },
  created() {
    this.loadUserInfo()
  },
  methods: {
    getRoleTagType() {
      const userRole = localStorage.getItem('userRole')
      if (userRole === 'admin') return 'danger'
      if (userRole === 'librarian') return 'primary'
      return 'success'
    },
    formatDate(dateStr) {
      if (!dateStr) return '-'
      return dateStr.substring(0, 10)
    },
    async loadUserInfo() {
      try {
        const userInfoStr = localStorage.getItem('userInfo')
        let userId = null
        if (userInfoStr) {
          const userInfo = JSON.parse(userInfoStr)
          userId = userInfo.id
        }
        const res = await getProfile(userId)
        if (res.data) {
          this.userInfo = { ...this.userInfo, ...res.data }
          this.form = {
            name: res.data.name || '',
            gender: res.data.gender || 0,
            phone: res.data.phone || '',
            email: res.data.email || ''
          }
        }
      } catch (error) {
        console.error('加载用户信息失败:', error)
      }
    },
    beforeAvatarUpload(file) {
      const isJPG = file.type === 'image/jpeg' || file.type === 'image/png'
      const isLt2M = file.size / 1024 / 1024 < 2
      if (!isJPG) {
        this.$message.error('上传头像图片只能是 JPG/PNG 格式!')
      }
      if (!isLt2M) {
        this.$message.error('上传头像图片大小不能超过 2MB!')
      }
      return isJPG && isLt2M
    },
    async handleAvatarUpload(options) {
      const file = options.file
      try {
        const res = await uploadAvatar(file)
        if (res.success) {
          await updateAvatar(this.userInfo.id, res.url)
          this.userInfo.avatar = res.url
          const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
          userInfo.avatar = res.url
          localStorage.setItem('userInfo', JSON.stringify(userInfo))
          this.$message.success('头像上传成功')
        } else {
          this.$message.error(res.message || '头像上传失败')
        }
      } catch (error) {
        this.$message.error('头像上传失败')
      }
    },
    async handleSubmit() {
      this.$refs.form.validate(async valid => {
        if (valid) {
          try {
            await updateProfile({ ...this.form, id: this.userInfo.id })
            this.$message.success('个人信息保存成功')
            this.loadUserInfo()
          } catch (error) {
            this.$message.error('保存失败')
          }
        }
      })
    },
    async handlePasswordSubmit() {
      this.$refs.passwordForm.validate(async valid => {
        if (valid) {
          try {
            await changePassword({
              oldPassword: this.passwordForm.oldPassword,
              newPassword: this.passwordForm.newPassword
            })
            this.$message.success('密码修改成功，请重新登录')
            await logout()
            localStorage.removeItem('token')
            localStorage.removeItem('userInfo')
            localStorage.removeItem('userType')
            localStorage.removeItem('userRole')
            localStorage.removeItem('permissions')
            localStorage.removeItem('ai_session_id')
            localStorage.removeItem('ai_user_id')
            this.$router.push('/login')
          } catch (error) {
            this.$message.error('修改密码失败')
          }
        }
      })
    }
  }
}
</script>

<style scoped>
.profile-page {
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

/* ========== 个人信息卡片 ========== */
.profile-card {
  border-radius: var(--radius-medium);
  overflow: hidden;
}

.profile-card__header {
  text-align: center;
  padding: var(--spacing-xl) var(--spacing-lg);
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
  border-bottom: 1px solid #f0f0f0;
}

.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--spacing-base);
}

.user-avatar {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  font-size: 32px;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.user-status {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--spacing-xs);
  margin-top: var(--spacing-sm);
}

.user-name {
  font-size: var(--font-size-lg);
  font-weight: 600;
  color: var(--color-text-primary);
}

.user-info-list {
  padding: var(--spacing-base);
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--spacing-base) var(--spacing-sm);
  border-bottom: 1px solid #f5f5f5;
}

.info-item:last-child {
  border-bottom: none;
}

.info-item__label {
  display: flex;
  align-items: center;
  gap: 8px;
  color: var(--color-text-secondary);
  font-size: var(--font-size-sm);
}

.info-item__label i {
  color: var(--color-primary);
}

.info-item__value {
  color: var(--color-text-primary);
  font-size: var(--font-size-sm);
  font-weight: 500;
}

/* ========== 编辑卡片 ========== */
.edit-card,
.password-card {
  border-radius: var(--radius-medium);
  margin-bottom: var(--spacing-lg);
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

.profile-form >>> .el-form-item__label {
  font-weight: 500;
}

.profile-form >>> .el-input__inner {
  border-radius: var(--radius-small);
}

.profile-form >>> .el-radio-group {
  display: flex;
  gap: var(--spacing-lg);
}

.profile-form >>> .el-radio {
  display: flex;
  align-items: center;
  gap: 4px;
}
</style>
