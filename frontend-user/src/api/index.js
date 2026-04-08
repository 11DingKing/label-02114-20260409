import request from './request'

export const login = data => request.post('/auth/login', data)
export const getCategoryList = () => request.get('/category/list')
export const getDishByCategory = categoryId => request.get('/dish/listByCategory', { params: { categoryId } })
export const getRandomDish = (count = 1) => request.get('/dish/random', { params: { count } })
export const submitOrder = data => request.post('/order/save', data)
export const getTodayOrders = () => request.get('/order/today')
export const getMyOrders = params => request.get('/order/my', { params })
export const getWishlist = () => request.get('/wishlist/list')
export const addWishlist = data => request.post('/wishlist/save', data)
export const deleteWishlist = id => request.delete(`/wishlist/${id}`)
