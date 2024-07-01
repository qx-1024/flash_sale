<template>
    <el-button class="mybtn" :icon="Plus" @click="add" size="small" round>添 加 用 户</el-button>
    <img class="empty_data_img" src="../../assets/empty_data.svg" alt="空空如也~" v-if="tableData.length == 0">
    <el-table :data="tableData" stripe style="width: 100%" v-else>
        <el-table-column prop="realName" align="center" label="用户姓名" show-overflow-tooltip/>
        <el-table-column prop="nickName" align="center" label="用户昵称" show-overflow-tooltip/>
        <el-table-column prop="account" align="center" label="账号" show-overflow-tooltip/>
        <el-table-column prop="password" align="center" label="密码" show-overflow-tooltip/>
        <el-table-column prop="phoneNumber" align="center" label="手机号" show-overflow-tooltip/>
        <el-table-column prop="address" align="center" label="用户地址" show-overflow-tooltip/>
        <el-table-column prop="userType" align="center" label="用户类型"/>
        <el-table-column prop="gender" align="center" label="用户性别"/>
        <el-table-column prop="createTime" align="center" label="添加时间" show-overflow-tooltip/>
        <el-table-column prop="options" align="center" fixed="right" label="操作" width="180">
            <template #default="scoped">
                <el-button type="success" :icon="Edit" size="small" @click="view(scoped.row.userId)" plain round>编 辑</el-button>
                <el-button type="danger" :icon="Delete" size="small" @click="del(scoped.row.userId)" plain round>删 除</el-button>
            </template>
        </el-table-column>
    </el-table>


    <!-- 分页组件 -->
    <el-pagination
        class="mt-4"
        layout="prev, pager, next"
        :page-size="size"
        :total="total"
        @prev-click="toPage"
        @next-click="toPage"
        @current-change="toPage"
    />



    <!-- 详情与编辑对话框 -->
    <el-dialog v-model="viewAndEditDialogVisible" title="编辑用户" width="500" center>
        
        <template #footer>
        <div class="dialog-footer">
            <el-button @click="viewAndEditDialogVisible = false">取 消</el-button>
            <el-button type="primary" @click="commitEdit">确 定</el-button>
        </div>
        </template>
    </el-dialog>

    <!-- 添加对话框 -->
    <el-dialog v-model="addDialogVisible" title="新增用户" width="500" center>
        <el-form :model="addForm" label-width="auto" style="max-width: 600px">
            <el-form-item label="姓名">
                <el-input v-model="addForm.realName" />
            </el-form-item>
            <el-form-item label="昵称">
                <el-input v-model="addForm.nickName" />
            </el-form-item>
            <el-form-item label="账号">
                <el-input v-model="addForm.account" />
            </el-form-item>
            <el-form-item label="密码">
                <el-input v-model="addForm.password" type="password"/>
            </el-form-item>
            <el-form-item label="手机">
                <el-input v-model="addForm.phoneNumber"/>
            </el-form-item>
            <el-form-item label="地址">
                <el-input v-model="addForm.address"/>
            </el-form-item>
            <el-form-item label="用户类型">
                <el-select v-model="addForm.userType" placeholder="请选择用户类型">
                    <el-option label="普通用户" value="普通用户" />
                    <el-option label="VIP用户" value="VIP用户" />
                </el-select>
            </el-form-item>
            <el-form-item label="性别">
                <el-select v-model="addForm.region" placeholder="请选择性别">
                    <el-option label="男" value="男" />
                    <el-option label="女" value="女" />
                </el-select>
            </el-form-item>
        </el-form>

        <template #footer>
        <div class="dialog-footer">
            <el-button @click="addDialogVisible = false">取 消</el-button>
            <el-button type="primary" @click="commitAdd">确 定</el-button>
        </div>
        </template>
    </el-dialog>
</template>


<script setup>
import { onMounted, ref } from "vue";
import { Plus, Delete, Edit, View } from '@element-plus/icons-vue'
import { doGet } from "../../http/httpRequest";

const tableData = ref([])

onMounted(() => {
    loadData(1)
})

/****************************************** 分页 *********************************************/
const size = ref(0)
const total = ref(0)

/**
 * @description 页面跳转
 */
const toPage = (current) => {
    loadData(current)
}

/**
 * @description 加载用户信息
 */
const loadData = (current) => {
    doGet('/user/page',{
        current: current
    }).then(res => {
        if(res.data.code === 200){
            tableData.value = res.data.data.records
            size.value = res.data.data.size
            total.value = res.data.data.total

            console.log(tableData.value);

            tableData.value.forEach(item => {
                switch(item.userType){
                    case 0:
                        item.userType = '普通用户'
                        break
                    case 1:
                        item.userType = 'VIP用户'
                        break
                }
                switch(item.gender){
                    case 0:
                        item.gender = '男'
                       break
                    case 1:
                        item.gender = '女'
                        break
                    case 2:
                        item.gender = '未知'
                        break
                }
            })

        }
    })
}

/*************************************** 详情与编辑 ******************************************/
const viewAndEditDialogVisible = ref(false)

const view = () => {
    viewAndEditDialogVisible.value = true
}

const commitEdit = () => {
    viewAndEditDialogVisible.value = false
}

/****************************************** 添加 *********************************************/
const addDialogVisible = ref(false)

const addForm = ref({})

const add = () => {
    addDialogVisible.value = true
}

const commitAdd = () => {
    addDialogVisible.value = false
}

</script>


<style scoped>
.el-table {
    margin-top: 20px;
    margin-bottom: 20px;
}

.empty_data_img {
    width: 200px;
    position: absolute;
    left: 50%;
    top: 50%;
    transform: translateX(-25%) translateY(-50%);
}

.mybtn {
    border: none;
    color: #fff;
    background-color: #32dadd;
}

.mybtn:hover {
    background-color: #59f9fc;
}

.el-pagination {
    position: absolute;
    bottom: 35px;
    left: 50%;
    transform: translateX(-30%);
}
</style>