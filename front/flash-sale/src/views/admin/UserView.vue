<template>
  <!-- 添加按钮 -->
  <el-button class="addBtn" :icon="Plus" @click="add" size="small" round
    >添 加 用 户</el-button
  >

  <!-- 空数据图片 -->
  <img
    class="empty_data_img"
    src="../../assets/empty_data.svg"
    alt="空空如也~"
    v-if="tableData.length == 0"
  />

  <!-- 表格 -->
  <el-table :data="tableData" stripe style="width: 100%" v-else>
    <el-table-column
      prop="realName"
      align="center"
      label="用户姓名"
      show-overflow-tooltip
    />
    <el-table-column
      prop="nickName"
      align="center"
      label="用户昵称"
      show-overflow-tooltip
    />
    <el-table-column
      prop="account"
      align="center"
      label="账号"
      show-overflow-tooltip
    />
    <el-table-column
      prop="password"
      align="center"
      label="密码"
      show-overflow-tooltip
    />
    <el-table-column
      prop="phoneNumber"
      align="center"
      label="手机号"
      show-overflow-tooltip
    />
    <el-table-column
      prop="address"
      align="center"
      label="用户地址"
      show-overflow-tooltip
    />
    <el-table-column prop="userType" align="center" label="用户类型" />
    <el-table-column prop="gender" align="center" label="用户性别" />
    <el-table-column
      prop="createTime"
      align="center"
      label="添加时间"
      show-overflow-tooltip
    />
    <el-table-column
      prop="options"
      align="center"
      fixed="right"
      label="操作"
      width="180"
    >
      <template #default="scoped">
        <el-button
          class="editBtn"
          :icon="Edit"
          size="small"
          @click="view(scoped.row.userId)"
          round
          >编 辑</el-button
        >
        <el-button
          class="delBtn"
          :icon="Delete"
          size="small"
          @click="del(scoped.row.userId)"
          round
          >删 除</el-button
        >
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

  <!-- 编辑对话框 -->
  <el-dialog v-model="EditDialogVisible" title="编辑用户" width="500" center>
    <el-form
      ref="editForm"
      label-width="auto"
      :model="editQuery"
      :scroll-to-error="true"
      :rules="addUserRules"
    >
      <el-form-item label="姓名" prop="realName">
        <el-input v-model="editQuery.realName" />
      </el-form-item>
      <el-form-item label="昵称" prop="nickName">
        <el-input v-model="editQuery.nickName" />
      </el-form-item>
      <el-form-item label="账号" prop="account">
        <el-input v-model="editQuery.account" />
      </el-form-item>
      <el-form-item label="密码" prop="password">
        <el-input v-model="editQuery.password" type="password" />
      </el-form-item>
      <el-form-item label="手机" prop="phoneNumber">
        <el-input v-model="editQuery.phoneNumber" />
      </el-form-item>
      <el-form-item label="地址" prop="address">
        <el-input v-model="editQuery.address" />
      </el-form-item>
      <el-form-item label="用户类型">
        <el-select v-model="editQuery.userType" placeholder="请选择用户类型">
          <el-option label="普通用户" value="普通用户" />
          <el-option label="VIP用户" value="VIP用户" />
        </el-select>
      </el-form-item>
      <el-form-item label="性别" prop="gender">
        <el-select v-model="editQuery.gender" placeholder="请选择性别">
          <el-option label="男" value="男" />
          <el-option label="女" value="女" />
        </el-select>
      </el-form-item>
    </el-form>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="EditDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="commitEdit">确 定</el-button>
      </div>
    </template>
  </el-dialog>

  <!-- 添加对话框 -->
  <el-dialog v-model="addDialogVisible" title="新增用户" width="500" center>
    <el-form
      ref="addForm"
      label-width="auto"
      :model="addQuery"
      :scroll-to-error="true"
      :rules="addUserRules"
    >
      <el-form-item label="姓名" prop="realName">
        <el-input v-model="addQuery.realName" />
      </el-form-item>
      <el-form-item label="昵称" prop="nickName">
        <el-input v-model="addQuery.nickName" />
      </el-form-item>
      <el-form-item label="账号" prop="account">
        <el-input v-model="addQuery.account" />
      </el-form-item>
      <el-form-item label="密码" prop="password">
        <el-input v-model="addQuery.password" type="password" />
      </el-form-item>
      <el-form-item label="手机" prop="phoneNumber">
        <el-input v-model="addQuery.phoneNumber" />
      </el-form-item>
      <el-form-item label="地址" prop="address">
        <el-input v-model="addQuery.address" />
      </el-form-item>
      <el-form-item label="用户类型">
        <el-select v-model="addQuery.userType" placeholder="请选择用户类型">
          <el-option label="普通用户" value="普通用户" />
          <el-option label="VIP用户" value="VIP用户" />
        </el-select>
      </el-form-item>
      <el-form-item label="性别" prop="gender">
        <el-select v-model="addQuery.gender" placeholder="请选择性别">
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
import { Plus, Delete, Edit, View } from "@element-plus/icons-vue";
import { onMounted, ref } from "vue";
import { ElMessage } from "element-plus";
import { doGet, doPost, doPut, doDelete } from "../../http/httpRequest";

const tableData = ref([]);

onMounted(() => {
  loadData(1);
});

/**************************************** 加 密 算 法 *****************************************/
/**
 * @description 加密算法（SHA-256）
 */
const sha_256_encrypt = async (str) => {
  const encoder = new TextEncoder();
  const data = encoder.encode(str);
  const hash = await crypto.subtle.digest("SHA-256", data);
  const hexHash = Array.from(new Uint8Array(hash))
    .map((byte) => byte.toString(16).padStart(2, "0"))
    .join("");
  return hexHash;
};

/****************************************** 分页 *********************************************/
const size = ref(0);
const total = ref(0);

/**
 * @description 页面跳转
 */
const toPage = (current) => {
  loadData(current);
};

/**
 * @description 用户类型映射
 */
const USER_TYPE_MAP = {
  0: "普通用户",
  1: "VIP用户",
};

/**
 * @description 用户性别映射
 */
const GENDER_MAP = {
  0: "男",
  1: "女",
  2: "未知",
};

/**
 * @description 根据value获取key
 */
function getKeyByValue(object, value) {
  return Object.keys(object).find((key) => object[key] === value);
}

/**
 * @description 加载用户信息
 */
const loadData = (current) => {
  doGet("/user/page", {
    current: current,
  }).then((res) => {
    if (res.data.code === 200) {
      tableData.value = res.data.data.records;
      size.value = res.data.data.size;
      total.value = res.data.data.total;

      tableData.value.forEach((item) => {
        item.userType = USER_TYPE_MAP[item.userType] || "未知用户类型";
        item.gender = GENDER_MAP[item.gender] || "未知性别";
      });
    }
  });
};

/****************************************** 编辑 *********************************************/
const EditDialogVisible = ref(false);
const editForm = ref(null);
let editQuery = ref({
  realName: "",
  account: "",
  password: "",
  phoneNumber: "",
  nickName: "",
  address: "",
  gender: "",
  userType: "",
});

/**
 * @description 打开编辑对话框
 */
const view = (userId) => {
  doGet("/user/one", {
    userId: userId,
  })
    .then((res) => {
      if (res.data.code === 200) {
        editQuery.value = res.data.data;

        editQuery.value.gender = GENDER_MAP[res.data.data.gender] || "未知性别";
        editQuery.value.userType =
          USER_TYPE_MAP[res.data.data.userType] || "未知用户类型";

        EditDialogVisible.value = true;
      } else {
        ElMessage.error(res.data.msg);
      }
    })
    .catch((err) => {
      ElMessage.error(err);
    });
};

/**
 * @description 提交编辑
 */
const commitEdit = () => {
  editForm.value.validate((isValid) => {
    if (isValid) {
      sha_256_encrypt(editQuery.value.password).then((encryptedPassword) => {
        editQuery.value.password = encryptedPassword;

        // 使用 GENDER_MAP 来获取对应的枚举值
        editQuery.value.gender = getKeyByValue(
          GENDER_MAP,
          editQuery.value.gender
        );

        // 使用 USER_TYPE_MAP 来获取对应的枚举值
        editQuery.value.userType = getKeyByValue(
          USER_TYPE_MAP,
          editQuery.value.userType
        );

        let json = JSON.stringify(editQuery.value);

        console.log(json);

        doPut("/user/update", json)
          .then((res) => {
            if (res.data.code === 200) {
              ElMessage.success("修改成功");
              toPage(1);
            } else {
              ElMessage.error(res.data.msg);
            }
          })
          .catch((err) => {
            ElMessage.error(err);
          });
      });
    } else {
      ElMessage.error("表单验证失败");
    }
  });

  EditDialogVisible.value = false;
};

/****************************************** 添加 *********************************************/
const addDialogVisible = ref(false);
/**
 * @description 注册表单验证规则
 */
const addUserRules = ref({
  account: [
    { required: true, message: "请输入账号", trigger: "blur" },
    { min: 3, max: 20, message: "长度在 3 到 20 个字符", trigger: "blur" },
  ],
  password: [
    { required: true, message: "请输入密码", trigger: "blur" },
    { min: 6, max: 20, message: "长度在 6 到 20 个字符", trigger: "blur" },
  ],
  realName: [
    { required: true, message: "请输入姓名", trigger: "blur" },
    { min: 3, max: 20, message: "长度在 3 到 20 个字符", trigger: "blur" },
  ],
  nickName: [
    { required: true, message: "请输入昵称", trigger: "blur" },
    { min: 2, max: 20, message: "长度在 2 到 20 个字符", trigger: "blur" },
  ],
  phoneNumber: [
    { required: true, message: "手机号不能为空", trigger: "blur" },
    { pattern: /^1[3-9]\d{9}$/, message: "手机号格式错误", trigger: "blur" },
  ],
  address: [
    { required: true, message: "请输入地址", trigger: "blur" },
    { min: 3, max: 255, message: "长度在 3 到 255 个字符", trigger: "blur" },
  ],
  gender: [{ required: true, message: "请选择性别", trigger: "blur" }],
});

const addForm = ref(null);
let addQuery = ref({
  realName: "",
  account: "",
  password: "",
  phoneNumber: "",
  nickName: "",
  address: "",
  gender: "",
  userType: "",
});

/**
 * @description 打开添加用户对话框
 */
const add = () => {
  // 重置表单
  addQuery.value = {};
  addDialogVisible.value = true;
};

/**
 * @description 提交添加用户
 */
const commitAdd = () => {
  addForm.value.validate((isValid) => {
    if (isValid) {
      sha_256_encrypt(addQuery.value.password).then((encryptedPassword) => {
        addQuery.value.password = encryptedPassword;

        // 使用 GENDER_MAP 来获取对应的枚举值
        addQuery.value.gender = getKeyByValue(
          GENDER_MAP,
          addQuery.value.gender
        );

        // 使用 USER_TYPE_MAP 来获取对应的枚举值
        addQuery.value.userType = getKeyByValue(
          USER_TYPE_MAP,
          addQuery.value.userType
        );

        let json = JSON.stringify(addQuery.value);

        doPost("/user/save", json).then((res) => {
          if (res.data.code === 200) {
            ElMessage({
              message: "新增用户成功",
              type: "success",
            });
            toPage(1);
          } else {
            ElMessage.error(res.data.msg);
          }
        });
      });
    } else {
      ElMessage.error("表单验证失败");
    }
  });

  addDialogVisible.value = false;
};

/****************************************** 删除 *********************************************/
/**
 * @description 删除用户
 */
const del = (userId) => {
  doDelete("/user/delete", {
    userId: userId,
  }).then((res) => {
    if (res.data.code === 200) {
      ElMessage.success("删除成功");
      toPage(1);
    } else {
      ElMessage.error("删除失败");
    }
  });
};
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

.el-pagination {
  position: absolute;
  bottom: 35px;
  left: 50%;
  transform: translateX(-30%);
}

/******************************************** 按钮样式 ****************************************** */
.addBtn {
  border: none;
  color: #fff;
  background-color: var(--flash-blue-lighter-2);
}

.addBtn:hover {
  background-color: var(--flash-blue-lighter-3);
}

.delBtn {
  border: none;
  color: var(--flash-red-lighter-4);
  background-color: var(--flash-red-lighter-2);
}

.delBtn:hover {
  background-color: var(--flash-red-lighter-3);
}

.editBtn {
  border: none;
  color: #fff;
  background-color: var(--flash-green-color);
}

.editBtn:hover {
  background-color: var(--flash-green-lighter-2);
}
</style>