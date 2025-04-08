<template>
  <div>
    <!-- 查询区域 -->
    <div class="card">
      <el-collapse v-model="data.activeCollapse">
        <el-collapse-item name="basic">
          <template #title>
            <span style="font-size: 14px; padding-left: 10px">商品搜索</span>
          </template>
          <el-row :gutter="10" align="middle">
            <el-col :span="6">
              <el-input
                  v-model="data.queryParams.name"
                  placeholder="商品名称"
                  clearable
                  prefix-icon="Search"
              />
            </el-col>
            <el-col :span="4">
              <el-button type="primary" @click="load">查询</el-button>
              <el-button @click="reset">重置</el-button>
            </el-col>
          </el-row>
        </el-collapse-item>

        <el-collapse-item name="advanced">
          <template #title>
            <span style="font-size: 14px; padding-left: 10px">高级筛选</span>
          </template>
          <el-row :gutter="10" align="middle">
            <!--            <el-col :span="6">-->
            <!--              <el-input-->
            <!--                  v-model="data.queryParams.minPrice"-->
            <!--                  placeholder="最低价格"-->
            <!--                  clearable-->
            <!--              />-->
            <!--            </el-col>-->
            <!--            <el-col :span="6">-->
            <!--              <el-input-->
            <!--                  v-model="data.queryParams.maxPrice"-->
            <!--                  placeholder="最高价格"-->
            <!--                  clearable-->
            <!--              />-->
            <!--            </el-col>-->
            <el-col :span="6">
              <el-input
                  v-model="data.queryParams.supplier"
                  placeholder="供应商渠道"
                  clearable
              />
            </el-col>
            <el-col :span="6">
              <el-input
                  v-model="data.queryParams.contact"
                  placeholder="供应商联系方式"
                  clearable
              />
            </el-col>
            <el-col :span="6">
              <el-date-picker
                  v-model="data.queryParams.dateRange"
                  type="daterange"
                  range-separator="-"
                  start-placeholder="进货开始日期"
                  end-placeholder="进货结束日期"
                  style="width: 100%"
              />
            </el-col>
          </el-row>
        </el-collapse-item>
      </el-collapse>
    </div>

    <!-- 操作按钮 -->
    <div class="card" style="margin-top: 5px">
      <el-button type="primary" @click="handleAdd">新增商品</el-button>
      <el-button type="warning" @click="handleBatchDelete">批量删除</el-button>
    </div>

    <!-- 新增/编辑抽屉 -->
    <el-drawer v-model="drawerVisible" direction="rtl" size="40%" close-on-click-modal="false"
               :before-close="handleDrawerClose">
      <template #header>
        <h4>{{ editMode ? '编辑商品信息' : '新建商品信息' }}</h4>
      </template>

      <template #default>
        <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
          <el-form-item label="商品名称" prop="productName">
            <el-input v-model="form.productName" placeholder="请输入商品名称"/>
          </el-form-item>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="单价" prop="price">
                <el-input-number
                    v-model="form.price"
                    :min="0"
                    :precision="2"
                    style="width: 100%"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="库存数量" prop="stockQuantity">
                <el-input-number
                    v-model="form.stockQuantity"
                    :min="0"
                    style="width: 100%"
                />
              </el-form-item>
            </el-col>
          </el-row>

          <el-form-item label="进货日期" prop="purchaseDate">
            <el-date-picker
                v-model="form.purchaseDate"
                type="date"
                placeholder="选择日期"
                style="width: 100%"
            />
          </el-form-item>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="供应商渠道" prop="supplierChannel">
                <el-input v-model="form.supplierChannel"/>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="联系方式" prop="supplierContact">
                <el-input v-model="form.supplierContact"/>
              </el-form-item>
            </el-col>
          </el-row>

          <el-form-item label="商品图片">
            <el-upload
                action="http://localhost:8090/files/upload"
                :headers="{accessToken:data.accessToken}"
                :auto-upload="true"
                :show-file-list="false"
                :on-success="handleUploadSuccess"
                :on-change="handleImageChange"
                :before-upload="beforeImageUpload"
                list-type="picture"
            >
              <template #trigger>
                <el-button type="primary">选择图片</el-button>
              </template>
              <div v-if="form.productImage" class="preview-box">
                <img :src="form.tempImageUrl" class="preview-image"/>
                <div class="image-info">
                  <span>{{ form.imageType }} ({{ (form.imageSize / 1024).toFixed(1) }}KB)</span>
                </div>
              </div>
            </el-upload>
          </el-form-item>
        </el-form>
      </template>

      <template #footer>
        <div style="flex: auto">
          <el-button @click="handleCancel">取消</el-button>
          <el-button type="primary" @click="submitForm">提交</el-button>
        </div>
      </template>
    </el-drawer>

    <!-- 数据表格 -->
    <div class="card" style="margin-top: 5px">
      <el-table
          :data="data.tableData"
          @selection-change="handleSelectionChange"
          border
          stripe
      >
        <el-table-column type="selection" width="55"/>
        <el-table-column label="商品名称" prop="productName" width="180"/>
        <el-table-column label="单价" prop="price" width="120">
          <template #default="{row}">¥{{ row.price.toFixed(2) }}</template>
        </el-table-column>
        <el-table-column label="库存" prop="stockQuantity" width="100"/>
        <el-table-column label="进货日期" prop="purchaseDate" width="150"/>
        <el-table-column label="供应商渠道" prop="supplierChannel" width="200"/>
        <el-table-column label="供应商联系方式" prop="supplierContact" width="200"/>
        <el-table-column label="图片" prop="productImage" width="120">
          <template #default="{row}">
            <el-image
                v-if="row.productImage + '?accessToken=' + data.accessToken"
                :src="row.productImage + '?accessToken=' + data.accessToken"
                :preview-src-list="[row.productImage + '?accessToken=' + data.accessToken]"
                fit="cover"
                class="thumb-image"
            />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180">
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(scope.row.productId)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div style="margin-top: 10px">
        <el-pagination
            v-model:current-page="data.queryParams.pageNum"
            v-model:page-size="data.queryParams.pageSize"
            :page-sizes="[5, 10, 15, 20]"
            layout="total, sizes, prev, pager, next, jumper"
            :total="data.total"
            @size-change="load"
            @current-change="load"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import {reactive, ref, onMounted, onBeforeUnmount} from 'vue'
import {dayjs, ElMessage, ElMessageBox} from 'element-plus'
import request from '@/utils/request'

const data = reactive({
  accessToken: localStorage.getItem('accessToken'),
  queryParams: {
    name: '',
    supplier: '',
    contact: '',
    dateRange: [],
    pageNum: 1,
    pageSize: 10
  },
  tableData: [],
  total: 0,
  activeCollapse: ['basic'],
})

// 表单相关
const form = reactive({
  productName: '',
  price: 0,
  stockQuantity: 0,
  purchaseDate: '',
  supplierChannel: '',
  supplierContact: '',
  productImage: '',
  imageType: '',
  imageSize: 0,
  tempImageUrl: null     // 新增临时URL存储
})

const rules = {
  productName: [{required: true, message: '请输入商品名称', trigger: 'blur'}],
  price: [{required: true, message: '请输入商品价格', trigger: 'blur'}],
  stockQuantity: [{required: true, message: '请输入库存数量', trigger: 'blur'}],
  purchaseDate: [{required: true, message: '请选择进货日期', trigger: 'change'}],
  supplierChannel: [{required: true, message: '请输入供应商渠道', trigger: 'blur'}],
  supplierContact: [
    {required: true, message: '请输入联系方式', trigger: 'blur'},
    {
      validator: (_, value, callback) => {
        const phoneRegex = /^(?:\d{3,4}-\d{7,8}|\d{3}-\d{3,4}-\d{4}|1[3-9]\d{9})$/
        const emailRegex = /^[\w-]+(\.[\w-]+)*@([\w-]+\.)+[a-zA-Z]{2,7}$/
        if (!phoneRegex.test(value) && !emailRegex.test(value)) {
          callback(new Error('请输入有效的手机号或邮箱'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 图片处理
const handleImageChange = (file) => {
  form.tempImageUrl = URL.createObjectURL(file.raw)
  // form.productImage = form.tempImageUrl
  form.imageType = file.raw.type
  form.imageSize = file.raw.size
}

// 图片上传处理
const beforeImageUpload = (file) => {
  const isImage = ['image/jpeg', 'image/png'].includes(file.type)
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('只能上传JPG/PNG格式图片!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过2MB!')
    return false
  }
  return true
}
// 图片成功上传后的处理
const handleUploadSuccess = (response) => {
  console.log('完整上传响应:', response)
  if (response.code === '200') {
    form.productImage = response.data
    form.imageType = response.data.split('.').pop()
    form.imageSize = 0
    URL.revokeObjectURL(form.tempImageUrl)
    form.tempImageUrl = null
  } else {
    ElMessage.error('图片上传格式异常')
  }
}

// 组件卸载时清理
onBeforeUnmount(() => {
  if(form.tempImageUrl?.startsWith('blob:')) {
    URL.revokeObjectURL(form.tempImageUrl)
  }
})

// 在提交前添加日期验证
const validateDateRange = () => {
  if (data.queryParams.dateRange?.[0] > data.queryParams.dateRange?.[1]) {
    ElMessage.error('结束日期不能早于开始日期');
    return false;
  }
  return true;
}

// 加载商品数据
const load = () => {
  if (!validateDateRange()) return;
  // 深拷贝参数避免污染原始数据
  const params = {...data.queryParams};
  // 转换日期范围参数
  if (params.dateRange && params.dateRange.length === 2) {
    params.beginPurchaseDate = dayjs(params.dateRange[0]).format('YYYY-MM-DD');
    params.endPurchaseDate = dayjs(params.dateRange[1]).format('YYYY-MM-DD');
  }
  delete params.dateRange; // 移除原始参数

  request.get("/product/page", {
    params: params
  }).then(res => {
    data.tableData = res.data.records
    data.total = res.data.total
  })
}

// 查询条件重置
const reset = () => {
  data.queryParams = {
    ...data.queryParams,  // 保留分页参数
    name: '',
    supplier: '',
    contact: '',
    dateRange: [],
  }
  load()
}

// 抽屉控制
const drawerVisible = ref(false)
const formRef = ref()
const editMode = ref(false)
const currentEditId = ref(null)

// 新增/编辑操作
const handleAdd = () => {
  resetForm()
  editMode.value = false
  drawerVisible.value = true
}

const handleEdit = (row) => {
  editMode.value = true
  currentEditId.value = row.productId

  Object.keys(form).forEach(key => {
    form[key] = row[key] || ''
  })
  drawerVisible.value = true
}

const resetForm = () => {
  if(form.tempImageUrl?.startsWith('blob:')) {
    URL.revokeObjectURL(form.tempImageUrl)
  }
  formRef.value?.resetFields()
  Object.keys(form).forEach(key => form[key] = '')
  form["tempImageUrl"]=null
}


// 抽屉的取消操作
const handleCancel = async () => {
  try {
    await ElMessageBox.confirm(
        '确定要取消编辑吗？所有未保存的更改将丢失。',
        '确认取消',
        {
          confirmButtonText: '确认',
          cancelButtonText: '取消',
          type: 'warning'
        }
    )
    // 用户确认取消后执行
    resetForm()
    drawerVisible.value = false
  } catch (error) {
    // 用户点击取消或关闭对话框时，不做操作
    console.log('用户取消了关闭操作')
  }
}

// 提交表单
const submitForm = async () => {
  try {
    console.log("看看提交的是什么："+form.productImage)
    if (!form.productImage || form.productImage.startsWith('blob:')) {
      ElMessage.error('请先完成图片上传')
      return
    }
    await formRef.value.validate()

    const submitData = {
      ...form,
      // 转换日期格式
      purchaseDate: dayjs(form.purchaseDate).format('YYYY-MM-DD')
    }
    delete submitData.tempImageUrl
    if (editMode.value) submitData.productId = currentEditId.value

    const apiUrl = editMode.value ? '/product/update' : '/product/add'
    const res = await request.post(apiUrl, submitData)

    if (res.code === '200') {
      ElMessage.success(editMode.value ? '修改成功' : '新增成功')
      drawerVisible.value = false
      data.queryParams.pageNum = 1 // 新增数据后回到第一页
      load()
    }
  } catch (error) {
    console.error('提交失败:', error)
  }
}

// 批量删除操作逻辑
// 新增响应式数据存储选中项
const selectedRows = ref([])

// 多选处理函数
const handleSelectionChange = (selection) => {
  selectedRows.value = selection
}

// 批量删除逻辑
const handleBatchDelete = () => {
  if (!selectedRows.value?.length) {
    ElMessage.warning('请至少选择一条要删除的数据')
    return
  }

  ElMessageBox.confirm(
      `确定要删除这 ${selectedRows.value.length} 条记录吗？此操作不可逆！`,
      '危险操作确认',
      {
        confirmButtonText: '确认删除',
        cancelButtonText: '取消',
        type: 'warning',
        dangerouslyUseHTMLString: true, // 允许显示红色提醒
        customClass: 'message-box-custom' // 自定义样式类（可选）
      }
  ).then(async () => {
    try {
      // 提取选中项的ID集合（假设数据中包含id字段）
      const ids = selectedRows.value.map(item => item.productId)

      const response = await request.post('/product/delete/batch', {ids})

      if (response.code === '200') {
        ElMessage.success(`成功删除 ${response.data} 条记录`)
        load() // 刷新表格
        selectedRows.value = [] // 清空选中
      }
    } catch (error) {
      console.error('批量删除失败:', error)
      const errorMsg = error.response?.data?.msg || '删除操作失败'
      ElMessage.error(errorMsg)
    }
  }).catch(() => {
    // 取消操作
  })
}

// 删除操作
const handleDelete = (id) => {
  ElMessageBox.confirm('确定删除该商品？', '警告', {
    confirmButtonText: '确认',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    // 复用批量删除接口
    await request.post('/product/delete/batch', {ids: [id]})
    ElMessage.success('删除成功')
    load()
  })
}

// 新增关闭前处理逻辑
const handleDrawerClose = (done) => {
  if (isFormDirty()) {
    ElMessageBox.confirm('表单有未保存的内容，确定要关闭吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      resetForm()
      done()
    }).catch(() => {
      // 取消关闭操作
    })
  } else {
    resetForm()
    done()
  }
}

// 判断表单是否有修改
const isFormDirty = () => {
  return Object.values(form).some(value => !!value)
}

// 初始化
onMounted(() => {
  load()
})
</script>

<style scoped>
.preview-box {
  margin-top: 10px;
  position: relative;
}

.preview-image {
  max-width: 200px;
  max-height: 200px;
  border-radius: 4px;
}

.image-info {
  margin-top: 5px;
  color: #666;
  font-size: 12px;
}

.thumb-image {
  width: 60px;
  height: 60px;
  border-radius: 4px;
}
</style>