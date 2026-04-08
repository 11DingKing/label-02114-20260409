import axios from 'axios'

/**
 * 创建 Axios 请求实例
 * @param {Object} options 配置选项
 * @param {Function} options.getToken 获取 token 的函数
 * @param {Function} options.onUnauthorized 401 未授权时的回调
 * @param {Function} options.onError 错误处理回调
 * @param {string} options.baseURL 基础 URL，默认 '/api'
 * @param {number} options.timeout 超时时间，默认 10000ms
 */
export function createRequest(options = {}) {
  const {
    getToken = () => null,
    onUnauthorized = () => {},
    onError = (msg) => console.error(msg),
    baseURL = '/api',
    timeout = 10000
  } = options

  const instance = axios.create({
    baseURL,
    timeout
  })

  // 请求拦截器
  instance.interceptors.request.use(
    config => {
      const token = getToken()
      if (token) {
        config.headers.Authorization = `Bearer ${token}`
      }
      return config
    },
    error => Promise.reject(error)
  )

  // 响应拦截器
  instance.interceptors.response.use(
    response => {
      const { code, message, data } = response.data
      if (code === 200) {
        return data
      }
      onError(message || '请求失败')
      return Promise.reject(new Error(message))
    },
    error => {
      if (error.response?.status === 401) {
        onUnauthorized()
        onError('登录已过期，请重新登录')
      } else if (error.response?.status === 403) {
        onError('权限不足，无法执行此操作')
      } else {
        onError(error.message || '网络错误')
      }
      return Promise.reject(error)
    }
  )

  return instance
}

export default createRequest
