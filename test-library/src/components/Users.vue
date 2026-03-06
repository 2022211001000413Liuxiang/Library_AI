<template>
  <div class="page-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-title"><i class="el-icon-user"></i>用户管理</h2>
      <div class="page-desc">管理图书馆用户信息</div>
    </div>

    <!-- 搜索栏 -->
    <el-card shadow="hover" class="search-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="姓名">
          <el-input v-model="searchForm.name" placeholder="请输入姓名" clearable prefix-icon="el-icon-search"></el-input>
        </el-form-item>
        <el-form-item label="用户名">
          <el-input v-model="searchForm.username" placeholder="请输入用户名" clearable></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch" icon="el-icon-search">搜索</el-button>
          <el-button @click="handleReset" icon="el-icon-refresh">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 操作栏 -->
    <el-card shadow="hover" class="table-card">
      <div slot="header" class="table-header">
        <span><i class="el-icon-files"></i>用户列表</span>
        <el-button type="primary" icon="el-icon-plus" @click="handleAdd">添加用户</el-button>
      </div>

      <!-- 用户列表 -->
      <el-table :data="tableData" border style="width: 100%" :header-cell-style="{ background: '#f5f7fa', color: '#606266' }" stripe>
        <el-table-column prop="id" label="编号" width="80" align="center"></el-table-column>
        <el-table-column prop="avatar" label="头像" width="80" align="center">
          <template slot-scope="scope">
            <el-avatar :size="40" :src="scope.row.avatar || defaultAvatar"></el-avatar>
          </template>
        </el-table-column>
        <el-table-column prop="username" label="用户名" width="120"></el-table-column>
        <el-table-column prop="name" label="姓名" width="120"></el-table-column>
        <el-table-column prop="gender" label="性别" width="80" align="center">
          <template slot-scope="scope">
            <span>{{ scope.row.gender === 0 ? '男' : '女' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="电话" width="130"></el-table-column>
        <el-table-column prop="email" label="邮箱" min-width="180"></el-table-column>
        <el-table-column prop="createTime" label="注册日期" width="120" align="center">
          <template slot-scope="scope">
            {{ scope.row.createTime ? scope.row.createTime.substring(0, 10) : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === 0 ? 'success' : 'info'" size="small">
              {{ scope.row.status === 0 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" align="center" fixed="right">
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
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="600px" center>
      <el-form :model="form" :rules="rules" ref="form" label-width="100px" class="dialog-form">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名"></el-input>
        </el-form-item>
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
        <el-form-item label="状态" prop="status">
          <el-switch v-model="form.status" :active-value="0" :inactive-value="1" active-text="正常" inactive-text="禁用"></el-switch>
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
      dialogVisible: false,
      dialogTitle: '添加用户',
      form: {
        id: null,
        username: '',
        name: '',
        gender: 0,
        phone: '',
        email: '',
        status: 0
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
      this.form = { id: null, username: '', name: '', gender: 0, phone: '', email: '', status: 0 }
      this.dialogVisible = true
    },
    handleEdit(row) {
      this.dialogTitle = '编辑用户'
      this.form = { ...row }
      this.dialogVisible = true
    },
    async handleDelete(row) {
      try {
        await this.$confirm('确定要删除这个用户吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        await deleteUser(row.id)
        this.$message.success('删除成功')
        this.loadUsers()
      } catch (error) {
        if (error !== 'cancel') {
          console.error('删除失败:', error)
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
            console.error('操作失败:', error)
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

.search-card,
.table-card {
  border-radius: 12px;
  margin-bottom: 20px;
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.table-header span {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
}

.table-header i {
  color: #667eea;
}

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.dialog-form {
  padding: 0 20px;
}
</style>
