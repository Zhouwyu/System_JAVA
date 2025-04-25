import {createRouter, createWebHistory} from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {path: '/', redirect: '/login'},
    {path: '/login', name: 'Login', meta: {title: "登录页面"}, component: () => import('../views/Login.vue'),},

    // 父级路由嵌套子级路由
    {
      path: '/manager', component: () => import('../views/Manager.vue'), children: [
        {
          path: 'home',
          name: 'home',
          meta: {title: "主页", requiresAuth: true }, // 需要认证
          component: () => import('../views/Home.vue'),
        },
        {
          path: 'commodity',
          name: 'commodity',
          meta: {title: "商品管理"},
          component: () => import('../views/Commodity.vue'),
        },
        {
          path: 'order',
          name: 'order',
          meta: {title: "订单管理"},
          component: () => import('../views/Order.vue'),
        },
        {
          path: 'customer',
          name: 'customer',
          meta: {title: "客户管理"},
          component: () => import('../views/Customer.vue'),
        },
        {
          path: 'user',
          name: 'user',
          meta: {title: "用户管理"},
          component: () => import('../views/User.vue'),
        },
      ]
    },
    {path: '/404', name: 'notFound', meta: {title: "找不到页面"}, component: () => import('../views/404.vue'),},
    {path: '/:pathMatch(.*)', redirect: '/404'},
  ],
})

// 修改网页标题
router.beforeEach((to, from, next) => {
  document.title = to.meta.title
  const isAuthenticated = localStorage.getItem('accessToken')

  // 需要登录且未认证
  if (to.meta.requiresAuth && !isAuthenticated) {
    next('/login') // 重定向到登录页
  } else {
    next() // 放行
  }
})

export default router
