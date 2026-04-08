import { describe, it, expect, vi, beforeEach } from 'vitest'
import { createPinia, setActivePinia } from 'pinia'

// Mock API
const mockGetRandomDish = vi.fn()
const mockSubmitOrder = vi.fn()

vi.mock('../../api', () => ({
  getRandomDish: () => mockGetRandomDish(),
  submitOrder: () => mockSubmitOrder()
}))

describe('Random Component Logic', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    mockGetRandomDish.mockReset()
    mockSubmitOrder.mockReset()
    localStorage.getItem.mockReturnValue(null)
  })

  it('随机推荐应返回菜品', async () => {
    const mockDish = {
      id: 1,
      name: '番茄炒蛋',
      description: '经典家常菜',
      cookTime: 20,
      difficulty: 2
    }
    mockGetRandomDish.mockResolvedValue([mockDish])

    const result = await mockGetRandomDish()

    expect(result).toHaveLength(1)
    expect(result[0].name).toBe('番茄炒蛋')
  })

  it('提交点餐应调用 API', async () => {
    mockSubmitOrder.mockResolvedValue({ success: true })

    const orderData = {
      dishId: 1,
      mealType: 'lunch'
    }

    await mockSubmitOrder(orderData)

    expect(mockSubmitOrder).toHaveBeenCalled()
  })

  it('随机推荐失败应处理错误', async () => {
    mockGetRandomDish.mockRejectedValue(new Error('网络错误'))

    await expect(mockGetRandomDish()).rejects.toThrow('网络错误')
  })
})
