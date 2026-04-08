import { describe, it, expect } from 'vitest'

// 辅助函数
const helpers = {
  getGreeting: () => {
    const hour = new Date().getHours()
    if (hour < 11) return '早上好'
    if (hour < 14) return '中午好'
    if (hour < 18) return '下午好'
    return '晚上好'
  },
  
  getMealTypeLabel: (type) => {
    const map = {
      breakfast: '早餐',
      lunch: '午餐',
      dinner: '晚餐',
      snack: '夜宵'
    }
    return map[type] || '未知'
  },
  
  getStatusLabel: (status) => {
    const map = {
      0: '待做',
      1: '已做',
      2: '取消'
    }
    return map[status] || '未知'
  },
  
  formatDate: (dateStr) => {
    if (!dateStr) return ''
    return dateStr.split('T')[0]
  },
  
  getDifficultyStars: (level) => {
    return '⭐'.repeat(Math.min(Math.max(level, 1), 5))
  }
}

describe('Helpers', () => {
  describe('getGreeting', () => {
    it('应返回问候语', () => {
      const greeting = helpers.getGreeting()
      expect(['早上好', '中午好', '下午好', '晚上好']).toContain(greeting)
    })
  })

  describe('getMealTypeLabel', () => {
    it('breakfast 应返回 早餐', () => {
      expect(helpers.getMealTypeLabel('breakfast')).toBe('早餐')
    })

    it('lunch 应返回 午餐', () => {
      expect(helpers.getMealTypeLabel('lunch')).toBe('午餐')
    })

    it('dinner 应返回 晚餐', () => {
      expect(helpers.getMealTypeLabel('dinner')).toBe('晚餐')
    })

    it('snack 应返回 夜宵', () => {
      expect(helpers.getMealTypeLabel('snack')).toBe('夜宵')
    })

    it('未知类型应返回 未知', () => {
      expect(helpers.getMealTypeLabel('unknown')).toBe('未知')
    })
  })

  describe('getStatusLabel', () => {
    it('0 应返回 待做', () => {
      expect(helpers.getStatusLabel(0)).toBe('待做')
    })

    it('1 应返回 已做', () => {
      expect(helpers.getStatusLabel(1)).toBe('已做')
    })

    it('2 应返回 取消', () => {
      expect(helpers.getStatusLabel(2)).toBe('取消')
    })

    it('未知状态应返回 未知', () => {
      expect(helpers.getStatusLabel(99)).toBe('未知')
    })
  })

  describe('formatDate', () => {
    it('应正确格式化日期', () => {
      expect(helpers.formatDate('2024-01-15T10:30:00')).toBe('2024-01-15')
    })

    it('空值应返回空字符串', () => {
      expect(helpers.formatDate('')).toBe('')
      expect(helpers.formatDate(null)).toBe('')
    })
  })

  describe('getDifficultyStars', () => {
    it('难度1应返回1颗星', () => {
      expect(helpers.getDifficultyStars(1)).toBe('⭐')
    })

    it('难度3应返回3颗星', () => {
      expect(helpers.getDifficultyStars(3)).toBe('⭐⭐⭐')
    })

    it('难度5应返回5颗星', () => {
      expect(helpers.getDifficultyStars(5)).toBe('⭐⭐⭐⭐⭐')
    })

    it('难度超过5应返回5颗星', () => {
      expect(helpers.getDifficultyStars(10)).toBe('⭐⭐⭐⭐⭐')
    })

    it('难度小于1应返回1颗星', () => {
      expect(helpers.getDifficultyStars(0)).toBe('⭐')
    })
  })
})
