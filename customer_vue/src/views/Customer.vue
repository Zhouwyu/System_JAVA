<template>
  <div>
    <!-- 修改原查询区域 -->
    <div class="card">
      <el-collapse v-model="data.activeCollapse">
        <!-- 基本搜索 -->
        <el-collapse-item name="basic">
          <template #title>
            <span style="font-size: 14px; padding-left: 10px">基本搜索</span>
          </template>
          <el-row :gutter="10" align="middle">
            <el-col :span="6">
              <el-input
                  v-model="data.queryParams.name"
                  placeholder="客户名称"
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

        <!-- 高级筛选 -->
        <el-collapse-item name="advanced">
          <template #title>
            <span style="font-size: 14px; padding-left: 10px">更多筛选条件</span>
          </template>
          <el-row :gutter="10" align="middle">
            <el-col :span="6">
              <el-input
                  v-model="data.queryParams.industry"
                  placeholder="所属行业"
                  clearable
                  style="width: 100%"
              />
            </el-col>
            <el-col :span="6">
              <el-select
                  v-model="data.queryParams.gender"
                  placeholder="性别"
                  clearable
                  style="width: 100%"
              >
                <el-option label="男" value="男"/>
                <el-option label="女" value="女"/>
              </el-select>
            </el-col>
            <el-col :span="6">
              <el-input
                  v-model="data.queryParams.phone"
                  placeholder="手机号"
                  clearable
              />
            </el-col>
            <el-col :span="6">
              <el-input
                  v-model="data.queryParams.wechatNum"
                  placeholder="微信号"
                  clearable
              />
            </el-col>
          </el-row>
        </el-collapse-item>
      </el-collapse>
    </div>

    <div class="card" style="margin-top: 5px">
      <el-button type="primary" @click="handleAdd">新增客户</el-button>
      <el-button type="warning" @click="handleBatchDelete">批量删除</el-button>
    </div>

    <!-- 新增/编辑抽屉 -->
    <el-drawer v-model="drawer2" :direction="direction" size="40%" close-on-click-modal="false"
               :before-close="handleDrawerClose">
      <template #header>
        <h4>{{ editMode ? '编辑客户信息' : '新建客户信息' }}</h4>
      </template>

      <template #default>
        <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
          <!-- 客户名称 -->
          <el-form-item label="客户名称" prop="customerName">
            <el-input v-model="form.customerName" placeholder="请输入客户名称" clearable/>
          </el-form-item>

          <!-- 性别 & 行业 -->
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="性别" prop="sex">
                <el-select v-model="form.sex" placeholder="请选择性别">
                  <el-option label="男" value="男"/>
                  <el-option label="女" value="女"/>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="经营行业" prop="businessIndustry">
                <el-input
                    v-model="form.businessIndustry"
                    placeholder="请输入行业名称"
                    clearable
                />
              </el-form-item>
            </el-col>
          </el-row>

          <!-- 微信号 & 手机号 -->
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="微信号" prop="wechatNum">
                <el-input
                    v-model="form.wechatNum"
                    placeholder="请输入微信号"
                    clearable
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="手机号" prop="phoneNum">
                <el-input
                    v-model="form.phoneNum"
                    placeholder="请输入手机号"
                    clearable
                />
              </el-form-item>
            </el-col>
          </el-row>

          <!-- 地址 -->
          <el-form-item label="地址" prop="address">
            <el-input
                v-model="form.address"
                type="textarea"
                :rows="3"
                placeholder="请输入详细地址"
                maxlength="100"
                show-word-limit
            />
          </el-form-item>

          <el-form-item label="备注" prop="remark">
            <el-input
                v-model="form.remark"
                type="textarea"
                :rows="3"
                placeholder="请输入备注信息"
                maxlength="100"
                show-word-limit
            />
          </el-form-item>
        </el-form>
      </template>

      <template #footer>
        <div style="flex: auto">
          <el-button @click="handleCancel">取消</el-button>
          <el-button type="primary" @click="submitForm">
            {{ editMode ? '保存修改' : '提交' }}
          </el-button>
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
          style="width: 100%"
      >
        <el-table-column type="selection" width="55"/>
        <el-table-column label="客户名称" prop="customerName"></el-table-column>
        <el-table-column label="性别" prop="sex"></el-table-column>
        <el-table-column label="行业" prop="businessIndustry"></el-table-column>
        <el-table-column label="微信号" prop="wechatNum"></el-table-column>
        <el-table-column label="手机号" prop="phoneNum"></el-table-column>
        <el-table-column label="备注" prop="remark"></el-table-column>
        <el-table-column label="地址" prop="address" width="200"></el-table-column>
        <el-table-column label="操作" width="180">
          <template #default="scope">
            <el-button
                type="primary"
                size="small"
                @click="handleEdit(scope.row)"
            >编辑
            </el-button>
            <el-button
                type="danger"
                size="small"
                @click="handleDelete(scope.row.customerId)"
            >删除
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
import {Search} from "@element-plus/icons-vue"
import {reactive, ref, onMounted} from "vue"
import request from "@/utils/request.js"
import {ElMessage, ElMessageBox} from "element-plus"

const data = reactive({
  queryParams: {
    name: '',
    industry: '',
    gender: '',
    phone: '',
    wechatNum: '',
    pageNum: 1,
    pageSize: 10,
  },
  activeCollapse: ['basic'], // 默认展开的面板
  total: 0,
  tableData: [],
})

// 加载客户数据
const load = () => {
  request.get("/customer/page", {
    params: data.queryParams
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
    industry: '',
    gender: '',
    phone: '',
    wechatNum: '',
  }
  load()
}


// 表单相关
const form = reactive({
  customerName: '',
  sex: '',
  businessIndustry: '',
  wechatNum: '',
  phoneNum: '',
  remark: '',
  address: ''
})

const rules = {
  customerName: [{required: true, message: '客户名称不能为空', trigger: 'blur'}],
  sex: [{required: true, message: '请选择性别', trigger: 'change'}],
  businessIndustry: [
    { required: false, message: '请输入行业名称', trigger: 'blur' }
  ],
  wechatNum: [
    {required: false, message: '微信号不能为空', trigger: 'blur'},
    // {pattern: /^[a-zA-Z][\w-]{5,19}$/, message: '微信号格式不正确'}
  ],
  phoneNum: [
    {required: false, message: '手机号不能为空', trigger: 'blur'},
    {pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确'}
  ]
}

// 唯一性校验
const originalWechat = ref('')
const originalPhone = ref('')

const checkWechat = async () => {
  if (!form.wechatNum || (editMode.value && form.wechatNum === originalWechat.value)) return

  const res = await request.get('/customer/checkWechat', {
    params: {
      wechatNum: form.wechatNum,
      excludeId: editMode.value ? currentEditId.value : undefined
    }
  })
  if (res.data.exist) {
    ElMessage.error('该微信号已存在')
    form.wechatNum = ''
  }
}

const checkPhone = async () => {
  if (!form.phoneNum || (editMode.value && form.phoneNum === originalPhone.value)) return

  const res = await request.get('/customer/checkPhone', {
    params: {
      phoneNum: form.phoneNum,
      excludeId: editMode.value ? currentEditId.value : undefined
    }
  })
  if (res.data.exist) {
    ElMessage.error('该手机号已存在')
    form.phoneNum = ''
  }
}

// 抽屉控制
const drawer2 = ref(false)
const direction = 'rtl'
const formRef = ref()
const editMode = ref(false)
const currentEditId = ref(null)

// 新增/编辑操作
const handleAdd = () => {
  resetForm()
  editMode.value = false
  drawer2.value = true
}

const handleEdit = (row) => {
  editMode.value = true
  currentEditId.value = row.customerId
  originalWechat.value = row.wechatNum
  originalPhone.value = row.phoneNum

  Object.keys(form).forEach(key => {
    form[key] = row[key] || ''
  })
  drawer2.value = true
}

const resetForm = () => {
  formRef.value?.resetFields()
  Object.keys(form).forEach(key => form[key] = '')
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
    drawer2.value = false
  } catch (error) {
    // 用户点击取消或关闭对话框时，不做操作
    console.log('用户取消了关闭操作')
  }
}
// 提交表单
const submitForm = async () => {
  try {
    await formRef.value.validate()
    const submitData = {...form}
    if (editMode.value) submitData.customerId = currentEditId.value

    const apiUrl = editMode.value ? '/customer/update' : '/customer/add'
    const res = await request.post(apiUrl, submitData)

    if (res.code === '200') {
      ElMessage.success(editMode.value ? '修改成功' : '新增成功')
      drawer2.value = false
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
      const ids = selectedRows.value.map(item => item.customerId)

      const response = await request.post('/customer/delete/batch', {ids})

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
  ElMessageBox.confirm('确定删除该客户？', '警告', {
    confirmButtonText: '确认',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    // 复用批量删除接口
    await request.post('/customer/delete/batch', {ids: [id]})
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
.card {
  padding: 20px;
  background: #fff;
  border-radius: 5px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  margin-bottom: 10px;
}

/* 优化折叠面板样式 */
:deep(.el-collapse-item__header) {
  height: 40px !important;
  line-height: 40px !important;
  border-bottom: none !important;
}

:deep(.el-collapse-item__wrap) {
  border-bottom: none !important;
}

:deep(.el-collapse) {
  border: none;
}

/* 表单元素间距 */
.el-col {
  margin-bottom: 10px;
}
</style>