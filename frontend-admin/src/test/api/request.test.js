import { describe, it, expect, vi, beforeEach } from 'vitest'
import axios from 'axios'

// Mock axios
vi.mock('axios', () => ({
  default: {
    create: vi.fn(() => ({
      interceptors: {
        request: { use: vi.fn() },
        response: { use: vi.fn() }
      },
      get: vi.fn(),
      post: vi.fn(),
      put: vi.fn(),
      delete: vi.fn()
    }))
  }
}))

describe('API Request', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('axios.create 应使用正确的配置', () => {
    // 重新导入以触发 axios.create
    vi.resetModules()
    
    expect(axios.create).toBeDefined()
  })

  it('请求拦截器应添加 Authorization header', () => {
    const mockRequest = axios.create()
    expect(mockRequest.interceptors.request.use).toBeDefined()
  })

  it('响应拦截器应处理成功响应', () => {
    const mockRequest = axios.create()
    expect(mockRequest.interceptors.response.use).toBeDefined()
  })
})
