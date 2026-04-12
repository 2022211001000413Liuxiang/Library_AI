<template>
  <div class="books-page page-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="page-header__left">
        <h2 class="page-title"><i class="el-icon-reading"></i>图书管理</h2>
        <p class="page-desc">管理图书馆藏书信息</p>
      </div>
      <div class="page-header__right">
        <el-button type="primary" icon="el-icon-plus" @click="handleAdd">添加图书</el-button>
      </div>
    </div>

    <!-- 搜索栏 -->
    <el-card shadow="hover" class="search-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="书名">
          <el-input v-model="searchForm.name" placeholder="请输入书名" clearable prefix-icon="el-icon-search" size="medium"></el-input>
        </el-form-item>
        <el-form-item label="作者">
          <el-input v-model="searchForm.author" placeholder="请输入作者" clearable size="medium"></el-input>
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="searchForm.category" placeholder="请选择分类" clearable size="medium">
            <el-option label="计算机" value="computer"></el-option>
            <el-option label="文学" value="literature"></el-option>
            <el-option label="历史" value="history"></el-option>
            <el-option label="科学" value="science"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="handleSearch" size="medium">搜索</el-button>
          <el-button icon="el-icon-refresh" @click="handleReset" size="medium">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 图书列表 -->
    <el-card shadow="hover" class="table-card">
      <div slot="header" class="card-header">
        <span><i class="el-icon-files"></i>图书列表</span>
        <span class="card-count">共 {{ pagination.total }} 本图书</span>
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
        <el-table-column prop="name" label="书名" min-width="180">
          <template slot-scope="scope">
            <div class="book-name">
              <i class="el-icon-reading book-icon"></i>
              <span>{{ scope.row.name }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="author" label="作者" width="140">
          <template slot-scope="scope">
            <span class="author-text">{{ scope.row.author }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="category" label="分类" width="120" align="center">
          <template slot-scope="scope">
            <el-tag size="medium" effect="plain" :type="getCategoryType(scope.row.category)">
              {{ scope.row.category }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="publisher" label="出版社" width="150" show-overflow-tooltip></el-table-column>
        <el-table-column prop="publishDate" label="出版日期" width="120" align="center"></el-table-column>
        <el-table-column prop="stock" label="库存" width="80" align="center">
          <template slot-scope="scope">
            <span :class="['stock-badge', { 'stock-badge--low': scope.row.stock < 5 }]">
              {{ scope.row.stock }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === 0 ? 'success' : 'danger'" size="small" effect="dark" round>
              {{ scope.row.status === 0 ? '可借' : '已借出' }}
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
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="600px" center custom-class="book-dialog">
      <el-form :model="form" :rules="rules" ref="form" label-width="100px" class="dialog-form">
        <el-form-item label="书名" prop="name">
          <el-input v-model="form.name" placeholder="请输入书名" prefix-icon="el-icon-reading"></el-input>
        </el-form-item>
        <el-form-item label="作者" prop="author">
          <el-input v-model="form.author" placeholder="请输入作者" prefix-icon="el-icon-user"></el-input>
        </el-form-item>
        <el-form-item label="分类" prop="category">
          <el-select v-model="form.category" placeholder="请选择分类" style="width: 100%;">
            <el-option label="计算机" value="computer">
              <span><i class="el-icon-monitor"></i> 计算机</span>
            </el-option>
            <el-option label="文学" value="literature">
              <span><i class="el-icon-document"></i> 文学</span>
            </el-option>
            <el-option label="历史" value="history">
              <span><i class="el-icon-time"></i> 历史</span>
            </el-option>
            <el-option label="科学" value="science">
              <span><i class="el-icon-discover"></i> 科学</span>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="出版社" prop="publisher">
          <el-input v-model="form.publisher" placeholder="请输入出版社"></el-input>
        </el-form-item>
        <el-form-item label="出版日期" prop="publishDate">
          <el-date-picker v-model="form.publishDate" type="date" placeholder="选择日期" style="width: 100%;" value-format="yyyy-MM-dd"></el-date-picker>
        </el-form-item>
        <el-form-item label="库存" prop="stock">
          <el-input-number v-model="form.stock" :min="0" :max="999" style="width: 100%;"></el-input-number>
        </el-form-item>
        <el-form-item label="简介" prop="description">
          <el-input type="textarea" v-model="form.description" :rows="3" placeholder="请输入图书简介" maxlength="200" show-word-limit></el-input>
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
import { getBooks, addBook, updateBook, deleteBook } from '@/api/book'

export default {
  name: 'Books',
  data() {
    return {
      searchForm: {
        name: '',
        author: '',
        category: ''
      },
      tableData: [],
      pagination: {
        currentPage: 1,
        pageSize: 10,
        total: 0
      },
      loading: false,
      dialogVisible: false,
      dialogTitle: '添加图书',
      form: {
        id: null,
        name: '',
        author: '',
        category: '',
        publisher: '',
        publishDate: '',
        stock: 0,
        description: ''
      },
      rules: {
        name: [{ required: true, message: '请输入书名', trigger: 'blur' }],
        author: [{ required: true, message: '请输入作者', trigger: 'blur' }],
        category: [{ required: true, message: '请选择分类', trigger: 'change' }]
      }
    }
  },
  created() {
    this.loadBooks()
  },
  methods: {
    getCategoryType(category) {
      const types = {
        computer: '',
        literature: 'success',
        history: 'warning',
        science: 'info'
      }
      return types[category] || ''
    },
    async loadBooks() {
      this.loading = true
      try {
        const res = await getBooks({
          current: this.pagination.currentPage,
          size: this.pagination.pageSize,
          ...this.searchForm
        })
        this.tableData = res.data || []
        this.pagination.total = res.total || 0
      } catch (error) {
        console.error('加载图书列表失败:', error)
      } finally {
        this.loading = false
      }
    },
    handleSearch() {
      this.pagination.currentPage = 1
      this.loadBooks()
    },
    handleReset() {
      this.searchForm = { name: '', author: '', category: '' }
      this.handleSearch()
    },
    handleAdd() {
      this.dialogTitle = '添加图书'
      this.form = { id: null, name: '', author: '', category: '', publisher: '', publishDate: '', stock: 0, description: '' }
      this.dialogVisible = true
    },
    handleEdit(row) {
      this.dialogTitle = '编辑图书'
      this.form = { ...row }
      this.dialogVisible = true
    },
    async handleDelete(row) {
      try {
        await this.$confirm(`确定要删除《${row.name}》吗？此操作不可恢复。`, '删除确认', {
          confirmButtonText: '确定删除',
          cancelButtonText: '取消',
          type: 'warning'
        })
        await deleteBook(row.id)
        this.$message.success('删除成功')
        this.loadBooks()
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
              await updateBook(this.form.id, this.form)
              this.$message.success('编辑成功')
            } else {
              await addBook(this.form)
              this.$message.success('添加成功')
            }
            this.dialogVisible = false
            this.loadBooks()
          } catch (error) {
            this.$message.error('操作失败')
          }
        }
      })
    },
    handleSizeChange(val) {
      this.pagination.pageSize = val
      this.loadBooks()
    },
    handleCurrentChange(val) {
      this.pagination.currentPage = val
      this.loadBooks()
    }
  }
}
</script>

<style scoped>
.books-page {
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

.search-form >>> .el-button {
  border-radius: var(--radius-small);
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

/* ========== 表格样式 ========== */
.book-name {
  display: flex;
  align-items: center;
  gap: 8px;
}

.book-icon {
  color: var(--color-primary);
  font-size: 16px;
}

.author-text {
  color: var(--color-text-secondary);
}

.stock-badge {
  display: inline-block;
  padding: 2px 10px;
  border-radius: 10px;
  font-size: var(--font-size-sm);
  font-weight: 500;
  background: rgba(67, 233, 123, 0.1);
  color: #2eb872;
}

.stock-badge--low {
  background: rgba(245, 87, 108, 0.1);
  color: var(--color-danger);
}

/* ========== 分页 ========== */
.pagination-wrapper {
  margin-top: var(--spacing-lg);
  display: flex;
  justify-content: flex-end;
}

/* ========== 对话框 ========== */
.book-dialog >>> .el-dialog__header {
  background: linear-gradient(135deg, var(--color-primary) 0%, #764ba2 100%);
  color: #fff;
  padding: 20px;
}

.book-dialog >>> .el-dialog__title {
  color: #fff;
  font-size: 18px;
  font-weight: 600;
}

.book-dialog >>> .el-dialog__headerbtn .el-dialog__close {
  color: #fff;
}

.book-dialog >>> .el-dialog__body {
  padding: 30px 20px;
}

.dialog-form >>> .el-form-item__label {
  font-weight: 500;
}

.dialog-form >>> .el-input__inner {
  border-radius: var(--radius-small);
}

.dialog-footer >>> .el-button {
  border-radius: var(--radius-small);
  padding: 10px 24px;
}
</style>
