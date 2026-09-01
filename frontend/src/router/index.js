import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '../stores/user'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue'),
    meta: { guestOnly: true }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/Register.vue'),
    meta: { guestOnly: true }
  },
  {
    path: '/',
    name: 'Layout',
    component: () => import('../layout/Layout.vue'),
    redirect: '/animals',
    children: [
      {
        path: 'animals',
        name: 'AnimalList',
        component: () => import('../views/AnimalList.vue'),
        meta: { public: true }
      },
      {
        path: 'animals/:id',
        name: 'AnimalDetail',
        component: () => import('../views/AnimalDetail.vue'),
        meta: { public: true }
      },
      {
        path: 'checkin',
        name: 'CheckIn',
        component: () => import('../views/CheckIn.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: 'my-checkins',
        name: 'MyCheckIns',
        component: () => import('../views/MyCheckIns.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: 'admin/animals',
        name: 'AdminAnimals',
        component: () => import('../views/admin/AnimalManage.vue'),
        meta: { requiresAuth: true, requireAdmin: true }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach(async to => {
  const userStore = useUserStore()

  if (to.meta.guestOnly && userStore.isLogin) {
    return '/animals'
  }
  if (to.meta.requiresAuth && !userStore.isLogin) {
    return { path: '/login', query: { redirect: to.fullPath } }
  }
  if (to.meta.requireAdmin) {
    try {
      await userStore.getUserInfo()
    } catch {
      return { path: '/login', query: { redirect: to.fullPath } }
    }
    if (!userStore.isAdmin) return '/animals'
  }
  return true
})

export default router
