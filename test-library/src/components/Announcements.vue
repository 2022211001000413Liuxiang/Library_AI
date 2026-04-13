<template>
  <div class="announcements-page page-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="page-header__left">
        <h2 class="page-title"><i class="el-icon-bell"></i>公告管理</h2>
        <p class="page-desc">发布和管理系统公告</p>
      </div>
      <div class="page-header__right" v-if="isAdmin">
        <el-button type="primary" icon="el-icon-plus" @click="handleAdd">发布公告</el-button>
      </div>
    </div>

    <!-- 搜索栏 -->
    <el-card shadow="hover" class="search-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="公告标题">
          <el-input v-model="searchForm.title" placeholder="请输入公告标题" clearable prefix-icon="el-icon-search" size="medium"></el-input>
        </el-form-item>
        <el-form-item label="优先级">
          <el-select v-model="searchForm.priority" placeholder="请选择" clearable size="medium">
            <el-option label="普通" :value="0">
              <span><i class="el-icon-info"></i> 普通</span>
            </el-option>
            <el-option label="重要" :value="1">
              <span><i class="el-icon-warning"></i> 重要</span>
            </el-option>
            <el-option label="紧急" :value="2">
              <span><i class="el-icon-error"></i> 紧急</span>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="handleSearch" size="medium">搜索</el-button>
          <el-button icon="el-icon-refresh" @click="handleReset" size="medium">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 公告列表 -->
    <el-card shadow="hover" class="table-card">
      <div slot="header" class="card-header">
        <span><i class="el-icon-files"></i>公告列表</span>
        <span class="card-count">共 {{ pagination.total }} 条公告</span>
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
        <el-table-column prop="title" label="标题" min-width="200">
          <template slot-scope="scope">
            <span class="title-link" @click="handleView(scope.row)">{{ scope.row.title }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="priority" label="优先级" width="100" align="center">
          <template slot-scope="scope">
            <el-tag :type="getPriorityType(scope.row.priority)" size="small" effect="dark" round>
              {{ getPriorityText(scope.row.priority) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="publisher" label="发布人" width="120" align="center"></el-table-column>
        <el-table-column prop="publishTime" label="发布时间" width="180" align="center">
          <template slot-scope="scope">
            <span class="date-text">{{ scope.row.publishTime ? scope.row.publishTime.substring(0, 19) : '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'info'" size="small" effect="dark" round>
              {{ scope.row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" align="center" fixed="right" v-if="isAdmin">
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

    <!-- 新增/编辑对话框 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="600px" center custom-class="announcement-dialog">
      <el-form :model="form" :rules="rules" ref="form" label-width="100px" class="dialog-form">
        <el-form-item label="公告标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入公告标题" maxlength="200" show-word-limit prefix-icon="el-icon-document"></el-input>
        </el-form-item>
        <el-form-item label="优先级" prop="priority">
          <el-radio-group v-model="form.priority">
            <el-radio :label="0">
              <i class="el-icon-info"></i> 普通
            </el-radio>
            <el-radio :label="1">
              <i class="el-icon-warning"></i> 重要
            </el-radio>
            <el-radio :label="2">
              <i class="el-icon-error"></i> 紧急
            </el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-switch
            v-model="form.status"
            :active-value="1"
            :inactive-value="0"
            active-text="启用"
            inactive-text="禁用"
          ></el-switch>
        </el-form-item>
        <el-form-item label="发布人" prop="publisher">
          <el-input v-model="form.publisher" placeholder="请输入发布人姓名" prefix-icon="el-icon-user"></el-input>
        </el-form-item>
        <el-form-item label="发布时间" prop="publishTime">
          <el-date-picker
            v-model="form.publishTime"
            type="datetime"
            placeholder="选择发布时间"
            value-format="yyyy-MM-dd HH:mm:ss"
            style="width: 100%"
          ></el-date-picker>
        </el-form-item>
        <el-form-item label="公告内容" prop="content">
          <el-input type="textarea" v-model="form.content" placeholder="请输入公告内容" :rows="5" maxlength="2000" show-word-limit></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false" icon="el-icon-close">取 消</el-button>
        <el-button type="primary" @click="handleSubmit" icon="el-icon-check" :loading="submitting">确 定</el-button>
      </span>
    </el-dialog>

    <!-- 公告详情对话框 -->
    <el-dialog title="公告详情" :visible.sync="viewDialogVisible" width="600px" center custom-class="announcement-dialog">
      <div class="announcement-view" v-if="currentAnnouncement">
        <div class="announcement-header">
          <h2 class="announcement-title">{{ currentAnnouncement.title }}</h2>
          <div class="announcement-meta">
            <el-tag :type="getPriorityType(currentAnnouncement.priority)" size="small" effect="dark">
              {{ getPriorityText(currentAnnouncement.priority) }}
            </el-tag>
            <span class="meta-item">
              <i class="el-icon-user"></i> {{ currentAnnouncement.publisher }}
            </span>
            <span class="meta-item">
              <i class="el-icon-time"></i> {{ currentAnnouncement.publishTime ? currentAnnouncement.publishTime.substring(0, 19) : '-' }}
            </span>
            <el-tag :type="currentAnnouncement.status === 1 ? 'success' : 'info'" size="small">
              {{ currentAnnouncement.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </div>
        </div>
        <el-divider></el-divider>
        <div class="announcement-content">{{ currentAnnouncement.content }}</div>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="viewDialogVisible = false">关 闭</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { getAnnouncements, addAnnouncement, updateAnnouncement, deleteAnnouncement } from '@/api/announcement'

export default {
  name: 'Announcements',
  data() {
    return {
      isAdmin: localStorage.getItem('userRole') === 'admin',
      searchForm: {
        title: '',
        priority: null
      },
      tableData: [],
      pagination: {
        currentPage: 1,
        pageSize: 10,
        total: 0
      },
      loading: false,
      dialogVisible: false,
      dialogTitle: '发布公告',
      submitting: false,
      form: {
        id: null,
        title: '',
        content: '',
        priority: 0,
        status: 1,
        publisher: '',
        publishTime: null
      },
      rules: {
        title: [{ required: true, message: '请输入公告标题', trigger: 'blur' }],
        content: [{ required: true, message: '请输入公告内容', trigger: 'blur' }]
      },
      viewDialogVisible: false,
      currentAnnouncement: null
    }
  },
  created() {
    this.loadData()
  },
  methods: {
    async loadData() {
      this.loading = true
      try {
        const res = await getAnnouncements({
          current: this.pagination.currentPage,
          size: this.pagination.pageSize,
          title: this.searchForm.title || null,
          priority: this.searchForm.priority
        })
        this.tableData = res.data || []
        this.pagination.total = res.total || 0
      } catch (error) {
        console.error('加载公告列表失败:', error)
      } finally {
        this.loading = false
      }
    },
    handleSearch() {
      this.pagination.currentPage = 1
      this.loadData()
    },
    handleReset() {
      this.searchForm = { title: '', priority: null }
      this.pagination.currentPage = 1
      this.loadData()
    },
    handleSizeChange(val) {
      this.pagination.pageSize = val
      this.pagination.currentPage = 1
      this.loadData()
    },
    handleCurrentChange(val) {
      this.pagination.currentPage = val
      this.loadData()
    },
    handleAdd() {
      const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
      this.dialogTitle = '发布公告'
      this.form = {
        id: null,
        title: '',
        content: '',
        priority: 0,
        status: 1,
        publisher: userInfo.name || userInfo.username || '',
        publishTime: null
      }
      this.dialogVisible = true
    },
    handleEdit(row) {
      this.dialogTitle = '编辑公告'
      this.form = { ...row }
      this.dialogVisible = true
    },
    handleView(row) {
      this.currentAnnouncement = row
      this.viewDialogVisible = true
    },
    async handleDelete(row) {
      try {
        await this.$confirm(`确定要删除公告「${row.title}」吗？此操作不可恢复。`, '删除确认', {
          confirmButtonText: '确定删除',
          cancelButtonText: '取消',
          type: 'warning'
        })
        await deleteAnnouncement(row.id)
        this.$message.success('删除成功')
        this.loadData()
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error('删除失败')
        }
      }
    },
    handleDialogClose() {
      this.$refs.form && this.$refs.form.resetFields()
    },
    async handleSubmit() {
      this.$refs.form.validate(async valid => {
        if (valid) {
          this.submitting = true
          try {
            if (this.form.id) {
              await updateAnnouncement(this.form.id, this.form)
              this.$message.success('更新成功')
            } else {
              await addAnnouncement(this.form)
              this.$message.success('发布成功')
            }
            this.dialogVisible = false
            this.loadData()
          } catch (error) {
            this.$message.error('操作失败')
          } finally {
            this.submitting = false
          }
        }
      })
    },
    getPriorityType(priority) {
      const types = { 0: 'info', 1: 'warning', 2: 'danger' }
      return types[priority] || 'info'
    },
    getPriorityText(priority) {
      const texts = { 0: '普通', 1: '重要', 2: '紧急' }
      return texts[priority] || '普通'
    }
  }
}
</script>

<style scoped>
.announcements-page {
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

.title-link {
  color: var(--color-primary);
  cursor: pointer;
}

.title-link:hover {
  text-decoration: underline;
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
.announcement-dialog >>> .el-dialog__header {
  background: linear-gradient(135deg, var(--color-primary) 0%, #764ba2 100%);
  color: #fff;
  padding: 20px;
}

.announcement-dialog >>> .el-dialog__title {
  color: #fff;
  font-size: 18px;
  font-weight: 600;
}

.announcement-dialog >>> .el-dialog__headerbtn .el-dialog__close {
  color: #fff;
}

.announcement-dialog >>> .el-dialog__body {
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

/* ========== 公告详情 ========== */
.announcement-view {
  padding: var(--spacing-base) 0;
}

.announcement-title {
  text-align: center;
  font-size: 20px;
  font-weight: 600;
  color: var(--color-text-primary);
  margin: 0 0 var(--spacing-base) 0;
}

.announcement-meta {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: var(--spacing-base);
  flex-wrap: wrap;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
  color: var(--color-text-secondary);
  font-size: var(--font-size-sm);
}

.meta-item i {
  color: var(--color-primary);
}

.announcement-content {
  line-height: 1.8;
  white-space: pre-wrap;
  color: var(--color-text-regular);
}
</style>
