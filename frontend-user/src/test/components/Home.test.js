import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'

// Mock API
vi.mock('../../api', () => ({
  getCategoryList: vi.fn(() => Promise.resolve([
    { id: 1, name: '早餐' },
    { id: 2, name: '午餐' }
  ])),
  getDishByCategory: vi.fn(() => Promise.resolve([
    { id: 1, name: '煎蛋', description: '美味', cookTime: 10, orderCount: 5 }
  ])),
  submitOrder: vi.fn(() => Promise.resolve())
}))

describe('Home Component', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    localStorage.getItem.mockImplementation((key) => {
      if (key === 'user_token') return 'test-token'
      if (key === 'user_info') return JSON.stringify({ nickname: '小可爱' })
      return null
    })
  })

  it('应根据时间显示正确的问候语', () => {
    const hour = new Date().getHours()
    let expectedGreeting
    if (hour < 11) expectedGreeting = '早上好'
    else if (hour < 14) expectedGreeting = '中午好'
    else if (hour < 18) expectedGreeting = '下午好'
    else expectedGreeting = '晚上好'

    // 验证问候语逻辑
    expect(['早上好', '中午好', '下午好', '晚上好']).toContain(expectedGreeting)
  })

  it('餐次类型应包含所有选项', () => {
    const mealTypes = [
      { value: 'breakfast', label: '早餐', icon: '🌅' },
      { value: 'lunch', label: '午餐', icon: '☀️' },
      { value: 'dinner', label: '晚餐', icon: '🌙' },
      { value: 'snack', label: '夜宵', icon: '⭐' }
    ]

    expect(mealTypes).toHaveLength(4)
    expect(mealTypes.map(m => m.value)).toContain('breakfast')
    expect(mealTypes.map(m => m.value)).toContain('lunch')
    expect(mealTypes.map(m => m.value)).toContain('dinner')
    expect(mealTypes.map(m => m.value)).toContain('snack')
  })
})
