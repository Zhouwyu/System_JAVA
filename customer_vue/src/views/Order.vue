<template>
  <div>
    <!-- 查询区域 -->
    <div class="card">
      <el-collapse v-model="data.activeCollapse">
        <el-collapse-item name="basic">
          <template #title>
            <span style="font-size: 14px; padding-left: 10px">订单查询</span>
          </template>
          <el-row :gutter="10" align="middle">
            <el-col :span="6">
              <el-input
                  v-model="data.queryParams.orderNo"
                  placeholder="订单编号"
                  clearable
                  prefix-icon="Search"
              />
            </el-col>
            <el-col :span="6">
              <el-select
                  v-model="data.queryParams.customerId"
                  placeholder="选择客户"
                  filterable
                  clearable
              >
                <el-option
                    v-for="item in data.customerOptions"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                />
              </el-select>
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
            <!--              <el-select-->
            <!--                  v-model="data.queryParams.productId"-->
            <!--                  placeholder="选择商品"-->
            <!--                  filterable-->
            <!--                  clearable-->
            <!--              >-->
            <!--                <el-option-->
            <!--                    v-for="item in data.productOptions"-->
            <!--                    :key="item.value"-->
            <!--                    :label="item.label"-->
            <!--                    :value="item.value"-->
            <!--                />-->
            <!--              </el-select>-->
            <!--            </el-col>-->
            <el-col :span="6">
              <el-date-picker
                  v-model="data.queryParams.dateRange"
                  type="daterange"
                  range-separator="-"
                  start-placeholder="出货开始日期"
                  end-placeholder="出货结束日期"
                  style="width: 100%"
              />
            </el-col>
          </el-row>
        </el-collapse-item>
      </el-collapse>
    </div>

    <!-- 操作按钮 -->
    <div class="card" style="margin-top: 5px">
      <el-button type="primary" @click="handleAdd">新建订单</el-button>
      <el-button type="warning" @click="handleBatchDelete">批量删除</el-button>
    </div>

    <!-- 新增抽屉 -->
    <el-drawer v-model="drawerVisible" direction="rtl" size="50%" :before-close="handleBeforeClose"
               @close="resetForm">
      <template #header>
        <h4>新建订单信息</h4>
      </template>

      <template #default>
        <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
          <el-form-item label="选择客户" prop="customerId">
            <el-select
                v-model="form.customerId"
                placeholder="请选择客户"
                filterable
                :filter-method="filterCustomers"
                @change="handleCustomerChange"
            >
              <el-option
                  v-for="c in filteredCustomers"
                  :key="c.value"
                  :label="c.label"
                  :value="c.value"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="客户信息" prop="customerInfo">
            <el-descriptions :column="3" border>
              <el-descriptions-item label="联系电话">
                {{ form.customerPhone || '请先选择客户' }}
              </el-descriptions-item>
              <el-descriptions-item label="联系地址">
                {{ form.customerAddress || '-' }}
              </el-descriptions-item>
              <el-descriptions-item label="业务行业">
                {{ form.customerBusinessIndustry || '-' }}
              </el-descriptions-item>
            </el-descriptions>
          </el-form-item>
          <el-form-item label="商品清单" prop="products">
            <div class="product-selector">
              <el-select
                  v-model="form.selectedProducts"
                  multiple
                  value-key="productId"
                  placeholder="选择商品"
                  filterable
                  :filter-method="filterProducts"
                  @change="handleProductSelect"
              >
                <el-option
                    v-for="p in filteredProducts"
                    :key="p.productId"
                    :value="p"
                    :disabled="p.stock <= 0"
                >
                  <span style="float: left">{{ p.productName }}</span>
                  <span style="float: right; color: #8492a6; font-size: 13px">
            ¥{{ p.price }} (库存: {{ p.stock }})
        </span>
                </el-option>
              </el-select>

              <!-- 商品数量及明细 -->
              <div v-for="(item, index) in form.products" :key="index" class="product-item">
                <div class="product-info">
                  <span>{{ item.productName }}</span>
                  <el-input-number
                      v-model="item.salePrice"
                      :min="0"
                      :precision="2"
                      :step="10"
                      @change="calculateTotal"
                  />
                  <span>单价: ¥{{ item.price }}</span>
                  <span>成交价: ¥{{ item.salePrice }}</span>
                </div>
                <el-input-number
                    v-model="item.quantity"
                    :min="1"
                    :max="item.stock"
                    @change="calculateTotal"
                />
                <el-button
                    type="danger"
                    icon="Delete"
                    circle
                    @click="removeProduct(index)"
                />
              </div>
            </div>
          </el-form-item>

          <el-row :gutter="20">
            <el-col :span="8">
              <el-form-item label="总金额" prop="totalPrice">
                <el-input-number
                    v-model="form.totalPrice"
                    :min="0"
                    :precision="2"
                    :disabled="true"
                />
              </el-form-item>
            </el-col>
          </el-row>

          <el-form-item label="出货时间" prop="shipmentDate">
            <el-date-picker
                v-model="form.shipmentDate"
                type="datetime"
                placeholder="选择出货时间"
                value-format="YYYY-MM-DD HH:mm:ss"
                style="width: 100%"
                :disabled-date="disabledDate"
                @change="handleDateChange"
            />
          </el-form-item>

          <!-- 新增备注字段 -->
          <el-form-item label="订单备注" prop="remark">
            <el-input
                v-model="form.remark"
                type="textarea"
                :rows="2"
                placeholder="请输入备注信息"
                maxlength="100"
                show-word-limit
            />
          </el-form-item>
        </el-form>
      </template>

      <template #footer>
        <div style="flex: auto">
          <el-button @click="drawerVisible = false">取消</el-button>
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
        <el-table-column label="订单编号" prop="orderNum"/>
        <el-table-column label="订单名称" prop="orderName"/>
        <el-table-column label="下单时间" prop="createTime" :formatter="(row) => formatDate(row.createTime)"/>
        <el-table-column label="出货时间" prop="shipmentDate" :formatter="(row) => formatDate(row.shipmentDate)"/>
        <el-table-column label="修改时间" prop="updateTime"
                         :formatter="(row) => formatDate(row.updateTime)"></el-table-column>
        <el-table-column label="订单状态" prop="status" width="120">
          <template #default="{row}">
            <el-tag :type="statusTagType[row.status]" effect="light">
              {{ orderStatusMap[row.status] }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="订单明细" prop="orderDetail" width="100">
          <template #default="scope">
            <el-button
                type="primary"
                size="small"
                @click="showOrderDetail(scope.row)"
            >
              查看详情
            </el-button>
            <!-- 订单详情对话框 -->
            <el-dialog
                v-model="detailDialogVisible"
                title="订单详细信息"
                width="60%"
                top="5vh"
                modal-append-to-body
                append-to-body
            >
              <!-- 新增导出按钮 -->
              <template #header>
                <div class="dialog-header">
                  <span>订单详细信息</span>
                  <el-button
                      type="primary"
                      size="small"
                      @click="exportToPDF"
                  >
                    导出PDF
                  </el-button>
                </div>
              </template>

              <!-- 内容容器添加ref -->
              <div ref="pdfContent" class="pdf-content">
                <el-descriptions :column="2" border>
                  <!-- 客户信息 -->
                  <el-descriptions-item label="客户名称">
                    {{ currentOrder.customer?.customerName || '-' }}
                  </el-descriptions-item>
                  <el-descriptions-item label="联系电话">
                    {{ currentOrder.customer?.phoneNum || '-' }}
                  </el-descriptions-item>
                  <el-descriptions-item label="联系地址" :span="2">
                    {{ currentOrder.customer?.address || '-' }}
                  </el-descriptions-item>

                  <!-- 订单基础信息 -->
                  <el-descriptions-item label="订单编号">
                    {{ currentOrder.orderNo }}
                  </el-descriptions-item>
                  <el-descriptions-item label="下单时间">
                    {{ dayjs(currentOrder.createTime).format('YYYY-MM-DD HH:mm:ss') }}
                  </el-descriptions-item>
                  <el-descriptions-item label="出货时间">
                    {{ dayjs(currentOrder.shipmentDate).format('YYYY-MM-DD HH:mm:ss') }}
                  </el-descriptions-item>
                  <el-descriptions-item label="订单状态">
                    <el-tag :type="statusTagType[currentOrder.status]">
                      {{ orderStatusMap[currentOrder.status] }}
                    </el-tag>
                  </el-descriptions-item>

                  <!-- 商品信息 -->
                  <!-- 替换原有商品信息展示部分 -->
                  <el-descriptions-item label="商品清单" :span="2">
                    <el-table :data="currentOrder.products" border size="small">
                      <el-table-column label="商品名称" prop="productName" width="180"/>
                      <el-table-column label="成交价" width="120">
                        <template #default="{row}">
                          ¥{{ row.salePrice.toFixed(2) }}
                        </template>
                      </el-table-column>
                      <el-table-column label="数量" prop="quantity" width="100"/>
                      <el-table-column label="小计" width="120">
                        <template #default="{row}">
                          <span style="color:#67C23A">¥{{ row.subtotal }}</span>
                        </template>
                      </el-table-column>
                      <el-table-column label="折扣信息" width="120">
                        <template #default="{row}">
                          <el-tag
                              v-if="row.itemDiscount > 0"
                              type="success"
                              size="small"
                              class="discount-tag"
                          >
                            -¥{{ (row.itemDiscount*row.quantity).toFixed(2) }}
                          </el-tag>
                          <span v-else>-</span>
                        </template>
                      </el-table-column>
                    </el-table>
                  </el-descriptions-item>

                  <el-descriptions-item label="总金额" :span="2">
                    <div class="total-price-container">
                      <!-- 原始金额 -->
                      <div class="price-line original">
                        <span class="label">商品总额：</span>
                        <span class="value">¥{{ calculateOriginalTotal.toFixed(2) }}</span>
                      </div>

                      <!-- 全局折扣信息 -->
                      <div v-if="currentOrder.discountType !== 0" class="price-line discount">
      <span class="label">
        {{ discountTypeLabel }}：
      </span>
                        <span class="value">
        <el-tag type="info" size="small" effect="light">
          {{ discountDetailDisplay }}
        </el-tag>
      </span>
                      </div>

                      <!-- 最终金额 -->
                      <div class="price-line final">
                        <span class="label">应付总额：</span>
                        <span class="value">¥{{ Math.max(currentOrder.totalPrice, 0).toFixed(2) }}</span>
                      </div>
                    </div>
                  </el-descriptions-item>

                  <!-- 其他信息 -->
                  <el-descriptions-item label="订单备注" :span="2">
                    {{ currentOrder.remark || '无备注信息' }}
                  </el-descriptions-item>
                  <el-descriptions-item label="操作人员">
                    {{ currentOrder.operator.name }}
                  </el-descriptions-item>
                  <el-descriptions-item label="最后修改时间">
                    {{ formatDate(currentOrder.updateTime) }}
                  </el-descriptions-item>
                </el-descriptions>
              </div>

              <template #footer>
                <el-button type="primary" @click="detailDialogVisible = false">
                  关闭
                </el-button>
              </template>
            </el-dialog>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180">
          <template #default="scope">
            <div class="action-buttons">
              <el-button
                  v-if="scope.row.status === 1"
                  type="warning"
                  size="small"
                  @click="handleRevise(scope.row)"
              >
                修订订单
              </el-button>

              <el-dialog v-model="reviseDialogVisible" title="订单修订" width="65%" append-to-body
                         modal-append-to-body>
                <!-- 增加版本提示 -->
                <el-alert type="warning" show-icon :closable="false">
                  当前修改版本：{{ reviseForm.version }} | 最后修改时间：{{ formatDate(reviseForm.updateTime) }}
                </el-alert>
                <div class="revision-meta">
                  <el-form-item label="出货时间" prop="newShipmentDate">
                    <el-date-picker
                        v-model="reviseForm.newShipmentDate"
                        type="datetime"
                        placeholder="选择修订后的出货时间"
                        value-format="YYYY-MM-DD HH:mm:ss"
                        :disabled-date="disabledRevisionDate"
                        :shortcuts="dateShortcuts"
                    />
                  </el-form-item>

                  <el-form-item label="修订备注" prop="revisionRemark">
                    <el-input
                        v-model="reviseForm.revisionRemark"
                        type="textarea"
                        :rows="2"
                        placeholder="请输入修订备注信息（如修改原因等）"
                        maxlength="200"
                        show-word-limit
                        resize="none"
                    />
                  </el-form-item>
                </div>
                <el-form :model="reviseForm">
                  <el-input type="hidden" v-model="reviseForm.version"/>
                  <!-- 折扣类型选择 -->
                  <el-form-item label="折扣类型">
                    <el-radio-group v-model="reviseForm.discountType">
                      <el-radio :label="0">无折扣</el-radio>
                      <el-radio :label="1">百分比</el-radio>
                      <el-radio :label="2">固定金额</el-radio>
                    </el-radio-group>
                  </el-form-item>

                  <!-- 折扣详情 -->
                  <el-form-item v-if="reviseForm.discountType === 1" label="折扣率">
                    <el-input-number
                        v-model="reviseForm.discountRate"
                        :min="0"
                        :max="100"
                        :precision="0"
                        suffix="%"
                    />
                  </el-form-item>

                  <el-form-item v-if="reviseForm.discountType === 2" label="优惠金额">
                    <el-input-number
                        v-model="reviseForm.discountAmount"
                        :min="0"
                        :precision="2"
                    ></el-input-number>
                  </el-form-item>

                  <!-- 商品级折扣 -->
                  <el-table :data="reviseForm.products">
                    <el-table-column label="商品名称" prop="productName"/>
                    <el-table-column label="成交价" prop="salePrice"/>
                    <el-table-column label="数量">
                      <template #default="{row}">
                        <el-input-number
                            v-model="row.quantity"
                            :min="0"
                            :max="row.currentStock + row.originalQuantity"
                        />
                      </template>
                    </el-table-column>
                    <el-table-column label="单品折扣">
                      <template #default="{row}">
                        <el-input-number
                            v-model="row.itemDiscount"
                            :min="0"
                            :max="row.salePrice"
                            :precision="2"
                        />
                      </template>
                    </el-table-column>
                    <!-- 新增小计列 -->
                    <el-table-column label="小计" width="120">
                      <template #default="{row}">
                        ¥{{ ((row.salePrice - row.itemDiscount) * row.quantity).toFixed(2) }}
                      </template>
                    </el-table-column>
                  </el-table>
                  <!-- 在表格下方添加金额汇总 -->
                  <div class="price-summary">
                    <el-row :gutter="20">
                      <el-col :span="12">
                        <div class="total-line">
                          <span class="label">商品合计：</span>
                          <span class="value">¥{{ calculateSubtotal.toFixed(2) }}</span>
                        </div>
                        <div v-if="reviseForm.discountType !== 0" class="total-line">
                          <span class="label">折扣金额：</span>
                          <span class="value discount">
          {{ discountDisplay }}
        </span>
                        </div>
                        <div class="total-line grand-total">
                          <span class="label">应付总额：</span>
                          <span class="value">¥{{ calculateGrandTotal.toFixed(2) }}</span>
                        </div>
                      </el-col>
                    </el-row>
                  </div>
                </el-form>

                <template #footer>
                  <el-button @click="reviseDialogVisible = false">取消</el-button>
                  <el-button type="primary" @click="submitRevision">提交修订</el-button>
                </template>
                <!-- 在金额汇总区域后添加分隔线 -->
                <el-divider />
                <!-- 增加历史记录查看 -->
                <div class="history-link" v-if="false">
                  <el-link type="primary" @click="showRevisionHistory">
                    <el-icon><Clock /></el-icon>
                    查看修订历史
                  </el-link>
                </div>
                <el-dialog
                    v-model="historyDialogVisible"
                    title="修订历史记录"
                    width="70%">
                  <el-timeline>
                    <el-timeline-item
                        v-for="rev in revisionHistory"
                        :key="rev.revisionId"
                        :timestamp="formatDate(rev.createTime)"
                        placement="top">
                      <el-card>
                        <h4>版本 {{ rev.revisionNumber }} - {{ rev.operator }}</h4>
                        <div v-if="rev.revisionRemark" class="revision-remark">
                          备注：{{ rev.revisionRemark }}
                        </div>
                        <el-collapse>
                          <el-collapse-item title="订单快照">
                            <order-snapshot-view :data="rev.snapshot"/>
                          </el-collapse-item>
                        </el-collapse>
                      </el-card>
                    </el-timeline-item>
                  </el-timeline>
                </el-dialog>
              </el-dialog>
              <el-button
                  v-if="scope.row.status === 1"
                  size="small"
                  type="success"
                  @click="handleShip(scope.row)"
              >
                标记出货
              </el-button>

              <el-button
                  type="danger"
                  size="small"
                  @click="handleDelete(scope.row.orderId)"
              >
                删除
              </el-button>
            </div>
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
import {reactive, ref, onMounted, nextTick, watch, computed} from 'vue'
import {dayjs, ElMessage, ElMessageBox} from 'element-plus'
import request from '@/utils/request'
import jsPDF from "jspdf";
import html2canvas from "html2canvas";

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
  activeCollapse: ['basic']
})

// 改造后的form结构
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

const rules = {
  customerId: [{required: true, message: '请选择客户', trigger: 'change'}],
  productId: [{required: true, message: '请选择商品', trigger: 'change'}],
  products_num: [
    {required: true, message: '请输入数量', trigger: 'blur'},
    {type: 'number', min: 1, message: '数量至少为1', trigger: 'change'}
  ],
  products: [
    {
      validator: (_, value, callback) => {
        if (value.length === 0) {
          callback(new Error('至少选择一个商品'))
        } else {
          callback()
        }
      },
      trigger: 'change'
    }
  ],
  shipmentDate: [
    {
      required: true,
      message: '请选择出货时间',
      trigger: 'change'
    },
    {
      validator: (_, value, callback) => {
        if (!value) return callback()

        const selected = new Date(value)
        const now = new Date()

        // 允许1分钟缓冲时间
        if (selected.getTime() + 60000 < now.getTime()) {
          callback(new Error('必须选择当前时间之后的时间'))
        } else {
          callback()
        }
      },
      trigger: ['change', 'blur']
    }
  ]
}

// 新增日期禁用函数
const disabledDate = (date) => {
  // 禁用今天之前的日期（以当前时间计算）
  const now = new Date()
  now.setHours(0, 0, 0, 0) // 当天0点
  return date.getTime() < now.getTime()
}

// 新增时间校验处理
const handleDateChange = (value) => {
  if (!value) return

  const selected = new Date(value)
  const now = new Date()

  // 精确到秒的时间校验
  if (selected.getTime() < now.getTime()) {
    ElMessage.warning('不能选择当前时间之前的日期时间')
    form.shipmentDate = '' // 清空非法选择
  }
}

// 计算小计总和
const calculateSubtotal = computed(() => {
  return reviseForm.value.products.reduce((sum, item) => {
    return sum + (item.salePrice - item.itemDiscount) * item.quantity
  }, 0)
})

// 计算折扣显示
const discountDisplay = computed(() => {
  if (reviseForm.value.discountType === 1) {
    return `${reviseForm.value.discountRate}% OFF`
  }
  if (reviseForm.value.discountType === 2) {
    return `-¥${reviseForm.value.discountAmount}`
  }
  return '-'
})

// 计算最终金额
const calculateGrandTotal = computed(() => {
  let total = calculateSubtotal.value
  if (reviseForm.value.discountType === 1) {
    total *= (1 - reviseForm.value.discountRate / 100)
  } else if (reviseForm.value.discountType === 2) {
    total -= reviseForm.value.discountAmount
  }
  return Math.max(total, 0) // 防止负数
})


// 抽屉控制
const drawerVisible = ref(false)
const formRef = ref()
const initialFormState = ref(null) // 保存表单初始状态

// 用于实现商品模糊搜索
const searchQuery = ref('')
const filteredProducts = ref([])
// 用于实现客户模糊搜索
const filteredCustomers = ref([])

// 商品过滤方法
const filterProducts = (query) => {
  searchQuery.value = query
  if (!query) {
    filteredProducts.value = data.productOptions
    return
  }
  filteredProducts.value = data.productOptions.filter(item => {
    return item.productName.toLowerCase().includes(query.toLowerCase())
  })
}

// 监听商品选项变化（当基础数据更新时同步）
watch(() => data.productOptions, (newVal) => {
  filteredProducts.value = [...newVal]
}, {deep: true})

// 客户过滤方法
const filterCustomers = (query) => {
  if (!query) {
    filteredCustomers.value = data.customerOptions
    return
  }
  filteredCustomers.value = data.customerOptions.filter(item => {
    return item.label.toLowerCase().includes(query.toLowerCase())
  })
}

// 监听客户数据变化
watch(() => data.customerOptions, (newVal) => {
  filteredCustomers.value = [...newVal]
}, {deep: true})

// 新增/编辑操作
const handleAdd = () => {
  resetForm()
  drawerVisible.value = true
  // 记录初始状态
  initialFormState.value = JSON.parse(JSON.stringify(form))
}

// 新增关闭前确认方法
const handleBeforeClose = (done) => {
  const isChanged = JSON.stringify(form) !== JSON.stringify(initialFormState.value)

  if (isChanged) {
    ElMessageBox.confirm('有未保存的修改，确认关闭吗？', '警告', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      done()
    }).catch(() => {
      // 阻止关闭
    })
  } else {
    done()
  }
}

// 重置表单内容
const resetForm = () => {
  formRef.value?.resetFields()
  Object.assign(form, {
    customerId: null,
    customerPhone: '',
    customerAddress: '',
    customerBusinessIndustry: '',
    selectedProducts: [],
    products: [],
    totalPrice: 0,
    remark: '',
    shipmentDate: ''
  })
}

// 库存最大数量（根据选中商品动态获取）
const maxStock = ref(999)

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
      // 补充完整商品信息
      ...p
    }))
  } catch (error) {
    ElMessage.error('基础数据加载失败')
  }
}

// 计算总金额
const calculateTotal = () => {
  form.totalPrice = form.products.reduce((sum, item) =>
      sum + (item.salePrice * item.quantity), 0
  )
}

// 商品选择变化时
// 商品选择处理
const handleProductSelect = (selectedProducts) => {
  // 创建商品映射表用于快速查找
  const productMap = new Map(
      form.products.map(p => [p.productId, p])
  )

  // 合并商品数据
  const mergedProducts = selectedProducts.map(sp => {
    return productMap.get(sp.productId) || {
      ...sp,
      salePrice: sp.price, // 默认使用原价
      quantity: 1
    }
  })

  // 响应式更新（关键！）
  form.products = mergedProducts
  form.selectedProducts = [...selectedProducts] // 保持引用一致
  calculateTotal()
}

// 移除商品
const removeProduct = (index) => {
  const removedProduct = form.products[index]
  // 同步更新selectedProducts
  form.selectedProducts = form.selectedProducts.filter(
      p => p.productId !== removedProduct.productId
  )
  form.products.splice(index, 1)
  calculateTotal()
}

// 客户选择变化时获取详情
const handleCustomerChange = async (customerId) => {
  try {
    const res = await request.get(`/customer/${customerId}`)
    form.customerPhone = res.data.phoneNum
    form.customerAddress = res.data.address
    form.customerBusinessIndustry = res.data.businessIndustry
  } catch (error) {
    ElMessage.error('获取客户信息失败')
  }
}

// 库存预检查
const stockPreCheck = async (isRevision=false) => {
  // 提取需要检查的商品数据
  const checkItems = form.products.map(item => ({
    productId: item.productId,
    quantity: item.quantity,
    isRevision // 标记是否为修订操作
  }))
  try {
    await request.post('/order/precheck', checkItems)
  } catch (error) {
    // 明确抛出库存不足信息
    throw new Error(`库存检查失败: ${error.response?.data?.message || '未知错误'}`)
  }
}

// 提交逻辑改造
const submitForm = async () => {
  try {
    // 1. 表单基础验证
    await formRef.value.validate()
    // 2. 执行库存预检查
    await stockPreCheck()
    // 3. 组装请求数据
    const payload = {
      customerId: form.customerId,
      productsDtos: form.products.map(item => ({
        productId: item.productId,
        productName: item.productName,
        quantity: item.quantity,
        price: item.price,
        salePrice: item.salePrice,
        stock: item.stock,
        orderNo: "",
      })),
      totalPrice: form.totalPrice,
      remark: form.remark,
      shipmentDate: form.shipmentDate,
      operator: localStorage.getItem("username")
    }
    // 4. 提交请求
    const url = '/order/add'
    const res = await request.post(url, payload)
    // 5. 处理成功逻辑
    if (res.code === '200') {
      ElMessage.success('新增成功')
      // 提交成功后重置初始状态
      initialFormState.value = null
      drawerVisible.value = false
      data.queryParams.pageNum = 1 // 新增数据后回到第一页
      loadBaseData()
      load()
    }
  } catch (error) {
    // 统一错误处理（包含表单验证、预检查、提交的错误）
    const msg = error.response?.data?.message ||
        (error.message.includes("validation failed") ? "表单校验失败" : "操作失败")
    ElMessage.error(msg)
  }
}

// 查询条件重置
const reset = () => {
  data.queryParams = {
    ...data.queryParams,  // 保留分页参数
    orderNo: '',
    customerId: null,
    productId: null,
    dateRange: [],
  }
  load()
}

// 加载订单数据
const load = async () => {
  try {
    const params = {
      ...data.queryParams,
    }
    // 转换日期范围参数
    if (params.dateRange && params.dateRange.length === 2) {
      params.beginShipmentDate = dayjs(params.dateRange[0]).format('YYYY-MM-DD');
      params.endShipmentDate = dayjs(params.dateRange[1]).format('YYYY-MM-DD');
    }
    delete params.dateRange; // 移除原始参数
    const res = await request.get('/order/page', {params})
    data.tableData = res.data.records
    data.total = res.data.total
    data.status = res.data.status
  } catch (error) {
    ElMessage.error('数据加载失败')
  }
}

// 其他逻辑（提交表单、批量删除等）参考商品管理代码结构
// 需根据订单接口调整字段映射和请求参数

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
      const ids = selectedRows.value.map(item => item.orderId)

      const response = await request.post('/order/delete/batch', {ids})

      if (response.code === '200') {
        ElMessage.success(`成功删除 ${response.data} 条记录`)
        load() // 刷新表格
        loadBaseData()
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
  ElMessageBox.confirm('确定删除该订单记录？', '警告', {
    confirmButtonText: '确认',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    // 复用批量删除接口
    await request.post('/order/delete/batch', {ids: [id]})
    ElMessage.success('删除成功')
    load()
    loadBaseData()
  })
}

// 更新出货状态
const handleShip = async (row) => {
  try {
    await request.get(`/order/ship/${row.orderId}`)
    ElMessage.success('订单状态已更新')
    load() // 刷新数据
  } catch (error) {
    ElMessage.error('状态更新失败: ' + (error.response?.data?.message || error.message))
  }
}

// 在setup()中添加响应式数据和状态映射
const detailDialogVisible = ref(false)

// 修订相关状态
const reviseDialogVisible = ref(false)

const historyDialogVisible = ref(false)
const revisionHistory = ref([])

// 查看修订历史
const showRevisionHistory = async () => {
  try {
    const res = await request.get(`/order/${reviseForm.value.originalOrderId}/revisions`)
    revisionHistory.value = res.data.map(item => ({
      ...item,
      snapshot: JSON.parse(item.revisionData)
    }))
    historyDialogVisible.value = true
  } catch (error) {
    ElMessage.error('获取修订历史失败')
  }
}

const reviseForm = ref({
  originalOrderId: null,
  originalOrderNo: '',
  discountType: 0,
  discountRate: 0,
  discountAmount: 0,
  version: null,
  updateTime: '',
  newShipmentDate: '',          // 新增出货时间
  revisionRemark: '',           // 新增修订备注
  originalShipmentDate: '',     // 记录原始出货时间
  products: []
})

// 初始化包含完整结构的响应式对象
// 在setup()中初始化currentOrder
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
      itemDiscount: 0 // 单品折扣
    }
  ],

  // 做修改订单用
  discountType: 0,       // 折扣类型 0-无 1-百分比 2-固定金额
  discountRate: 0,       // 折扣率（百分比）
  discountAmount: 0,     // 优惠金额
  originalOrderId: null, // 原始订单ID（用于关联修订）
  isRevised: false       // 是否是修订订单
})

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

// 计算属性
const calculateOriginalTotal = computed(() => {
  return currentOrder.value.products?.reduce((sum, item) =>
      sum + (item.salePrice * item.quantity), 0) || 0
})

const discountTypeLabel = computed(() => {
  const typeMap = {
    1: '折扣率',
    2: '优惠金额'
  }
  return typeMap[currentOrder.value.discountType] || '优惠'
})

const discountDetailDisplay = computed(() => {
  const order = currentOrder.value
  if (order.discountType === 1) {
    return `${order.discountRate}% OFF`
  }
  if (order.discountType === 2) {
    return `-¥${order.discountAmount.toFixed(2)}`
  }
  return '无优惠'
})

// 显示订单详情
const showOrderDetail = async (row) => {
  try {
    // 请求订单详情数据（根据实际接口调整）
    const res = await request.get(`/order/detail/${row.orderId}`)
    // 处理商品数据格式
    currentOrder.value = {
      ...res.data,
      products: res.data.products.map(p => ({
        ...p,
        subtotal: (p.salePrice * p.quantity).toFixed(2)
      })),
    }
    detailDialogVisible.value = true
  } catch (error) {
    ElMessage.error('获取订单详情失败')
  }
}

// 打开修订对话框
const handleRevise = async (order) => {
  try {
    reviseDialogVisible.value = true
    // 先获取完整订单数据
    const { data } = await request.get(`/order/detail/${order.orderId}`)
    // 填充修订表单
    reviseForm.value = {
      version: data.version,
      updateTime: data.updateTime,
      originalOrderId: data.orderId, // 记录原始订单ID
      originalOrderNo: data.orderNo, // 原始订单编号
      newShipmentDate: data.shipmentDate,
      originalShipmentDate: data.shipmentDate,
      revisionRemark: data.remark || '',
      discountType: data.discountType, // 折扣类型, 0为无折扣，1为百分比，2为固定金额
      discountRate: data.discountRate, // 百分比折扣
      discountAmount: data.discountAmount, // 固定金额折扣
      // 对应的产品明细
      products: data.products.map(p => ({
        ...p,
        originalQuantity: p.quantity, // 当前单品选的数量
        itemDiscount: p.itemDiscount, // 单品折扣
        currentStock: p.stockQuantity, // 当前单品库存
      }))
    }
  } catch (error) {
    ElMessage.error('获取订单详情失败')
    reviseDialogVisible.value = false
  }
}

// 提交修订
const submitRevision = async () => {
  try {
    // 计算新总金额
    const newTotal = reviseForm.value.products.reduce((total, item) => {
      const itemTotal = (item.salePrice - item.itemDiscount) * item.quantity
      return total + itemTotal
    }, 0)

    // 应用全局折扣，1为打折，2为扣减一定金额
    let finalTotal = newTotal
    if(reviseForm.value.discountType === 1) {
      finalTotal *= (1 - reviseForm.value.discountRate/100)
    } else if(reviseForm.value.discountType === 2) {
      finalTotal -= reviseForm.value.discountAmount
    }

    const res = await request.post('/order/revise', {
      originalOrderId: reviseForm.value.originalOrderId,
      originalOrderNo: reviseForm.value.originalOrderNo,
      version: reviseForm.value.version,
      productsRevise: reviseForm.value.products,
      newShipmentDate: reviseForm.value.newShipmentDate,
      revisionRemark: reviseForm.value.revisionRemark,
      // 添加时间变更校验
      isShipmentChanged: reviseForm.value.newShipmentDate !== reviseForm.value.originalShipmentDate,
      totalPrice: finalTotal,
      operator: localStorage.getItem('username'),
      discountType: reviseForm.value.discountType,
      discountAmount: reviseForm.value.discountAmount,
      discountRate: reviseForm.value.discountRate
    })
    if (res.code === '200') {
      ElMessage.success('订单修订成功')
      reviseDialogVisible.value = false
    }
    load()
  } catch (error) {
    ElMessage.error('修订失败：' + error.message)
  }
}

// 新增日期禁用规则
const disabledRevisionDate = (date) => {
  return date < new Date(reviseForm.value.originalShipmentDate)
}
// 快捷选项
const dateShortcuts = [
  {
    text: '次日9:00',
    value: () => {
      const date = new Date()
      date.setDate(date.getDate() + 1)
      date.setHours(9, 0, 0)
      return date
    }
  },
  {
    text: '本周五截止',
    value: () => {
      const date = new Date()
      while(date.getDay() !== 5) {
        date.setDate(date.getDate() + 1)
      }
      return date
    }
  }
]

onMounted(() => {
  load()
  loadBaseData()
  filteredProducts.value = [...data.productOptions]
  filteredCustomers.value = [...data.customerOptions]
})

// 通用日期格式化方法
const formatDate = (dateString) => {
  return dateString ? dayjs(dateString).format('YYYY-MM-DD HH:mm:ss') : '-'
}

// 导出PDF方法
const pdfContent = ref(null)

const exportToPDF = async () => {
  try {
    // 1. 获取DOM元素
    const element = pdfContent.value

    // 2. 转换为Canvas
    const canvas = await html2canvas(element, {
      useCORS: true,    // 允许跨域图片
      scale: 2,         // 提高分辨率
      logging: true,     // 开启日志
      backgroundColor: '#ffffff' // 设置白色背景
    })

    // 3. 转换为图片数据
    const imgData = canvas.toDataURL('image/png', 1.0)

    // 4. 创建PDF
    const pdf = new jsPDF('p', 'mm', 'a4')
    const imgWidth = 210 // A4纸张宽度 210mm
    const imgHeight = (canvas.height * imgWidth) / canvas.width

    // 5. 添加图片到PDF
    pdf.addImage(imgData, 'PNG', 0, 0, imgWidth, imgHeight)

    // 6. 保存文件
    const fileName = `订单_${currentOrder.value.orderNo || '未知'}.pdf`
    pdf.save(fileName)

    ElMessage.success('导出成功')
  } catch (error) {
    ElMessage.error('导出失败: ' + error.message)
  }
}

</script>

<style scoped>
/* 保持与之前一致的样式 */

/* 添加打印优化样式 */
.pdf-content {
  padding: 20px;
  background: white;
}

/* 优化描述项间距 */
:deep(.el-descriptions__body .el-descriptions__table) {
  width: 100%;
}

/* 强制黑色文字 */
:deep(.el-descriptions__body),
:deep(.el-tag) {
  color: #000 !important;
}

/* 对话框头部布局 */
.dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.product-item {
  display: flex;
  align-items: center;
  margin: 10px 0;
  padding: 10px;
  border: 1px solid #ebeef5;
  border-radius: 4px;
}

.product-info {
  flex: 1;
  display: flex;
  justify-content: space-between;
  margin-right: 15px;
}

/* 添加商品表格样式 */
:deep(.el-table .cell) {
  padding: 4px 8px !important;
}

/* 金额数字对齐 */
:deep(.el-table__body .el-table__cell:nth-child(n+2)) {
  text-align: right;
}

/* 小计金额高亮 */
:deep(.el-table__row .subtotal-cell) {
  font-weight: 500;
  color: #67C23A;
}

/* 添加操作按钮样式 */
.action-buttons {
  display: flex;
  flex-wrap: wrap;
  gap: 5px;
  justify-content: center;
}

/* 优化标签样式 */
:deep(.el-tag) {
  font-weight: 500;
  letter-spacing: 0.5px;

  /* 不同状态的具体样式 */

  &--warning {
    background-color: #fdf6ec;
    border-color: #f5dab1;
  }

  &--success {
    background-color: #f0f9eb;
    border-color: #c2e7b0;
  }
}


/* 确保表格容器不会限制溢出 */
.el-table__body-wrapper {
  overflow: visible !important;
}

/* 调整表格行高度避免挤压 */
.el-table .cell {
  padding: 4px 0;
}

/* 对话框头部布局 */
.dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  padding-right: 10px;
}

.price-summary {
  margin-top: 20px;
  padding: 15px;
  background-color: #f8f9fa;
  border-radius: 4px;
}

.total-line {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  font-size: 14px;

  .label {
    color: #606266;
  }

  .value {
    color: #f56c6c;
    font-weight: 500;

    &.discount {
      color: #67c23a;
    }
  }
}

.grand-total {
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #ebeef5;

  .value {
    font-size: 16px;
  }
}

.revision-meta {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-bottom: 20px;
}

:deep(.revision-meta .el-form-item) {
  margin-bottom: 0;
}

:deep(.revision-meta .el-date-editor) {
  width: 100%;
}

:deep(.revision-meta .el-textarea__inner) {
  min-height: 80px !important;
}

/* 折扣标签样式 */
.discount-tag {
  margin-left: 5px;
  vertical-align: middle;
  transform: scale(0.9);
}

/* 总金额容器 */
.total-price-container {
  line-height: 1.8;
  padding: 8px 0;
}

/* 价格行样式 */
.price-line {
  display: flex;
  justify-content: space-between;
  &.original .value {
    color: #999;
    text-decoration: line-through;
  }
  &.discount .value {
    color: #409eff;
  }
  &.final .value {
    color: #f56c6c;
    font-weight: 600;
    font-size: 1.1em;
  }
}

/* 标签排列优化 */
.el-tag + .el-tag {
  margin-left: 5px;
}

:deep(.el-overlay) {
  z-index: 2000 !important;
}
.el-table__body-wrapper {
  overflow-x: auto;
  position: relative;
}
</style>