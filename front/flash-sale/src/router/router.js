import { createRouter, createWebHistory } from 'vue-router'

let router = createRouter({
    history: createWebHistory(),
    routes: [
        {
            path: '/',
            component: () => import('../views/HomeView.vue')
        },
        {
            path: '/home',
            component: () => import('../views/HomeView.vue')
        },
        {
            path: '/product',
            component: () => import('../views/DetailView.vue')
        },
        {
            path: '/login',
            component: () => import('../views/LoginView.vue')
        },
        {
            path: '/admin',
            component: () => import('../views/admin/DashboardView.vue'),
            children: [
                {
                    path: '',
                    component: () => import('../views/admin/StatisticsView.vue')
                },
                {
                    path: 'product',
                    component: () => import('../views/admin/ProductView.vue')
                },
                {
                    path: 'reservation',
                    component: () => import('../views/admin/ReservationView.vue')
                },
                {
                    path: 'reservationUser',
                    component: () => import('../views/admin/ReservationUserView.vue')
                },
                {
                    path: 'flashSale',
                    component: () => import('../views/admin/FlashSaleActivityView.vue')
                },
                {
                    path: 'user',
                    component: () => import('../views/admin/UserView.vue')
                },
                {
                    path: 'order',
                    component: () => import('../views/admin/OrderView.vue')
                }
            ]
        },
        {
            path: '/404',
            component: () => import('../views/404.vue')
        },
        {
            path: '/:pathMatch(.*)',
            redirect: '/404',
            hidden: true
        }
    ]
})

export default router;