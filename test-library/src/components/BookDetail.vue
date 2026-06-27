<template>
  <div class="book-detail-page page-container">
    <div class="page-header">
      <div class="page-header__left">
        <el-button icon="el-icon-arrow-left" size="small" @click="$router.back()">返回</el-button>
        <h2 class="page-title"><i class="el-icon-reading"></i>图书详情</h2>
      </div>
    </div>

    <div v-loading="loading">
      <el-row :gutter="24">
        <el-col :xs="24" :sm="8">
          <el-card shadow="hover" class="cover-card">
            <div class="cover-wrapper">
              <img v-if="book.coverUrl" :src="book.coverUrl" alt="封面" class="cover-img" />
              <i v-else class="el-icon-reading cover-icon"></i>
            </div>
            <div class="stock-info">
              <span :class="book.stock > 0 ? 'stock-available' : 'stock-empty'">
                <i :class="book.stock > 0 ? 'el-icon-circle-check' : 'el-icon-circle-close'"></i>
                {{ book.stock > 0 ? `可借库存: ${book.stock}` : '暂无库存' }}
              </span>
            </div>
            <el-button
              v-if="isReader"
              type="primary"
              :disabled="book.stock <= 0"
              @click="handleBorrow"
              style="width: 100%; margin-top: 16px;"
            >
              {{ book.stock > 0 ? '立即借阅' : '已借完' }}
            </el-button>
          </el-card>
        </el-col>

        <el-col :xs="24" :sm="16">
          <el-card shadow="hover" class="info-card">
            <h1 class="book-title">{{ book.name }}</h1>
            <div class="book-meta">
              <span><i class="el-icon-user"></i> {{ book.author }}</span>
              <span v-if="book.category"><el-tag size="small" effect="plain">{{ getCategoryLabel(book.category) }}</el-tag></span>
            </div>

            <el-divider></el-divider>

            <div class="detail-list">
              <div class="detail-item">
                <span class="detail-label">出版社</span>
                <span class="detail-value">{{ book.publisher || '-' }}</span>
              </div>
              <div class="detail-item">
                <span class="detail-label">出版日期</span>
                <span class="detail-value">{{ book.publishDate || '-' }}</span>
              </div>
              <div class="detail-item">
                <span class="detail-label">库存数量</span>
                <span class="detail-value">{{ book.stock }}</span>
              </div>
            </div>

            <el-divider></el-divider>

            <div class="description-section">
              <h3>图书简介</h3>
              <p class="description-text">{{ book.description || '暂无简介' }}</p>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <el-dialog title="借阅图书" :visible.sync="dialogVisible" width="400px" center>
      <p>确定要借阅《{{ book.name }}》吗？</p>
      <el-form :model="borrowForm" label-width="80px">
        <el-form-item label="借阅天数">
          <el-slider v-model="borrowForm.days" :min="1" :max="90" :step="1" show-input></el-slider>
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="borrowLoading" @click="confirmBorrow">确认借阅</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { getBook } from '@/api/book'
import { borrowBook } from '@/api/borrow'

export default {
  name: 'BookDetail',
  data() {
    return {
      book: {},
      loading: false,
      isReader: false,
      dialogVisible: false,
      borrowForm: { days: 30 },
      borrowLoading: false,
      categories: [
        { label: '计算机', value: 'computer' },
        { label: '文学', value: 'literature' },
        { label: '历史', value: 'history' },
        { label: '科学', value: 'science' }
      ]
    }
  },
  created() {
    this.isReader = localStorage.getItem('userRole') === 'reader'
    this.loadBook()
  },
  methods: {
    async loadBook() {
      const id = this.$route.params.id
      if (!id) return
      this.loading = true
      try {
        const res = await getBook(id)
        this.book = res.data || {}
      } catch (error) {
        console.error('加载图书详情失败:', error)
        this.$message.error('加载图书详情失败')
      } finally {
        this.loading = false
      }
    },
    getCategoryLabel(value) {
      const cat = this.categories.find(c => c.value === value)
      return cat ? cat.label : value
    },
    handleBorrow() {
      this.borrowForm.days = 30
      this.dialogVisible = true
    },
    async confirmBorrow() {
      const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
      this.borrowLoading = true
      try {
        await borrowBook({
          bookId: this.book.id,
          userId: userInfo.id,
          days: this.borrowForm.days
        })
        this.$message.success('借阅成功')
        this.dialogVisible = false
        this.loadBook()
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
.cover-card {
  text-align: center;
}

.cover-wrapper {
  height: 200px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 8px;
  margin-bottom: 16px;
}
.cover-icon {
  font-size: 80px;
  color: rgba(255,255,255,0.8);
}
.cover-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 8px;
}

.stock-info {
  font-size: 14px;
}
.stock-available { color: #67c23a; }
.stock-empty { color: #f56c6c; }

.book-title {
  font-size: 24px;
  font-weight: 700;
  margin: 0 0 12px;
}

.book-meta {
  display: flex;
  align-items: center;
  gap: 16px;
  color: #909399;
  font-size: 14px;
}

.detail-list {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.detail-item {
  display: flex;
  flex-direction: column;
}
.detail-label {
  font-size: 13px;
  color: #909399;
  margin-bottom: 4px;
}
.detail-value {
  font-size: 15px;
  color: #303133;
}

.description-section h3 {
  font-size: 16px;
  margin-bottom: 12px;
}
.description-text {
  font-size: 14px;
  color: #606266;
  line-height: 1.8;
}
</style>
