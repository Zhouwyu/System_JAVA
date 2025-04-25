<template>
  <!-- 查询区域 -->
  <van-collapse v-model="activeCollapse" accordion>
    <van-collapse-item title="订单查询" name="basic">
      <van-search v-model="queryParams.orderNo" placeholder="订单编号" clearable />
      <van-field
          readonly
          clickable
          name="customer"
          :value="selectedCustomer"
          label="选择客户"
          placeholder="点击选择客户"
          @click="showCustomerPicker = true"
      />
    </van-collapse-item>

    <van-collapse-item title="高级筛选" name="advanced">
      <van-field
          readonly
          clickable
          name="dateRange"
          :value="dateRangeText"
          label="出货日期"
          placeholder="选择日期范围"
          @click="showDatePicker = true"
      />
    </van-collapse-item>
  </van-collapse>

  <!-- 操作按钮 -->
  <div class="action-buttons">
    <van-button type="primary" block @click="handleAdd">新建订单</van-button>
    <van-button type="warning" block @click="handleBatchDelete">批量删除</van-button>
  </div>

  <!-- 订单列表 -->
  <van-list
      v-model:loading="loading"
      :finished="finished"
      finished-text="没有更多了"
      @load="onLoad"
  >
    <van-swipe-cell
        v-for="item in tableData"
        :key="item.orderId"
        :before-close="beforeClose"
    >
      <van-card
          :title="item.orderNo"
          :desc="`状态: ${orderStatusMap[item.status]}`"
      >
        <template #tags>
          <van-tag :type="statusTagType[item.status]">
            {{ orderStatusMap[item.status] }}
          </van-tag>
        </template>
        <template #footer>
          <div class="card-footer">
            <span>{{ formatDate(item.createTime) }}</span>
            <van-checkbox
                v-model="selectedRows[item.orderId]"
                @click.stop
            />
          </div>
        </template>
      </van-card>
      <template #right>
        <van-button
            square type="danger"
            text="删除"
            @click="handleDelete(item.orderId)"
        />
        <van-button
            v-if="item.status === 1"
            square type="primary"
            text="出货"
            @click="handleShip(item)"
        />
      </template>
    </van-swipe-cell>
  </van-list>

  <!-- 新建订单弹窗 -->
  <van-popup
      v-model:show="showCreate"
      position="bottom"
      :style="{ height: '90%' }"
  >
    <van-nav-bar
        title="新建订单"
        left-text="返回"
        right-text="提交"
        @click-left="showCreate = false"
        @click-right="submitForm"
    />

    <van-form ref="formRef">
      <van-field
          readonly
          clickable
          name="customer"
          label="选择客户"
          :value="form.customerName"
          placeholder="点击选择客户"
          :rules="[{ required: true, message: '请选择客户' }]"
          @click="showCustomerSelect = true"
      />

      <van-field
          v-model="form.customerPhone"
          label="客户电话"
          readonly
      />
      <van-field
          v-model="form.customerAddress"
          label="客户地址"
          readonly
      />

      <van-field
          readonly
          clickable
          name="products"
          label="选择商品"
          placeholder="点击选择商品"
          @click="showProductSelect = true"
      />

      <div v-for="(product, index) in form.products" :key="index" class="product-item">
        <van-card :title="product.productName">
          <template #price>
            <div class="price-controls">
              <van-stepper
                  v-model="product.quantity"
                  :min="1"
                  :max="product.stock"
                  @change="calculateTotal"
              />
              <span class="price">¥{{ (product.salePrice * product.quantity).toFixed(2) }}</span>
            </div>
          </template>
          <template #footer>
            <van-button
                size="mini"
                icon="delete"
                @click="removeProduct(index)"
            />
          </template>
        </van-card>
      </div>

      <van-field
          readonly
          clickable
          name="shipmentDate"
          label="出货时间"
          :value="form.shipmentDate ? formatDate(form.shipmentDate) : ''"
          placeholder="点击选择时间"
          :rules="[{ required: true, message: '请选择出货时间' }]"
          @click="showDateSelect = true"
      />

      <van-field
          v-model="form.remark"
          label="订单备注"
          type="textarea"
          rows="2"
          maxlength="100"
          show-word-limit
      />

      <div class="total-price">
        总金额：¥{{ form.totalPrice.toFixed(2) }}
      </div>
    </van-form>
  </van-popup>

  <!-- 客户选择 -->
  <van-popup v-model:show="showCustomerSelect" round position="bottom">
    <van-picker
        :columns="customerOptions"
        @confirm="onCustomerConfirm"
        @cancel="showCustomerSelect = false"
    />
  </van-popup>

  <!-- 商品选择 -->
  <van-popup
      v-model:show="showProductSelect"
      position="bottom"
      :style="{ height: '70%' }"
  >
    <van-search v-model="productSearch" placeholder="搜索商品" />
    <van-checkbox-group v-model="selectedProducts">
      <van-cell-group>
        <van-cell
            v-for="product in filteredProducts"
            :key="product.productId"
            clickable
            :title="product.productName"
            :value="`库存: ${product.stock}`"
            @click="toggleProduct(product)"
        >
          <template #right-icon>
            <van-checkbox
                :name="product.productId"
                :disabled="product.stock <= 0"
            />
          </template>
        </van-cell>
      </van-cell-group>
    </van-checkbox-group>
  </van-popup>

  <!-- 日期选择 -->
<!--  <van-popup v-model:show="showDateSelect" position="bottom">-->
<!--    <van-datetime-picker-->
<!--        type="datetime"-->
<!--        :min-date="minDate"-->
<!--        @confirm="onDateConfirm"-->
<!--        @cancel="showDateSelect = false"-->
<!--    />-->
<!--  </van-popup>-->

  <!-- 订单详情 -->
  <van-popup
      v-model:show="showDetail"
      position="right"
      :style="{ width: '100%', height: '100%' }"
  >
    <van-nav-bar
        title="订单详情"
        left-text="返回"
        @click-left="showDetail = false"
    />

    <div class="detail-content">
      <van-cell-group>
        <van-cell title="订单编号" :value="currentOrder.orderNo" />
        <van-cell title="订单状态">
          <van-tag :type="statusTagType[currentOrder.status]">
            {{ orderStatusMap[currentOrder.status] }}
          </van-tag>
        </van-cell>
        <van-cell title="创建时间" :value="formatDate(currentOrder.createTime)" />
        <van-cell title="出货时间" :value="formatDate(currentOrder.shipmentDate)" />
      </van-cell-group>

      <van-divider>客户信息</van-divider>
      <van-card>
        <template #desc>
          <div class="customer-info">
            <div>{{ currentOrder.customerName }}</div>
            <div>{{ currentOrder.customerPhone }}</div>
            <div>{{ currentOrder.customerAddress }}</div>
          </div>
        </template>
      </van-card>

      <van-divider>商品清单</van-divider>
      <van-card
          v-for="product in currentOrder.products"
          :key="product.productId"
          :title="product.productName"
          :num="product.quantity"
          :price="product.subtotal"
      >
        <template #desc>
          单价：¥{{ product.price.toFixed(2) }}
          <van-tag v-if="product.itemDiscount" type="success">
            优惠：¥{{ product.itemDiscount }}
          </van-tag>
        </template>
      </van-card>

      <van-divider>金额汇总</van-divider>
      <van-cell-group>
        <van-cell title="商品总额" :value="`¥${calculateOriginalTotal.toFixed(2)}`" />
        <van-cell v-if="currentOrder.discountType !== 0" :title="discountTypeLabel" :value="discountDetailDisplay" />
        <van-cell title="应付总额" :value="`¥${currentOrder.totalPrice.toFixed(2)}`" value-class="total-price" />
      </van-cell-group>
    </div>
  </van-popup>

  <!-- 修订订单 -->
  <van-popup
      v-model:show="showRevise"
      position="right"
      :style="{ width: '100%', height: '100%' }"
  >
    <van-nav-bar
        :title="`订单修订 v${reviseForm.version}`"
        left-text="返回"
        @click-left="showRevise = false"
    />

    <van-form>
      <van-cell-group>
        <van-cell title="原出货时间" :value="formatDate(reviseForm.originalShipmentDate)" />
        <van-cell title="新出货时间">
          <van-field
              readonly
              clickable
              name="newShipmentDate"
              :value="reviseForm.newShipmentDate"
              placeholder="点击修改时间"
              @click="showReviseDatePicker = true"
          />
        </van-cell>
      </van-cell-group>

      <van-divider>折扣设置</van-divider>
      <van-radio-group v-model="reviseForm.discountType">
        <van-cell title="无折扣">
          <van-radio :name="0" />
        </van-cell>
        <van-cell title="百分比折扣">
          <van-radio :name="1" />
          <van-field
              v-if="reviseForm.discountType === 1"
              v-model="reviseForm.discountRate"
              type="number"
              suffix="%"
              :rules="[{ validator: v => v >=0 && v <=100, message: '0-100之间' }]"
          />
        </van-cell>
        <van-cell title="固定金额">
          <van-radio :name="2" />
          <van-field
              v-if="reviseForm.discountType === 2"
              v-model="reviseForm.discountAmount"
              type="number"
              prefix="¥"
          />
        </van-cell>
      </van-radio-group>

      <van-divider>商品调整</van-divider>
      <van-card
          v-for="(product, index) in reviseForm.products"
          :key="index"
          :title="product.productName"
          :desc="`库存: ${product.currentStock}`"
      >
        <template #price>
          <div class="product-controls">
            <van-stepper
                v-model="product.quantity"
                :min="0"
                :max="product.currentStock + product.originalQuantity"
                :disabled="product.isDeleted"
            />
            <van-field
                v-model="product.itemDiscount"
                label="单品折扣"
                type="number"
                :disabled="product.isDeleted"
            />
          </div>
        </template>
      </van-card>

      <van-field
          v-model="reviseForm.revisionRemark"
          label="修订备注"
          type="textarea"
          rows="2"
          maxlength="200"
          show-word-limit
      />

      <div class="form-actions">
        <van-button block type="primary" @click="submitRevision">提交修订</van-button>
      </div>
    </van-form>
  </van-popup>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { Dialog, Toast  } from 'vant'
import request from '@/utils/request'
import dayjs from 'dayjs'

// 数据状态
const queryParams = reactive({
  orderNo: '',
  customerId: null,
  dateRange: [],
  pageNum: 1,
  pageSize: 10
})

const form = reactive({
  customerId: null,
  customerName: '',
  customerPhone: '',
  customerAddress: '',
  products: [],
  totalPrice: 0,
  shipmentDate: '',
  remark: ''
})

const currentOrder = ref({})
const reviseForm = reactive({
  originalOrderId: null,
  version: 0,
  newShipmentDate: '',
  discountType: 0,
  discountRate: 0,
  discountAmount: 0,
  products: [],
  revisionRemark: ''
})

// UI状态
const showCustomerSelect = ref(false)
const showProductSelect = ref(false)
const showDateSelect = ref(false)
const showReviseDatePicker = ref(false)
const activeCollapse = ref(['basic'])
const selectedRows = ref({})

// 基础数据
const customerOptions = ref([])
const productOptions = ref([])
const filteredProducts = ref([])
const productSearch = ref('')

// 计算属性
const selectedCustomer = computed(() => {
  return customerOptions.value.find(c => c.value === queryParams.customerId)?.text || ''
})

const dateRangeText = computed(() => {
  if (queryParams.dateRange.length === 2) {
    return `${formatDate(queryParams.dateRange[0])} 至 ${formatDate(queryParams.dateRange[1])}`
  }
  return ''
})

const calculateOriginalTotal = computed(() => {
  return currentOrder.value.products?.reduce((sum, p) => sum + p.quantity * p.price, 0) || 0
})

const discountTypeLabel = computed(() => {
  return reviseForm.discountType === 1 ? '折扣率' : '优惠金额'
})

const discountDetailDisplay = computed(() => {
  return reviseForm.discountType === 1
      ? `${reviseForm.discountRate}%`
      : `¥${reviseForm.discountAmount}`
})

// 方法
const loadBaseData = async () => {
  const [customers, products] = await Promise.all([
    request.get('/customer/list'),
    request.get('/product/list')
  ])

  customerOptions.value = customers.data.map(c => ({
    value: c.customerId,
    text: c.customerName
  }))

  productOptions.value = products.data.map(p => ({
    ...p,
    text: p.productName,
    value: p.productId
  }))

  filteredProducts.value = [...productOptions.value]
}

const loadOrders = async () => {
  const params = {
    ...queryParams,
    beginShipmentDate: queryParams.dateRange[0],
    endShipmentDate: queryParams.dateRange[1]
  }

  const res = await request.get('/order/page', { params })
  return res.data
}

const showOrderDetail = async (order) => {
  const res = await request.get(`/order/detail/${order.orderId}`)
  currentOrder.value = res.data
  showDetail.value = true
}

const submitRevision = async () => {
  try {
    const payload = {
      ...reviseForm,
      products: reviseForm.products.map(p => ({
        productId: p.productId,
        quantity: p.quantity,
        itemDiscount: p.itemDiscount
      }))
    }

    await request.post('/order/revise', payload)
    Toast.success('修订成功')
    showRevise.value = false
    loadOrders()
  } catch (error) {
    Toast.fail('修订失败')
  }
}

// 表单验证与提交
const validateStock = (products) => {
  return products.every(p =>
      p.quantity <= p.stock &&
      (!p.isDeleted || p.quantity === p.originalQuantity)
  )
}

const submitForm = async () => {
  if (!form.products.length) {
    Toast.fail('请选择商品')
    return
  }

  const payload = {
    ...form,
    products: form.products.map(p => ({
      productId: p.productId,
      quantity: p.quantity,
      salePrice: p.salePrice
    }))
  }

  try {
    await request.post('/order/add', payload)
    Toast.success('创建成功')
    showCreate.value = false
    loadOrders()
  } catch (error) {
    Toast.fail('创建失败')
  }
}

// 工具方法
const formatDate = (date) => {
  return dayjs(date).format('YYYY-MM-DD HH:mm')
}

// 生命周期
onMounted(async () => {
  await loadBaseData()
  loadOrders()
})
</script>

<style scoped>
.product-item {
  margin: 10px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.price-controls {
  display: flex;
  align-items: center;
  gap: 10px;
}

.total-price {
  padding: 15px;
  font-size: 16px;
  color: #ee0a24;
  text-align: right;
}

.detail-content {
  padding: 10px;
}

.customer-info div {
  color: #969799;
  font-size: 12px;
  margin: 4px 0;
}

.form-actions {
  position: sticky;
  bottom: 0;
  padding: 10px;
  background: white;
}

.product-controls {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
  align-items: center;
}

.van-checkbox-group {
  width: 100%;
}

.van-picker__columns {
  height: 300px;
}
</style>