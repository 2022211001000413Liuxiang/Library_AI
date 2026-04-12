<template>
  <div class="page-header">
    <div class="page-header__content">
      <div class="page-header__title">
        <i :class="icon" v-if="icon"></i>
        <span>{{ title }}</span>
      </div>
      <div class="page-header__desc" v-if="description">{{ description }}</div>
    </div>
    <div class="page-header__actions" v-if="$slots.actions">
      <slot name="actions"></slot>
    </div>
  </div>
</template>

<script>
export default {
  name: 'PageHeader',
  props: {
    title: {
      type: String,
      required: true
    },
    icon: {
      type: String,
      default: ''
    },
    description: {
      type: String,
      default: ''
    }
  }
}
</script>

<style scoped>
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: var(--spacing-lg);
  padding: var(--spacing-lg);
  background: var(--color-bg-card);
  border-radius: var(--radius-medium);
  box-shadow: var(--shadow-light);
}

.page-header__title {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  font-size: var(--font-size-xl);
  font-weight: 600;
  color: var(--color-text-primary);
}

.page-header__title i {
  color: var(--color-primary);
  font-size: 22px;
}

.page-header__desc {
  margin-top: var(--spacing-xs);
  color: var(--color-text-secondary);
  font-size: var(--font-size-sm);
  padding-left: 30px;
}

.page-header__actions {
  display: flex;
  gap: var(--spacing-sm);
}
</style>
