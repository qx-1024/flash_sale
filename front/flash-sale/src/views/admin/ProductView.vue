<template>
  <el-button class="addBtn" :icon="Plus" @click="add" size="small" round
    >添 加 商 品</el-button
  >
  <img
    class="empty_data_img"
    src="../../assets/empty_data.svg"
    alt="空空如也~"
    v-if="tableData.length == 0"
  />
  <el-table
    :data="tableData"
    stripe
    style="width: 100%; max-height: 610px"
    v-else
  >
    <el-table-column
      prop="productName"
      align="center"
      label="商品名称"
      show-overflow-tooltip
    />
    <el-table-column prop="price" align="center" label="价格" />
    <el-table-column prop="stock" align="center" label="库存" />
    <el-table-column prop="sales" align="center" label="销量" />
    <el-table-column
      prop="images"
      align="center"
      label="图片"
      show-overflow-tooltip
    >
      <template v-slot="scope">
        <img
          :src="scope.row.images"
          style="width: 50px; height: 50px"
          alt="盗图必究"
        />
      </template>
    </el-table-column>
    <el-table-column
      prop="createTime"
      align="center"
      label="添加时间"
      :formatter="formatDate"
      show-overflow-tooltip
    />
    <el-table-column prop="isFlashSale" align="center" label="是否参与闪购" />
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
          @click="edit(scoped.row.productId)"
          round
          >编 辑</el-button
        >
        <el-button
          class="delBtn"
          :icon="Delete"
          size="small"
          @click="del(scoped.row.productId)"
          round
          >删 除</el-button
        >
      </template>
    </el-table-column>
  </el-table>

  <!-- 分页组件 -->
  <el-pagination
    layout="prev, pager, next"
    :page-size="size"
    :total="total"
    :current-page="currentPage"
    @prev-click="toPage"
    @next-click="toPage"
    @current-change="toPage"
  />

  <!-- 编辑对话框 -->
  <el-dialog v-model="editDialogVisible" title="编辑商品" width="500" center>
    <el-form
      ref="editRefForm"
      :model="editProductForm"
      :rules="productRules"
      label-width="auto"
      style="max-width: 600px"
    >
      <el-form-item label="商品名称" prop="productName">
        <el-input v-model="editProductForm.productName" />
      </el-form-item>
      <el-form-item label="价格" prop="price">
        <el-input v-model="editProductForm.price" />
      </el-form-item>
      <el-form-item label="库存" prop="stock">
        <el-input v-model="editProductForm.stock" />
      </el-form-item>
      <el-form-item label="是否参与闪购">
        <el-select
          v-model="editProductForm.isFlashSale"
          placeholder="请选择是否参与闪购"
        >
          <el-option label="是" value="1" />
          <el-option label="否" value="0" />
        </el-select>
      </el-form-item>
      <el-form-item label="图片">
        <el-upload
          drag
          ref="uploadRef"
          method="post"
          style="width: 100%"
          accept=".jpg,.png,.jpeg"
          :limit="1"
          :show-file-list="true"
          :on-exceed="handleExceed"
          :http-request="uploadImage"
        >
          <el-icon><upload-filled color="#409eff" /></el-icon>
          <div>拖拽文件到此处或 <em>点击上传</em></div>
          <template #tip>
            <div class="el-upload__tip">
              仅支持 *.jpg, *.png, *.jpeg 格式文件
            </div>
          </template>
        </el-upload>
      </el-form-item>
    </el-form>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="editDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="commitEdit">确 定</el-button>
      </div>
    </template>
  </el-dialog>

  <!-- 添加对话框 -->
  <el-dialog v-model="addDialogVisible" title="新增商品" width="500" center>
    <el-form
      ref="addRefForm"
      :model="addProductForm"
      :rules="productRules"
      label-width="auto"
      style="max-width: 600px"
    >
      <el-form-item label="商品名称" prop="productName">
        <el-input v-model="addProductForm.productName" />
      </el-form-item>
      <el-form-item label="价格" prop="price">
        <el-input v-model="addProductForm.price" />
      </el-form-item>
      <el-form-item label="库存" prop="stock">
        <el-input v-model="addProductForm.stock" />
      </el-form-item>
      <el-form-item label="是否参与闪购">
        <el-select
          v-model="addProductForm.isFlashSale"
          placeholder="请选择是否参与闪购"
        >
          <el-option label="是" value="1" />
          <el-option label="否" value="0" />
        </el-select>
      </el-form-item>
      <el-form-item label="图片">
        <el-upload
          drag
          ref="uploadRef"
          method="post"
          style="width: 100%"
          accept=".jpg,.png,.jpeg"
          :limit="1"
          :show-file-list="true"
          :on-exceed="handleExceed"
          :http-request="uploadImage"
        >
          <el-icon><upload-filled color="#409eff" /></el-icon>
          <div>拖拽文件到此处或 <em>点击上传</em></div>
          <template #tip>
            <div class="el-upload__tip">
              仅支持 *.jpg, *.png, *.jpeg 格式文件
            </div>
          </template>
        </el-upload>
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
import { onMounted, ref, toRefs } from "vue";
import {
  Plus,
  Delete,
  Edit,
  View,
  UploadFilled,
} from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import {
  doDelete,
  doGet,
  doPost,
  doPut,
  uploadFile,
} from "../../http/httpRequest";

/********************************************* 表 单 *********************************************/
/**
 * @description 商品列表数据
 */
const tableData = ref([]);

/**
 * @description 商品表单规则
 */
const productRules = ref({
  productName: [
    { required: true, message: "商品名称不能为空", trigger: "blur" },
  ],
  price: [
    { required: true, message: "商品价格不能为空", trigger: "blur" },
    { pattern: /^\d+(\.\d+)?$/, message: "价格必须是数字", trigger: "blur" },
  ],
  stock: [
    { required: true, message: "商品库存不能为空", trigger: "blur" },
    { pattern: /^[1-9]\d*$/, message: "库存必须是整数", trigger: "blur" },
  ],
});

/********************************************* 分 页 *********************************************/
const size = ref(0);
const total = ref(0);
const currentPage = ref(1);

onMounted(() => {
  loadData(1);
});

/**
 * @description 分页，页面跳转
 */
const toPage = (current) => {
  currentPage.value = current;
  loadData(current);
};

/**
 * @description 加载商品信息
 */
const loadData = (current) => {
  doGet("/product/page", {
    current: current,
  }).then((res) => {
    if (res.data.code === 200) {
      tableData.value = res.data.data.records;
      total.value = res.data.data.total;
      size.value = res.data.data.size;

      // 遍历修改tableData
      tableData.value.forEach((item) => {
        item.isFlashSale = item.isFlashSale === 1 ? "是" : "否";
      });
    }
  });
};

/********************************************* 编 辑 *********************************************/
const editDialogVisible = ref(false);
const editProductForm = ref({});
const editRefForm = ref(null);

/**
 * @description 根据 id 编辑商品信息
 */
const edit = (id) => {
  editDialogVisible.value = true;

  // 根据 id 查询商品信息
  doGet("/product/one", {
    productId: id,
  })
    .then((res) => {
      if (res.data.code === 200) {
        editProductForm.value = res.data.data;

        editProductForm.value.isFlashSale =
          editProductForm.value.isFlashSale === 1 ? "是" : "否";
      }
    })
    .catch((err) => {
      console.log(err);
    });
};

/**
 * @description 提交修改
 */
const commitEdit = () => {
  editDialogVisible.value = false;

  editRefForm.value.validate((isValid) => {
    if (isValid) {
      // 封装修改商品表单数据
      let formData = new FormData();

      for (let key in editProductForm.value) {
        if (key === "isFlashSale") {
          if (editProductForm.value[key] === "是") {
            formData.append(key, 1);
          } else {
            formData.append(key, 0);
          }
        } else if (key === "images") {
          formData.append(key, imageUrl.value);
        } else {
          formData.append(key, editProductForm.value[key]);
        }
      }

      // 将 FormData 转换为 JSON 对象
      let json = {};
      for (let [key, value] of formData.entries()) {
        json[key] = value;
      }

      // 发送请求
      doPut("/product/update", json).then((res) => {
        if (res.data.code === 200) {
          ElMessage.success("修改成功");

          currentPage.value = 1;

          // 重新加载数据
          loadData(1);
        }
      });
    }
  });
};

/********************************************* 添 加 *********************************************/
const addDialogVisible = ref(false);
const addProductForm = ref({});
const addRefForm = ref(null);
const uploadRef = ref(null);
const imageUrl = ref("");

/**
 * @description 显示添加商品对话框
 */
const add = () => {
  addDialogVisible.value = true;
};

/**
 * @description 提交新增商品信息
 */
const commitAdd = () => {
  addRefForm.value.validate((isValid) => {
    if (isValid) {
      let formData = new FormData();

      // 图片是否已上传判断
      if (imageUrl.value !== "") {
        formData.append("images", imageUrl.value);
      } else {
        ElMessage.error("图片不能为空");
        return;
      }

      // 封装新增商品表单数据
      for (let field in addProductForm.value) {
        if (addProductForm.value[field]) {
          formData.append(field, addProductForm.value[field]);
        }
      }

      // 发送请求都后端，新增商品
      doPost("/product/save", formData).then((res) => {
        if (res.data.code === 200) {
          ElMessage({
            message: "新增商品成功",
            type: "success",
          });
          toPage(1);
        } else {
          ElMessage.error("新增商品失败");
        }
      });

      // 新增成功请求结束，关闭新增商品对话框
      addDialogVisible.value = false;
    }
  });
};

/********************************************* 删 除 *********************************************/
/**
 * @description 删除商品
 */
const del = (id) => {
  doDelete("/product/delete", {
    productId: id,
  }).then((res) => {
    if (res.data.code === 200) {
      ElMessage.success("删除成功");
      toPage(1);
    } else {
      ElMessage.error("删除失败");
    }
  });
};

/********************************************* 图 片 *********************************************/

/**
 * @description 上传图片数量超出1张时的提示
 */
const handleExceed = () => {
  // 提示只能上传一个文件
  ElMessage.error("仅支持上传一张图片哦~！");
};

/**
 * @description 上传图片，得到一个图片的 http 链接
 */
const uploadImage = (param) => {
  let fileObj = param.file;
  let fileExtension = fileObj.name.split(".").pop().toLowerCase();
  if (
    fileExtension !== "jpg" &&
    fileExtension !== "png" &&
    fileExtension !== "jpeg"
  ) {
    ElMessage.error("只能上传后缀名为 .jpg 或 .png 或 .jpeg 的文件");
    return;
  }
  let formData = new FormData();
  formData.append("file", fileObj);
  formData.append("type", "product");
  uploadFile("/store/upload", formData).then((res) => {
    if (res.data.code === 200) {
      imageUrl.value = res.data.data;

      ElMessage({
        message: "上传成功",
        type: "success",
      });
      uploadRef.value.clearFiles();
    } else {
      ElMessage.error("上传失败");
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

em {
  color: #409eff;
}

.el-upload__tip {
  color: #ea5455;
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