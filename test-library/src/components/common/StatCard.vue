<template>
  <div class="stat-card" :class="[`stat-card--${type}`]">
    <div class="stat-card__icon">
      <i :class="icon"></i>
    </div>
    <div class="stat-card__content">
      <div class="stat-card__value">{{ value }}</div>
      <div class="stat-card__label">{{ label }}</div>
    </div>
    <div class="stat-card__trend" v-if="trend">
      <i :class="trend > 0 ? 'el-icon-top' : 'el-icon-bottom'"></i>
      <span>{{ Math.abs(trend) }}%</span>
    </div>
  </div>
</template>

<script>
export default {
  name: 'StatCard',
  props: {
    icon: {
      type: String,
      default: 'el-icon-data-line'
    },
    value: {
      type: [String, Number],
      required: true
    },
    label: {
      type: String,
      required: true
    },
    type: {
      type: String,
      default: 'primary',
      validator: value => ['primary', 'success', 'warning', 'danger', 'info'].includes(value)
    },
    trend: {
      type: Number,
      default: null
    }
  }
}
</script>

<style scoped>
.stat-card {
  display: flex;
  align-items: center;
  padding: var(--spacing-lg);
  background: var(--color-bg-card);
  border-radius: var(--radius-medium);
  box-shadow: var(--shadow-light);
  transition: all var(--transition-normal);
  position: relative;
  overflow: hidden;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-medium);
}

.stat-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
}

.stat-card--primary::before {
  background: linear-gradient(90deg, var(--color-primary), #764ba2);
}

.stat-card--success::before {
  background: linear-gradient(90deg, var(--color-success), #20e3b2);
}

.stat-card--warning::before {
  background: linear-gradient(90deg, var(--color-warning), #e040fb);
}

.stat-card--danger::before {
  background: linear-gradient(90deg, var(--color-danger), #ff6b6b);
}

.stat-card--info::before {
  background: linear-gradient(90deg, var(--color-info), #00b4d8);
}

.stat-card__icon {
  width: 56px;
  height: 56px;
  border-radius: var(--radius-medium);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  margin-right: var(--spacing-base);
}

.stat-card--primary .stat-card__icon {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.15) 0%, rgba(118, 75, 162, 0.15) 100%);
  color: var(--color-primary);
}

.stat-card--success .stat-card__icon {
  background: linear-gradient(135deg, rgba(67, 233, 123, 0.15) 0%, rgba(32, 227, 178, 0.1) 100%);
  color: var(--color-success);
}

.stat-card--warning .stat-card__icon {
  background: linear-gradient(135deg, rgba(240, 147, 251, 0.15) 0%, rgba(224, 64, 251, 0.1) 100%);
  color: #d24dc6;
}

.stat-card--danger .stat-card__icon {
  background: linear-gradient(135deg, rgba(245, 87, 108, 0.15) 0%, rgba(255, 107, 107, 0.1) 100%);
  color: var(--color-danger);
}

.stat-card--info .stat-card__icon {
  background: linear-gradient(135deg, rgba(79, 172, 254, 0.15) 0%, rgba(0, 180, 216, 0.1) 100%);
  color: var(--color-info);
}

.stat-card__content {
  flex: 1;
}

.stat-card__value {
  font-size: 28px;
  font-weight: 700;
  color: var(--color-text-primary);
  line-height: 1.2;
}

.stat-card__label {
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
  margin-top: 4px;
}

.stat-card__trend {
  display: flex;
  align-items: center;
  gap: 2px;
  font-size: var(--font-size-sm);
  padding: 4px 8px;
  border-radius: 20px;
}

.stat-card__trend i {
  font-size: 12px;
}

.stat-card__trend {
  color: var(--color-success);
  background: rgba(67, 233, 123, 0.1);
}
</style>
