<template>
  <div class="page-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-title"><i class="el-icon-s-order"></i>我的借阅</h2>
      <div class="page-desc">查看和管理您的借阅记录</div>
    </div>

    <!-- 搜索栏 -->
    <el-card shadow="hover" class="search-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="借阅中" :value="0"></el-option>
            <el-option label="已归还" :value="1"></el-option>
            <el-option label="已逾期" :value="2"></el-option>
          </el-select>
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
        <span><i class="el-icon-files"></i>我的借阅记录</span>
        <el-button type="primary" icon="el-icon-plus" @click="handleAdd">借阅图书</el-button>
      </div>

      <!-- 借阅列表 -->
      <el-table :data="tableData" border style="width: 100%" :header-cell-style="{ background: '#f5f7fa', color: '#606266' }" stripe>
        <el-table-column prop="id" label="编号" width="80" align="center"></el-table-column>
        <el-table-column prop="bookName" label="书名" min-width="180"></el-table-column>
        <el-table-column prop="borrowDate" label="借阅日期" width="120" align="center"></el-table-column>
        <el-table-column prop="dueDate" label="应还日期" width="120" align="center"></el-table-column>
        <el-table-column prop="returnDate" label="归还日期" width="120" align="center">
          <template slot-scope="scope">
            <span :class="{ 'text-muted': !scope.row.returnDate }">{{ scope.row.returnDate || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template slot-scope="scope">
            <el-tag :type="getStatusType(scope.row.status)" size="small">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" align="center" fixed="right">
          <template slot-scope="scope">
            <el-button v-if="scope.row.status === 0 || scope.row.status === 2" size="mini" type="success" plain @click="handleReturn(scope.row)">归还</el-button>
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
    <el-dialog title="借阅图书" :visible.sync="dialogVisible" width="500px" center>
      <el-form :model="form" :rules="rules" ref="form" label-width="100px" class="dialog-form">
        <el-form-item label="选择图书" prop="bookId">
          <el-select v-model="form.bookId" placeholder="请选择图书" style="width: 100%;">
            <el-option v-for="book in availableBooks" :key="book.id" :label="book.name" :value="book.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="借阅人">
          <el-input :value="currentUserName" disabled></el-input>
        </el-form-item>
        <el-form-item label="借阅天数" prop="days">
          <el-slider v-model="form.days" :min="1" :max="90" show-input></el-slider>
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
import { getMyBorrows, borrowBook, returnBook } from '@/api/borrow'
import { getBooks } from '@/api/book'

export default {
  name: 'ReaderBorrow',
  data() {
    return {
      searchForm: {
        status: ''
      },
      tableData: [],
      pagination: {
        currentPage: 1,
        pageSize: 10,
        total: 0
      },
      dialogVisible: false,
      form: {
        bookId: '',
        days: 30
      },
      rules: {
        bookId: [{ required: true, message: '请选择图书', trigger: 'change' }]
      },
      availableBooks: [],
      currentUserId: null,
      currentUserName: ''
    }
  },
  created() {
    this.loadCurrentUser()
    this.loadBorrows()
    this.loadBooks()
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
    loadCurrentUser() {
      const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
      this.currentUserId = userInfo.id
      this.currentUserName = userInfo.name || userInfo.username
    },
    async loadBorrows() {
      try {
        const params = {
          current: this.pagination.currentPage,
          size: this.pagination.pageSize,
          userId: this.currentUserId,
          status: this.searchForm.status || null
        }
        const res = await getMyBorrows(params)
        this.tableData = res.data || []
        this.pagination.total = res.total || 0
      } catch (error) {
        console.error('加载借阅记录失败:', error)
      }
    },
    async loadBooks() {
      try {
        const res = await getBooks({ current: 1, size: 100 })
        this.availableBooks = (res.data || []).map(item => ({ id: item.id, name: item.name }))
      } catch (error) {
        console.error('加载图书列表失败:', error)
      }
    },
    handleSearch() {
      this.pagination.currentPage = 1
      this.loadBorrows()
    },
    handleReset() {
      this.searchForm = { status: '' }
      this.handleSearch()
    },
    handleAdd() {
      this.form = { bookId: '', days: 30 }
      this.dialogVisible = true
    },
    async handleReturn(row) {
      try {
        await this.$confirm('确定要归还这本书吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'info'
        })
        await returnBook(row.id)
        this.$message.success('归还成功')
        this.loadBorrows()
      } catch (error) {
        if (error !== 'cancel') {
          console.error('归还失败:', error)
        }
      }
    },
    async handleSubmit() {
      this.$refs.form.validate(async valid => {
        if (valid) {
          try {
            await borrowBook({
              bookId: this.form.bookId,
              userId: this.currentUserId,
              days: this.form.days
            })
            this.$message.success('借阅成功')
            this.dialogVisible = false
            this.loadBorrows()
          } catch (error) {
            console.error('借阅失败:', error)
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

.text-muted {
  color: #c0c4cc;
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
