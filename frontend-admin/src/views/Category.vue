<template>
  <div class="page-card">
    <div class="card-header">
      <span class="title">
        <span style="font-size: 18px">📂</span>
        分类列表
      </span>
      <el-button type="primary" @click="openDialog()">
        <el-icon><Plus /></el-icon>新增分类
      </el-button>
    </div>
    <el-table :data="list" stripe v-loading="loading">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="分类名称">
        <template #default="{ row }">
          <div class="category-name">
            <span class="category-icon">{{ getIconByName(row.icon, row.name) }}</span>
            <span>{{ row.name }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="icon" label="图标标识" width="120">
        <template #default="{ row }">
          <el-tag size="small" type="info">{{ row.icon || '-' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="sortOrder" label="排序" width="100">
        <template #default="{ row }">
          <el-tag size="small">{{ row.sortOrder }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'" effect="light">
            {{ row.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150">
        <template #default="{ row }">
          <el-button link type="primary" @click="openDialog(row)">编辑</el-button>
          <el-popconfirm 
            title="确定删除这个分类吗？" 
            confirm-button-text="确定"
            cancel-button-text="取消"
            @confirm="handleDelete(row.id)"
          >
            <template #reference>
              <el-button link type="danger">删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>
    
    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑分类' : '新增分类'" width="420px" destroy-on-close>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="70px" class="category-form">
        <!-- 图标预览区 -->
        <div class="icon-preview-header">
          <div class="preview-box">
            <span class="preview-emoji">{{ currentIcon }}</span>
          </div>
          <div class="preview-name">{{ form.name || '分类名称' }}</div>
        </div>
        
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="图标" prop="icon">
          <el-select 
            v-model="form.icon" 
            placeholder="选择图标" 
            style="width: 100%"
            filterable
          >
            <el-option 
              v-for="item in iconOptions" 
              :key="item.value" 
              :label="item.label" 
              :value="item.value"
            >
              <span style="font-size: 18px; margin-right: 8px;">{{ item.emoji }}</span>
              <span>{{ item.label }}</span>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number v-model="form.sortOrder" :min="0" controls-position="right" style="width: 100%" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-switch 
            v-model="form.status" 
            :active-value="1" 
            :inactive-value="0"
            inline-prompt
            active-text="启用"
            inactive-text="禁用"
          />
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
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getCategoryAll, saveCategory, deleteCategory } from '../api'

const list = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const saving = ref(false)
const formRef = ref()

const form = reactive({
  id: null,
  name: '',
  icon: '',
  sortOrder: 0,
  status: 1
})

const rules = {
  name: [{ required: true, message: '请输入分类名称', trigger: 'blur' }]
}

// 图标映射表
const iconMap = {
  'sunrise': '🌅',
  'sun': '☀️',
  'moon': '🌙',
  'star': '⭐',
  'cake': '🍰',
  'coffee': '🧋',
  'bowl': '🍲',
  'noodle': '🍜',
  'egg': '🍳',
  'meat': '🥩',
  'fish': '🐟',
  'salad': '🥗',
  'pizza': '🍕',
  'burger': '🍔',
  'fries': '🍟',
  'hotdog': '🌭',
  'taco': '🌮',
  'sushi': '🍣',
  'rice': '🍚',
  'curry': '🍛',
  'ramen': '🍜',
  'pot': '🥘',
  'bread': '🍞',
  'croissant': '🥐',
  'cookie': '🍪',
  'icecream': '🍦',
  'candy': '🍬',
  'chocolate': '🍫',
  'fruit': '🍎',
  'grape': '🍇',
  'watermelon': '🍉',
  'orange': '🍊',
  'lemon': '🍋',
  'banana': '🍌',
  'strawberry': '🍓',
  'peach': '🍑',
  'cherry': '🍒',
  'tea': '🍵',
  'beer': '🍺',
  'wine': '🍷',
  'cocktail': '🍸',
  'juice': '🧃',
  'milk': '🥛',
  'fire': '🔥',
  'heart': '❤️',
  'sparkle': '✨'
}

// 图标选项列表
const iconOptions = Object.entries(iconMap).map(([value, emoji]) => ({
  value,
  emoji,
  label: value
}))

// 根据 icon 字段或名称获取 emoji
const getIconByName = (icon, name) => {
  if (icon && iconMap[icon]) {
    return iconMap[icon]
  }
  // 根据名称匹配
  const nameIcons = { 
    '早餐': '🌅', 
    '午餐': '☀️', 
    '晚餐': '🌙', 
    '夜宵': '⭐', 
    '甜点': '🍰', 
    '饮品': '🧋' 
  }
  return nameIcons[name] || '🍽️'
}

// 当前选中图标的实时预览
const currentIcon = computed(() => {
  if (form.icon && iconMap[form.icon]) {
    return iconMap[form.icon]
  }
  return '🍽️'
})

const fetchList = async () => {
  loading.value = true
  try {
    list.value = await getCategoryAll()
  } finally {
    loading.value = false
  }
}

const openDialog = (row) => {
  if (row) {
    Object.assign(form, row)
  } else {
    Object.assign(form, { id: null, name: '', icon: '', sortOrder: 0, status: 1 })
  }
  dialogVisible.value = true
}

const handleSave = async () => {
  await formRef.value.validate()
  saving.value = true
  try {
    await saveCategory(form)
    ElMessage.success(form.id ? '更新成功' : '添加成功')
    dialogVisible.value = false
    fetchList()
  } finally {
    saving.value = false
  }
}

const handleDelete = async (id) => {
  await deleteCategory(id)
  ElMessage.success('删除成功')
  fetchList()
}

onMounted(fetchList)
</script>

<style scoped lang="scss">
.category-name {
  display: flex;
  align-items: center;
  gap: 8px;
  white-space: nowrap;
  
  .category-icon {
    font-size: 20px;
  }
}

// 弹窗表单样式
.category-form {
  .icon-preview-header {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 20px 0 24px;
    margin-bottom: 8px;
    background: linear-gradient(135deg, #FFF5F7, #FFE8EE);
    border-radius: 16px;
    
    .preview-box {
      width: 72px;
      height: 72px;
      background: white;
      border-radius: 20px;
      display: flex;
      align-items: center;
      justify-content: center;
      box-shadow: 0 4px 16px rgba(255, 107, 157, 0.15);
      margin-bottom: 12px;
      
      .preview-emoji {
        font-size: 40px;
        transition: transform 0.3s ease;
      }
      
      &:hover .preview-emoji {
        transform: scale(1.1);
      }
    }
    
    .preview-name {
      font-size: 16px;
      font-weight: 600;
      color: #2D3436;
    }
  }
}

// 表格操作列一行显示
:deep(.el-table) {
  .cell {
    white-space: nowrap;
  }
}
</style>
