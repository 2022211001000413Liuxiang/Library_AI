<template>
  <div class="page-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-title"><i class="el-icon-reading"></i>图书管理</h2>
      <div class="page-desc">管理图书馆藏书信息</div>
    </div>

    <!-- 搜索栏 -->
    <el-card shadow="hover" class="search-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="书名">
          <el-input v-model="searchForm.name" placeholder="请输入书名" clearable prefix-icon="el-icon-search"></el-input>
        </el-form-item>
        <el-form-item label="作者">
          <el-input v-model="searchForm.author" placeholder="请输入作者" clearable></el-input>
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="searchForm.category" placeholder="请选择分类" clearable>
            <el-option label="计算机" value="computer"></el-option>
            <el-option label="文学" value="literature"></el-option>
            <el-option label="历史" value="history"></el-option>
            <el-option label="科学" value="science"></el-option>
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
        <span><i class="el-icon-files"></i>图书列表</span>
        <el-button type="primary" icon="el-icon-plus" @click="handleAdd">添加图书</el-button>
      </div>

      <!-- 图书列表 -->
      <el-table :data="tableData" border style="width: 100%" :header-cell-style="{ background: '#f5f7fa', color: '#606266' }" stripe>
        <el-table-column prop="id" label="编号" width="80" align="center"></el-table-column>
        <el-table-column prop="name" label="书名" min-width="180"></el-table-column>
        <el-table-column prop="author" label="作者" width="120"></el-table-column>
        <el-table-column prop="category" label="分类" width="100" align="center">
          <template slot-scope="scope">
            <el-tag size="medium">{{ scope.row.category }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="publisher" label="出版社" width="150"></el-table-column>
        <el-table-column prop="publishDate" label="出版日期" width="120" align="center"></el-table-column>
        <el-table-column prop="stock" label="库存" width="80" align="center">
          <template slot-scope="scope">
            <span :class="{ 'low-stock': scope.row.stock < 5 }">{{ scope.row.stock }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === 0 ? 'success' : 'danger'" size="small">
              {{ scope.row.status === 0 ? '可借' : '已借出' }}
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
        <el-form-item label="书名" prop="name">
          <el-input v-model="form.name" placeholder="请输入书名"></el-input>
        </el-form-item>
        <el-form-item label="作者" prop="author">
          <el-input v-model="form.author" placeholder="请输入作者"></el-input>
        </el-form-item>
        <el-form-item label="分类" prop="category">
          <el-select v-model="form.category" placeholder="请选择分类" style="width: 100%;">
            <el-option label="计算机" value="computer"></el-option>
            <el-option label="文学" value="literature"></el-option>
            <el-option label="历史" value="history"></el-option>
            <el-option label="科学" value="science"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="出版社" prop="publisher">
          <el-input v-model="form.publisher" placeholder="请输入出版社"></el-input>
        </el-form-item>
        <el-form-item label="出版日期" prop="publishDate">
          <el-date-picker v-model="form.publishDate" type="date" placeholder="选择日期" style="width: 100%;"></el-date-picker>
        </el-form-item>
        <el-form-item label="库存" prop="stock">
          <el-input-number v-model="form.stock" :min="0" style="width: 100%;"></el-input-number>
        </el-form-item>
        <el-form-item label="简介" prop="description">
          <el-input type="textarea" v-model="form.description" :rows="3" placeholder="请输入图书简介"></el-input>
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
    async loadBooks() {
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
        await this.$confirm('确定要删除这本书吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        await deleteBook(row.id)
        this.$message.success('删除成功')
        this.loadBooks()
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
              await updateBook(this.form.id, this.form)
              this.$message.success('编辑成功')
            } else {
              await addBook(this.form)
              this.$message.success('添加成功')
            }
            this.dialogVisible = false
            this.loadBooks()
          } catch (error) {
            console.error('操作失败:', error)
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

.low-stock {
  color: #F56C6C;
  font-weight: 600;
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
