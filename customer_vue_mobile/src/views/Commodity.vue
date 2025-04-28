<template>
  <div class="mobile-container">
    <!-- 搜索栏 -->
    <van-search
        v-model="data.queryParams.name"
        placeholder="请输入商品名称"
        shape="round"
        @search="handleQuery"
    >
      <template #right-icon>
        <van-button size="small" type="primary" @click="handleQuery">查询</van-button>
      </template>
    </van-search>

    <!-- 筛选工具栏 -->
    <div class="filter-bar">
      <van-button plain hairline type="primary" size="small" @click="showAdvanced = true">
        高级筛选
      </van-button>
      <div class="space"></div>
      <van-button type="primary" size="small" @click="handleAdd">新增商品</van-button>
    </div>

    <!-- 商品列表 -->
    <van-list
        v-model:loading="loading"
        :finished="true"
        :immediate-check="false"
        :offset="0"
    >
      <van-checkbox-group v-model="selectedIds">
        <van-cell
            v-for="item in data.tableData"
            :key="item.productId"
            :class="{ 'low-stock': item.stockQuantity <= 50 }"
        >
          <van-checkbox :name="item.productId" />
          <div class="product-item">
            <div class="product-header">
              <span class="name">{{ item.productName }}</span>
              <van-tag v-if="item.stockQuantity <= 50" type="danger" plain>
                低库存
              </van-tag>
            </div>

            <div class="product-content">
              <!-- 商品图片 -->
              <van-image
                  v-if="item.productImage"
                  :src="item.productImage + '?accessToken=' + data.accessToken"
                  width="80"
                  height="80"
                  fit="cover"
                  class="product-image"
              />
              <div v-else class="no-image">
                <van-icon name="photo" size="24" />
              </div>

              <!-- 商品信息 -->
              <div class="product-info">
                <div class="info-row">
                  <span class="label">单价：</span>
                  <span class="value">¥{{ item.price.toFixed(2) }}</span>
                </div>
                <div class="info-row">
                  <span class="label">零售价：</span>
                  <span class="value">¥{{ item.retailPrice?.toFixed(2) || '-' }}</span>
                </div>
                <div class="info-row">
                  <span class="label">库存：</span>
                  <span class="value">{{ item.stockQuantity }}{{ item.unit || '件' }}</span>
                </div>
                <div class="info-row">
                  <span class="label">供应商：</span>
                  <span class="value">{{ item.supplierChannel }}</span>
                </div>
                <div class="info-row">
                  <span class="label">进货日期：</span>
                  <span class="value">{{ item.purchaseDate }}</span>
                </div>
              </div>
            </div>

            <!-- 操作按钮 -->
            <div class="product-actions">
              <van-button size="mini" @click="handleEdit(item)">编辑</van-button>
              <van-button size="mini" type="danger" @click="handleDelete(item.productId)">
                删除
              </van-button>
            </div>
          </div>
        </van-cell>
      </van-checkbox-group>
    </van-list>

    <!-- 分页按钮 -->
    <div class="pagination">
      <van-button
          :disabled="data.queryParams.pageNum <= 1"
          @click="prevPage"
          size="small"
      >
        上一页
      </van-button>
      <span class="page-info">第 {{ data.queryParams.pageNum }} 页 / 共 {{ totalPages }} 页</span>
      <van-button
          :disabled="finished"
          @click="nextPage"
          size="small"
      >
        下一页
      </van-button>
    </div>


    <!-- 批量操作栏 -->
    <div v-if="selectedIds.length" class="batch-actions">
      <van-button block type="danger" @click="handleBatchDelete">
        删除选中({{ selectedIds.length }})
      </van-button>
    </div>

    <!-- 高级筛选弹窗 -->
    <van-popup v-model:show="showAdvanced" round position="bottom" :style="{ height: '60%' }">
      <div class="filter-popup">
        <van-form @submit="applyFilter">
          <div class="popup-header">
            <h3>高级筛选</h3>
            <van-button native-type="submit" type="primary" size="small">应用筛选</van-button>
          </div>

          <van-field
              v-model="data.queryParams.supplier"
              is-link
              readonly
              label="供应商渠道"
              placeholder="请选择供应商"
              @click="showFilterSupplierPicker = true"
          />
          <van-popup v-model:show="showFilterSupplierPicker">
            <van-picker
                :columns="customerOptions"
                @confirm="onFilterSupplierConfirm"
            />
          </van-popup>

          <van-field
              v-model="data.queryParams.contact"
              label="联系方式"
              placeholder="供应商联系方式"
          />

          <van-field
              v-model="dateRangeText"
              readonly
              label="进货日期"
              placeholder="选择日期范围"
              @click="showDatePicker = true"
          />

          <van-field
              v-model="data.queryParams.stockThreshold"
              label="最大库存"
              type="digit"
              placeholder="输入最大库存值"
          >
            <template #extra>
              <span class="threshold-symbol">≤</span>
            </template>
          </van-field>

          <div class="filter-actions">
            <van-button block plain type="default" @click="resetFilter">重置条件</van-button>
          </div>
        </van-form>
      </div>
    </van-popup>

    <!-- 日期选择器 -->
    <van-popup v-model:show="showDatePicker" round position="bottom">
      <van-date-picker
          v-model="data.queryParams.dateRange"
          type="range"
          title="选择日期范围"
          @confirm="onDateConfirm"
          @cancel="showDatePicker = false"
      />
    </van-popup>

    <!-- 商品表单弹窗 -->
    <van-popup
        v-model:show="showForm"
        position="bottom"
        round
        :style="{ height: '90%' }"
    >
      <van-form ref="formRef" @submit="submitForm">
        <div class="form-header">
          <h3>{{ editMode ? '编辑商品' : '新建商品' }}</h3>
          <div class="form-actions">
            <van-button plain @click="handleFormCancel">取消</van-button>
            <van-button native-type="submit" type="primary">提交</van-button>
          </div>
        </div>

        <van-cell-group inset>
          <van-field
              v-model="form.productName"
              label="商品名称"
              placeholder="请输入商品名称"
              :rules="[{ required: true, message: '请填写商品名称' }]"
          />

          <van-field
              v-model="form.price"
              label="商品单价"
              type="number"
              :rules="[{ required: true, message: '请输入价格' }]"
          >
            <template #extra>
              <span class="currency">¥</span>
            </template>
          </van-field>

          <van-field
              v-model="form.retailPrice"
              label="零售价"
              type="number"
              :rules="[{ required: true, message: '请输入零售价' }]"
          >
            <template #extra>
              <span class="currency">¥</span>
            </template>
          </van-field>

          <van-field
              v-model="form.stockQuantity"
              label="库存数量"
              type="number"
              :rules="[{ required: true, message: '请输入库存数量' }]"
          />

          <van-field
              v-model="form.unit"
              label="单位"
              placeholder="件/个/箱"
              :rules="[{ max: 10, message: '最多10个字符' }]"
          />

          <van-field
              v-model="form.purchaseDate"
              readonly
              label="进货日期"
              placeholder="选择日期"
              @click="showPurchaseDatePicker = true"
              :rules="[{ required: true, message: '请选择日期' }]"
          />

          <van-field
              v-model="form.supplierChannel"
              is-link
              readonly
              label="供应商渠道"
              placeholder="请选择供应商"
              @click="showSupplierPicker = true"
          />
          <van-popup v-model:show="showSupplierPicker">
            <van-picker
                :columns="customerOptions"
                @confirm="onSupplierConfirm"
            />
          </van-popup>

          <van-field
              v-model="form.supplierContact"
              label="联系方式"
              placeholder="手机号或邮箱"
              :rules="[
              { required: false, message: '请输入联系方式' },
            ]"
          />

          <van-field name="uploader" label="商品图片">
            <template #input>
              <van-uploader
                  v-model="fileList"
                  :max-count="1"
                  :before-read="beforeImageUpload"
                  :after-read="handleUploadSuccess"
                  :deletable
                  @delete="handleImageDelete"
              >
                <template #upload-icon>
                  <van-icon name="photo" size="24" />
                </template>
              </van-uploader>
              <div class="upload-tip">支持 JPG/PNG，小于10MB</div>
            </template>
          </van-field>
        </van-cell-group>
      </van-form>
    </van-popup>

    <!-- 进货日期选择 -->
    <van-popup v-model:show="showPurchaseDatePicker" round position="bottom">
      <van-date-picker
          v-model="purchaseDate"
          title="选择进货日期"
          @confirm="onPurchaseDateConfirm"
          @cancel="showPurchaseDatePicker = false"
      />
    </van-popup>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, nextTick } from 'vue'
import { showConfirmDialog, showToast } from 'vant'
import request from '@/utils/request'
import dayjs from 'dayjs'

const data = reactive({
  accessToken: localStorage.getItem('accessToken'),
  queryParams: {
    name: '',
    supplier: '',
    contact: '',
    dateRange: [],
    stockThreshold: '',
    pageNum: 1,
    pageSize: 10,
  },
  tableData: [],
  total: 0,
})

// 表单相关
const form = reactive({
  productName: '',
  price: 0,
  retailPrice: 0,
  stockQuantity: 0,
  unit: '',
  purchaseDate: '',
  supplierChannel: '',
  supplierContact: '',
  productImage: '',
})

// 表单引用
const formRef = ref(null)

// 列表相关
const loading = ref(false)
const finished = ref(false)
const selectedIds = ref([])
const showAdvanced = ref(false)
const showForm = ref(false)
const showDatePicker = ref(false)
const editMode = ref(false)
const currentEditId = ref(null)
const fileList = ref([])
const showPurchaseDatePicker = ref(false)
const customerOptions = ref([])
const showSupplierPicker = ref(false)
const showFilterSupplierPicker = ref(false)

// 日期范围显示文本
const dateRangeText = computed(() => {
  if (data.queryParams.dateRange?.length === 2) {
    return `${dayjs(data.queryParams.dateRange[0]).format('YYYY-MM-DD')} 至 ${
        dayjs(data.queryParams.dateRange[1]).format('YYYY-MM-DD')}`
  }
  return ''
})

const onSupplierConfirm = ({ selectedOptions }) => {
  const selectedCustomer = selectedOptions[0]?.value
  if (selectedCustomer) {
    form.supplierChannel = selectedCustomer.customerName
    form.supplierContact = selectedCustomer.phoneNum || '' // 自动填充联系方式
  }
  showSupplierPicker.value = false
}

const onFilterSupplierConfirm = ({ selectedOptions }) => {
  data.queryParams.supplier = selectedOptions[0]?.value || ''
  showFilterSupplierPicker.value = false
}

// 加载客户列表
const loadCustomers = async () => {
  try {
    const res = await request.get('/customer/list')
    customerOptions.value = res.data.map(c => ({
      text: c.customerName,
      value: c
    }))
  } catch (error) {
    showToast('供应商加载失败')
  }
}

// 加载数据
const load = async () => {
  try {
    loading.value = true;
    const params = {
      ...data.queryParams,
      beginPurchaseDate: data.queryParams.dateRange?.[0]
          ? dayjs(data.queryParams.dateRange[0]).format('YYYY-MM-DD')
          : '',
      endPurchaseDate: data.queryParams.dateRange?.[1]
          ? dayjs(data.queryParams.dateRange[1]).format('YYYY-MM-DD')
          : '',
      stockThreshold: data.queryParams.stockThreshold || undefined,
    }
    delete params.dateRange

    const res = await request.get("/product/page", { params })
    data.tableData = res.data.records
    data.total = res.data.total
    // 计算总页数
    const totalPages = Math.ceil(data.total / data.queryParams.pageSize);
    finished.value = data.queryParams.pageNum >= totalPages;
  } catch (error) {
    showToast('加载失败')
  } finally {
    loading.value = false;
  }
}

const handleQuery = () => {
  data.queryParams.pageNum = 1 // 强制重置页码
  load()
}

// 计算属性
const totalPages = computed(() => {
  return Math.ceil(data.total / data.queryParams.pageSize);
});

// 新增分页方法
const prevPage = () => {
  if (data.queryParams.pageNum > 1) {
    data.queryParams.pageNum--;
    load();
  }
};

const nextPage = () => {
  if (!finished.value) {
    data.queryParams.pageNum++;
    load();
  }
};

// 表单取消处理
const handleFormCancel = () => {
  showForm.value = false
  resetForm()
}

// 应用筛选
const applyFilter = () => {
  showAdvanced.value = false
  data.queryParams.pageNum = 1
  load()
}

// 重置筛选
const resetFilter = () => {
  data.queryParams = {
    ...data.queryParams,
    supplier: '',
    contact: '',
    dateRange: [],
    stockThreshold: '',
    pageNum: 1  // 重置页码
  };
  load();
}

// 处理日期选择
const onDateConfirm = (values) => {
  data.queryParams.dateRange = values.selectedValues
  showDatePicker.value = false
}

// 表单验证规则
const contactValidator = (val) => {
  const phoneRegex = /^(?:\d{3,4}-\d{7,8}|\d{3}-\d{3,4}-\d{4}|1[3-9]\d{9})$/
  const emailRegex = /^[\w-]+(\.[\w-]+)*@([\w-]+\.)+[a-zA-Z]{2,7}$/
  return phoneRegex.test(val) || emailRegex.test(val) || '格式不正确'
}

// 图片上传处理
const beforeImageUpload = (file) => {
  const isImage = ['image/jpeg', 'image/png'].includes(file.type)
  const isLt10M = file.size / 1024 / 1024 < 10
  if (!isImage) {
    showToast('请上传图片')
    return false
  }
  if (!isLt10M) {
    showToast('图片过大')
    return false
  }
  return true
}

const handleUploadSuccess = async (file) => {
  try {
    const formData = new FormData()
    formData.append('file', file.file)
    const res = await request.post('/files/upload', formData, {
      headers: {
        accessToken: data.accessToken
      },
      // 显式设置请求体类型（针对某些请求库需要）
      transformRequest: (data) => data
    })
    if (res.code === '200') {
      form.productImage = res.data
      fileList.value = [{
        url: res.data,
        status: 'done',
        message: '上传成功'
      }];
      showToast('图片上传成功');
    }
  } catch (error) {
    showToast('上传失败')
    fileList.value = []
  }
}

const handleImageDelete = () => {
  form.productImage = ''
  fileList.value = []
}

// 处理进货日期选择
const purchaseDate = ref([])
const onPurchaseDateConfirm = ({ selectedValues }) => {
  form.purchaseDate = selectedValues.join('-')
  showPurchaseDatePicker.value = false
}

// 表单操作
const handleAdd = () => {
  resetForm()
  editMode.value = false
  showForm.value = true
}

const handleEdit = (item) => {
  editMode.value = true
  currentEditId.value = item.productId
  // 重置表单前先清空文件列表
  fileList.value = []
  // 延迟设置表单数据确保组件更新
  nextTick(() => {
    Object.assign(form, {
      ...item,
      purchaseDate: dayjs(item.purchaseDate).format('YYYY-MM-DD'),
      // 自动关联联系方式（兼容旧数据）
      supplierContact: item.supplierContact || findCustomerContact(item.supplierChannel)
    })

    // 处理图片显示（添加时间戳避免缓存）
    if (item.productImage) {
      fileList.value = [{
        url: `${item.productImage}?accessToken=${localStorage.getItem("accessToken")}`,
        isImage: true
      }]
    }
  })
  showForm.value = true
}

// 新增根据供应商名称查找联系方式的方法
const findCustomerContact = (supplierName) => {
  const customer = customerOptions.value.find(
      c => c.value.customerName === supplierName
  )?.value
  return customer?.phoneNum || ''
}

const resetForm = () => {
  formRef.value?.resetValidation()
  Object.keys(form).forEach(key => form[key] = '')
  fileList.value = []
  currentEditId.value = null
}

// 提交表单
const submitForm = async () => {
  try {
    const submitData = {
      ...form,
      purchaseDate: dayjs(form.purchaseDate).format('YYYY-MM-DD')
    }
    if (editMode.value) submitData.productId = currentEditId.value

    const apiUrl = editMode.value ? '/product/update' : '/product/add'
    const res = await request.post(apiUrl, submitData)

    if (res.code === '200') {
      showToast(editMode.value ? '修改成功' : '新增成功')
      showForm.value = false
      data.queryParams.pageNum = 1
      load()
    }
  } catch (error) {
    showToast('操作失败')
  }
}

// 删除操作
const handleDelete = (id) => {
  showConfirmDialog({
    title: '删除确认',
    message: '确定删除该商品？'
  }).then(async () => {
    await request.post('/product/delete/batch', { ids: [id] })
    showToast('删除成功')
    load()
  })
}

// 批量删除
const handleBatchDelete = () => {
  if (!selectedIds.value.length) return showToast('请选择商品')

  showConfirmDialog({
    title: `确认删除${selectedIds.value.length}项？`,
    message: '此操作不可恢复'
  }).then(async () => {
    await request.post('/product/delete/batch', { ids: selectedIds.value })
    showToast('删除成功')
    selectedIds.value = []
    load()
  })
}

// 初始化
onMounted(() => {
  load()
  loadCustomers() // 新增客户列表加载
})
</script>

<style scoped>
.mobile-container {
  padding: 12px;
  background: #f7f8fa;
  min-height: 100vh;
}

.filter-bar {
  display: flex;
  align-items: center;
  margin: 12px 0;
  gap: 8px;
}

.space {
  flex: 1;
}

.product-item {
  display: flex;
  flex-direction: column;
  width: 100%;
}

.product-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 6px;
}

.name {
  font-weight: 500;
  font-size: 16px;
}

.product-content {
  display: flex;
  gap: 12px;
}

.product-image {
  border-radius: 4px;
  overflow: hidden;
}

.no-image {
  width: 80px;
  height: 80px;
  background: #f5f5f5;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #969799;
}

.product-info {
  flex: 1;
}

.info-row {
  display: flex;
  font-size: 13px;
  line-height: 1.6;
  color: #666;
}

.label {
  min-width: 70px;
  color: #969799;
}

.value {
  flex: 1;
}

.product-actions {
  margin-top: 8px;
  display: flex;
  gap: 6px;
  justify-content: flex-end;
}

.batch-actions {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: #fff;
  padding: 12px;
  box-shadow: 0 -2px 12px rgba(0,0,0,0.1);
}

.filter-popup {
  padding: 16px;
}

.popup-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding: 0 8px;
}

.threshold-symbol {
  color: #969799;
  margin-left: 4px;
}

.filter-actions {
  margin-top: 16px;
}

.form-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid #eee;
}

.upload-tip {
  font-size: 12px;
  color: #969799;
  margin-top: 4px;
}

.currency {
  color: #ee0a24;
  margin-left: 2px;
}

.low-stock {
  background: #fff0f0;
}

/* 新增分页样式 */
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 20px;
  padding: 16px;
  background: #f7f8fa;
}

.page-info {
  font-size: 14px;
  color: #969799;
}

/* 添加强制显示上传按钮的样式 */
.van-uploader__preview-image {
  position: relative;
}
.van-uploader__preview-image::after {
  content: "重新上传";
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: rgba(0,0,0,0.5);
  color: white;
  text-align: center;
  font-size: 12px;
  padding: 2px 0;
}

/* 添加选择器样式 */
.van-picker {
  --van-picker-toolbar-height: 44px;
  --van-picker-title-font-size: 16px;
}

.van-picker__toolbar {
  padding: 0 16px;
}

.van-picker-column__item {
  font-size: 16px;
}
</style>