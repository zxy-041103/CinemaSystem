import Vue from 'vue'
import VueRouter from 'vue-router'
import store from '../store'

Vue.use(VueRouter)

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue')
  },
  // 管理端路由
  {
    path: '/admin',
    component: () => import('../views/admin/Layout.vue'),
    redirect: '/admin/statistics',
    children: [
      { path: 'statistics', name: 'Statistics', component: () => import('../views/admin/Statistics.vue') },
      { path: 'movie', name: 'MovieManage', component: () => import('../views/admin/MovieManage.vue') },
      { path: 'hall', name: 'HallManage', component: () => import('../views/admin/HallManage.vue') },
      { path: 'screening', name: 'ScreeningManage', component: () => import('../views/admin/ScreeningManage.vue') },
      { path: 'order', name: 'OrderManage', component: () => import('../views/admin/OrderManage.vue') },
      { path: 'user', name: 'UserManage', component: () => import('../views/admin/UserManage.vue') }
    ]
  },
  // 用户端路由
  {
    path: '/',
    component: () => import('../views/front/FrontLayout.vue'),
    redirect: '/home',
    children: [
      { path: 'home', name: 'Home', component: () => import('../views/front/Home.vue') },
      { path: 'movie/:id', name: 'MovieDetail', component: () => import('../views/front/MovieDetail.vue') },
      { path: 'seat-select/:screeningId', name: 'SeatSelect', component: () => import('../views/front/SeatSelect.vue') },
      { path: 'my-orders', name: 'MyOrders', component: () => import('../views/front/MyOrders.vue') },
      { path: 'user-center', name: 'UserCenter', component: () => import('../views/front/UserCenter.vue') }
    ]
  }
]

const router = new VueRouter({
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  if (to.path === '/login') {
    next()
    return
  }
  if (to.path.startsWith('/admin')) {
    if (!store.getters.isLogin || !store.getters.isAdmin) {
      next('/login')
      return
    }
  }
  if (['/my-orders', '/user-center'].includes(to.path) || to.path.startsWith('/seat-select')) {
    if (!store.getters.isLogin) {
      next('/login')
      return
    }
  }
  next()
})

export default router
