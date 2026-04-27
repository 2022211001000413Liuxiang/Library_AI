<template>
  <div class="borrow-page page-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="page-header__left">
        <h2 class="page-title"><i class="el-icon-s-order"></i>借阅管理</h2>
        <p class="page-desc">管理图书借阅与归还</p>
      </div>
      <div class="page-header__right">
        <el-button type="primary" icon="el-icon-plus" @click="handleAdd">借阅图书</el-button>
      </div>
    </div>

    <!-- 统计概览 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :xs="12" :sm="6">
        <div class="stat-item stat-item--borrowing">
          <div class="stat-item__icon">
            <i class="el-icon-reading"></i>
          </div>
          <div class="stat-item__content">
            <div class="stat-item__value">{{ stats.borrowing }}</div>
            <div class="stat-item__label">借阅中</div>
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="6">
        <div class="stat-item stat-item--returned">
          <div class="stat-item__icon">
            <i class="el-icon-circle-check"></i>
          </div>
          <div class="stat-item__content">
            <div class="stat-item__value">{{ stats.returned }}</div>
            <div class="stat-item__label">已归还</div>
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="6">
        <div class="stat-item stat-item--overdue">
          <div class="stat-item__icon">
            <i class="el-icon-warning"></i>
          </div>
          <div class="stat-item__content">
            <div class="stat-item__value">{{ stats.overdue }}</div>
            <div class="stat-item__label">已逾期</div>
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="6">
        <div class="stat-item stat-item--total">
          <div class="stat-item__icon">
            <i class="el-icon-document"></i>
          </div>
          <div class="stat-item__content">
            <div class="stat-item__value">{{ stats.total || pagination.total }}</div>
            <div class="stat-item__label">总记录</div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 搜索栏 -->
    <el-card shadow="hover" class="search-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="书名">
          <el-input v-model="searchForm.bookName" placeholder="请输入书名" clearable prefix-icon="el-icon-search" size="medium"></el-input>
        </el-form-item>
        <el-form-item label="借阅人">
          <el-input v-model="searchForm.userName" placeholder="请输入借阅人" clearable size="medium"></el-input>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable size="medium">
            <el-option label="借阅中" :value="0">
              <span><i class="el-icon-reading"></i> 借阅中</span>
            </el-option>
            <el-option label="已归还" :value="1">
              <span><i class="el-icon-circle-check"></i> 已归还</span>
            </el-option>
            <el-option label="已逾期" :value="2">
              <span><i class="el-icon-warning"></i> 已逾期</span>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="handleSearch" size="medium">搜索</el-button>
          <el-button icon="el-icon-refresh" @click="handleReset" size="medium">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 借阅列表 -->
    <el-card shadow="hover" class="table-card">
      <div slot="header" class="card-header">
        <span><i class="el-icon-files"></i>借阅记录</span>
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
        <el-table-column prop="bookName" label="书名" min-width="180">
          <template slot-scope="scope">
            <div class="book-name">
              <i class="el-icon-reading book-icon"></i>
              <span>{{ scope.row.bookName }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="userName" label="借阅人" width="120" align="center">
          <template slot-scope="scope">
            <el-avatar size="small" :src="getAvatar(scope.row)" class="user-avatar">
              {{ scope.row.userName && scope.row.userName[0] }}
            </el-avatar>
            <span class="user-name">{{ scope.row.userName }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="borrowDate" label="借阅日期" width="120" align="center"></el-table-column>
        <el-table-column prop="dueDate" label="应还日期" width="120" align="center">
          <template slot-scope="scope">
            <span :class="{ 'overdue-text': isOverdue(scope.row) }">
              {{ scope.row.dueDate }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="returnDate" label="归还日期" width="120" align="center">
          <template slot-scope="scope">
            <span :class="['return-date', { 'return-date--empty': !scope.row.returnDate }]">
              {{ scope.row.returnDate || '-' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template slot-scope="scope">
            <el-tag :type="getStatusType(scope.row.status)" size="small" effect="dark" round>
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" align="center" fixed="right">
          <template slot-scope="scope">
            <el-button
              v-if="scope.row.status === 0 || scope.row.status === 2"
              size="mini"
              type="success"
              plain
              icon="el-icon-circle-check"
              @click="handleReturn(scope.row)"
            >
              归还
            </el-button>
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

    <!-- 借阅对话框 -->
    <el-dialog title="借阅图书" :visible.sync="dialogVisible" width="500px" center custom-class="borrow-dialog">
      <el-form :model="form" :rules="rules" ref="form" label-width="100px" class="dialog-form">
        <el-form-item label="选择图书" prop="bookId">
          <el-select v-model="form.bookId" placeholder="请选择图书" style="width: 100%;" filterable>
            <el-option
              v-for="book in availableBooks"
              :key="book.id"
              :label="book.name"
              :value="book.id"
            >
              <div class="book-option">
                <i class="el-icon-reading"></i>
                <span>{{ book.name }}</span>
                <small v-if="book.stock !== undefined">库存: {{ book.stock }}</small>
              </div>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="借阅人" prop="userId">
          <el-select v-model="form.userId" placeholder="请选择用户" style="width: 100%;" filterable>
            <el-option
              v-for="user in users"
              :key="user.id"
              :label="user.name"
              :value="user.id"
            >
              <div class="user-option">
                <el-avatar size="small">{{ user.name && user.name[0] }}</el-avatar>
                <span>{{ user.name }}</span>
              </div>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="借阅天数" prop="days">
          <div class="days-slider">
            <el-slider v-model="form.days" :min="1" :max="90" :step="1" show-input></el-slider>
          </div>
          <div class="due-date-preview">
            <i class="el-icon-calendar"></i>
            应还日期：{{ getDueDatePreview() }}
          </div>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false" icon="el-icon-close">取 消</el-button>
        <el-button type="primary" @click="handleSubmit" icon="el-icon-check">确认借阅</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { getBorrows, borrowBook, returnBook } from '@/api/borrow'
import { getBooks } from '@/api/book'
import { getUsers } from '@/api/user'

export default {
  name: 'Borrow',
  data() {
    return {
      searchForm: {
        bookName: '',
        userName: '',
        status: ''
      },
      tableData: [],
      pagination: {
        currentPage: 1,
        pageSize: 10,
        total: 0
      },
      stats: {
        borrowing: 0,
        returned: 0,
        overdue: 0,
        total: 0
      },
      loading: false,
      dialogVisible: false,
      form: {
        bookId: '',
        userId: '',
        days: 30
      },
      rules: {
        bookId: [{ required: true, message: '请选择图书', trigger: 'change' }],
        userId: [{ required: true, message: '请选择借阅人', trigger: 'change' }]
      },
      availableBooks: [],
      users: []
    }
  },
  created() {
    this.loadBorrows()
    this.loadBooks()
    this.loadUsers()
  },
  methods: {
    getStatusType(status) {
      const types = { 0: 'primary', 1: 'success', 2: 'danger' }
      return types[status] || 'info'
    },
    getStatusText(status) {
      const texts = { 0: '借阅中', 1: '已归还', 2: '已逾期' }
      return texts[status] || status
    },
    isOverdue(row) {
      if (row.status === 2) return true
      if (row.status === 0 && row.dueDate) {
        return new Date(row.dueDate) < new Date()
      }
      return false
    },
    getAvatar(row) {
      return row.userAvatar || ''
    },
    getDueDatePreview() {
      const date = new Date()
      date.setDate(date.getDate() + this.form.days)
      return date.toLocaleDateString('zh-CN')
    },
    async loadBorrows() {
      this.loading = true
      try {
        const params = {
          current: this.pagination.currentPage,
          size: this.pagination.pageSize,
          bookName: this.searchForm.bookName,
          userName: this.searchForm.userName,
          status: this.searchForm.status || null
        }
        const res = await getBorrows(params)
        this.tableData = res.data || []
        this.pagination.total = res.total || 0
        this.stats.total = this.pagination.total

        // 使用后端返回的统计数据
        if (res.stats) {
          this.stats.borrowing = res.stats.borrowing || 0
          this.stats.returned = res.stats.returned || 0
          this.stats.overdue = res.stats.overdue || 0
        }
      } catch (error) {
        console.error('加载借阅记录失败:', error)
      } finally {
        this.loading = false
      }
    },
    async loadBooks() {
      try {
        const res = await getBooks({ current: 1, size: 100 })
        this.availableBooks = (res.data || []).map(item => ({
          id: item.id,
          name: item.name,
          stock: item.stock
        }))
      } catch (error) {
        console.error('加载图书列表失败:', error)
      }
    },
    async loadUsers() {
      try {
        const res = await getUsers({ current: 1, size: 100 })
        this.users = (res.data || []).map(item => ({
          id: item.id,
          name: item.name
        }))
      } catch (error) {
        console.error('加载用户列表失败:', error)
      }
    },
    handleSearch() {
      this.pagination.currentPage = 1
      this.loadBorrows()
    },
    handleReset() {
      this.searchForm = { bookName: '', userName: '', status: '' }
      this.handleSearch()
    },
    handleAdd() {
      this.form = { bookId: '', userId: '', days: 30 }
      this.dialogVisible = true
    },
    async handleReturn(row) {
      try {
        await this.$confirm(`确定要归还《${row.bookName}》吗？`, '归还确认', {
          confirmButtonText: '确认归还',
          cancelButtonText: '取消',
          type: 'success'
        })
        await returnBook(row.id)
        this.$message.success('归还成功')
        this.loadBorrows()
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error('归还失败')
        }
      }
    },
    async handleSubmit() {
      this.$refs.form.validate(async valid => {
        if (valid) {
          try {
            await borrowBook(this.form)
            this.$message.success('借阅成功')
            this.dialogVisible = false
            this.loadBorrows()
          } catch (error) {
            this.$message.error('借阅失败')
          }
        }
      })
    },
    handleSizeChange(val) {
      this.pagination.pageSize = val
      this.loadBorrows()
    },
    handleCurrentChange(val) {
      this.pagination.currentPage = val
      this.loadBorrows()
    }
  }
}
</script>

<style scoped>
.borrow-page {
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

/* ========== 统计概览 ========== */
.stats-row {
  margin-bottom: var(--spacing-lg);
}

.stat-item {
  display: flex;
  align-items: center;
  padding: var(--spacing-base) var(--spacing-lg);
  background: var(--color-bg-card);
  border-radius: var(--radius-medium);
  box-shadow: var(--shadow-light);
  margin-bottom: var(--spacing-base);
  transition: all var(--transition-normal);
}

.stat-item:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-medium);
}

.stat-item__icon {
  width: 48px;
  height: 48px;
  border-radius: var(--radius-small);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  color: #fff;
  margin-right: var(--spacing-base);
}

.stat-item--borrowing .stat-item__icon { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
.stat-item--returned .stat-item__icon { background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%); }
.stat-item--overdue .stat-item__icon { background: linear-gradient(135deg, #f5576c 0%, #ff6b6b 100%); }
.stat-item--total .stat-item__icon { background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%); }

.stat-item__value {
  font-size: 24px;
  font-weight: 700;
  color: var(--color-text-primary);
  line-height: 1.2;
}

.stat-item__label {
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
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

.card-header span {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
  font-weight: 600;
}

.card-header i {
  color: var(--color-primary);
}

/* ========== 表格样式 ========== */
.book-name {
  display: flex;
  align-items: center;
  gap: 8px;
}

.book-icon {
  color: var(--color-primary);
}

.user-avatar {
  margin-right: 8px;
  background: linear-gradient(135deg, var(--color-primary) 0%, #764ba2 100%);
}

.user-name {
  font-size: var(--font-size-sm);
}

.overdue-text {
  color: var(--color-danger);
  font-weight: 500;
}

.return-date {
  color: var(--color-text-regular);
}

.return-date--empty {
  color: var(--color-text-placeholder);
}

/* ========== 分页 ========== */
.pagination-wrapper {
  margin-top: var(--spacing-lg);
  display: flex;
  justify-content: flex-end;
}

/* ========== 对话框 ========== */
.borrow-dialog >>> .el-dialog__header {
  background: linear-gradient(135deg, var(--color-primary) 0%, #764ba2 100%);
  color: #fff;
  padding: 20px;
}

.borrow-dialog >>> .el-dialog__title {
  color: #fff;
  font-size: 18px;
  font-weight: 600;
}

.borrow-dialog >>> .el-dialog__headerbtn .el-dialog__close {
  color: #fff;
}

.borrow-dialog >>> .el-dialog__body {
  padding: 30px 20px;
}

.book-option {
  display: flex;
  align-items: center;
  gap: 8px;
}

.book-option i {
  color: var(--color-primary);
}

.book-option small {
  color: var(--color-text-secondary);
  margin-left: auto;
}

.user-option {
  display: flex;
  align-items: center;
  gap: 8px;
}

.days-slider {
  padding: 0 10px;
}

.due-date-preview {
  margin-top: var(--spacing-sm);
  padding: var(--spacing-xs) var(--spacing-sm);
  background: #f5f7fa;
  border-radius: var(--radius-small);
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
}

.due-date-preview i {
  margin-right: 6px;
}

.dialog-footer >>> .el-button {
  border-radius: var(--radius-small);
  padding: 10px 24px;
}
</style>
