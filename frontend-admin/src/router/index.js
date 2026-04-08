import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '../stores/user'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/',
    component: () => import('../views/Layout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('../views/Dashboard.vue'),
        meta: { title: '数据概览' }
      },
      {
        path: 'category',
        name: 'Category',
        component: () => import('../views/Category.vue'),
        meta: { title: '分类管理' }
      },
      {
        path: 'dish',
        name: 'Dish',
        component: () => import('../views/Dish.vue'),
        meta: { title: '菜品管理' }
      },
      {
        path: 'order',
        name: 'Order',
        component: () => import('../views/Order.vue'),
        meta: { title: '点餐记录' }
      },
      {
        path: 'wishlist',
        name: 'Wishlist',
        component: () => import('../views/Wishlist.vue'),
        meta: { title: '心愿清单' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  if (to.path !== '/login' && !userStore.token) {
    next('/login')
  } else {
    next()
  }
})

export default router
