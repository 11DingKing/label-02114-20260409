import { createApp, ref, h } from 'vue'

// 全局单例 Toast 管理器
let toastInstance = null
let toastContainer = null

const createToastComponent = () => {
  const visible = ref(false)
  const message = ref('')
  const type = ref('default')
  let timer = null

  const icons = {
    success: '✓',
    error: '✕',
    default: '💕'
  }

  const show = (msg, t = 'default', duration = 2000) => {
    // 清除之前的定时器
    if (timer) {
      clearTimeout(timer)
      timer = null
    }
    
    // 如果正在显示，先隐藏
    if (visible.value) {
      visible.value = false
    }
    
    // 使用 nextTick 确保状态更新
    setTimeout(() => {
      message.value = msg
      type.value = t
      visible.value = true
      
      if (duration > 0) {
        timer = setTimeout(() => {
          visible.value = false
        }, duration)
      }
    }, 50)
  }

  return {
    visible,
    message,
    type,
    icons,
    show
  }
}

// 初始化 Toast
const initToast = () => {
  if (toastInstance) return toastInstance

  const state = createToastComponent()
  
  // 创建容器
  toastContainer = document.createElement('div')
  toastContainer.id = 'global-toast-container'
  document.body.appendChild(toastContainer)

  // 创建 Vue 应用
  const app = createApp({
    setup() {
      return () => {
        if (!state.visible.value) return null
        
        return h('div', {
          class: ['global-toast', state.type.value],
          key: Date.now() // 强制重新渲染
        }, [
          h('span', { class: 'toast-icon' }, state.icons[state.type.value] || state.icons.default),
          h('span', { class: 'toast-message' }, state.message.value)
        ])
      }
    }
  })

  app.mount(toastContainer)
  
  // 添加样式
  if (!document.getElementById('global-toast-styles')) {
    const style = document.createElement('style')
    style.id = 'global-toast-styles'
    style.textContent = `
      .global-toast {
        position: fixed;
        top: 50%;
        left: 50%;
        transform: translate(-50%, -50%);
        background: rgba(45, 52, 54, 0.95);
        color: white;
        padding: 14px 24px;
        border-radius: 50px;
        font-size: 14px;
        font-weight: 500;
        z-index: 99999;
        backdrop-filter: blur(10px);
        box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
        display: flex;
        align-items: center;
        gap: 8px;
        white-space: nowrap;
        max-width: 90vw;
        animation: toastFadeIn 0.3s ease;
      }
      .global-toast.success {
        background: linear-gradient(135deg, rgba(76, 175, 80, 0.95), rgba(139, 195, 74, 0.95));
      }
      .global-toast.error {
        background: linear-gradient(135deg, rgba(244, 67, 54, 0.95), rgba(255, 87, 34, 0.95));
      }
      .global-toast .toast-icon {
        font-size: 16px;
        flex-shrink: 0;
      }
      .global-toast .toast-message {
        overflow: hidden;
        text-overflow: ellipsis;
      }
      @keyframes toastFadeIn {
        from { opacity: 0; transform: translate(-50%, -50%) scale(0.8); }
        to { opacity: 1; transform: translate(-50%, -50%) scale(1); }
      }
    `
    document.head.appendChild(style)
  }

  toastInstance = state
  return state
}

// 导出 toast 方法
export const toast = {
  show(message, type = 'default', duration = 2000) {
    const instance = initToast()
    instance.show(message, type, duration)
  },
  success(message, duration = 2000) {
    this.show(message, 'success', duration)
  },
  error(message, duration = 2000) {
    this.show(message, 'error', duration)
  }
}

export default toast
