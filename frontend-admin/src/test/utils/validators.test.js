import { describe, it, expect } from 'vitest'

// 验证工具函数
const validators = {
  isRequired: (value) => {
    if (value === null || value === undefined) return false
    if (typeof value === 'string') return value.trim().length > 0
    return true
  },
  
  isValidUsername: (username) => {
    if (!username) return false
    return /^[a-zA-Z0-9_]{3,20}$/.test(username)
  },
  
  isValidPassword: (password) => {
    if (!password) return false
    return password.length >= 6
  },
  
  isPositiveNumber: (num) => {
    return typeof num === 'number' && num > 0
  },
  
  isValidMealType: (type) => {
    const validTypes = ['breakfast', 'lunch', 'dinner', 'snack']
    return validTypes.includes(type)
  }
}

describe('Validators', () => {
  describe('isRequired', () => {
    it('空字符串应返回 false', () => {
      expect(validators.isRequired('')).toBe(false)
    })

    it('空格字符串应返回 false', () => {
      expect(validators.isRequired('   ')).toBe(false)
    })

    it('null 应返回 false', () => {
      expect(validators.isRequired(null)).toBe(false)
    })

    it('undefined 应返回 false', () => {
      expect(validators.isRequired(undefined)).toBe(false)
    })

    it('有效字符串应返回 true', () => {
      expect(validators.isRequired('hello')).toBe(true)
    })

    it('数字应返回 true', () => {
      expect(validators.isRequired(123)).toBe(true)
    })
  })

  describe('isValidUsername', () => {
    it('有效用户名应返回 true', () => {
      expect(validators.isValidUsername('admin')).toBe(true)
      expect(validators.isValidUsername('user_123')).toBe(true)
    })

    it('太短的用户名应返回 false', () => {
      expect(validators.isValidUsername('ab')).toBe(false)
    })

    it('包含特殊字符应返回 false', () => {
      expect(validators.isValidUsername('user@name')).toBe(false)
    })

    it('空用户名应返回 false', () => {
      expect(validators.isValidUsername('')).toBe(false)
    })
  })

  describe('isValidPassword', () => {
    it('有效密码应返回 true', () => {
      expect(validators.isValidPassword('password123')).toBe(true)
    })

    it('太短的密码应返回 false', () => {
      expect(validators.isValidPassword('12345')).toBe(false)
    })

    it('空密码应返回 false', () => {
      expect(validators.isValidPassword('')).toBe(false)
    })
  })

  describe('isPositiveNumber', () => {
    it('正数应返回 true', () => {
      expect(validators.isPositiveNumber(1)).toBe(true)
      expect(validators.isPositiveNumber(100)).toBe(true)
    })

    it('零应返回 false', () => {
      expect(validators.isPositiveNumber(0)).toBe(false)
    })

    it('负数应返回 false', () => {
      expect(validators.isPositiveNumber(-1)).toBe(false)
    })

    it('非数字应返回 false', () => {
      expect(validators.isPositiveNumber('1')).toBe(false)
    })
  })

  describe('isValidMealType', () => {
    it('有效餐次应返回 true', () => {
      expect(validators.isValidMealType('breakfast')).toBe(true)
      expect(validators.isValidMealType('lunch')).toBe(true)
      expect(validators.isValidMealType('dinner')).toBe(true)
      expect(validators.isValidMealType('snack')).toBe(true)
    })

    it('无效餐次应返回 false', () => {
      expect(validators.isValidMealType('brunch')).toBe(false)
      expect(validators.isValidMealType('')).toBe(false)
    })
  })
})
