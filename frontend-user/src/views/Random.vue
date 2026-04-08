<template>
  <div class="random-page">
    <div class="page-header">
      <div class="greeting">选择困难症？🤔</div>
      <div class="title">让命运来决定！</div>
    </div>
    
    <div class="random-card">
      <!-- 未抽取状态 - 老虎机风格 -->
      <div v-if="!result" class="slot-machine">
        <div class="slot-window">
          <div class="slot-reel" :class="{ spinning: loading }">
            <div class="slot-item">🍜</div>
            <div class="slot-item">🍲</div>
            <div class="slot-item">🍳</div>
            <div class="slot-item">🥘</div>
            <div class="slot-item">🍰</div>
            <div class="slot-item">🧋</div>
            <div class="slot-item">🍜</div>
            <div class="slot-item">🍲</div>
          </div>
        </div>
        <div class="slot-text">
          {{ loading ? '美食转动中...' : '点击下方按钮开始抽取' }}
        </div>
      </div>
      
      <!-- 抽取结果 -->
      <div v-else class="random-result">
        <div class="result-celebration">
          <span class="sparkle">✨</span>
          <span class="sparkle">✨</span>
          <span class="sparkle">✨</span>
        </div>
        <div class="result-emoji">{{ getDishEmoji(result.categoryId) }}</div>
        <div class="result-badge">今日推荐</div>
        <div class="result-name">{{ result.name }}</div>
        <div class="result-desc">{{ result.description || '美味可口，值得一试' }}</div>
        <div class="result-tags">
          <span class="tag">
            <span class="tag-icon">⏱️</span>
            {{ result.cookTime }}分钟
          </span>
          <span class="tag">
            <span class="tag-icon">⭐</span>
            {{ result.difficulty }}星难度
          </span>
          <span class="tag">
            <span class="tag-icon">🔥</span>
            点过{{ result.orderCount || 0 }}次
          </span>
        </div>
      </div>
    </div>
    
    <div class="action-buttons">
      <button class="btn btn-primary btn-large" :disabled="loading" @click="getRandom">
        <span v-if="loading" class="btn-loading"></span>
        <span class="btn-icon">{{ loading ? '🎰' : (result ? '🔄' : '🎯') }}</span>
        {{ loading ? '抽取中...' : (result ? '再来一次' : '开始抽取') }}
      </button>
      <Transition name="fade">
        <button v-if="result" class="btn btn-success btn-large" @click="confirmOrder">
          <span class="btn-icon">✓</span>
          就吃这个！
        </button>
      </Transition>
    </div>
    
    <!-- 点餐弹窗 -->
    <Transition name="modal">
      <div v-if="showModal" class="modal-overlay" @click="showModal = false">
        <div class="modal-content" @click.stop>
          <div class="modal-header">确认点餐</div>
          <div class="selected-dish">{{ result?.name }} {{ getDishEmoji(result?.categoryId) }}</div>
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
          <div class="modal-footer">
            <button class="btn btn-outline" @click="showModal = false">再想想</button>
            <button class="btn btn-primary" :disabled="ordering" @click="submitOrderHandler">
              {{ ordering ? '提交中...' : '确认点餐' }}
            </button>
          </div>
        </div>
      </div>
    </Transition>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { getRandomDish, submitOrder } from '../api'
import toast from '../utils/toast'

const result = ref(null)
const loading = ref(false)
const showModal = ref(false)
const mealType = ref('lunch')
const ordering = ref(false)

const mealTypes = [
  { value: 'breakfast', label: '早餐', icon: '🌅' },
  { value: 'lunch', label: '午餐', icon: '☀️' },
  { value: 'dinner', label: '晚餐', icon: '🌙' },
  { value: 'snack', label: '夜宵', icon: '⭐' }
]

const getDishEmoji = (categoryId) => {
  const emojis = { 1: '🍳', 2: '🍲', 3: '🥘', 4: '🍜', 5: '🍰', 6: '🧋' }
  return emojis[categoryId] || '🍽️'
}

const getRandom = async () => {
  if (loading.value) return
  loading.value = true
  result.value = null
  
  await new Promise(resolve => setTimeout(resolve, 2000))
  
  try {
    const data = await getRandomDish(1)
    result.value = data[0]
  } catch (e) {
    toast.error('获取推荐失败')
  } finally {
    loading.value = false
  }
}

const confirmOrder = () => {
  if (showModal.value) return
  const hour = new Date().getHours()
  if (hour < 10) mealType.value = 'breakfast'
  else if (hour < 14) mealType.value = 'lunch'
  else if (hour < 20) mealType.value = 'dinner'
  else mealType.value = 'snack'
  showModal.value = true
}

const submitOrderHandler = async () => {
  if (ordering.value) return
  ordering.value = true
  try {
    await submitOrder({
      dishId: result.value.id,
      mealType: mealType.value
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
</script>

<style scoped lang="scss">
.random-card {
  margin: 24px 20px;
  background: white;
  border-radius: 28px;
  padding: 40px 24px;
  text-align: center;
  box-shadow: 0 8px 32px rgba(255, 107, 157, 0.12);
  min-height: 320px;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
  
  // 顶部装饰条
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 4px;
    background: linear-gradient(90deg, #FF6B9D, #FF8FB1, #FFB6C1, #FF8FB1, #FF6B9D);
    background-size: 200% 100%;
    animation: shimmer 3s linear infinite;
  }
}

@keyframes shimmer {
  0% { background-position: -200% 0; }
  100% { background-position: 200% 0; }
}

// 老虎机样式
.slot-machine {
  .slot-window {
    width: 120px;
    height: 120px;
    margin: 0 auto 24px;
    background: linear-gradient(145deg, #FFF5F7, #FFE8EE);
    border-radius: 24px;
    overflow: hidden;
    position: relative;
    box-shadow: 
      inset 0 4px 8px rgba(255, 107, 157, 0.1),
      0 4px 16px rgba(255, 107, 157, 0.15);
    
    // 上下渐变遮罩
    &::before, &::after {
      content: '';
      position: absolute;
      left: 0;
      right: 0;
      height: 30px;
      z-index: 2;
      pointer-events: none;
    }
    
    &::before {
      top: 0;
      background: linear-gradient(to bottom, #FFF5F7, transparent);
    }
    
    &::after {
      bottom: 0;
      background: linear-gradient(to top, #FFF5F7, transparent);
    }
  }
  
  .slot-reel {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding-top: 40px;
    
    &.spinning {
      animation: slotSpin 0.15s linear infinite;
    }
    
    .slot-item {
      font-size: 56px;
      height: 80px;
      display: flex;
      align-items: center;
      justify-content: center;
      filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.1));
    }
  }
  
  .slot-text {
    color: #636E72;
    font-size: 15px;
    line-height: 1.6;
  }
}

@keyframes slotSpin {
  0% { transform: translateY(0); }
  100% { transform: translateY(-80px); }
}

// 结果展示
.random-result {
  animation: resultReveal 0.6s cubic-bezier(0.34, 1.56, 0.64, 1);
  
  .result-celebration {
    margin-bottom: 8px;
    
    .sparkle {
      display: inline-block;
      font-size: 20px;
      animation: sparkle 1s ease-in-out infinite;
      
      &:nth-child(1) { animation-delay: 0s; }
      &:nth-child(2) { animation-delay: 0.2s; }
      &:nth-child(3) { animation-delay: 0.4s; }
    }
  }
  
  .result-emoji {
    font-size: 72px;
    margin-bottom: 8px;
    animation: emojiPop 0.5s cubic-bezier(0.34, 1.56, 0.64, 1) 0.2s both;
    filter: drop-shadow(0 4px 12px rgba(255, 107, 157, 0.3));
  }
  
  .result-badge {
    display: inline-block;
    background: linear-gradient(135deg, #FF6B9D, #FF4081);
    color: white;
    font-size: 11px;
    font-weight: 600;
    padding: 4px 12px;
    border-radius: 20px;
    margin-bottom: 12px;
    letter-spacing: 1px;
  }
  
  .result-name {
    font-size: 28px;
    font-weight: 700;
    color: #2D3436;
    margin-bottom: 8px;
    letter-spacing: -0.5px;
  }
  
  .result-desc {
    color: #636E72;
    font-size: 14px;
    margin-bottom: 20px;
    max-width: 280px;
    margin-left: auto;
    margin-right: auto;
    line-height: 1.5;
  }
  
  .result-tags {
    display: flex;
    justify-content: center;
    gap: 12px;
    flex-wrap: wrap;
    
    .tag {
      display: inline-flex;
      align-items: center;
      gap: 4px;
      background: #FFF5F7;
      color: #636E72;
      padding: 8px 14px;
      border-radius: 20px;
      font-size: 13px;
      font-weight: 500;
      
      .tag-icon {
        font-size: 14px;
      }
    }
  }
}

@keyframes resultReveal {
  0% {
    opacity: 0;
    transform: scale(0.8) translateY(20px);
  }
  100% {
    opacity: 1;
    transform: scale(1) translateY(0);
  }
}

@keyframes sparkle {
  0%, 100% { opacity: 1; transform: scale(1); }
  50% { opacity: 0.5; transform: scale(1.2); }
}

@keyframes emojiPop {
  0% {
    opacity: 0;
    transform: scale(0) rotate(-20deg);
  }
  100% {
    opacity: 1;
    transform: scale(1) rotate(0deg);
  }
}

// 按钮区域
.action-buttons {
  padding: 0 20px;
  display: flex;
  flex-direction: column;
  gap: 12px;
  
  .btn-large {
    height: 56px;
    font-size: 16px;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8px;
    
    .btn-icon {
      font-size: 20px;
    }
    
    .btn-loading {
      width: 20px;
      height: 20px;
      border: 2px solid rgba(255, 255, 255, 0.3);
      border-top-color: white;
      border-radius: 50%;
      animation: spin 0.8s linear infinite;
    }
  }
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

// 过渡动画
.fade-enter-active,
.fade-leave-active {
  transition: all 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
  transform: translateY(10px);
}

// Modal 动画
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
  from {
    transform: translateY(100%);
  }
  to {
    transform: translateY(0);
  }
}

@keyframes modalSlideDown {
  from {
    transform: translateY(0);
  }
  to {
    transform: translateY(100%);
  }
}
</style>
