import { createRouter, createWebHistory } from 'vue-router'
import { doGet } from '../http/httpRequest'

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
            component: () => import('../views/404.vue')
        },
        {
            path: '/:pathMatch(.*)',
            redirect: '/404',
            hidden: true
        }
    ]
})



// 路由守卫
// 当前登录的用户
let currentUser = null;

router.beforeEach(async (to, from, next) => {
    // 等待获取当前用户信息
    await getCurrentUser();

    // 检查路由是否需要 admin 权限
    if (to.matched.some(record => record.meta.requiresAdmin)) {
        // 如果需要 admin 权限
        if (isAdmin()) {
            // 用户是 admin，放行
            next();
        } else {
            // 用户不是 admin，重定向到其他页面，比如登录页
            next('/');
        }
    } else {
        // 如果路由不需要 admin 权限，直接放行
        next();
    }
});

function isAdmin() {
    return currentUser !== null && currentUser.account === 'admin';
}

async function getCurrentUser() {
    if (currentUser !== null) {
        return; // 如果已经获取过当前用户信息，则直接返回
    }

    try {
        const res = await doGet('/user/currentUser', {});
        if (res.data.code === 200) {
            currentUser = res.data.data;
        }
    } catch (err) {
        // 处理异常情况，例如清除 token 并跳转到登录页
        window.localStorage.removeItem('token');
        router.push('/login');
    }
}





export default router;