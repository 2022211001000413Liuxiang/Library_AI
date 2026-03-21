<template>
  <div class="page-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-title"><i class="el-icon-microphone"></i>AI 图书助手</h2>
      <div class="page-desc">智能推荐图书，解答藏书问题</div>
    </div>

    <el-row :gutter="20">
      <!-- 功能选择 -->
      <el-col :span="24">
        <el-card shadow="hover" class="function-card">
          <el-radio-group v-model="currentMode" @change="handleModeChange">
            <el-radio-button label="recommend">
              <i class="el-icon-star-on"></i> 智能推荐
            </el-radio-button>
            <el-radio-button label="chat">
              <i class="el-icon-chat-line-round"></i> 图书咨询
            </el-radio-button>
          </el-radio-group>
        </el-card>
      </el-col>

      <!-- 智能推荐模式 -->
      <el-col :span="24" v-if="currentMode === 'recommend'">
        <el-card shadow="hover" class="chat-card">
          <div slot="header" class="card-header">
            <span><i class="el-icon-star-on"></i>智能推荐</span>
          </div>
          <el-input
            v-model="recommendInput"
            type="textarea"
            :rows="3"
            placeholder="请描述您的阅读偏好，例如：我喜欢科幻小说，我喜欢历史传记..."
          ></el-input>
          <div class="action-bar">
            <el-button type="primary" icon="el-icon-search" @click="handleRecommend" :loading="loading">
              为我推荐图书
            </el-button>
          </div>
          <!-- 推荐结果 -->
          <div v-if="recommendResult" class="result-box">
            <div class="result-title">推荐结果：</div>
            <div class="result-content">{{ recommendResult }}</div>
          </div>
        </el-card>
      </el-col>

      <!-- 图书咨询模式 -->
      <el-col :span="24" v-if="currentMode === 'chat'">
        <el-card shadow="hover" class="chat-card">
          <div slot="header" class="card-header">
            <span><i class="el-icon-chat-line-round"></i>图书咨询</span>
          </div>
          <!-- 对话列表 -->
          <div class="chat-messages" ref="chatContainer">
            <div v-for="(msg, index) in messages" :key="index" :class="['message', msg.role]">
              <div class="message-avatar">
                <el-avatar :size="36" :icon="msg.role === 'user' ? 'el-icon-user' : 'el-icon-service'"></el-avatar>
              </div>
              <div class="message-content">
                <div class="message-text">{{ msg.content }}</div>
              </div>
            </div>
          </div>
          <!-- 输入框 -->
          <div class="chat-input">
            <el-input
              v-model="chatInput"
              placeholder="请输入关于图书馆藏书的问题..."
              @keyup.enter.native="handleChat"
              :disabled="loading"
            >
              <el-button slot="append" icon="el-icon-s-promotion" @click="handleChat" :loading="loading">发送</el-button>
            </el-input>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { recommendBooks, chatWithAI } from '@/api/ai'

export default {
  name: 'AiRobot',
  data() {
    return {
      currentMode: 'recommend',
      recommendInput: '',
      recommendResult: '',
      chatInput: '',
      messages: [
        {
          role: 'assistant',
          content: '您好！我是图书馆的 AI 助手。我可以帮您：\n1. 根据您的阅读偏好推荐图书\n2. 回答关于图书馆藏书的问题\n\n请问有什么可以帮您的？'
        }
      ],
      loading: false
    }
  },
  methods: {
    handleModeChange() {
      this.recommendInput = ''
      this.recommendResult = ''
    },
    async handleRecommend() {
      if (!this.recommendInput.trim()) {
        this.$message.warning('请输入您的阅读偏好')
        return
      }

      this.loading = true
      try {
        const res = await recommendBooks(this.recommendInput)
        if (res.success) {
          this.recommendResult = res.recommendation
        } else {
          this.$message.error(res.message || '推荐失败')
        }
      } catch (error) {
        this.$message.error('推荐失败，请稍后重试')
      } finally {
        this.loading = false
      }
    },
    async handleChat() {
      if (!this.chatInput.trim()) {
        this.$message.warning('请输入问题')
        return
      }

      const question = this.chatInput
      this.messages.push({ role: 'user', content: question })
      this.chatInput = ''
      this.loading = true

      // 滚动到底部
      this.$nextTick(() => {
        this.$refs.chatContainer.scrollTop = this.$refs.chatContainer.scrollHeight
      })

      try {
        const res = await chatWithAI(question)
        if (res.success) {
          this.messages.push({ role: 'assistant', content: res.answer })
        } else {
          this.messages.push({ role: 'assistant', content: res.message || '抱歉，我暂时无法回答这个问题。' })
        }
      } catch (error) {
        this.messages.push({ role: 'assistant', content: '抱歉，我暂时无法回答这个问题，请稍后重试。' })
      } finally {
        this.loading = false
        // 滚动到底部
        this.$nextTick(() => {
          this.$refs.chatContainer.scrollTop = this.$refs.chatContainer.scrollHeight
        })
      }
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

.function-card {
  margin-bottom: 20px;
  text-align: center;
}

.function-card .el-radio-button {
  margin: 0 10px;
}

.chat-card {
  border-radius: 12px;
  min-height: 500px;
  display: flex;
  flex-direction: column;
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

.action-bar {
  margin-top: 15px;
  text-align: center;
}

.result-box {
  margin-top: 20px;
  padding: 15px;
  background: #f5f7fa;
  border-radius: 8px;
}

.result-title {
  font-weight: 600;
  color: #303133;
  margin-bottom: 10px;
}

.result-content {
  color: #606266;
  line-height: 1.8;
  white-space: pre-wrap;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 10px 0;
  max-height: 400px;
  min-height: 300px;
}

.message {
  display: flex;
  margin-bottom: 20px;
  align-items: flex-start;
}

.message.user {
  flex-direction: row-reverse;
}

.message-avatar {
  flex-shrink: 0;
  margin: 0 10px;
}

.message-content {
  max: 70%;
}

.message.user .message-content {
  text-align: right;
}

.message-text {
  display: inline-block;
  padding: 12px 16px;
  border-radius: 8px;
  line-height: 1.5;
  word-break: break-word;
}

.message.user .message-text {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
}

.message.assistant .message-text {
  background: #f5f7fa;
  color: #303133;
}

.chat-input {
  margin-top: 15px;
  display: flex;
}

.chat-input .el-input {
  flex: 1;
}
</style>
