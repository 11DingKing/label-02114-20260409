import { describe, it, expect, beforeEach, vi } from 'vitest'
import { setActivePinia, createPinia } from 'pinia'
import { useUserStore } from '../../stores/user'

describe('User Store (H5)', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    localStorage.getItem.mockReturnValue(null)
    localStorage.setItem.mockClear()
    localStorage.removeItem.mockClear()
  })

  it('初始状态应为空', () => {
    const store = useUserStore()
    expect(store.token).toBe('')
    expect(store.userInfo).toEqual({})
  })

  it('setToken 应正确设置 token', () => {
    const store = useUserStore()
    const testToken = 'user-jwt-token'

    store.setToken(testToken)

    expect(store.token).toBe(testToken)
    expect(localStorage.setItem).toHaveBeenCalledWith('user_token', testToken)
  })

  it('setUserInfo 应正确设置用户信息', () => {
    const store = useUserStore()
    const testUserInfo = {
      userId: 2,
      username: 'girlfriend',
      nickname: '小可爱',
      role: 0
    }

    store.setUserInfo(testUserInfo)

    expect(store.userInfo).toEqual(testUserInfo)
    expect(localStorage.setItem).toHaveBeenCalledWith('user_info', JSON.stringify(testUserInfo))
  })

  it('logout 应清除所有状态', () => {
    const store = useUserStore()
    store.setToken('test-token')
    store.setUserInfo({ username: 'girlfriend' })

    store.logout()

    expect(store.token).toBe('')
    expect(store.userInfo).toEqual({})
    expect(localStorage.removeItem).toHaveBeenCalledWith('user_token')
    expect(localStorage.removeItem).toHaveBeenCalledWith('user_info')
  })
})
