<template>
  <div class="login-page">
    <!-- 漂浮装饰 -->
    <div class="floating-hearts">
      <span v-for="i in 6" :key="i" class="heart" :style="{ animationDelay: `${i * 0.5}s` }">💕</span>
    </div>
    
    <div class="logo">💕</div>
    <h1>今天吃什么</h1>
    <p>让选择变得简单</p>
    
    <form class="login-form" @submit.prevent="handleLogin">
      <div class="input-group">
        <span class="input-icon">👤</span>
        <input 
          v-model="form.username" 
          type="text" 
          placeholder="请输入用户名"
          autocomplete="username"
        />
      </div>
      <div class="input-group">
        <span class="input-icon">🔒</span>
        <input 
          v-model="form.password" 
          type="password" 
          placeholder="请输入密码"
          autocomplete="current-password"
        />
      </div>
      <button type="submit" class="btn btn-primary" :disabled="loading">
        <span v-if="loading" class="btn-spinner"></span>
        {{ loading ? '登录中...' : '登 录' }}
      </button>
    </form>
    
    <div class="footer-text">
      用爱发电 ❤️
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { login } from '../api'
import { useUserStore } from '../stores/user'
import toast from '../utils/toast'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)

const form = reactive({
  username: '',
  password: ''
})

const handleLogin = async () => {
  if (!form.username || !form.password) {
    toast.error('请输入用户名和密码')
    return
  }
  loading.value = true
  try {
    const data = await login(form)
    userStore.setToken(data.token)
    userStore.setUserInfo(data)
    toast.success('登录成功 💕')
    setTimeout(() => {
      router.push('/')
    }, 500)
  } catch (e) {
    toast.error(e.message || '登录失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped lang="scss">
.floating-hearts {
  position: fixed;
  inset: 0;
  pointer-events: none;
  overflow: hidden;
  
  .heart {
    position: absolute;
    font-size: 24px;
    opacity: 0.15;
    animation: floatHeart 10s ease-in-out infinite;
    
    &:nth-child(1) { left: 10%; top: 20%; }
    &:nth-child(2) { left: 80%; top: 15%; font-size: 18px; }
    &:nth-child(3) { left: 20%; top: 70%; font-size: 20px; }
    &:nth-child(4) { left: 70%; top: 60%; }
    &:nth-child(5) { left: 5%; top: 45%; font-size: 16px; }
    &:nth-child(6) { left: 90%; top: 80%; font-size: 22px; }
  }
}

@keyframes floatHeart {
  0%, 100% { transform: translateY(0) rotate(0deg); opacity: 0.15; }
  50% { transform: translateY(-20px) rotate(10deg); opacity: 0.25; }
}

.input-group {
  position: relative;
  
  .input-icon {
    position: absolute;
    left: 16px;
    top: 50%;
    transform: translateY(-50%);
    font-size: 18px;
    z-index: 1;
  }
  
  input {
    padding-left: 48px !important;
  }
}

.footer-text {
  margin-top: 32px;
  font-size: 12px;
  color: #B2BEC3;
}

.btn-spinner {
  width: 18px;
  height: 18px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top-color: white;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  margin-right: 8px;
  display: inline-block;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}
</style>
