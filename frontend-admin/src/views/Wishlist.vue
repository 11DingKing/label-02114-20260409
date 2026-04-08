<template>
  <div class="page-card">
    <div class="card-header">
      <span class="title">
        <span style="font-size: 18px">💝</span>
        心愿清单
      </span>
      <div class="header-actions">
        <el-radio-group v-model="statusFilter" @change="filterList" size="default">
          <el-radio-button :label="null">全部</el-radio-button>
          <el-radio-button :label="0">待添加</el-radio-button>
          <el-radio-button :label="1">已添加</el-radio-button>
        </el-radio-group>
      </div>
    </div>
    
    <el-table :data="filteredList" v-loading="loading" :row-class-name="tableRowClassName">
      <el-table-column prop="id" label="ID" width="70" align="center" />
      <el-table-column prop="dishName" label="想吃的菜" min-width="180">
        <template #default="{ row }">
          <div class="dish-cell">
            <span class="dish-icon">{{ row.status === 1 ? '✅' : '💭' }}</span>
            <span class="dish-name" :class="{ done: row.status === 1 }">{{ row.dishName }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="description" label="描述" min-width="200">
        <template #default="{ row }">
          <span class="desc-text" v-if="row.description">{{ row.description }}</span>
          <span class="desc-empty" v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="许愿时间" width="120" align="center">
        <template #default="{ row }">
          <div class="date-cell">
            <span class="date-text">{{ formatDate(row.createdAt) }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="120" align="center">
        <template #default="{ row }">
          <div class="status-badge" :class="row.status === 1 ? 'done' : 'pending'">
            <span class="status-dot"></span>
            <span>{{ row.status === 1 ? '已添加' : '待添加' }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" align="center" fixed="right">
        <template #default="{ row }">
          <el-button 
            v-if="row.status === 0" 
            link
            type="success" 
            @click="handleStatus(row.id, 1)"
          >标记已添加</el-button>
          <el-button 
            v-if="row.status === 1" 
            link
            type="primary" 
            @click="handleStatus(row.id, 0)"
          >重置</el-button>
          <el-popconfirm title="确定删除这个心愿吗？" @confirm="handleDelete(row.id)">
            <template #reference>
              <el-button link type="danger">删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>
    
    <div v-if="!loading && filteredList.length === 0" class="empty-state">
      <div class="empty-icon">💭</div>
      <div class="empty-text">暂无心愿，等她来许愿吧~</div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getWishlist, deleteWishlist, updateWishlistStatus } from '../api'

const list = ref([])
const loading = ref(false)
const statusFilter = ref(null)

const filteredList = computed(() => {
  if (statusFilter.value === null) return list.value
  return list.value.filter(item => item.status === statusFilter.value)
})

const fetchList = async () => {
  loading.value = true
  try {
    list.value = await getWishlist()
  } finally {
    loading.value = false
  }
}

const filterList = () => {
  // 筛选由 computed 自动处理
}

const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  const month = date.getMonth() + 1
  const day = date.getDate()
  return `${month}月${day}日`
}

const tableRowClassName = ({ row }) => {
  if (row.status === 1) return 'row-done'
  return ''
}

const handleStatus = async (id, status) => {
  await updateWishlistStatus(id, status)
  ElMessage.success(status === 1 ? '已标记为已添加 👍' : '已重置')
  fetchList()
}

const handleDelete = async (id) => {
  await deleteWishlist(id)
  ElMessage.success('删除成功')
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
  
  :deep(.el-radio-button) {
    --el-radio-button-checked-bg-color: #FF6B9D;
    --el-radio-button-checked-text-color: #FFFFFF;
    --el-radio-button-checked-border-color: #FF6B9D;
  }
  
  :deep(.el-radio-button__inner) {
    border-radius: 20px !important;
    padding: 10px 20px;
    border: none !important;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
    letter-spacing: 1px;
  }
  
  :deep(.el-radio-button:first-child .el-radio-button__inner) {
    border-radius: 20px !important;
  }
  
  :deep(.el-radio-button:last-child .el-radio-button__inner) {
    border-radius: 20px !important;
  }
  
  :deep(.el-radio-button__original-radio:checked + .el-radio-button__inner) {
    background: linear-gradient(135deg, #FF6B9D, #FF8E53);
    color: #FFFFFF !important;
    border-color: transparent !important;
    box-shadow: 0 2px 12px rgba(255, 107, 157, 0.4);
  }
}

.dish-cell {
  display: flex;
  align-items: center;
  gap: 10px;
  
  .dish-icon {
    font-size: 20px;
  }
  
  .dish-name {
    font-weight: 600;
    color: #2D3436;
    font-size: 15px;
    
    &.done {
      text-decoration: line-through;
      color: #909399;
    }
  }
}

.desc-text {
  color: #636E72;
  font-size: 13px;
}

.desc-empty {
  color: #B2BEC3;
}

.date-cell {
  .date-text {
    font-size: 13px;
    color: #636E72;
  }
}

.status-badge {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
  
  .status-dot {
    width: 6px;
    height: 6px;
    border-radius: 50%;
  }
  
  &.pending {
    background: linear-gradient(135deg, #FFF5F7, #FFE8EE);
    color: #FF6B9D;
    
    .status-dot {
      background: #FF6B9D;
      animation: pulse 1.5s infinite;
    }
  }
  
  &.done {
    background: #E8F5E9;
    color: #2E7D32;
    
    .status-dot {
      background: #4CAF50;
    }
  }
}

@keyframes pulse {
  0%, 100% { opacity: 1; transform: scale(1); }
  50% { opacity: 0.5; transform: scale(1.2); }
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

// 表格行样式
:deep(.el-table) {
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
