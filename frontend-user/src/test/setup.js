import { vi } from 'vitest'

// Mock localStorage
const localStorageMock = {
  getItem: vi.fn(() => null),
  setItem: vi.fn(),
  removeItem: vi.fn(),
  clear: vi.fn()
}
global.localStorage = localStorageMock

// Mock alert
global.alert = vi.fn()

// Mock confirm
global.confirm = vi.fn(() => true)
