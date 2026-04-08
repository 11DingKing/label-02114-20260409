import { describe, it, expect, beforeEach, vi } from 'vitest'
import { setActivePinia, createPinia } from 'pinia'
import { useUserStore } from '../../stores/user'

describe('User Store', () => {
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

  it('setToken 应正确设置 token 并存储到 localStorage', () => {
    const store = useUserStore()
    const testToken = 'test-jwt-token'

    store.setToken(testToken)

    expect(store.token).toBe(testToken)
    expect(localStorage.setItem).toHaveBeenCalledWith('token', testToken)
  })

  it('setUserInfo 应正确设置用户信息并存储到 localStorage', () => {
    const store = useUserStore()
    const testUserInfo = {
      userId: 1,
      username: 'admin',
      nickname: '管理员',
      role: 1
    }

    store.setUserInfo(testUserInfo)

    expect(store.userInfo).toEqual(testUserInfo)
    expect(localStorage.setItem).toHaveBeenCalledWith('userInfo', JSON.stringify(testUserInfo))
  })

  it('logout 应清除所有状态和 localStorage', () => {
    const store = useUserStore()
    store.setToken('test-token')
    store.setUserInfo({ username: 'admin' })

    store.logout()

    expect(store.token).toBe('')
    expect(store.userInfo).toEqual({})
    expect(localStorage.removeItem).toHaveBeenCalledWith('token')
    expect(localStorage.removeItem).toHaveBeenCalledWith('userInfo')
  })

  it('从 localStorage 恢复状态', () => {
    localStorage.getItem.mockImplementation((key) => {
      if (key === 'token') return 'stored-token'
      if (key === 'userInfo') return JSON.stringify({ username: 'admin' })
      return null
    })

    setActivePinia(createPinia())
    const store = useUserStore()

    expect(store.token).toBe('stored-token')
    expect(store.userInfo).toEqual({ username: 'admin' })
  })
})
