<template>
  <el-container class="layout">
    <el-aside width="220px" class="sidebar">
      <div class="logo">
        <span class="logo-icon">💕</span>
        <span class="logo-text">女友点餐</span>
      </div>
      <el-menu :default-active="route.path" router>
        <el-menu-item index="/dashboard">
          <el-icon><DataAnalysis /></el-icon>
          <span>数据概览</span>
        </el-menu-item>
        <el-menu-item index="/category">
          <el-icon><Menu /></el-icon>
          <span>分类管理</span>
        </el-menu-item>
        <el-menu-item index="/dish">
          <el-icon><Dish /></el-icon>
          <span>菜品管理</span>
        </el-menu-item>
        <el-menu-item index="/order">
          <el-icon><List /></el-icon>
          <span>点餐记录</span>
        </el-menu-item>
        <el-menu-item index="/wishlist">
          <el-icon><Star /></el-icon>
          <span>心愿清单</span>
        </el-menu-item>
      </el-menu>
      <div class="sidebar-footer">
        <div class="version">v1.0.0</div>
      </div>
    </el-aside>
    <el-container class="main-container">
      <el-header class="header">
        <div class="header-left">
          <span class="page-title">{{ route.meta.title }}</span>
          <span class="breadcrumb">/ {{ getBreadcrumb() }}</span>
        </div>
        <div class="header-right">
          <div class="time-display">{{ currentTime }}</div>
          <el-dropdown @command="handleCommand">
            <div class="user-info">
              <div class="avatar">{{ getAvatar() }}</div>
              <div class="user-detail">
                <span class="username">{{ userStore.userInfo.nickname || userStore.userInfo.username }}</span>
                <span class="role">管理员</span>
              </div>
              <el-icon class="arrow"><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">
                  <el-icon><SwitchButton /></el-icon>退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      <el-main class="main-content">
        <transition name="fade-slide" mode="out-in">
          <router-view />
        </transition>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { SwitchButton } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const currentTime = ref('')

let timer = null

const updateTime = () => {
  const now = new Date()
  const options = { 
    month: 'short', 
    day: 'numeric',
    hour: '2-digit', 
    minute: '2-digit'
  }
  currentTime.value = now.toLocaleString('zh-CN', options)
}

const getAvatar = () => {
  const name = userStore.userInfo.nickname || userStore.userInfo.username || 'A'
  return name.charAt(0).toUpperCase()
}

const getBreadcrumb = () => {
  const titles = {
    '/dashboard': '数据统计',
    '/category': '分类列表',
    '/dish': '菜品列表',
    '/order': '订单列表',
    '/wishlist': '心愿列表'
  }
  return titles[route.path] || '首页'
}

const handleCommand = (command) => {
  if (command === 'logout') {
    userStore.logout()
    router.push('/login')
  }
}

onMounted(() => {
  updateTime()
  timer = setInterval(updateTime, 60000)
})

onUnmounted(() => {
  if (timer) clearInterval(timer)
})
</script>

<style scoped lang="scss">
.sidebar {
  .logo {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 10px;
    
    .logo-icon {
      font-size: 28px;
      animation: heartbeat 2s ease-in-out infinite;
    }
    
    .logo-text {
      font-size: 20px;
      font-weight: 700;
      letter-spacing: -0.5px;
    }
  }
  
  .sidebar-footer {
    position: absolute;
    bottom: 20px;
    left: 0;
    right: 0;
    text-align: center;
    
    .version {
      font-size: 12px;
      color: rgba(255, 255, 255, 0.5);
    }
  }
}

.header {
  .header-left {
    display: flex;
    align-items: center;
    gap: 8px;
    
    .breadcrumb {
      font-size: 14px;
      color: #B2BEC3;
    }
  }
  
  .header-right {
    display: flex;
    align-items: center;
    gap: 24px;
    
    .time-display {
      font-size: 14px;
      color: #636E72;
      padding: 6px 14px;
      background: #F8F9FC;
      border-radius: 8px;
    }
  }
  
  .user-info {
    display: flex;
    align-items: center;
    gap: 12px;
    cursor: pointer;
    padding: 8px 12px;
    border-radius: 12px;
    transition: all 0.3s ease;
    
    &:hover {
      background: #FFF5F7;
    }
    
    .avatar {
      width: 40px;
      height: 40px;
      background: linear-gradient(135deg, #FF6B9D 0%, #E91E63 100%);
      border-radius: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
      color: white;
      font-weight: 700;
      font-size: 16px;
      box-shadow: 0 4px 12px rgba(255, 107, 157, 0.3);
    }
    
    .user-detail {
      display: flex;
      flex-direction: column;
      
      .username {
        font-size: 14px;
        font-weight: 600;
        color: #2D3436;
      }
      
      .role {
        font-size: 12px;
        color: #B2BEC3;
      }
    }
    
    .arrow {
      color: #B2BEC3;
      transition: transform 0.3s;
    }
    
    &:hover .arrow {
      transform: rotate(180deg);
    }
  }
}

// 页面切换动画
.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: all 0.3s ease;
}

.fade-slide-enter-from {
  opacity: 0;
  transform: translateY(10px);
}

.fade-slide-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

@keyframes heartbeat {
  0%, 100% { transform: scale(1); }
  10% { transform: scale(1.1); }
  20% { transform: scale(1); }
  30% { transform: scale(1.1); }
  40% { transform: scale(1); }
}
</style>
