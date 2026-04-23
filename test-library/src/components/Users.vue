<template>
  <div class="users-page page-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="page-header__left">
        <h2 class="page-title"><i class="el-icon-user"></i>用户管理</h2>
        <p class="page-desc">管理系统用户信息</p>
      </div>
      <div class="page-header__right">
        <el-button type="primary" icon="el-icon-plus" @click="handleAdd">添加用户</el-button>
      </div>
    </div>

    <!-- 搜索栏 -->
    <el-card shadow="hover" class="search-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="姓名">
          <el-input v-model="searchForm.name" placeholder="请输入姓名" clearable prefix-icon="el-icon-search" size="medium"></el-input>
        </el-form-item>
        <el-form-item label="用户名">
          <el-input v-model="searchForm.username" placeholder="请输入用户名" clearable size="medium"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="handleSearch" size="medium">搜索</el-button>
          <el-button icon="el-icon-refresh" @click="handleReset" size="medium">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 用户列表 -->
    <el-card shadow="hover" class="table-card">
      <div slot="header" class="card-header">
        <span><i class="el-icon-files"></i>用户列表</span>
        <span class="card-count">共 {{ pagination.total }} 位用户</span>
      </div>

      <el-table
        :data="tableData"
        border
        style="width: 100%"
        :header-cell-style="{ background: '#fafbfc', color: '#606266' }"
        stripe
        v-loading="loading"
      >
        <el-table-column prop="id" label="编号" width="80" align="center"></el-table-column>
        <el-table-column label="用户信息" min-width="200">
          <template slot-scope="scope">
            <div class="user-info-cell">
              <el-avatar :size="44" :src="scope.row.avatar || defaultAvatar" class="user-avatar">
                {{ scope.row.name && scope.row.name[0] }}
              </el-avatar>
              <div class="user-details">
                <div class="user-name">{{ scope.row.name }}</div>
                <div class="user-username">@{{ scope.row.username }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="gender" label="性别" width="80" align="center">
          <template slot-scope="scope">
            <i :class="['gender-icon', scope.row.gender === 0 ? 'el-icon-male gender-icon--male' : 'el-icon-female gender-icon--female']"></i>
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="电话" width="140">
          <template slot-scope="scope">
            <span class="phone-text">{{ scope.row.phone || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="email" label="邮箱" min-width="200" show-overflow-tooltip></el-table-column>
        <el-table-column prop="createTime" label="注册日期" width="120" align="center">
          <template slot-scope="scope">
            <span class="date-text">{{ scope.row.createTime ? scope.row.createTime.substring(0, 10) : '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === 0 ? 'success' : 'info'" size="small" effect="dark" round>
              {{ scope.row.status === 0 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" align="center" fixed="right">
          <template slot-scope="scope">
            <el-button size="mini" type="primary" plain @click="handleEdit(scope.row)" icon="el-icon-edit">编辑</el-button>
            <el-button size="mini" type="danger" plain @click="handleDelete(scope.row)" icon="el-icon-delete">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="pagination.currentPage"
          :page-sizes="[10, 20, 50, 100]"
          :page-size="pagination.pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="pagination.total"
          background
        >
        </el-pagination>
      </div>
    </el-card>

    <!-- 添加/编辑对话框 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="550px" center custom-class="user-dialog">
      <el-form :model="form" :rules="rules" ref="form" label-width="100px" class="dialog-form">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" prefix-icon="el-icon-user"></el-input>
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" placeholder="请输入姓名" prefix-icon="el-icon-postcard"></el-input>
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="form.gender">
            <el-radio :label="0">
              <i class="el-icon-male"></i> 男
            </el-radio>
            <el-radio :label="1">
              <i class="el-icon-female"></i> 女
            </el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="电话" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入电话" prefix-icon="el-icon-phone"></el-input>
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱" prefix-icon="el-icon-message"></el-input>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-switch
            v-model="form.status"
            :active-value="0"
            :inactive-value="1"
            active-text="正常"
            inactive-text="禁用"
          ></el-switch>
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="form.role" placeholder="请选择角色" style="width: 100%;">
            <el-option label="读者" value="reader"></el-option>
            <el-option label="图书管理员" value="librarian"></el-option>
            <el-option label="系统管理员" value="admin"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false" icon="el-icon-close">取 消</el-button>
        <el-button type="primary" @click="handleSubmit" icon="el-icon-check">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { getUsers, addUser, updateUser, deleteUser } from '@/api/user'

export default {
  name: 'Users',
  data() {
    return {
      defaultAvatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png',
      searchForm: {
        name: '',
        username: ''
      },
      tableData: [],
      pagination: {
        currentPage: 1,
        pageSize: 10,
        total: 0
      },
      loading: false,
      dialogVisible: false,
      dialogTitle: '添加用户',
      form: {
        id: null,
        username: '',
        name: '',
        gender: 0,
        phone: '',
        email: '',
        status: 0,
        role: 'reader'
      },
      rules: {
        username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
        name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
        phone: [{ required: true, message: '请输入电话', trigger: 'blur' }],
        email: [
          { required: true, message: '请输入邮箱', trigger: 'blur' },
          { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.loadUsers()
  },
  methods: {
    async loadUsers() {
      this.loading = true
      try {
        const res = await getUsers({
          current: this.pagination.currentPage,
          size: this.pagination.pageSize,
          ...this.searchForm
        })
        this.tableData = res.data || []
        this.pagination.total = res.total || 0
      } catch (error) {
        console.error('加载用户列表失败:', error)
      } finally {
        this.loading = false
      }
    },
    handleSearch() {
      this.pagination.currentPage = 1
      this.loadUsers()
    },
    handleReset() {
      this.searchForm = { name: '', username: '' }
      this.handleSearch()
    },
    handleAdd() {
      this.dialogTitle = '添加用户'
      this.form = { id: null, username: '', name: '', gender: 0, phone: '', email: '', status: 0, role: 'reader' }
      this.dialogVisible = true
    },
    handleEdit(row) {
      this.dialogTitle = '编辑用户'
      this.form = { ...row }
      this.dialogVisible = true
    },
    async handleDelete(row) {
      try {
        await this.$confirm(`确定要删除用户「${row.name}」吗？此操作不可恢复。`, '删除确认', {
          confirmButtonText: '确定删除',
          cancelButtonText: '取消',
          type: 'warning'
        })
        await deleteUser(row.id)
        this.$message.success('删除成功')
        this.loadUsers()
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error('删除失败')
        }
      }
    },
    async handleSubmit() {
      this.$refs.form.validate(async valid => {
        if (valid) {
          try {
            if (this.form.id) {
              await updateUser(this.form.id, this.form)
              this.$message.success('编辑成功')
            } else {
              await addUser(this.form)
              this.$message.success('添加成功')
            }
            this.dialogVisible = false
            this.loadUsers()
          } catch (error) {
            this.$message.error('操作失败')
          }
        }
      })
    },
    handleSizeChange(val) {
      this.pagination.pageSize = val
      this.loadUsers()
    },
    handleCurrentChange(val) {
      this.pagination.currentPage = val
      this.loadUsers()
    }
  }
}
</script>

<style scoped>
.users-page {
  max-width: 1400px;
  margin: 0 auto;
}

/* ========== 页面标题 ========== */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
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

/* ========== 搜索卡片 ========== */
.search-card {
  border-radius: var(--radius-medium);
  margin-bottom: var(--spacing-lg);
}

.search-form >>> .el-form-item {
  margin-bottom: 0;
}

/* ========== 表格卡片 ========== */
.table-card {
  border-radius: var(--radius-medium);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--spacing-xs) 0;
}

.card-header span:first-child {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
  font-weight: 600;
}

.card-header i {
  color: var(--color-primary);
}

.card-count {
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
  font-weight: normal !important;
}

/* ========== 用户信息单元格 ========== */
.user-info-cell {
  display: flex;
  align-items: center;
  gap: var(--spacing-base);
}

.user-avatar {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  flex-shrink: 0;
}

.user-details {
  min-width: 0;
}

.user-name {
  font-size: var(--font-size-base);
  font-weight: 500;
  color: var(--color-text-primary);
}

.user-username {
  font-size: var(--font-size-xs);
  color: var(--color-text-secondary);
}

/* ========== 性别图标 ========== */
.gender-icon {
  font-size: 18px;
}

.gender-icon--male {
  color: #409EFF;
}

.gender-icon--female {
  color: #f093fb;
}

/* ========== 电话和日期 ========== */
.phone-text {
  color: var(--color-text-regular);
  font-family: 'Monaco', 'Menlo', monospace;
}

.date-text {
  color: var(--color-text-secondary);
  font-size: var(--font-size-sm);
}

/* ========== 分页 ========== */
.pagination-wrapper {
  margin-top: var(--spacing-lg);
  display: flex;
  justify-content: flex-end;
}

/* ========== 对话框 ========== */
.user-dialog >>> .el-dialog__header {
  background: linear-gradient(135deg, var(--color-primary) 0%, #764ba2 100%);
  color: #fff;
  padding: 20px;
}

.user-dialog >>> .el-dialog__title {
  color: #fff;
  font-size: 18px;
  font-weight: 600;
}

.user-dialog >>> .el-dialog__headerbtn .el-dialog__close {
  color: #fff;
}

.user-dialog >>> .el-dialog__body {
  padding: 30px 20px;
}

.dialog-form >>> .el-form-item__label {
  font-weight: 500;
}

.dialog-form >>> .el-input__inner {
  border-radius: var(--radius-small);
}

.dialog-form >>> .el-radio-group {
  display: flex;
  gap: var(--spacing-lg);
}

.dialog-form >>> .el-radio {
  display: flex;
  align-items: center;
  gap: 4px;
}

.dialog-footer >>> .el-button {
  border-radius: var(--radius-small);
  padding: 10px 24px;
}
</style>
