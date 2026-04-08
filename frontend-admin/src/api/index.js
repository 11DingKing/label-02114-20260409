import request from './request'

// 认证
export const login = data => request.post('/auth/login', data)
export const getUserInfo = () => request.get('/auth/info')

// 分类
export const getCategoryList = () => request.get('/category/list')
export const getCategoryAll = () => request.get('/category/listAll')
export const saveCategory = data => request.post('/category/save', data)
export const deleteCategory = id => request.delete(`/category/${id}`)

// 菜品
export const getDishList = params => request.get('/dish/list', { params })
export const getDishById = id => request.get(`/dish/${id}`)
export const saveDish = data => request.post('/dish/save', data)
export const deleteDish = id => request.delete(`/dish/${id}`)

// 点餐
export const getOrderList = params => request.get('/order/list', { params })
export const updateOrderStatus = (id, status) => request.put(`/order/status/${id}`, null, { params: { status } })
export const getOrderStats = () => request.get('/order/stats')

// 心愿
export const getWishlist = () => request.get('/wishlist/list')
export const deleteWishlist = id => request.delete(`/wishlist/${id}`)
export const updateWishlistStatus = (id, status) => request.put(`/wishlist/status/${id}`, null, { params: { status } })
