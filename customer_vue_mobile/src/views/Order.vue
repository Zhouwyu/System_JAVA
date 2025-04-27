<template>
  <div class="order-mobile">
    <!-- 搜索栏 -->
    <van-sticky>
      <van-search
          v-model="data.queryParams.orderNo"
          placeholder="订单编号"
          @search="load"
      />
      <div class="filter-btns">
        <van-button size="small" @click="showCustomerPicker = true">
          选择客户
        </van-button>
        <van-button size="small" @click="showDatePicker = true">
          选择日期
        </van-button>
        <van-button type="primary" size="small" @click="load">
          查询
        </van-button>
      </div>
    </van-sticky>

    <!-- 操作按钮 -->
    <van-sticky :offset-top="94">
      <div class="action-btns">
        <van-button type="primary" size="small" @click="handleAdd">
          新建
        </van-button>
        <van-button type="danger" size="small" @click="handleBatchDelete">
          删除
        </van-button>
      </div>
    </van-sticky>

    <!-- 订单列表 -->
    <div class="order-list">
      <van-swipe-cell
          v-for="item in data.tableData"
          :key="item.orderId"
      >
        <van-cell
            :title="`订单号：${item.orderNum}`"
            :value="orderStatusMap[item.status]"
            :label="formatDate(item.createTime)"
            @click="showOrderDetail(item)"
        />
        <template #right>
          <van-button
              square
              type="danger"
              text="删除"
              @click="handleDelete(item.orderId)"
          />
        </template>
      </van-swipe-cell>
      <!-- 分页控件 -->
      <div class="pagination">
        <van-button
            size="small"
            :disabled="data.queryParams.pageNum === 1"
            @click="prevPage"
        >
          上一页
        </van-button>
        <span class="page-info">
        {{ data.queryParams.pageNum }} / {{ totalPages }}
      </span>
        <van-button
            size="small"
            :disabled="data.queryParams.pageNum >= totalPages"
            @click="nextPage"
        >
          下一页
        </van-button>
      </div>
    </div>

    <!-- 客户选择弹窗增加搜索 -->
    <van-popup v-model:show="showCustomerSelect" position="bottom" class="customer-select-popup">
      <van-search
          v-model="customerSearch"
          placeholder="输入客户名称搜索"
          shape="round"
          @update:model-value="handleCustomerSearch"
      />
      <van-picker
          :columns="filteredCustomerColumns"
          show-toolbar
          title="选择客户"
          @confirm="onCustomerSelectConfirm"
          @cancel="showCustomerSelect = false"
      />
    </van-popup>

    <!-- 商品选择弹窗增加搜索 -->
    <van-popup v-model:show="showProductSelect" position="bottom" class="product-select-popup">
      <van-search
          v-model="productSearch"
          placeholder="输入商品名称搜索"
          shape="round"
          @update:model-value="handleProductSearch"
      />
      <div class="product-list">
        <van-checkbox-group v-model="tempSelectedProducts">
          <van-cell
              v-for="product in filteredProducts"
              :key="product.productId"
              clickable
              :title="product.productName"
              :label="`库存: ${product.stock} | 单价: ¥${product.price}`"
              @click="toggleProductSelection(product)"
          >
            <template #right-icon>
              <van-checkbox :name="product.productId" :disabled="product.stock <= 0" />
            </template>
          </van-cell>
        </van-checkbox-group>
      </div>
      <div class="popup-footer">
        <van-button type="primary" block @click="confirmProductSelection">确认选择</van-button>
      </div>
    </van-popup>

    <!-- 日期选择 -->
    <van-popup v-model:show="showDatePicker" position="bottom">
      <van-date-picker
          v-model="currentDate"
          type="range"
          @confirm="onDateConfirm"
          @cancel="showDatePicker = false"
      />
    </van-popup>

    <!-- 新建订单弹窗 -->
    <van-popup
        v-model:show="showCreate"
        position="bottom"
        :style="{ height: '90%' }"
    >
      <van-form @submit="submitForm">
        <van-cell title="新建订单" class="form-title" />

        <!-- 客户选择 -->
        <van-field
            v-model="selectedCustomerName"
            label="选择客户"
            placeholder="点击选择客户"
            readonly
            clickable
            @click="showCustomerSelect = true"
        />

        <!-- 商品选择 -->
        <van-field
            v-model="selectedProductsText"
            label="选择商品"
            placeholder="点击选择商品"
            readonly
            clickable
            @click="openProductSelect"
        />

        <!-- 商品明细 -->
        <div v-for="(item, index) in form.products" :key="index" class="product-item">
          <van-cell :title="item.productName" :value="`¥${item.salePrice}`" />
          <div class="product-controls">
            <van-stepper
                v-model="item.quantity"
                integer
                :min="1"
                :max="item.stock"
                @change="calculateTotal"
            />
            <van-field
                v-model="item.productRemark"
                placeholder="商品备注"
                class="remark-field"
            />
            <van-button
                size="small"
                icon="delete"
                type="danger"
                @click="removeProduct(index)"
            />
          </div>
        </div>

        <van-field
            v-model="form.totalPrice"
            label="总金额"
            readonly
        />

        <van-field
            v-model="form.shipmentDate"
            label="出货时间"
            placeholder="点击选择时间"
            readonly
            clickable
            @click="showTimePicker = true"
        />

        <van-field
            v-model="form.remark"
            label="订单备注"
            placeholder="请输入备注"
            type="textarea"
            rows="2"
            autosize
        />

        <div class="submit-btn">
          <van-button round block type="primary" native-type="submit">
            提交
          </van-button>
        </div>
      </van-form>
    </van-popup>

    <!-- 日期时间选择组合 -->
    <van-popup v-model:show="showDatePicker" position="bottom">
      <van-date-picker
          v-model="currentDate"
          :min-date="minDate"
          @confirm="handleDateConfirm"
          @cancel="showDatePicker = false"
      />
    </van-popup>

    <van-popup v-model:show="showTimePicker" position="bottom">
      <van-time-picker
          v-model="currentTime"
          @confirm="handleTimeConfirm"
          @cancel="showTimePicker = false"
      />
    </van-popup>

    <!-- 订单详情 -->
    <van-popup
        v-model:show="showDetail"
        position="bottom"
        :style="{ height: '90%' }"
    >
      <div class="detail-content" v-if="currentOrder">
        <h3 class="detail-title">订单详情</h3>
        <van-cell title="订单编号" :value="currentOrder.orderNo" />
        <van-cell title="客户名称" :value="currentOrder.customer?.customerName" />
        <van-cell title="联系方式" :value="currentOrder.customer?.phoneNum" />
        <van-cell title="联系地址" :value="currentOrder.customer?.address" />

        <van-collapse v-model="activeCollapse">
          <van-collapse-item title="商品明细" name="products">
            <div
                v-for="(product, index) in currentOrder.products"
                :key="index"
                class="detail-product"
            >
              <div class="product-name">{{ product.productName }}</div>
              <div class="product-info">
                <span>成交单价：¥{{ `${product.salePrice}/${product.unit || '件'}` }}</span>
                <span>数量：{{ product.quantity }}</span>
                <span>优惠扣减：-¥{{ (product.itemDiscount * product.quantity).toFixed(2) }}</span>
                <span>商品备注：{{ product.productRemark }}</span>
              </div>
            </div>
          </van-collapse-item>
        </van-collapse>
        <van-cell title="总金额"  :value="`¥${Math.max(currentOrder.totalPrice, 0)}`"  />
        <van-cell
            :title="discountInfo.title"
            :value="discountInfo.value"
        />
        <van-cell title="订单备注" :value="currentOrder.remark" />
        <van-cell title="下单时间" :value="formatDate(currentOrder.createTime)" />
        <van-cell title="出货时间" :value="formatDate(currentOrder.shipmentDate)" />
        <van-cell title="订单状态" :value="orderStatusMap[currentOrder.status]" />
        <van-cell title="操作员" :value="currentOrder.operator.name" />

      </div>
    </van-popup>
  </div>
</template>

<script setup>
import { reactive, ref, computed, onMounted } from 'vue'
import {Dialog, showToast, Toast} from 'vant'
import request from '@/utils/request'
import dayjs from "dayjs";

const data = reactive({
  queryParams: {
    orderNo: '',
    customerId: null,
    productId: null,
    dateRange: [],
    pageNum: 1,
    pageSize: 10
  },
  tableData: [],
  total: 0,
  status: 1, // 默认状态码
  customerOptions: [],
  productOptions: [],
})

const form = reactive({
  customerId: null,
  customerPhone: '',
  customerAddress: '',
  customerBusinessIndustry: '',
  selectedProducts: [], // 选中的商品对象数组
  products: [],         // 最终提交的商品数组（包含id和数量）
  productId: null,
  quantity: 1,
  price: 0,
  totalPrice: 0,
  remark: '',
  shipmentDate: ''
})

const currentOrder = ref({
  // 基础订单信息
  orderId: null,
  orderNo: '',
  createTime: '',
  shipmentDate: '',
  totalPrice: 0,
  status: 1, // 默认状态码
  remark: '',
  updateTime: '',

  // 关联客户信息
  customer: {
    customerId: null,
    customerName: '',
    phoneNum: '',
    address: '',
    businessIndustry: ''
  },

  // 操作人员信息
  operator: {
    userId: null,
    name: '' || '系统自动'
  },

  // 商品清单（数组结构）
  products: [
    {
      productId: null,
      productName: '',
      price: 0,
      quantity: 1,
      salePrice: 0, // 成交价
      itemDiscount: 0, // 单品折扣
      unit: null,
      productRemark: ''
    }
  ],

  // 做修改订单用
  discountType: 0,       // 折扣类型 0-无 1-百分比 2-固定金额
  discountRate: 0,       // 折扣率（百分比）
  discountAmount: 0,     // 优惠金额
  originalOrderId: null, // 原始订单ID（用于关联修订）
  isRevised: false       // 是否是修订订单
})

// 移动端特有状态
const showCustomerPicker = ref(false)
const showDatePicker = ref(false)
const showCreate = ref(false)
const showTimePicker = ref(false)
const showDetail = ref(false)
const currentDate = ref(new Date())
const currentTime = ref('12:00')
const minDate = new Date()
const activeCollapse = ref([])
const showCustomerSelect = ref(false)
const selectedCustomerName = ref('')
const showProductSelect = ref(false)
const selectedProductsText = ref('')
// 新增临时选中商品状态
const tempSelectedProducts = ref([])
// 新增搜索相关状态
const customerSearch = ref('')
const productSearch = ref('')

// 订单状态映射
const orderStatusMap = {
  1: '待出货',
  2: '已出货',
}

// 状态标签样式
const statusTagType = {
  1: 'warning',
  2: 'success',
}

const discountInfo = computed(() => {
  const type = currentOrder.value.discountType
  const rate = currentOrder.value.discountRate
  const amount = currentOrder.value.discountAmount

  switch(type) {
    case 1:
      return {
        title: '总体折扣率',
        value: `${rate}%`
      }
    case 2:
      return {
        title: '总体扣减金额',
        value: `¥${amount}`
      }
    default:  // 0 或未定义
      return {
        title: '总体无折扣',
        value: '¥0'
      }
  }
})

// 获取可用商品列表（过滤已失效商品）
const availableProducts = computed(() => {
  return data.productOptions.filter(p => p.stock > 0)
})

// 切换商品选择
const toggleProductSelection = (product) => {
  if (product.stock <= 0) return
  const index = tempSelectedProducts.value.indexOf(product.productId)
  if (index > -1) {
    tempSelectedProducts.value.splice(index, 1)
  } else {
    tempSelectedProducts.value.push(product.productId)
  }
}

// 确认商品选择
const confirmProductSelection = () => {
  // 合并新旧商品
  const newProducts = [
    ...form.products.filter(p => tempSelectedProducts.value.includes(p.productId)),
    ...tempSelectedProducts.value
        .filter(id => !form.products.some(p => p.productId === id))
        .map(id => {
          const product = availableProducts.value.find(p => p.productId === id)
          return {
            ...product,
            quantity: 1,
            salePrice: product.price,
            productRemark: ''
          }
        })
  ]

  form.products = newProducts
  updateSelectedProductsText()
  calculateTotal()
  showProductSelect.value = false
}

// 打开商品选择弹窗时初始化临时选中
const openProductSelect = () => {
  productSearch.value = ''
  tempSelectedProducts.value = form.products.map(p => p.productId)
  showProductSelect.value = true
}

// 更新已选商品显示文本
const updateSelectedProductsText = () => {
  selectedProductsText.value = form.products
      .map(p => `${p.productName}×${p.quantity}`)
      .join('，')
}

// 修改商品数量处理方法
const calculateTotal = () => {
  form.totalPrice = form.products.reduce((sum, item) => {
    return sum + (item.salePrice * item.quantity)
  }, 0)
  updateSelectedProductsText()
}

// 修改商品移除方法
const removeProduct = (index) => {
  form.products.splice(index, 1)
  // 同步更新临时选中状态
  tempSelectedProducts.value = form.products.map(p => p.productId)
  calculateTotal()
  updateSelectedProductsText()
}

// 过滤后的商品列表
const filteredProducts = computed(() => {
  let list = availableProducts.value
  if (productSearch.value) {
    const keyword = productSearch.value.toLowerCase()
    list = list.filter(p =>
        p.productName.toLowerCase().includes(keyword) ||
        p.productId.toString().includes(keyword)
    )
  }
  return list
})

// 处理商品搜索
const handleProductSearch = (val) => {
  productSearch.value = val
}


// 客户选择器列
const customerColumns = computed(() => {
  return data.customerOptions.map(item => ({
    text: item.label,
    value: item.value
  }))
})

// 过滤后的客户列表
const filteredCustomerColumns = computed(() => {
  if (!customerSearch.value) return customerColumns.value
  return customerColumns.value.filter(item =>
      item.text.toLowerCase().includes(customerSearch.value.toLowerCase())
  )
})

// 处理客户搜索
const handleCustomerSearch = (val) => {
  customerSearch.value = val
}

// 打开弹窗时重置搜索条件
const openCustomerSelect = () => {
  customerSearch.value = ''
  showCustomerSelect.value = true
}

// 完善客户选择逻辑
const onCustomerSelectConfirm = ({ selectedOptions }) => {
  const selected = selectedOptions[0]
  form.customerId = selected.value
  selectedCustomerName.value = selected.text
  showCustomerSelect.value = false

  // 获取并填充客户详细信息
  const customer = data.customerOptions.find(c => c.value === selected.value)
  if (customer) {
    form.customerPhone = customer.phoneNum || ''
    form.customerAddress = customer.address || ''
    form.customerBusinessIndustry = customer.businessIndustry || ''
  }
}

// 加载基础数据
const loadBaseData = async () => {
  try {
    // 客户数据
    const [customers, products] = await Promise.all([
      request.get('/customer/list'),
      request.get('/product/list')
    ])

    data.customerOptions = customers.data.map(c => ({
      value: c.customerId,
      label: c.customerName
    }))

    data.productOptions = products.data.map(p => ({
      value: p.productId,
      label: p.productName,
      price: p.price,
      stock: p.stockQuantity,
      unit: p.unit,
      // 补充完整商品信息
      ...p
    }))
    // 强制更新过滤列表
    // filteredProducts.value = [...data.productOptions]
  } catch (error) {
    showToast('基础数据加载失败')
  }
}

const totalPages = computed(() => {
  return Math.ceil(data.total / data.queryParams.pageSize)
})

const prevPage = () => {
  if (data.queryParams.pageNum > 1) {
    data.queryParams.pageNum--
    load()
  }
}

const nextPage = () => {
  if (data.queryParams.pageNum < totalPages.value) {
    data.queryParams.pageNum++
    load()
  }
}

// 加载数据
const load = async () => {
  try {
    const params = {
      ...data.queryParams,
      beginShipmentDate: currentDate.value[0],
      endShipmentDate: currentDate.value[1]
    }

    const res = await request.get('/order/page', { params })
    data.tableData = res.data.records
    data.total = res.data.total
  } catch (error) {
    Toast.fail('加载失败')
  }
}

// 新建订单
const handleAdd = () => {
  showCreate.value = true
}

// 提交表单
const submitForm = async () => {
  try {
    const payload = {
      ...form,
      products: form.products.map(item => ({
        productId: item.productId,
        quantity: item.quantity,
        salePrice: item.salePrice,
        productRemark: item.productRemark
      }))
    }

    await request.post('/order/add', payload)
    Toast.success('创建成功')
    showCreate.value = false
    load()
  } catch (error) {
    Toast.fail('提交失败')
  }
}

// 显示订单详情
const showOrderDetail = async (item) => {
  const res = await request.get(`/order/detail/${item.orderId}`)
  currentOrder.value = res.data
  showDetail.value = true
}

// 处理日期确认
const handleDateConfirm = (date) => {
  currentDate.value = date
  showDatePicker.value = false
  showTimePicker.value = true
}

// 处理时间确认
const handleTimeConfirm = (time) => {
  const selectedDate = new Date(currentDate.value)
  const [hours, minutes] = time.split(':')

  selectedDate.setHours(hours)
  selectedDate.setMinutes(minutes)

  // 验证时间是否合法
  if (selectedDate < new Date()) {
    showToast('不能选择过去的时间')
    return
  }

  form.shipmentDate = dayjs(selectedDate).format('YYYY-MM-DD HH:mm:ss')
  showTimePicker.value = false
}

// 通用日期格式化方法
const formatDate = (dateString) => {
  return dateString ? dayjs(dateString).format('YYYY-MM-DD HH:mm:ss') : '-'
}

// 其他方法保持与PC端类似，调整交互方式即可

onMounted(() => {
  load()
  loadBaseData()
  // filteredProducts.value = [...data.productOptions]
  // filteredCustomers.value = [...data.customerOptions]
})
</script>

<style scoped>
.order-mobile {
  padding-bottom: 60px;
}

.filter-btns {
  padding: 8px;
  display: flex;
  gap: 8px;
  background: #fff;
}

.action-btns {
  padding: 8px;
  background: #fff;
  display: flex;
  gap: 8px;
}

.product-item {
  margin: 12px;
  padding: 8px;
  background: #f7f8fa;
  border-radius: 8px;
}

.product-controls {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 8px;
}

.remark-field {
  flex: 1;
}

.submit-btn {
  margin: 20px 16px;
}

.detail-content {
  padding: 16px;
}

.detail-title {
  text-align: center;
  margin-bottom: 16px;
}

.detail-product {
  padding: 8px 0;
  border-bottom: 1px solid #eee;
}

.product-name {
  font-weight: 500;
  margin-bottom: 4px;
}

.product-info {
  display: flex;
  justify-content: space-between;
  color: #666;
  font-size: 12px;
}

.pagination {
  position: relative;
  background: #fff;
  justify-content: center;
  align-items: center;
  gap: 15px;
  box-shadow: 0 -2px 10px rgba(0,0,0,0.1);
  display: flex;
  padding: 16px 0;
  margin-top: auto; /* 关键：使分页始终位于底部 */
}

.page-info {
  font-size: 14px;
  color: #666;
}

.order-list {
  padding-bottom: 60px; /* 留出分页栏空间 */
}

/* 调整弹窗样式 */
.customer-select-popup,
.product-select-popup {
  height: 70vh;
  display: flex;
  flex-direction: column;
}

.customer-select-popup .van-picker {
  flex: 1;
  overflow-y: auto;
}

.product-select-popup .product-list {
  flex: 1;
  overflow-y: auto;
}

/* 搜索框样式 */
:deep(.van-search) {
  padding: 8px;
  background: #fff;
  border-bottom: 1px solid #eee;
}

/* 商品列表项优化 */
:deep(.van-cell__title) {
  flex: 2;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>