import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('user_token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('user_info') || '{}'))

  const setToken = (t) => {
    token.value = t
    localStorage.setItem('user_token', t)
  }

  const setUserInfo = (info) => {
    userInfo.value = info
    localStorage.setItem('user_info', JSON.stringify(info))
  }

  const logout = () => {
    token.value = ''
    userInfo.value = {}
    localStorage.removeItem('user_token')
    localStorage.removeItem('user_info')
  }

  return { token, userInfo, setToken, setUserInfo, logout }
})
