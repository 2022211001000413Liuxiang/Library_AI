<template>
  <div class="page-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-title"><i class="el-icon-user"></i>个人中心</h2>
      <div class="page-desc">查看和修改个人信息</div>
    </div>

    <el-row :gutter="20">
      <!-- 个人信息卡片 -->
      <el-col :span="8">
        <el-card shadow="hover" class="profile-card">
          <div class="avatar-section">
            <el-avatar :size="100" :src="userInfo.avatar || defaultAvatar"></el-avatar>
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
          <div class="user-info-section">
            <div class="info-item">
              <span class="label">用户名</span>
              <span class="value">{{ userInfo.username }}</span>
            </div>
            <div class="info-item">
              <span class="label">姓名</span>
              <span class="value">{{ userInfo.name }}</span>
            </div>
            <div class="info-item">
              <span class="label">角色</span>
              <el-tag type="primary" size="small">{{ roleName }}</el-tag>
            </div>
            <div class="info-item">
              <span class="label">注册时间</span>
              <span class="value">{{ formatDate(userInfo.createTime) }}</span>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 编辑信息表单 -->
      <el-col :span="16">
        <el-card shadow="hover" class="edit-card">
          <div slot="header" class="card-header">
            <span><i class="el-icon-edit"></i>编辑个人信息</span>
          </div>
          <el-form :model="form" :rules="rules" ref="form" label-width="100px" class="profile-form">
            <el-form-item label="姓名" prop="name">
              <el-input v-model="form.name" placeholder="请输入姓名"></el-input>
            </el-form-item>
            <el-form-item label="性别" prop="gender">
              <el-radio-group v-model="form.gender">
                <el-radio :label="0">男</el-radio>
                <el-radio :label="1">女</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="电话" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入电话"></el-input>
            </el-form-item>
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="form.email" placeholder="请输入邮箱"></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSubmit" icon="el-icon-check">保存修改</el-button>
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
              <el-input v-model="passwordForm.oldPassword" type="password" placeholder="请输入当前密码" show-password></el-input>
            </el-form-item>
            <el-form-item label="新密码" prop="newPassword">
              <el-input v-model="passwordForm.newPassword" type="password" placeholder="请输入新密码" show-password></el-input>
            </el-form-item>
            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input v-model="passwordForm.confirmPassword" type="password" placeholder="请再次输入新密码" show-password></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="warning" @click="handlePasswordSubmit" icon="el-icon-key">修改密码</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { getProfile, updateProfile, changePassword, uploadAvatar, updateAvatar } from '@/api/profile'

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
    // 从 localStorage 获取角色名称
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
    formatDate(dateStr) {
      if (!dateStr) return '-'
      const date = new Date(dateStr)
      return date.toLocaleDateString('zh-CN')
    },
    async loadUserInfo() {
      try {
        // 从 localStorage 获取当前登录用户ID
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
        // 上传到 OSS
        const res = await uploadAvatar(file)
        if (res.success) {
          // 保存头像 URL 到数据库
          await updateAvatar(this.userInfo.id, res.url)
          // 更新本地显示
          this.userInfo.avatar = res.url
          // 更新 localStorage
          const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
          userInfo.avatar = res.url
          localStorage.setItem('userInfo', JSON.stringify(userInfo))
          this.$message.success('头像上传成功')
        } else {
          this.$message.error(res.message || '头像上传失败')
        }
      } catch (error) {
        console.error('头像上传失败:', error)
        this.$message.error('头像上传失败')
      }
    },
    async handleSubmit() {
      this.$refs.form.validate(async valid => {
        if (valid) {
          try {
            // 传递用户ID
            const userId = this.userInfo.id
            await updateProfile({ ...this.form, id: userId })
            this.$message.success('个人信息保存成功')
            this.loadUserInfo()
          } catch (error) {
            console.error('保存失败:', error)
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
            this.passwordForm = {
              oldPassword: '',
              newPassword: '',
              confirmPassword: ''
            }
          } catch (error) {
            console.error('修改密码失败:', error)
          }
        }
      })
    }
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

.profile-card {
  border-radius: 12px;
  text-align: center;
  padding: 20px 0;
}

.avatar-section {
  padding: 20px 0;
  border-bottom: 1px solid #f0f0f0;
}

.avatar-section .el-avatar {
  margin-bottom: 16px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.avatar-upload {
  display: inline-block;
}

.user-info-section {
  padding: 20px 0;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 20px;
  border-bottom: 1px solid #f5f5f5;
}

.info-item:last-child {
  border-bottom: none;
}

.info-item .label {
  color: #909399;
  font-size: 14px;
}

.info-item .value {
  color: #303133;
  font-size: 14px;
  font-weight: 500;
}

.edit-card,
.password-card {
  border-radius: 12px;
  margin-bottom: 20px;
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

.profile-form {
  padding: 20px 20px 0 0;
}
</style>
