import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/Register.vue')
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
        component: () => import('../views/AnimalList.vue')
      },
      {
        path: 'animals/:id',
        name: 'AnimalDetail',
        component: () => import('../views/AnimalDetail.vue')
      },
      {
        path: 'checkin',
        name: 'CheckIn',
        component: () => import('../views/CheckIn.vue')
      },
      {
        path: 'my-checkins',
        name: 'MyCheckIns',
        component: () => import('../views/MyCheckIns.vue')
      },
      {
        path: 'admin/animals',
        name: 'AdminAnimals',
        component: () => import('../views/admin/AnimalManage.vue'),
        meta: { requireAdmin: true }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  
  // 不需要登录的页面
  if (to.path === '/login' || to.path === '/register') {
    next()
    return
  }
  
  // 需要登录
  if (!token) {
    next('/login')
    return
  }
  
  next()
})

export default router
