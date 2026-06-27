<template>
  <div class="ai-robot-page page-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="page-header__left">
        <h2 class="page-title"><i class="el-icon-microphone"></i>AI 图书助手</h2>
        <p class="page-desc">智能推荐图书，解答藏书问题</p>
      </div>
    </div>

    <div class="ai-container">
      <!-- 左侧功能选择 -->
      <div class="ai-sidebar">
        <el-card shadow="hover" class="mode-card">
          <div class="mode-title">功能模式</div>
          <div
            class="mode-item"
            :class="{ 'mode-item--active': currentMode === 'recommend' }"
            @click="currentMode = 'recommend'"
          >
            <div class="mode-item__icon">
              <i class="el-icon-star-on"></i>
            </div>
            <div class="mode-item__content">
              <div class="mode-item__title">智能推荐</div>
              <div class="mode-item__desc">根据偏好推荐图书</div>
            </div>
          </div>
          <div
            class="mode-item"
            :class="{ 'mode-item--active': currentMode === 'chat' }"
            @click="currentMode = 'chat'"
          >
            <div class="mode-item__icon">
              <i class="el-icon-chat-line-round"></i>
            </div>
            <div class="mode-item__content">
              <div class="mode-item__title">图书咨询</div>
              <div class="mode-item__desc">解答藏书相关问题</div>
            </div>
          </div>
          <div
            class="mode-item"
            :class="{ 'mode-item--active': currentMode === 'summarize' }"
            @click="currentMode = 'summarize'"
          >
            <div class="mode-item__icon">
              <i class="el-icon-document"></i>
            </div>
            <div class="mode-item__content">
              <div class="mode-item__title">图书摘要</div>
              <div class="mode-item__desc">生成图书内容摘要</div>
            </div>
          </div>
          <div
            class="mode-item"
            :class="{ 'mode-item--active': currentMode === 'similar' }"
            @click="currentMode = 'similar'"
          >
            <div class="mode-item__icon">
              <i class="el-icon-connection"></i>
            </div>
            <div class="mode-item__content">
              <div class="mode-item__title">相似推荐</div>
              <div class="mode-item__desc">推荐相似风格图书</div>
            </div>
          </div>
        </el-card>

        <!-- 新建对话按钮 -->
        <el-card shadow="hover" class="new-chat-card">
          <el-button
            type="primary"
            icon="el-icon-plus"
            @click="startNewConversation"
            class="new-chat-btn"
            size="medium"
            plain
          >
            新建对话
          </el-button>
        </el-card>

        <!-- 历史对话 -->
        <el-card shadow="hover" class="history-card">
          <div class="history-title">
            <i class="el-icon-chat-dot-round"></i>
            历史对话
          </div>
          <div class="history-list" v-if="conversationList.length > 0">
            <div
              v-for="conv in conversationList"
              :key="conv.sessionId"
              class="history-item"
              :class="{ 'history-item--active': conv.sessionId === sessionId }"
              @click="switchConversation(conv)"
            >
              <div class="history-item__content">
                <div class="history-item__summary">{{ conv.summary || '新对话' }}</div>
                <div class="history-item__time">{{ formatTime(conv.createTime) }}</div>
              </div>
              <el-button
                class="history-item__delete"
                type="text"
                icon="el-icon-delete"
                size="mini"
                @click.stop="deleteConversation(conv)"
              ></el-button>
            </div>
          </div>
          <div v-else class="history-empty">暂无历史对话</div>
        </el-card>

        <!-- 快捷问题 -->
        <el-card shadow="hover" class="tips-card" v-if="currentMode === 'chat'">
          <div class="tips-title">
            <i class="el-icon-lightbulb"></i>
            快捷提问
          </div>
          <div class="tips-list">
            <el-tag
              v-for="(tip, index) in quickTips"
              :key="index"
              class="tips-tag"
              @click="handleTipClick(tip)"
            >
              {{ tip }}
            </el-tag>
          </div>
        </el-card>
      </div>

      <!-- 右侧内容区 -->
      <div class="ai-main">
        <!-- 智能推荐模式 -->
        <el-card shadow="hover" class="chat-card" v-if="currentMode === 'recommend'">
          <div slot="header" class="card-header">
            <span><i class="el-icon-star-on"></i>智能推荐</span>
          </div>

          <div class="recommend-wrapper">
            <div class="recommend-intro">
              <p>描述您的阅读偏好，我将为您推荐合适的图书：</p>
            </div>

            <el-input
              v-model="recommendInput"
              type="textarea"
              :rows="4"
              placeholder="例如：我喜欢科幻小说，阿西莫夫的作品尤其喜欢。也喜欢历史传记类..."
              class="recommend-input"
              maxlength="500"
              show-word-limit
            ></el-input>

            <div class="recommend-actions">
              <el-button
                type="primary"
                icon="el-icon-magic-stick"
                @click="handleRecommend"
                :loading="loading"
                size="medium"
              >
                为我推荐图书
              </el-button>
            </div>

            <!-- 推荐结果 -->
            <div v-if="recommendResult" class="result-box">
              <div class="result-header">
                <i class="el-icon-data-line"></i>
                <span>推荐结果</span>
              </div>
              <div class="result-content">{{ recommendResult }}</div>
            </div>
          </div>
        </el-card>

        <!-- 图书咨询模式 -->
        <el-card shadow="hover" class="chat-card chat-mode" v-if="currentMode === 'chat'">
          <!-- 消息列表 -->
          <div class="messages-wrapper" ref="chatContainer">
            <div
              v-for="(msg, index) in messages"
              :key="index"
              :class="['message', msg.role]"
            >
              <div class="message-avatar">
                <el-avatar
                  :size="36"
                  :icon="msg.role === 'user' ? 'el-icon-user' : 'el-icon-service'"
                  :class="msg.role"
                ></el-avatar>
              </div>
              <div class="message-content">
                <div class="message-bubble">
                  <div class="message-text">{{ msg.content }}</div>
                  <div class="message-time" v-if="msg.time">{{ msg.time }}</div>
                </div>
              </div>
            </div>

            <!-- 加载动画 -->
            <div v-if="loading" class="message assistant">
              <div class="message-avatar">
                <el-avatar :size="36" icon="el-icon-service" class="assistant"></el-avatar>
              </div>
              <div class="message-content">
                <div class="message-bubble typing">
                  <span class="typing-dot"></span>
                  <span class="typing-dot"></span>
                  <span class="typing-dot"></span>
                </div>
              </div>
            </div>
          </div>

          <!-- 输入框 -->
          <div class="chat-input-wrapper">
            <el-input
              v-model="chatInput"
              placeholder="输入关于图书馆藏书的问题..."
              @keyup.enter.native="handleChat"
              :disabled="loading"
              class="chat-input"
              size="medium"
            >
              <el-button
                slot="append"
                icon="el-icon-s-promotion"
                @click="handleChat"
                :loading="loading"
                type="primary"
              >
                发送
              </el-button>
            </el-input>
          </div>
        </el-card>

        <!-- 图书摘要模式 -->
        <el-card shadow="hover" class="chat-card" v-if="currentMode === 'summarize'">
          <div slot="header" class="card-header">
            <span><i class="el-icon-document"></i>图书摘要</span>
          </div>
          <div class="recommend-wrapper">
            <div class="recommend-intro">
              <p>输入书名，我将为您生成内容摘要：</p>
            </div>
            <el-input
              v-model="summarizeInput"
              placeholder="请输入书名，例如：三体"
              class="recommend-input"
              size="medium"
              clearable
            >
              <el-button
                slot="append"
                icon="el-icon-document"
                @click="handleSummarize"
                :loading="loading"
                type="primary"
              >
                生成摘要
              </el-button>
            </el-input>
            <div v-if="summarizeResult" class="result-box">
              <div class="result-header">
                <i class="el-icon-document"></i>
                <span>内容摘要</span>
              </div>
              <div class="result-content">{{ summarizeResult }}</div>
            </div>
          </div>
        </el-card>

        <!-- 相似推荐模式 -->
        <el-card shadow="hover" class="chat-card" v-if="currentMode === 'similar'">
          <div slot="header" class="card-header">
            <span><i class="el-icon-connection"></i>相似图书推荐</span>
          </div>
          <div class="recommend-wrapper">
            <div class="recommend-intro">
              <p>输入您喜欢的书名，我将推荐风格相似的图书：</p>
            </div>
            <el-input
              v-model="similarInput"
              placeholder="请输入书名，例如：红楼梦"
              class="recommend-input"
              size="medium"
              clearable
            >
              <el-button
                slot="append"
                icon="el-icon-connection"
                @click="handleSimilar"
                :loading="loading"
                type="primary"
              >
                推荐相似
              </el-button>
            </el-input>
            <div v-if="similarResult" class="result-box">
              <div class="result-header">
                <i class="el-icon-connection"></i>
                <span>相似图书推荐</span>
              </div>
              <div class="result-content">{{ similarResult }}</div>
            </div>
          </div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script>
import { recommendBooks, chatWithAI, createSession, getHistory, getUserSessions, endSession, summarizeBook, recommendSimilar } from '@/api/ai'

export default {
  name: 'AiRobot',
  data() {
    return {
      currentMode: 'recommend',
      recommendInput: '',
      recommendResult: '',
      chatInput: '',
      summarizeInput: '',
      summarizeResult: '',
      similarInput: '',
      similarResult: '',
      messages: [
        {
          role: 'assistant',
          content: '您好！我是图书馆的 AI 助手。\n\n我可以帮您：\n1. 根据您的阅读偏好推荐图书\n2. 回答关于图书馆藏书的问题\n\n请问有什么可以帮您的？',
          time: this.getCurrentTime()
        }
      ],
      loading: false,
      quickTips: [
        '有什么关于历史的书推荐吗？',
        '推荐几本科幻小说',
        '借阅量最高的书有哪些？'
      ],
      sessionId: null,
      conversationId: null,
      conversationList: []
    }
  },
  mounted() {
    this.loadSession()
  },
  methods: {
    getCurrentTime() {
      return new Date().toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
    },
    async loadSession() {
      // 获取当前登录用户的真实ID
      const userInfoStr = localStorage.getItem('userInfo')
      let currentUserId = 1
      if (userInfoStr) {
        try {
          const userInfo = JSON.parse(userInfoStr)
          currentUserId = userInfo.id || 1
        } catch (e) {}
      }

      const savedSessionId = localStorage.getItem('ai_session_id')
      const savedUserId = localStorage.getItem('ai_user_id')

      // 如果用户变了或没有会话，创建新会话
      if (!savedSessionId || String(savedUserId) !== String(currentUserId)) {
        await this.initSession()
        return
      }

      this.sessionId = savedSessionId
      // 加载历史消息
      try {
        const res = await getHistory(savedSessionId)
        if (res.success && res.messages) {
          this.messages = res.messages.map(m => ({
            role: m.role,
            content: m.content,
            time: m.createTime ? new Date(m.createTime).toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' }) : ''
          }))
          if (this.messages.length === 0) {
            this.messages.push({
              role: 'assistant',
              content: '您好！我是图书馆的 AI 助手。\n\n我可以帮您：\n1. 根据您的阅读偏好推荐图书\n2. 回答关于图书馆藏书的问题\n\n请问有什么可以帮您的？',
              time: this.getCurrentTime()
            })
          }
        } else {
          // 会话在后端不存在，清除并创建新会话
          localStorage.removeItem('ai_session_id')
          await this.initSession()
        }
      } catch (error) {
        console.error('加载历史失败:', error)
      }
      // 加载会话列表
      this.loadConversationList()
    },
    async initSession() {
      try {
        const res = await createSession()
        if (res.success) {
          this.sessionId = res.sessionId
          this.conversationId = res.conversationId
          localStorage.setItem('ai_session_id', res.sessionId)
        }
      } catch (error) {
        console.error('初始化会话失败:', error)
      }
    },
    async startNewConversation() {
      this.messages = [
        {
          role: 'assistant',
          content: '您好！我是图书馆的 AI 助手。\n\n我可以帮您：\n1. 根据您的阅读偏好推荐图书\n2. 回答关于图书馆藏书的问题\n\n请问有什么可以帮您的？',
          time: this.getCurrentTime()
        }
      ]
      localStorage.removeItem('ai_session_id')
      await this.initSession()
      this.loadConversationList()
    },
    async loadConversationList() {
      try {
        const res = await getUserSessions()
        if (res.success) {
          this.conversationList = res.conversations || []
        }
      } catch (error) {
        console.error('加载会话列表失败:', error)
      }
    },
    async switchConversation(conv) {
      this.sessionId = conv.sessionId
      localStorage.setItem('ai_session_id', conv.sessionId)
      try {
        const res = await getHistory(conv.sessionId)
        if (res.success && res.messages) {
          this.messages = res.messages.map(m => ({
            role: m.role,
            content: m.content,
            time: m.createTime ? new Date(m.createTime).toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' }) : ''
          }))
          if (this.messages.length === 0) {
            this.messages.push({
              role: 'assistant',
              content: '您好！我是图书馆的 AI 助手。\n\n我可以帮您：\n1. 根据您的阅读偏好推荐图书\n2. 回答关于图书馆藏书的问题\n\n请问有什么可以帮您的？',
              time: this.getCurrentTime()
            })
          }
        }
      } catch (error) {
        console.error('加载会话失败:', error)
      }
    },
    formatTime(timeStr) {
      if (!timeStr) return ''
      const date = new Date(timeStr)
      return date.toLocaleDateString('zh-CN', { month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' })
    },
    handleModeChange() {
      this.recommendInput = ''
      this.recommendResult = ''
      this.summarizeInput = ''
      this.summarizeResult = ''
      this.similarInput = ''
      this.similarResult = ''
    },
    handleTipClick(tip) {
      this.chatInput = tip
      this.handleChat()
    },
    scrollToBottom() {
      this.$nextTick(() => {
        if (this.$refs.chatContainer) {
          this.$refs.chatContainer.scrollTop = this.$refs.chatContainer.scrollHeight
        }
      })
    },
    async handleSummarize() {
      if (!this.summarizeInput.trim()) {
        this.$message.warning('请输入书名')
        return
      }
      this.loading = true
      try {
        const res = await summarizeBook(this.summarizeInput, this.sessionId)
        if (res.success) {
          this.summarizeResult = res.summary
        } else {
          this.$message.error(res.message || '生成摘要失败')
        }
      } catch (error) {
        this.$message.error('生成摘要失败，请稍后重试')
      } finally {
        this.loading = false
      }
    },
    async handleSimilar() {
      if (!this.similarInput.trim()) {
        this.$message.warning('请输入书名')
        return
      }
      this.loading = true
      try {
        const res = await recommendSimilar(this.similarInput, this.sessionId)
        if (res.success) {
          this.similarResult = res.recommendation
        } else {
          this.$message.error(res.message || '推荐失败')
        }
      } catch (error) {
        this.$message.error('推荐失败，请稍后重试')
      } finally {
        this.loading = false
      }
    },
    async deleteConversation(conv) {
      try {
        await this.$confirm('确定要删除这个对话吗？', '删除确认', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        await endSession(conv.sessionId)
        this.$message.success('对话已删除')
        if (conv.sessionId === this.sessionId) {
          this.startNewConversation()
        }
        this.loadConversationList()
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error('删除失败')
        }
      }
    },
    async handleRecommend() {
      if (!this.recommendInput.trim()) {
        this.$message.warning('请输入您的阅读偏好')
        return
      }

      this.loading = true
      try {
        const res = await recommendBooks(this.recommendInput, this.sessionId)
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
      this.messages.push({ role: 'user', content: question, time: this.getCurrentTime() })
      this.chatInput = ''
      this.loading = true
      this.scrollToBottom()

      try {
        const res = await chatWithAI(question, this.sessionId)
        if (res.success) {
          this.messages.push({ role: 'assistant', content: res.answer, time: this.getCurrentTime() })
        } else {
          this.messages.push({
            role: 'assistant',
            content: '抱歉，我暂时无法回答这个问题。',
            time: this.getCurrentTime()
          })
        }
      } catch (error) {
        this.messages.push({
          role: 'assistant',
          content: '抱歉，我暂时无法回答这个问题，请稍后重试。',
          time: this.getCurrentTime()
        })
      } finally {
        this.loading = false
        this.scrollToBottom()
        this.loadConversationList()
      }
    }
  },
  watch: {
    currentMode() {
      this.handleModeChange()
    }
  }
}
</script>

<style scoped>
.ai-robot-page {
  max-width: 1400px;
  margin: 0 auto;
  height: calc(100vh - 120px);
  display: flex;
  flex-direction: column;
}

/* ========== 页面标题 ========== */
.page-header {
  margin-bottom: var(--spacing-base);
  flex-shrink: 0;
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

/* ========== AI 容器 ========== */
.ai-container {
  display: flex;
  gap: var(--spacing-lg);
  flex: 1;
  overflow: hidden;
  min-height: 0;
}

.ai-sidebar {
  width: 280px;
  flex-shrink: 0;
  overflow-y: auto;
}

/* ========== 模式选择卡片 ========== */
.mode-card {
  border-radius: var(--radius-medium);
  margin-bottom: var(--spacing-base);
}

/* ========== 新建对话按钮 ========== */
.new-chat-card {
  border-radius: var(--radius-medium);
  margin-bottom: var(--spacing-base);
}

.new-chat-btn {
  width: 100%;
  border-radius: var(--radius-small);
}

.mode-title {
  font-size: var(--font-size-sm);
  font-weight: 600;
  color: var(--color-text-secondary);
  margin-bottom: var(--spacing-base);
  text-transform: uppercase;
  letter-spacing: 1px;
}

.mode-item {
  display: flex;
  align-items: center;
  padding: var(--spacing-base);
  border-radius: var(--radius-small);
  cursor: pointer;
  transition: all var(--transition-fast);
  margin-bottom: var(--spacing-xs);
}

.mode-item:hover {
  background: #f5f7fa;
}

.mode-item--active {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
  border-left: 3px solid var(--color-primary);
}

.mode-item__icon {
  width: 44px;
  height: 44px;
  border-radius: var(--radius-small);
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 18px;
  margin-right: var(--spacing-base);
}

.mode-item__title {
  font-size: var(--font-size-base);
  font-weight: 500;
  color: var(--color-text-primary);
}

.mode-item__desc {
  font-size: var(--font-size-xs);
  color: var(--color-text-secondary);
}

/* ========== 历史对话 ========== */
.history-card {
  border-radius: var(--radius-medium);
}

.history-title {
  font-size: var(--font-size-sm);
  font-weight: 600;
  color: var(--color-text-secondary);
  margin-bottom: var(--spacing-base);
  display: flex;
  align-items: center;
  gap: 6px;
}

.history-title i {
  color: var(--color-primary);
}

.history-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xs);
}

.history-item {
  padding: var(--spacing-sm);
  border-radius: var(--radius-small);
  cursor: pointer;
  background: #f5f7fa;
  transition: all var(--transition-fast);
}

.history-item:hover {
  background: #e8ecf1;
}

.history-item--active {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
  border-left: 3px solid var(--color-primary);
}

.history-item__summary {
  font-size: var(--font-size-sm);
  color: var(--color-text-regular);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.history-item__time {
  font-size: var(--font-size-xs);
  color: var(--color-text-secondary);
  margin-top: 2px;
}

.history-item__delete {
  opacity: 0;
  transition: opacity 0.2s;
  padding: 4px;
  color: #f56c6c;
}
.history-item:hover .history-item__delete {
  opacity: 1;
}

.history-empty {
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
  text-align: center;
  padding: var(--spacing-base);
}

/* ========== 快捷提示卡片 ========== */
.tips-card {
  border-radius: var(--radius-medium);
}

.tips-title {
  font-size: var(--font-size-sm);
  font-weight: 600;
  color: var(--color-text-secondary);
  margin-bottom: var(--spacing-base);
  display: flex;
  align-items: center;
  gap: 6px;
}

.tips-title i {
  color: #f093fb;
}

.tips-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xs);
}

.tips-tag {
  cursor: pointer;
  text-align: left;
  padding: 8px 12px;
  border-radius: var(--radius-small);
  background: #f5f7fa;
  border: none;
  transition: all var(--transition-fast);
  font-size: var(--font-size-sm);
  color: var(--color-text-regular);
}

.tips-tag:hover {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
  color: var(--color-primary);
}

/* ========== 主内容区 ========== */
.ai-main {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
}

/* ========== 聊天卡片 ========== */
.chat-card {
  border-radius: var(--radius-medium);
  display: flex;
  flex-direction: column;
  flex: 1;
  overflow: hidden;
}

.chat-card >>> .el-card__header {
  flex-shrink: 0;
}

.chat-card >>> .el-card__body {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow-y: auto;
}

.card-header {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
  font-weight: 600;
}

.card-header i {
  color: var(--color-primary);
}

/* ========== 推荐模式 ========== */
.recommend-wrapper {
  flex: 1;
  overflow-y: auto;
  min-height: 0;
}

.recommend-intro {
  padding: var(--spacing-base);
  background: #f5f7fa;
  border-radius: var(--radius-small);
  margin-bottom: var(--spacing-base);
}

.recommend-intro p {
  margin: 0;
  color: var(--color-text-secondary);
  font-size: var(--font-size-sm);
}

.recommend-input >>> .el-textarea__inner {
  border-radius: var(--radius-small);
  resize: none;
}

.recommend-actions {
  margin-top: var(--spacing-base);
  display: flex;
  justify-content: center;
}

.recommend-actions .el-button {
  background: linear-gradient(135deg, var(--color-primary) 0%, #764ba2 100%);
  border: none;
  border-radius: var(--radius-small);
  padding: 12px 32px;
}

.result-box {
  margin-top: var(--spacing-lg);
  border: 1px solid #ebeef5;
  border-radius: var(--radius-medium);
  overflow: hidden;
}

.result-header {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
  padding: var(--spacing-base);
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  font-weight: 600;
}

.result-content {
  padding: var(--spacing-base);
  background: #fff;
  line-height: 1.8;
  white-space: pre-wrap;
}

/* ========== 消息区域 ========== */
.messages-wrapper {
  flex: 1;
  overflow-y: auto;
  padding: var(--spacing-base) 0;
  min-height: 0;
}

/* ========== 推荐模式内容 ========== */
.recommend-mode >>> .el-card__body {
  flex: 1;
  overflow-y: auto;
}

.message {
  display: flex;
  margin-bottom: var(--spacing-lg);
  align-items: flex-start;
}

.message.user {
  flex-direction: row-reverse;
}

.message-avatar {
  flex-shrink: 0;
  margin: 0 var(--spacing-sm);
}

.message-avatar >>> .el-avatar {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.message-avatar >>> .el-avatar.user {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.message-content {
  max-width: 70%;
}

.message-bubble {
  padding: var(--spacing-base);
  border-radius: var(--radius-medium);
  position: relative;
}

.message.user .message-bubble {
  background: linear-gradient(135deg, var(--color-primary) 0%, #764ba2 100%);
  color: #fff;
  border-bottom-right-radius: 4px;
}

.message.assistant .message-bubble {
  background: #f5f7fa;
  color: var(--color-text-primary);
  border-bottom-left-radius: 4px;
}

.message-text {
  line-height: 1.6;
  white-space: pre-wrap;
  word-break: break-word;
}

.message-time {
  font-size: 10px;
  margin-top: 6px;
  opacity: 0.7;
}

.message.user .message-time {
  text-align: right;
}

/* ========== 打字机动画 ========== */
.message-bubble.typing {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: var(--spacing-base) var(--spacing-lg);
}

.typing-dot {
  width: 8px;
  height: 8px;
  background: var(--color-primary);
  border-radius: 50%;
  animation: typing 1.4s infinite ease-in-out;
}

.typing-dot:nth-child(1) { animation-delay: 0s; }
.typing-dot:nth-child(2) { animation-delay: 0.2s; }
.typing-dot:nth-child(3) { animation-delay: 0.4s; }

@keyframes typing {
  0%, 60%, 100% {
    transform: translateY(0);
    opacity: 0.4;
  }
  30% {
    transform: translateY(-6px);
    opacity: 1;
  }
}

/* ========== 输入区域 ========== */
.chat-input-wrapper {
  padding-top: var(--spacing-base);
  border-top: 1px solid #ebeef5;
}

.chat-input >>> .el-input__inner {
  border-radius: var(--radius-small) 0 0 var(--radius-small);
}

.chat-input >>> .el-input-group__append {
  border-radius: 0 var(--radius-small) var(--radius-small) 0;
  background: linear-gradient(135deg, var(--color-primary) 0%, #764ba2 100%);
  border: none;
  color: #fff;
}

/* ========== 响应式 ========== */
@media (max-width: 900px) {
  .ai-container {
    flex-direction: column;
  }

  .ai-sidebar {
    width: 100%;
  }

  .mode-item {
    margin-bottom: var(--spacing-xs);
  }
}
</style>
