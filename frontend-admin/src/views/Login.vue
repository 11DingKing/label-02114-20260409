<template>
  <div class="login-page">
    <!-- 背景装饰 -->
    <div class="bg-decoration">
      <div class="circle circle-1"></div>
      <div class="circle circle-2"></div>
      <div class="circle circle-3"></div>
    </div>
    
    <div class="login-card">
      <div class="logo">
        <div class="icon">💕</div>
        <h1>女友点餐</h1>
        <p>管理后台</p>
      </div>
      
      <el-form :model="form" :rules="rules" ref="formRef">
        <el-form-item prop="username">
          <el-input 
            v-model="form.username" 
            placeholder="请输入用户名" 
            size="large" 
            prefix-icon="User"
            @keyup.enter="focusPassword"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input 
            ref="passwordRef"
            v-model="form.password" 
            type="password" 
            placeholder="请输入密码" 
            size="large" 
            prefix-icon="Lock" 
            show-password 
            @keyup.enter="handleLogin" 
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleLogin">
            {{ loading ? '登录中...' : '登 录' }}
          </el-button>
        </el-form-item>
      </el-form>
      
      <div class="footer-text">
        用爱发电 ❤️ Made with Love
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login } from '../api'
import { useUserStore } from '../stores/user'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref()
const passwordRef = ref()
const loading = ref(false)

const form = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const focusPassword = () => {
  passwordRef.value?.focus()
}

const handleLogin = async () => {
  if (!form.username || !form.password) {
    ElMessage.warning('请输入用户名和密码')
    return
  }
  loading.value = true
  try {
    const data = await login(form)
    userStore.setToken(data.token)
    userStore.setUserInfo(data)
    ElMessage.success({
      message: '登录成功，欢迎回来！💕',
      duration: 2000
    })
    router.push('/')
  } catch (e) {
    // 错误已在拦截器处理
  } finally {
    loading.value = false
  }
}
</script>

<style scoped lang="scss">
.bg-decoration {
  position: fixed;
  inset: 0;
  pointer-events: none;
  overflow: hidden;
  
  .circle {
    position: absolute;
    border-radius: 50%;
    background: linear-gradient(135deg, #FF6B9D, #E91E63);
    opacity: 0.08;
    animation: float 8s ease-in-out infinite;
  }
  
  .circle-1 {
    width: 400px;
    height: 400px;
    top: -100px;
    right: -100px;
  }
  
  .circle-2 {
    width: 300px;
    height: 300px;
    bottom: -50px;
    left: -50px;
    animation-delay: -4s;
  }
  
  .circle-3 {
    width: 200px;
    height: 200px;
    top: 50%;
    left: 10%;
    animation-delay: -2s;
  }
}

@keyframes float {
  0%, 100% { transform: translateY(0) rotate(0deg); }
  50% { transform: translateY(-30px) rotate(5deg); }
}

.footer-text {
  text-align: center;
  margin-top: 24px;
  font-size: 12px;
  color: #B2BEC3;
}
</style>
