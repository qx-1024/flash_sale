<template>
    <el-container>
        <el-aside width="150px">
            <el-menu
                class="rightMenu"
                text-color="#637ea9"
                active-text-color="#4dabf7"
                router
                :unique-opened="true"
                :default-active="route.path"
            >
                <el-menu-item @click="changeTab(item)" v-for="(item, index) in menuList" :key="index" :index="item.path">
                    <template #title>
                        <el-icon>
                            <component :is="item.icon"></component>
                        </el-icon>
                        <span>{{ item.name }}</span>
                    </template>
                </el-menu-item>
            </el-menu>

            <img src="../../assets/aside_bottom.svg" alt="">
        </el-aside>

        <el-container>
            <el-header>
                <el-menu
                    mode="horizontal"
                    class="topMenu"
                    :ellipsis="false"
                >
                    <el-menu-item index="0">
                        <img
                            style="height: 60px"
                            src="../../assets/logo.png"
                            alt="秋玄科技"
                        />
                    </el-menu-item>
                    <span class="flex-grow" />
                    <el-sub-menu index="2">
                        <template #title v-if="currentUser.length === 0">用 户 名</template>
                        <template #title v-else>{{ currentUser.realName }}</template>
                        <el-menu-item class="logoutItem" index="1-1" @click="logout">登 出</el-menu-item>
                    </el-sub-menu>
                </el-menu>
            </el-header>

            <el-main>
                <router-view />
            </el-main>

            <el-footer>
                Copyright © 2024-2025 秋玄 广东省梅州市梅江区金山街道嘉应学院计算机学院 版权所有
            </el-footer>
        </el-container>
    </el-container>
</template>


<script setup>
import { ref, onMounted } from "vue";
import { useRouter, useRoute } from 'vue-router';
import { doGet } from '../../http/httpRequest'

// 获取路由对象
const router = useRouter();
const route = useRoute();

let activeIndex = ref(0)
let activeTabName = ref('')

onMounted(() => {
    activeTabName.value = route.name
    getCurrentUser();
})

/**
 * @description 菜单数组
 */
const menuList = ref([
    {
        id: 1,
        icon: 'Operation',
        name: '统 计 面 板',
        path: '/admin'
    },
    {
        id: 2,
        icon: 'User',
        name: '用 户 管 理',
        path: '/admin/user'
    },
    {
        id: 3,
        icon: 'Goods',
        name: '商 品 管 理',
        path: '/admin/product'
    },
    {
        id: 4,
        icon: 'Postcard',
        name: '预 约 管 理',
        path: '/admin/reservationUser'
    },
    {
        id: 5,
        icon: 'Document',
        name: '订 单 管 理',
        path: '/admin/order'
    },
    {
        id: 6,
        icon: 'Timer',
        name: '预 约 活 动',
        path: '/admin/reservation'
    },
    {
        id: 7,
        icon: 'DataLine',
        name: '闪 购 活 动',
        path: '/admin/flashSale'
    },
])

/**
 * @description 切换菜单
 */
const changeTab = (item) => {
    activeIndex.value = item.id - 1;
    activeTabName.value = item.path;
    router.push(item.path);
}


/**
 * @description 获取当前登录用户
 */
const currentUser = ref({})
const getCurrentUser = () => {
    doGet('/user/currentUser', {})
        .then(res => {
            if (res.data.code === 200) {
                currentUser.value = res.data.data;
            }
        })
        .catch(err => {
            console.log(err)
            window.localStorage.removeItem('token')
            router.push('/login')
        })
}

/**
 * @description 退出
 */
const logout = () => {
    doGet('/user/logout', {})
        .then(res => {
            if (res.data.code === 200) {
                currentUser.value = {};
                window.localStorage.removeItem('token')
                router.push('/login')
            }
        })
        .catch(err => {
            console.log(err)
            window.localStorage.removeItem('token')
            router.push('/login')
        })
}

</script>


<style scoped>
.el-container {
    background-color: var(--flash-grey-lighter-4);
}

.el-header {
    width: calc(100vw - 150px);
    line-height: 60px;
    color: #e3e3e3;
    background-color: #fff;
    border-left: none;
}

.flex-grow {
  flex-grow: 1;
}

.topMenu {
    color: var(--flash-black-color);
    border-bottom: none;
}

.rightMenu{
    border-right: none;
}

.is-active{
    color: #fff;
    border-right: 3px solid var(--flash-red-lighter-1);
    background-color: var(--flash-blue-lighter-3);
}

.el-aside{
    height: calc(100vh - 1px);
    color: var(--flash-black-color);
    background-color: #fff;
    border-right: none;
}

.el-aside img {
    position: absolute;
    left: 10px;
    bottom: 30px;
    width: 100px;
}

.el-footer {
    height: 30px;
    font-size: 11px;
    line-height: 30px;
    text-align: center;
    color: var(--flash-black-lighter-3);
    background-color: #fff;
}
</style>