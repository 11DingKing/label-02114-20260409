<template>
  <div class="history-page">
    <div class="page-header">
      <div class="greeting">美食记忆 📖</div>
      <div class="title">点餐历史</div>
    </div>
    
    <div v-if="loading && list.length === 0" class="loading">加载中...</div>
    <div v-else-if="list.length === 0" class="empty">
      <div class="icon">📜</div>
      <div class="text">还没有点餐记录哦<br>快去选择今天想吃的吧~</div>
    </div>
    <TransitionGroup name="list" tag="div" class="order-list" v-else>
      <div v-for="(order, index) in list" :key="order.id" class="order-item" :style="{ animationDelay: `${index * 0.05}s` }">
        <div class="order-icon">{{ getMealIcon(order.mealType) }}</div>
        <div class="order-info">
          <div class="order-name">{{ order.dishName }}</div>
          <div class="order-meta">
            <span>{{ mealTypeMap[order.mealType] }}</span>
            <span>{{ formatDate(order.orderDate) }}</span>
          </div>
        </div>
        <div class="order-status" :class="statusClass[order.status]">
          {{ statusMap[order.status] }}
        </div>
      </div>
    </TransitionGroup>
    
    <div v-if="hasMore" class="load-more">
      <button class="btn btn-outline" :disabled="loadingMore" @click="loadMore">
        {{ loadingMore ? '加载中...' : '加载更多' }}
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getMyOrders } from '../api'

const list = ref([])
const loading = ref(false)
const loadingMore = ref(false)
const current = ref(1)
const total = ref(0)

const mealTypeMap = {
  breakfast: '早餐',
  lunch: '午餐',
  dinner: '晚餐',
  snack: '夜宵'
}

const statusMap = {
  0: '待做',
  1: '已做',
  2: '取消'
}

const statusClass = {
  0: 'pending',
  1: 'done',
  2: 'cancel'
}

const getMealIcon = (type) => {
  const icons = { breakfast: '🌅', lunch: '☀️', dinner: '🌙', snack: '⭐' }
  return icons[type] || '🍽️'
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  const today = new Date()
  const yesterday = new Date(today)
  yesterday.setDate(yesterday.getDate() - 1)
  
  if (dateStr === today.toISOString().split('T')[0]) return '今天'
  if (dateStr === yesterday.toISOString().split('T')[0]) return '昨天'
  
  const month = date.getMonth() + 1
  const day = date.getDate()
  return `${month}月${day}日`
}

const hasMore = computed(() => list.value.length < total.value)

const fetchList = async (append = false) => {
  if (append) {
    loadingMore.value = true
  } else {
    loading.value = true
  }
  try {
    const data = await getMyOrders({ current: current.value, size: 10 })
    if (append) {
      list.value.push(...data.records)
    } else {
      list.value = data.records
    }
    total.value = data.total
  } finally {
    loading.value = false
    loadingMore.value = false
  }
}

const loadMore = () => {
  current.value++
  fetchList(true)
}

onMounted(() => fetchList())
</script>

<style scoped lang="scss">
.list-enter-active {
  animation: slideInLeft 0.4s ease-out both;
}

.list-leave-active {
  animation: slideOutRight 0.3s ease-out both;
}

@keyframes slideInLeft {
  from {
    opacity: 0;
    transform: translateX(-20px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

@keyframes slideOutRight {
  to {
    opacity: 0;
    transform: translateX(20px);
  }
}
</style>
