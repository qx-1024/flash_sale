import { createRouter, createWebHistory } from 'vue-router'
import { doGet } from '../http/httpRequest'

let router = createRouter({
    history: createWebHistory(),
    routes: [
        {
            path: '/',
            component: () => import('../views/LoginView.vue'),
            meta: { requiresAdmin: false },
        },
        {
            path: '/home',
            component: () => import('../views/HomeView.vue'),
            meta: { requiresAdmin: false },
        },
        {
            path: '/product',
            component: () => import('../views/DetailView.vue'),
            meta: { requiresAdmin: false },
        },
        {
            path: '/login',
            component: () => import('../views/LoginView.vue'),
            meta: { requiresAdmin: false },
        },
        {
            path: '/admin',
            component: () => import('../views/admin/DashboardView.vue'),
            meta: { requiresAdmin: true },  // 设置 admin 路由需要管理员权限
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
            component: () => import('../views/404.vue'),
            meta: { requiresAdmin: false },
        },
        {
            path: '/:pathMatch(.*)',
            redirect: '/404',
            hidden: true,
            meta: { requiresAdmin: false },
        }
    ]
})




// 当前登录的用户
let currentUser = null;

router.beforeEach(async (to, from, next) => {
    // 如果用户访问的是需要管理员权限的路由
    if (to.path === '/admin') {
        // 先确保获取了当前用户信息
        await getCurrentUser();

        // 如果用户不是管理员，则重定向到登录页
        if (!isAdmin()) {
            next('/login');
            return;
        }
    }

    // 其他路由直接放行
    next();
});

function isAdmin() {
    return currentUser !== null && currentUser.account === 'admin';
}

async function getCurrentUser() {
    if (currentUser !== null) {
        return;
    }

    try {
        const res = await doGet('/user/currentUser', {});
        if (res.data.code === 200) {
            currentUser = res.data.data;
        }
    } catch (err) {
        // 处理异常情况，可以考虑记录错误信息或者重试机制
        console.error('Failed to fetch current user:', err);
        // 清除本地存储的 token 或其他处理
        window.localStorage.removeItem('token');
    }
}






export default router;