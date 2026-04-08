<template>
  <div class="wishlist-page">
    <div class="page-header">
      <div class="greeting">想吃的菜 💭</div>
      <div class="title">心愿清单</div>
    </div>
    
    <div class="add-wish card">
      <input 
        v-model="newWish.dishName" 
        placeholder="想吃什么？告诉他~" 
        class="wish-input"
        @keyup.enter="addWish"
      />
      <button class="btn btn-primary" :disabled="!newWish.dishName.trim() || adding" @click="addWish">
        {{ adding ? '...' : '许愿' }}
      </button>
    </div>
    
    <div v-if="loading" class="loading">加载中...</div>
    <div v-else-if="list.length === 0" class="empty">
      <div class="icon">💝</div>
      <div class="text">还没有心愿哦<br>快来许个愿吧~</div>
    </div>
    <TransitionGroup name="wish" tag="div" class="wish-list" v-else>
      <div 
        v-for="wish in list" 
        :key="wish.id" 
        class="wish-item" 
        :class="{ done: wish.status === 1 }"
      >
        <div class="wish-icon">{{ wish.status === 1 ? '✅' : '💭' }}</div>
        <div class="wish-info">
          <div class="wish-name">{{ wish.dishName }}</div>
          <div class="wish-desc" v-if="wish.description">{{ wish.description }}</div>
          <div class="wish-time">{{ formatDate(wish.createdAt) }}</div>
        </div>
        <Transition name="fade">
          <button v-if="wish.status === 0" class="delete-btn" @click="removeWish(wish.id)">×</button>
        </Transition>
      </div>
    </TransitionGroup>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getWishlist, addWishlist, deleteWishlist } from '../api'
import toast from '../utils/toast'

const list = ref([])
const loading = ref(false)
const adding = ref(false)
const newWish = reactive({ dishName: '', description: '' })

const fetchList = async () => {
  loading.value = true
  try {
    list.value = await getWishlist()
  } finally {
    loading.value = false
  }
}

const addWish = async () => {
  if (!newWish.dishName.trim()) return
  adding.value = true
  try {
    await addWishlist(newWish)
    newWish.dishName = ''
    newWish.description = ''
    toast.success('心愿已许下 💕')
    fetchList()
  } catch (e) {
    toast.error(e.message || '添加失败')
  } finally {
    adding.value = false
  }
}

const removeWish = async (id) => {
  try {
    await deleteWishlist(id)
    toast.show('已删除')
    fetchList()
  } catch (e) {
    toast.error(e.message || '删除失败')
  }
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  const month = date.getMonth() + 1
  const day = date.getDate()
  return `${month}月${day}日`
}

onMounted(fetchList)
</script>

<style scoped lang="scss">
.wish-enter-active {
  animation: wishIn 0.4s cubic-bezier(0.68, -0.55, 0.265, 1.55);
}

.wish-leave-active {
  animation: wishOut 0.3s ease-out;
}

@keyframes wishIn {
  from {
    opacity: 0;
    transform: scale(0.8) translateY(-10px);
  }
  to {
    opacity: 1;
    transform: scale(1) translateY(0);
  }
}

@keyframes wishOut {
  to {
    opacity: 0;
    transform: scale(0.8) translateX(20px);
  }
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
