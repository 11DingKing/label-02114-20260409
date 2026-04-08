import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'
import Login from '../../views/Login.vue'

// Mock vue-router
vi.mock('vue-router', () => ({
  useRouter: () => ({
    push: vi.fn()
  })
}))

// Mock API
vi.mock('../../api', () => ({
  login: vi.fn()
}))

describe('Login Component', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    localStorage.getItem.mockReturnValue(null)
  })

  it('应正确渲染登录表单', () => {
    const wrapper = mount(Login, {
      global: {
        stubs: {
          'el-form': true,
          'el-form-item': true,
          'el-input': true,
          'el-button': true
        }
      }
    })

    expect(wrapper.find('.login-page').exists()).toBe(true)
    expect(wrapper.find('.login-card').exists()).toBe(true)
    expect(wrapper.text()).toContain('女友点餐')
    expect(wrapper.text()).toContain('管理后台')
  })

  it('应显示 logo', () => {
    const wrapper = mount(Login, {
      global: {
        stubs: {
          'el-form': true,
          'el-form-item': true,
          'el-input': true,
          'el-button': true
        }
      }
    })

    expect(wrapper.find('.logo').exists()).toBe(true)
    expect(wrapper.find('.icon').text()).toBe('💕')
  })
})
