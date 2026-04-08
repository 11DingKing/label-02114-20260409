<template>
  <div class="page-card">
    <div class="card-header">
      <span class="title">
        <span style="font-size: 18px">🍳</span>
        菜品列表
      </span>
      <div class="header-actions">
        <el-select v-model="query.categoryId" placeholder="选择分类" clearable style="width: 140px" @change="fetchList">
          <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id">
            <span>{{ getCategoryIcon(c.name) }} {{ c.name }}</span>
          </el-option>
        </el-select>
        <el-input v-model="query.keyword" placeholder="搜索菜品" clearable style="width: 180px" @keyup.enter="fetchList">
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-button type="primary" @click="openDialog()">
          <el-icon><Plus /></el-icon>新增菜品
        </el-button>
      </div>
    </div>
    <el-table :data="list" stripe v-loading="loading">
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="name" label="菜品名称" min-width="140">
        <template #default="{ row }">
          <div class="dish-cell">
            <span class="dish-emoji">{{ getDishEmoji(row.categoryId) }}</span>
            <span class="dish-name">{{ row.name }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="categoryName" label="分类" width="100">
        <template #default="{ row }">
          <el-tag size="small" effect="plain">{{ row.categoryName }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="difficulty" label="难度" width="150">
        <template #default="{ row }">
          <el-rate v-model="row.difficulty" disabled size="small" :show-text="false" />
        </template>
      </el-table-column>
      <el-table-column prop="cookTime" label="烹饪时间" width="120">
        <template #default="{ row }">
          <span class="cook-time">⏱️ {{ row.cookTime }}分钟</span>
        </template>
      </el-table-column>
      <el-table-column prop="orderCount" label="点餐次数" width="120">
        <template #default="{ row }">
          <el-tag type="danger" effect="dark" round>🔥 {{ row.orderCount }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="openDialog(row)">编辑</el-button>
          <el-popconfirm title="确定删除这道菜吗？" @confirm="handleDelete(row.id)">
            <template #reference>
              <el-button link type="danger">删除</el-button>
            </template>
          </el-popconfirm>
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
    
    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑菜品' : '新增菜品'" width="480px" destroy-on-close>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="70px" class="dish-form">
        <!-- 预览区 -->
        <div class="dish-preview-header">
          <div class="preview-emoji">{{ getDishEmoji(form.categoryId) }}</div>
          <div class="preview-name">{{ form.name || '菜品名称' }}</div>
        </div>
        
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="分类" prop="categoryId">
              <el-select v-model="form.categoryId" placeholder="选择分类" style="width: 100%">
                <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id">
                  <span>{{ getCategoryIcon(c.name) }} {{ c.name }}</span>
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="名称" prop="name">
              <el-input v-model="form.name" placeholder="菜品名称" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="2" placeholder="描述一下这道菜的特点" />
        </el-form-item>
        <el-form-item label="难度" prop="difficulty">
          <el-rate v-model="form.difficulty" :show-text="false" />
        </el-form-item>
        <el-form-item label="时间" prop="cookTime">
          <el-input-number v-model="form.cookTime" :min="1" :max="180" style="width: 100%" controls-position="right">
            <template #suffix>分钟</template>
          </el-input-number>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getDishList, saveDish, deleteDish, getCategoryList } from '../api'

const list = ref([])
const categories = ref([])
const loading = ref(false)
const total = ref(0)
const dialogVisible = ref(false)
const saving = ref(false)
const formRef = ref()

const query = reactive({ current: 1, size: 10, categoryId: null, keyword: '' })

const form = reactive({
  id: null,
  categoryId: null,
  name: '',
  description: '',
  difficulty: 3,
  cookTime: 30
})

const rules = {
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
  name: [{ required: true, message: '请输入菜品名称', trigger: 'blur' }]
}

const getCategoryIcon = (name) => {
  const icons = { '早餐': '🌅', '午餐': '☀️', '晚餐': '🌙', '夜宵': '⭐', '甜点': '🍰', '饮品': '🧋' }
  return icons[name] || '🍽️'
}

const getDishEmoji = (categoryId) => {
  const emojis = { 1: '🍳', 2: '🍲', 3: '🥘', 4: '🍜', 5: '🍰', 6: '🧋' }
  return emojis[categoryId] || '🍽️'
}

const fetchList = async () => {
  loading.value = true
  try {
    const data = await getDishList(query)
    list.value = data.records
    total.value = data.total
  } finally {
    loading.value = false
  }
}

const fetchCategories = async () => {
  categories.value = await getCategoryList()
}

const openDialog = (row) => {
  if (row) {
    Object.assign(form, row)
  } else {
    Object.assign(form, { id: null, categoryId: null, name: '', description: '', difficulty: 3, cookTime: 30 })
  }
  dialogVisible.value = true
}

const handleSave = async () => {
  await formRef.value.validate()
  saving.value = true
  try {
    await saveDish(form)
    ElMessage.success(form.id ? '更新成功' : '添加成功')
    dialogVisible.value = false
    fetchList()
  } finally {
    saving.value = false
  }
}

const handleDelete = async (id) => {
  await deleteDish(id)
  ElMessage.success('删除成功')
  fetchList()
}

onMounted(() => {
  fetchCategories()
  fetchList()
})
</script>

<style scoped lang="scss">
.dish-cell {
  display: flex;
  align-items: center;
  gap: 8px;
  white-space: nowrap;
  
  .dish-emoji {
    font-size: 20px;
  }
  
  .dish-name {
    font-weight: 500;
  }
}

.cook-time {
  color: #636E72;
  font-size: 13px;
  white-space: nowrap;
}

// 弹窗表单样式
.dish-form {
  .dish-preview-header {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 20px 0 24px;
    margin-bottom: 8px;
    background: linear-gradient(135deg, #FFF5F7, #FFE8EE);
    border-radius: 16px;
    
    .preview-emoji {
      font-size: 48px;
      margin-bottom: 8px;
    }
    
    .preview-name {
      font-size: 16px;
      font-weight: 600;
      color: #2D3436;
    }
  }
}

// 表格单元格一行显示
:deep(.el-table) {
  .cell {
    white-space: nowrap;
  }
}
</style>
