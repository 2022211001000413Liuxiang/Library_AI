<template>
  <div class="book-browse-page page-container">
    <div class="page-header">
      <div class="page-header__left">
        <h2 class="page-title"><i class="el-icon-collection"></i>图书浏览</h2>
        <p class="page-desc">浏览图书馆藏书，发现感兴趣的图书</p>
      </div>
    </div>

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
            <el-option v-for="cat in categories" :key="cat.value" :label="cat.label" :value="cat.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="handleSearch" size="medium">搜索</el-button>
          <el-button icon="el-icon-refresh" @click="handleReset" size="medium">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <div class="book-grid" v-loading="loading">
      <el-card
        v-for="book in books"
        :key="book.id"
        shadow="hover"
        class="book-card"
      >
        <div class="book-card__cover">
          <img v-if="book.coverUrl" :src="book.coverUrl" alt="封面" class="cover-img" />
          <i v-else class="el-icon-reading"></i>
        </div>
        <div class="book-card__info">
          <h3 class="book-card__title" :title="book.name" @click="$router.push(`/book/${book.id}`)">{{ book.name }}</h3>
          <p class="book-card__author"><i class="el-icon-user"></i> {{ book.author }}</p>
          <p class="book-card__category" v-if="book.category">
            <el-tag size="mini" effect="plain">{{ getCategoryLabel(book.category) }}</el-tag>
          </p>
          <p class="book-card__publisher" v-if="book.publisher">
            <i class="el-icon-office-building"></i> {{ book.publisher }}
          </p>
          <div class="book-card__footer">
            <span class="book-card__stock" :class="{ 'stock-empty': book.stock <= 0 }">
              <i class="el-icon-goods"></i> 库存: {{ book.stock }}
            </span>
            <el-button
              type="primary"
              size="mini"
              :disabled="book.stock <= 0"
              @click="handleBorrow(book)"
            >
              {{ book.stock > 0 ? '借阅' : '已借完' }}
            </el-button>
          </div>
        </div>
      </el-card>
    </div>

    <div class="empty-state" v-if="!loading && books.length === 0">
      <i class="el-icon-collection"></i>
      <p>暂无图书数据</p>
    </div>

    <div class="pagination-wrapper" v-if="pagination.total > 0">
      <el-pagination
        background
        layout="total, sizes, prev, pager, next, jumper"
        :total="pagination.total"
        :page-sizes="[12, 24, 48]"
        :page-size="pagination.pageSize"
        :current-page="pagination.currentPage"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      ></el-pagination>
    </div>

    <el-dialog title="借阅图书" :visible.sync="dialogVisible" width="400px" center>
      <div class="borrow-dialog" v-if="selectedBook">
        <p>确定要借阅《{{ selectedBook.name }}》吗？</p>
        <el-form :model="borrowForm" label-width="80px">
          <el-form-item label="借阅天数">
            <el-slider v-model="borrowForm.days" :min="1" :max="90" :step="1" show-input></el-slider>
          </el-form-item>
        </el-form>
      </div>
      <span slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="borrowLoading" @click="confirmBorrow">确认借阅</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { getBooks } from '@/api/book'
import { borrowBook } from '@/api/borrow'

export default {
  name: 'BookBrowse',
  data() {
    return {
      books: [],
      loading: false,
      searchForm: { name: '', author: '', category: '' },
      pagination: { currentPage: 1, pageSize: 12, total: 0 },
      categories: [
        { label: '计算机', value: 'computer' },
        { label: '文学', value: 'literature' },
        { label: '历史', value: 'history' },
        { label: '科学', value: 'science' }
      ],
      dialogVisible: false,
      selectedBook: null,
      borrowForm: { days: 30 },
      borrowLoading: false
    }
  },
  created() {
    this.loadBooks()
  },
  methods: {
    async loadBooks() {
      this.loading = true
      try {
        const params = {
          current: this.pagination.currentPage,
          size: this.pagination.pageSize,
          ...this.searchForm
        }
        Object.keys(params).forEach(key => {
          if (params[key] === '' || params[key] === null) delete params[key]
        })
        const res = await getBooks(params)
        this.books = res.data || []
        this.pagination.total = res.total || 0
      } catch (error) {
        console.error('加载图书失败:', error)
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
    handleSizeChange(val) {
      this.pagination.pageSize = val
      this.pagination.currentPage = 1
      this.loadBooks()
    },
    handleCurrentChange(val) {
      this.pagination.currentPage = val
      this.loadBooks()
    },
    getCategoryLabel(value) {
      const cat = this.categories.find(c => c.value === value)
      return cat ? cat.label : value
    },
    handleBorrow(book) {
      this.selectedBook = book
      this.borrowForm.days = 30
      this.dialogVisible = true
    },
    async confirmBorrow() {
      if (!this.selectedBook) return
      const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
      this.borrowLoading = true
      try {
        await borrowBook({
          bookId: this.selectedBook.id,
          userId: userInfo.id,
          days: this.borrowForm.days
        })
        this.$message.success('借阅成功')
        this.dialogVisible = false
        this.loadBooks()
      } catch (error) {
        this.$message.error('借阅失败')
      } finally {
        this.borrowLoading = false
      }
    }
  }
}
</script>

<style scoped>
.book-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 20px;
  margin-top: 20px;
}

.book-card {
  transition: transform 0.3s;
}
.book-card:hover {
  transform: translateY(-4px);
}

.book-card__cover {
  height: 120px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 8px;
  margin-bottom: 16px;
}
.book-card__cover i {
  font-size: 48px;
  color: rgba(255,255,255,0.8);
}
.book-card__cover .cover-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 8px;
}

.book-card__title {
  font-size: 16px;
  font-weight: 600;
  margin: 0 0 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  cursor: pointer;
  color: #667eea;
}
.book-card__title:hover {
  color: #764ba2;
}

.book-card__author,
.book-card__publisher {
  font-size: 13px;
  color: #909399;
  margin: 4px 0;
}

.book-card__footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #ebeef5;
}

.book-card__stock {
  font-size: 13px;
  color: #67c23a;
}
.book-card__stock.stock-empty {
  color: #f56c6c;
}

.empty-state {
  text-align: center;
  padding: 60px 0;
  color: #909399;
}
.empty-state i {
  font-size: 64px;
  margin-bottom: 16px;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}

.borrow-dialog p {
  margin-bottom: 16px;
  font-size: 15px;
}
</style>
