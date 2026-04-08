<template>
  <div class="page-card">
    <div class="card-header">
      <span class="title">
        <span style="font-size: 18px">📋</span>
        点餐记录
      </span>
      <div class="header-actions">
        <el-radio-group v-model="query.status" @change="fetchList" size="default">
          <el-radio-button :label="null">全部</el-radio-button>
          <el-radio-button :label="0">🕐 待做</el-radio-button>
          <el-radio-button :label="1">✅ 已做</el-radio-button>
          <el-radio-button :label="2">❌ 取消</el-radio-button>
        </el-radio-group>
      </div>
    </div>
    
    <el-table :data="list" v-loading="loading" :row-class-name="tableRowClassName">
      <el-table-column prop="id" label="ID" width="70" align="center" />
      <el-table-column prop="username" label="点餐人" width="100" align="center">
        <template #default="{ row }">
          {{ row.nickname || row.username || '小可爱' }}
        </template>
      </el-table-column>
      <el-table-column prop="dishName" label="菜品" min-width="160">
        <template #default="{ row }">
          <div class="dish-cell">
            <span class="dish-emoji">{{ getDishEmoji(row.categoryId) }}</span>
            <span class="dish-name">{{ row.dishName }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="mealType" label="餐次" width="100" align="center">
        <template #default="{ row }">
          <div class="meal-badge" :class="row.mealType">
            <span class="meal-icon">{{ mealTypeMap[row.mealType]?.icon }}</span>
            <span class="meal-text">{{ mealTypeMap[row.mealType]?.label }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="orderDate" label="日期" width="120" align="center">
        <template #default="{ row }">
          <div class="date-cell">
            <span class="date-icon">📅</span>
            <span class="date-text" :class="{ today: isToday(row.orderDate) }">
              {{ formatDate(row.orderDate) }}
            </span>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="remark" label="备注" min-width="140">
        <template #default="{ row }">
          <span class="remark-text" v-if="row.remark">
            💬 {{ row.remark }}
          </span>
          <span class="remark-empty" v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="100" align="center">
        <template #default="{ row }">
          <el-tag :type="statusMap[row.status].type" effect="light">
            {{ statusMap[row.status].text }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150" align="center" fixed="right">
        <template #default="{ row }">
          <el-button 
            v-if="row.status === 0" 
            link
            type="success" 
            @click="handleStatus(row.id, 1)"
          >完成</el-button>
          <el-button 
            v-if="row.status === 0" 
            link
            type="warning" 
            @click="handleStatus(row.id, 2)"
          >取消</el-button>
          <el-button 
            v-if="row.status !== 0" 
            link
            type="primary" 
            @click="handleStatus(row.id, 0)"
          >重置</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <div class="pagination-wrapper">
      <el-pagination 
        v-model:current-page="query.current" 
        v-model:page-size="query.size" 
        :total="total" 
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next" 
        @current-change="fetchList"
        @size-change="fetchList"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getOrderList, updateOrderStatus } from '../api'

const list = ref([])
const loading = ref(false)
const total = ref(0)

const query = reactive({ current: 1, size: 10, status: null })

const mealTypeMap = {
  breakfast: { label: '早餐', icon: '🌅' },
  lunch: { label: '午餐', icon: '☀️' },
  dinner: { label: '晚餐', icon: '🌙' },
  snack: { label: '夜宵', icon: '⭐' }
}

const statusMap = {
  0: { text: '待做', type: 'warning' },
  1: { text: '已做', type: 'success' },
  2: { text: '取消', type: 'info' }
}

const getDishEmoji = (categoryId) => {
  const emojis = { 1: '🍳', 2: '🍲', 3: '🥘', 4: '🍜', 5: '🍰', 6: '🧋' }
  return emojis[categoryId] || '🍽️'
}

const isToday = (dateStr) => {
  if (!dateStr) return false
  const today = new Date().toISOString().split('T')[0]
  return dateStr === today
}

const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  const today = new Date()
  const yesterday = new Date(today)
  yesterday.setDate(yesterday.getDate() - 1)
  
  if (dateStr === today.toISOString().split('T')[0]) return '今天'
  if (dateStr === yesterday.toISOString().split('T')[0]) return '昨天'
  
  const date = new Date(dateStr)
  return `${date.getMonth() + 1}月${date.getDate()}日`
}

const tableRowClassName = ({ row }) => {
  if (row.status === 2) return 'row-cancelled'
  if (row.status === 1) return 'row-done'
  return ''
}

const fetchList = async () => {
  loading.value = true
  try {
    const data = await getOrderList(query)
    list.value = data.records
    total.value = data.total
  } finally {
    loading.value = false
  }
}

const handleStatus = async (id, status) => {
  await updateOrderStatus(id, status)
  const msg = status === 1 ? '已标记完成 👍' : status === 2 ? '已取消' : '已重置'
  ElMessage.success(msg)
  fetchList()
}

onMounted(fetchList)
</script>

<style scoped lang="scss">
.header-actions {
  :deep(.el-radio-group) {
    display: flex;
    gap: 12px;
  }
  
  :deep(.el-radio-button__inner) {
    border-radius: 20px !important;
    padding: 10px 20px;
    border: none !important;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  }
  
  :deep(.el-radio-button:first-child .el-radio-button__inner) {
    border-radius: 20px !important;
  }
  
  :deep(.el-radio-button:last-child .el-radio-button__inner) {
    border-radius: 20px !important;
  }
}

.dish-cell {
  display: flex;
  align-items: center;
  gap: 8px;
  
  .dish-emoji {
    font-size: 24px;
  }
  
  .dish-name {
    font-weight: 600;
    color: #2D3436;
    font-size: 14px;
  }
}

.meal-badge {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
  
  &.breakfast {
    background: linear-gradient(135deg, #FFF3E0, #FFE0B2);
    color: #E65100;
  }
  
  &.lunch {
    background: linear-gradient(135deg, #FFF8E1, #FFECB3);
    color: #FF8F00;
  }
  
  &.dinner {
    background: linear-gradient(135deg, #E8EAF6, #C5CAE9);
    color: #3949AB;
  }
  
  &.snack {
    background: linear-gradient(135deg, #F3E5F5, #E1BEE7);
    color: #7B1FA2;
  }
  
  .meal-icon {
    font-size: 14px;
  }
}

.date-cell {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
  
  .date-icon {
    font-size: 14px;
  }
  
  .date-text {
    font-size: 13px;
    color: #636E72;
    
    &.today {
      color: #FF6B9D;
      font-weight: 600;
    }
  }
}

.remark-text {
  color: #636E72;
  font-size: 13px;
}

.remark-empty {
  color: #B2BEC3;
}

.action-btns {
  display: flex;
  gap: 8px;
  justify-content: center;
}

// 表格行样式
:deep(.el-table) {
  .row-cancelled {
    opacity: 0.6;
    
    td {
      background: #FAFAFA !important;
    }
  }
  
  .row-done {
    td {
      background: #F9FBF9 !important;
    }
  }
  
  .cell {
    white-space: nowrap;
  }
}
</style>
