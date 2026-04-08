<template>
  <div class="dashboard">
    <el-row :gutter="24">
      <el-col :span="8">
        <div class="stat-card" style="--color: #FF6B9D">
          <div class="stat-icon" style="background: linear-gradient(135deg, #E91E63, #C2185B)">
            <el-icon><List /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">
              <CountUp :end-val="stats.totalOrders || 0" :duration="1.5" />
            </div>
            <div class="stat-label">总点餐数</div>
          </div>
        </div>
      </el-col>
      <el-col :span="8">
        <div class="stat-card" style="--color: #67C23A">
          <div class="stat-icon" style="background: linear-gradient(135deg, #388E3C, #2E7D32)">
            <el-icon><Check /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">
              <CountUp :end-val="stats.completedOrders || 0" :duration="1.5" />
            </div>
            <div class="stat-label">已完成</div>
          </div>
        </div>
      </el-col>
      <el-col :span="8">
        <div class="stat-card" style="--color: #FFAB40">
          <div class="stat-icon" style="background: linear-gradient(135deg, #F57C00, #E65100)">
            <el-icon><TrendCharts /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">
              {{ completionRate }}<span style="font-size: 18px">%</span>
            </div>
            <div class="stat-label">完成率</div>
          </div>
        </div>
      </el-col>
    </el-row>
    
    <div class="page-card" style="margin-top: 24px">
      <div class="card-header">
        <span class="title">
          <span style="font-size: 20px">🏆</span>
          她最爱吃的菜品排行
        </span>
      </div>
      <el-table :data="stats.topDishes" stripe v-loading="loading">
        <el-table-column type="index" label="排名" width="80">
          <template #default="{ $index }">
            <div class="rank-badge" :class="{ gold: $index === 0, silver: $index === 1, bronze: $index === 2 }">
              {{ $index + 1 }}
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="dishName" label="菜品名称">
          <template #default="{ row }">
            <span class="dish-name">{{ row.dishName }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="orderCount" label="点餐次数" min-width="180">
          <template #default="{ row }">
            <div class="count-bar">
              <div class="count-fill" :style="{ width: getBarWidth(row.orderCount) }"></div>
              <span class="count-text">{{ row.orderCount }} 次</span>
            </div>
          </template>
        </el-table-column>
      </el-table>
      
      <div v-if="!loading && (!stats.topDishes || stats.topDishes.length === 0)" class="empty-state">
        <div class="empty-icon">📊</div>
        <div class="empty-text">暂无数据，等她来点餐吧~</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getOrderStats } from '../api'

// 简单的数字动画组件
const CountUp = {
  props: ['endVal', 'duration'],
  setup(props) {
    const displayVal = ref(0)
    onMounted(() => {
      const start = 0
      const end = props.endVal
      const duration = (props.duration || 1) * 1000
      const startTime = performance.now()
      
      const animate = (currentTime) => {
        const elapsed = currentTime - startTime
        const progress = Math.min(elapsed / duration, 1)
        const easeProgress = 1 - Math.pow(1 - progress, 3)
        displayVal.value = Math.round(start + (end - start) * easeProgress)
        
        if (progress < 1) {
          requestAnimationFrame(animate)
        }
      }
      requestAnimationFrame(animate)
    })
    return () => displayVal.value
  }
}

const stats = ref({})
const loading = ref(true)
const maxCount = ref(1)

const completionRate = computed(() => {
  if (!stats.value.totalOrders) return 0
  return Math.round((stats.value.completedOrders / stats.value.totalOrders) * 100)
})

const getBarWidth = (count) => {
  const percentage = (count / maxCount.value) * 100
  // 最小 30%，最大 100%
  return `${Math.max(30, percentage)}%`
}

onMounted(async () => {
  try {
    stats.value = await getOrderStats()
    if (stats.value.topDishes?.length > 0) {
      maxCount.value = Math.max(...stats.value.topDishes.map(d => d.orderCount))
    }
  } finally {
    loading.value = false
  }
})
</script>

<style scoped lang="scss">
.rank-badge {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 13px;
  background: #F0F0F0;
  color: #636E72;
  
  &.gold {
    background: linear-gradient(135deg, #FFD700, #FFA500);
    color: white;
    box-shadow: 0 2px 8px rgba(255, 215, 0, 0.4);
  }
  
  &.silver {
    background: linear-gradient(135deg, #C0C0C0, #A8A8A8);
    color: white;
    box-shadow: 0 2px 8px rgba(192, 192, 192, 0.4);
  }
  
  &.bronze {
    background: linear-gradient(135deg, #CD7F32, #B87333);
    color: white;
    box-shadow: 0 2px 8px rgba(205, 127, 50, 0.4);
  }
}

.dish-name {
  font-weight: 500;
  color: #2D3436;
}

.count-bar {
  position: relative;
  height: 28px;
  background: linear-gradient(90deg, #F8F9FA, #F0F0F0);
  border-radius: 14px;
  overflow: visible;
  min-width: 100px;
  
  .count-fill {
    position: absolute;
    left: 0;
    top: 0;
    height: 100%;
    background: linear-gradient(90deg, #FF6B9D, #FF8FB1, #FFB6C1);
    border-radius: 14px;
    transition: width 1s cubic-bezier(0.4, 0, 0.2, 1);
    min-width: 50px;
    box-shadow: 0 2px 8px rgba(255, 107, 157, 0.3);
  }
  
  .count-text {
    position: absolute;
    left: 0;
    right: 0;
    top: 0;
    bottom: 0;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 13px;
    font-weight: 600;
    color: #2D3436;
    z-index: 1;
    white-space: nowrap;
  }
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  
  .empty-icon {
    font-size: 48px;
    margin-bottom: 16px;
  }
  
  .empty-text {
    color: #909399;
    font-size: 14px;
  }
}
</style>
