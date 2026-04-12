<template>
  <div class="empty-state">
    <div class="empty-state__icon">
      <i :class="icon"></i>
    </div>
    <div class="empty-state__title">{{ title }}</div>
    <div class="empty-state__description" v-if="description">{{ description }}</div>
    <div class="empty-state__action" v-if="$slots.action">
      <slot name="action"></slot>
    </div>
  </div>
</template>

<script>
export default {
  name: 'EmptyState',
  props: {
    icon: {
      type: String,
      default: 'el-icon-folder-opened'
    },
    title: {
      type: String,
      default: '暂无数据'
    },
    description: {
      type: String,
      default: ''
    }
  }
}
</script>

<style scoped>
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: var(--spacing-xl) var(--spacing-lg);
  text-align: center;
}

.empty-state__icon {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: var(--spacing-lg);
}

.empty-state__icon i {
  font-size: 36px;
  color: var(--color-primary);
  opacity: 0.6;
}

.empty-state__title {
  font-size: var(--font-size-lg);
  font-weight: 600;
  color: var(--color-text-primary);
  margin-bottom: var(--spacing-xs);
}

.empty-state__description {
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
  max-width: 300px;
  line-height: 1.6;
}

.empty-state__action {
  margin-top: var(--spacing-lg);
}
</style>
