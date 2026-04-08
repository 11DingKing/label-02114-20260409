<template>
  <div class="home">
    <div class="page-header">
      <div class="greeting">{{ greeting }}，{{ userStore.userInfo.nickname || '小可爱' }} ✨</div>
      <div class="title">今天想吃什么？</div>
    </div>
    
    <div class="category-tabs">
      <button 
        v-for="c in categories" 
        :key="c.id" 
        class="tab" 
        :class="{ active: activeCategory === c.id }"
        @click="selectCategory(c.id)"
      >
        {{ getCategoryIcon(c.name) }} {{ c.name }}
      </button>
    </div>
    
    <div v-if="loading" class="loading">正在加载美食...</div>
    <div v-else-if="dishes.length === 0" class="empty">
      <div class="icon">🍽️</div>
      <div class="text">这个分类还没有菜品哦<br>快让他添加一些吧~</div>
    </div>
    <TransitionGroup name="list" tag="div" v-else>
      <div 
        v-for="dish in dishes" 
        :key="dish.id" 
        class="dish-card"
        @click="selectDish(dish)"
      >
        <div class="dish-image">{{ getDishEmoji(dish.categoryId) }}</div>
        <div class="dish-info">
          <div class="name">{{ dish.name }}</div>
          <div class="desc">{{ dish.description || '美味可口，值得一试' }}</div>
          <div class="meta">
            <span class="tag">⏱️ {{ dish.cookTime }}分钟</span>
            <span class="tag">🔥 点过{{ dish.orderCount }}次</span>
          </div>
        </div>
      </div>
    </TransitionGroup>
    
    <!-- 点餐弹窗 -->
    <Transition name="modal">
      <div v-if="showModal" class="modal-overlay" @click="showModal = false">
        <div class="modal-content" @click.stop>
          <div class="modal-header">确认点餐</div>
          <div class="selected-dish">{{ selectedDish?.name }} {{ getDishEmoji(selectedDish?.categoryId) }}</div>
          <div class="meal-types">
            <button 
              v-for="m in mealTypes" 
              :key="m.value"
              class="meal-btn"
              :class="{ active: mealType === m.value }"
              @click="mealType = m.value"
            >
              <span style="font-size: 20px">{{ m.icon }}</span>
              <span>{{ m.label }}</span>
            </button>
          </div>
          <textarea v-model="remark" placeholder="备注一下口味偏好吧~（可选）" class="remark-input"></textarea>
          <div class="modal-footer">
            <button class="btn btn-outline" @click="showModal = false">再想想</button>
            <button class="btn btn-primary" :disabled="ordering" @click="confirmOrder">
              {{ ordering ? '提交中...' : '就吃这个！' }}
            </button>
          </div>
        </div>
      </div>
    </Transition>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getCategoryList, getDishByCategory, submitOrder } from '../api'
import { useUserStore } from '../stores/user'
import toast from '../utils/toast'

const userStore = useUserStore()
const categories = ref([])
const dishes = ref([])
const activeCategory = ref(null)
const loading = ref(false)
const showModal = ref(false)
const selectedDish = ref(null)
const mealType = ref('lunch')
const remark = ref('')
const ordering = ref(false)

const mealTypes = [
  { value: 'breakfast', label: '早餐', icon: '🌅' },
  { value: 'lunch', label: '午餐', icon: '☀️' },
  { value: 'dinner', label: '晚餐', icon: '🌙' },
  { value: 'snack', label: '夜宵', icon: '⭐' }
]

const greeting = computed(() => {
  const hour = new Date().getHours()
  if (hour < 6) return '夜深了'
  if (hour < 11) return '早上好'
  if (hour < 14) return '中午好'
  if (hour < 18) return '下午好'
  if (hour < 22) return '晚上好'
  return '夜深了'
})

const getCategoryIcon = (name) => {
  const icons = { '早餐': '🌅', '午餐': '☀️', '晚餐': '🌙', '夜宵': '⭐', '甜点': '🍰', '饮品': '🧋' }
  return icons[name] || '🍽️'
}

const getDishEmoji = (categoryId) => {
  const emojis = { 1: '🍳', 2: '🍲', 3: '🥘', 4: '🍜', 5: '🍰', 6: '🧋' }
  return emojis[categoryId] || '🍽️'
}

const fetchCategories = async () => {
  try {
    categories.value = await getCategoryList()
    if (categories.value.length > 0) {
      selectCategory(categories.value[0].id)
    }
  } catch (e) {
    toast.error('加载分类失败')
  }
}

const selectCategory = async (id) => {
  activeCategory.value = id
  loading.value = true
  try {
    dishes.value = await getDishByCategory(id)
  } catch (e) {
    toast.error('加载菜品失败')
  } finally {
    loading.value = false
  }
}

const selectDish = (dish) => {
  if (showModal.value) return
  selectedDish.value = dish
  const hour = new Date().getHours()
  if (hour < 10) mealType.value = 'breakfast'
  else if (hour < 14) mealType.value = 'lunch'
  else if (hour < 20) mealType.value = 'dinner'
  else mealType.value = 'snack'
  remark.value = ''
  showModal.value = true
}

const confirmOrder = async () => {
  if (ordering.value) return
  ordering.value = true
  try {
    await submitOrder({
      dishId: selectedDish.value.id,
      mealType: mealType.value,
      remark: remark.value
    })
    showModal.value = false
    setTimeout(() => {
      toast.success('点餐成功！他会看到的 💕')
    }, 300)
  } catch (e) {
    toast.error(e.message || '点餐失败')
  } finally {
    ordering.value = false
  }
}

onMounted(fetchCategories)
</script>

<style scoped lang="scss">
.list-enter-active,
.list-leave-active {
  transition: all 0.4s ease;
}

.list-enter-from {
  opacity: 0;
  transform: translateX(-30px);
}

.list-leave-to {
  opacity: 0;
  transform: translateX(30px);
}

.modal-enter-active,
.modal-leave-active {
  transition: opacity 0.25s ease;
}

.modal-enter-from,
.modal-leave-to {
  opacity: 0;
}

.modal-enter-active .modal-content {
  animation: modalSlideUp 0.3s ease-out;
}

.modal-leave-active .modal-content {
  animation: modalSlideDown 0.2s ease-in;
}

@keyframes modalSlideUp {
  from { transform: translateY(100%); }
  to { transform: translateY(0); }
}

@keyframes modalSlideDown {
  from { transform: translateY(0); }
  to { transform: translateY(100%); }
}
</style>
