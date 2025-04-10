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

    <!-- 新增/编辑抽屉 -->
    <el-drawer v-model="drawerVisible" direction="rtl" size="50%" :before-close="handleBeforeClose"
               @close="resetForm">
      <template #header>
        <h4>{{ editMode ? '编辑订单信息' : '新建订单信息' }}</h4>
      </template>

      <template #default>
        <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
          <el-form-item label="选择客户" prop="customerId">
            <el-select
                v-model="form.customerId"
                placeholder="请选择客户"
                filterable
                @change="handleCustomerChange"
            >
              <el-option
                  v-for="c in data.customerOptions"
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

          <!--          <el-form-item label="商品清单" prop="products">-->
          <!--            <div class="product-selector">-->
          <!--              <el-select-->
          <!--                  v-model="form.productId"-->
          <!--                  placeholder="选择商品"-->
          <!--                  @change="handleProductSelect"-->
          <!--              >-->
          <!--                <el-option-->
          <!--                    v-for="p in data.productOptions"-->
          <!--                    :key="p.value"-->
          <!--                    :value="p.value"-->
          <!--                    :label="`${p.label} | 库存: ${p.stock} | 单价: ¥${p.price}`"-->
          <!--                    :disabled="p.stock <= 0"-->
          <!--                />-->
          <!--              </el-select>-->

          <!--              <el-input-number-->
          <!--                  v-model="form.quantity"-->
          <!--                  :min="1"-->
          <!--                  :max="selectedProductStock"-->
          <!--                  @change="calculateTotal"-->
          <!--              />-->
          <!--            </div>-->
          <!--          </el-form-item>-->
          <el-form-item label="商品清单" prop="products">
            <div class="product-selector">
              <el-select
                  v-model="form.selectedProducts"
                  multiple
                  value-key="productId"
                  placeholder="选择商品"
                  @change="handleProductSelect"
              >
                <!-- 修改el-option的渲染方式 -->
                <el-option
                    v-for="p in data.productOptions"
                    :key="p.productId"
                    :value="p"
                    :label="`${p.productName} | 库存: ${p.stock} | 单价: ¥${p.price}`"
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
                  <span>单价: ¥{{ item.price }}</span>
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
            <!--            <el-col :span="8">-->
            <!--              <el-form-item label="商品单价" prop="price">-->
            <!--                <el-input-number-->
            <!--                    v-model="form.price"-->
            <!--                    :min="0"-->
            <!--                    :precision="2"-->
            <!--                    :disabled="true"-->
            <!--                    style="width: 100%"-->
            <!--                />-->
            <!--              </el-form-item>-->
            <!--            </el-col>-->
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
                      <el-table-column label="单价" width="120">
                        <template #default="{row}">
                          ¥{{ row.price.toFixed(2) }}
                        </template>
                      </el-table-column>
                      <el-table-column label="数量" prop="quantity" width="100"/>
                      <el-table-column label="小计" width="120">
                        <template #default="{row}">
                          <span style="color:#67C23A">¥{{ row.subtotal }}</span>
                        </template>
                      </el-table-column>
                    </el-table>
                  </el-descriptions-item>

                  <el-descriptions-item label="总金额" :span="2">
  <span style="color: #f56c6c; font-weight: 500">
    ¥{{ currentOrder.totalPrice.toFixed(2) }}
  </span>
                  </el-descriptions-item>

                  <!-- 其他信息 -->
                  <el-descriptions-item label="订单备注" :span="2">
                    {{ currentOrder.remark || '无备注信息' }}
                  </el-descriptions-item>
                  <el-descriptions-item label="操作人员">
                    {{ username }}
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
        <el-table-column label="操作" width="150">
          <template #default="scope">
            <div class="action-buttons">
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
import {reactive, ref, onMounted, nextTick} from 'vue'
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
  shipmentDate: [{required: true, message: '请选择出货时间', trigger: 'change'}]
}


// 抽屉控制
const drawerVisible = ref(false)
const formRef = ref()
const editMode = ref(false)
const currentEditId = ref(null)
const initialFormState = ref(null) // 保存表单初始状态
const hasUnsavedChanges = ref(false) // 跟踪修改状态

// 新增/编辑操作
const handleAdd = () => {
  resetForm()
  editMode.value = false
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

/**
 * 暂时废弃编辑功能
 */
const handleEdit = async (row) => {
  editMode.value = true
  currentEditId.value = row.orderId

  // 重置表单
  resetForm()

  // 基础字段赋值
  form.customerId = row.customerId
  form.remark = row.remark
  form.totalPrice = row.totalPrice
  form.shipmentDate = row.shipmentDate

  // 加载客户详情
  await handleCustomerChange(row.customerId)

  // 处理商品数据（关键修改）
  if (row.products?.length) {
    form.selectedProducts = row.products.map(p => {
      // 精确匹配商品对象
      const fullProduct = data.productOptions.find(
          op => op.productId === p.productId
      )
      if (!fullProduct) {
        console.warn('商品不存在:', p.productId)
        return null
      }
      return fullProduct
    }).filter(Boolean) // 过滤掉未找到的商品

    // 初始化商品数量（深拷贝）
    form.products = form.selectedProducts.map(sp => ({
      ...sp,
      quantity: row.products.find(p => p.productId === sp.productId)?.quantity || 1
    }))
  }

  // 强制视图更新
  await nextTick()
  drawerVisible.value = true
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
      sum + (item.price * item.quantity), 0
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
const stockPreCheck = async () => {
  // 提取需要检查的商品数据
  const checkItems = form.products.map(item => ({
    productId: item.productId,
    quantity: item.quantity
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
        stock: item.stock,
        orderNo: "",
      })),
      totalPrice: form.totalPrice,
      remark: form.remark,
      shipmentDate: form.shipmentDate
    }
    if (editMode.value) payload.oderId = currentEditId.value
    // 4. 提交请求
    const url = editMode.value ? `/order/update` : '/order/add'
    const res = await request.post(url, payload)
    // 5. 处理成功逻辑
    if (res.code === '200') {
      ElMessage.success(editMode.value ? '修改成功' : '新增成功')
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
  // operator: {
  //   userId: null,
  //   name: localStorage.getItem('username') || '系统自动'
  // },

  // 商品清单（数组结构）
  products: [
    {
      productId: null,
      productName: '',
      price: 0,
      quantity: 1,
      unitPrice: 0, // 购买时单价（可能不同于商品当前价格）
    }
  ]
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
        subtotal: (p.price * p.quantity).toFixed(2)
      })),
      totalPrice: res.data.products.reduce((sum, p) => sum + p.price * p.quantity, 0)
    }
    detailDialogVisible.value = true
  } catch (error) {
    ElMessage.error('获取订单详情失败')
  }
}

// 响应式用户名
const username = ref('')
onMounted(() => {
  load()
  loadBaseData()
  username.value = localStorage.getItem('username') || '未登录用户'
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
</style>