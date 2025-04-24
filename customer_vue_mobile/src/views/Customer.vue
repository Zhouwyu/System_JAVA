<template>
  <div class="mobile-container">
    <!-- 搜索栏 -->
    <van-search
        v-model="data.queryParams.name"
        placeholder="请输入客户名称"
        shape="round"
        @search="load"
    >
      <template #right-icon>
        <van-button size="small" type="primary" @click="load">查询</van-button>
      </template>
    </van-search>

    <!-- 筛选工具栏 -->
    <div class="filter-bar">
      <van-button plain hairline type="primary" size="small" @click="showAdvanced = true">
        高级筛选
      </van-button>
      <div class="space"></div>
      <van-button type="primary" size="small" @click="handleAdd">新增客户</van-button>
    </div>

    <!-- 客户列表 -->
    <van-list
        v-model:loading="loading"
        :finished="finished"
        finished-text="没有更多了"
        @load="load"
    >
      <van-checkbox-group v-model="selectedIds" ref="checkboxGroup">
        <van-cell v-for="item in data.tableData" :key="item.customerId">
          <van-checkbox :name="item.customerId" />
          <div class="customer-info">
            <div class="info-header">
              <span class="name">{{ item.customerName }}</span>
              <van-tag plain type="primary">{{ item.sex }}</van-tag>
            </div>
            <div class="info-detail">
              <div>行业：{{ item.businessIndustry }}</div>
              <div>微信：{{ item.wechatNum }}</div>
              <div>电话：{{ item.phoneNum }}</div>
            </div>
            <div class="info-actions">
              <van-button size="mini" @click="handleEdit(item)">编辑</van-button>
              <van-button size="mini" type="danger" @click="handleDelete(item.customerId)">
                删除
              </van-button>
            </div>
          </div>
        </van-cell>
      </van-checkbox-group>
    </van-list>

    <!-- 批量操作栏 -->
    <div v-if="selectedIds.length" class="batch-actions">
      <van-button type="danger" size="small" @click="handleBatchDelete">
        删除选中({{ selectedIds.length }})
      </van-button>
    </div>

    <!-- 高级筛选弹窗 -->
    <van-popup v-model:show="showAdvanced" round position="bottom">
      <div class="filter-popup">
        <div class="popup-header">
          <h3>高级筛选</h3>
          <van-button type="primary" size="small" @click="applyFilter">应用筛选</van-button>
        </div>
        <van-form>
          <van-field name="industry" label="所属行业">
            <template #input>
              <van-dropdown-menu>
                <van-dropdown-item
                    v-model="data.queryParams.industry"
                    :options="data.industries"
                />
              </van-dropdown-menu>
            </template>
          </van-field>

          <van-field name="gender" label="性别">
            <template #input>
              <van-radio-group v-model="data.queryParams.gender" direction="horizontal">
                <van-radio name="男">男</van-radio>
                <van-radio name="女">女</van-radio>
              </van-radio-group>
            </template>
          </van-field>

          <van-field
              v-model="data.queryParams.phone"
              label="手机号"
              placeholder="请输入手机号"
          />

          <van-field
              v-model="data.queryParams.wechatNum"
              label="微信号"
              placeholder="请输入微信号"
          />
        </van-form>
        <van-cell-group style="margin-top: 16px">
          <van-button
              block
              plain
              type="default"
              @click="resetFilter"
              style="margin-top: 12px"
          >
            重置筛选条件
          </van-button>
        </van-cell-group>
      </div>
    </van-popup>

    <!-- 客户表单弹窗 -->
    <van-popup
        v-model:show="showForm"
        position="bottom"
        round
        :style="{ height: '80%' }"
    >
      <van-form ref="formRef" @submit="submitForm">
        <div class="form-header">
          <h3>{{ editMode ? '编辑客户' : '新建客户' }}</h3>
          <div class="form-actions">
            <van-button plain @click="showForm = false">取消</van-button>
            <van-button native-type="submit" type="primary">
              {{ editMode ? '保存' : '创建' }}
            </van-button>
          </div>
        </div>

        <van-cell-group inset>
          <van-field
              v-model="form.customerName"
              label="客户名称"
              placeholder="请输入客户名称"
              :rules="[{ required: true, message: '请填写客户名称' }]"
          />

          <van-field name="sex" label="性别">
            <template #input>
              <van-radio-group v-model="form.sex" direction="horizontal">
                <van-radio name="男">男</van-radio>
                <van-radio name="女">女</van-radio>
              </van-radio-group>
            </template>
          </van-field>

          <van-field
              v-model="form.businessIndustry"
              is-link
              readonly
              name="industry"
              label="所属行业"
              placeholder="请选择行业"
              @click="showIndustryPicker = true"
              :rules="[{ required: true, message: '请选择行业' }]"
          />

          <van-field
              v-model="form.wechatNum"
              label="微信号"
              placeholder="请输入微信号"
              :rules="[
              { required: true, message: '请填写微信号' },
              { pattern: /^[a-zA-Z][\w-]{5,19}$/, message: '微信号格式不正确' }
            ]"
              @blur="checkWechat"
          />

          <van-field
              v-model="form.phoneNum"
              label="手机号"
              placeholder="请输入手机号"
              :rules="[
              { required: true, message: '请填写手机号' },
              { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确' }
            ]"
              @blur="checkPhone"
          />

          <van-field
              v-model="form.address"
              rows="2"
              autosize
              label="详细地址"
              type="textarea"
              placeholder="请输入详细地址"
              maxlength="100"
              show-word-limit
          />

          <van-field
              v-model="form.remark"
              rows="2"
              autosize
              label="备注信息"
              type="textarea"
              placeholder="请输入备注"
              maxlength="100"
              show-word-limit
          />
        </van-cell-group>
      </van-form>
    </van-popup>

    <!-- 行业选择器 -->
    <van-popup v-model:show="showIndustryPicker" round position="bottom">
      <van-picker
          :columns="data.industries"
          @confirm="onIndustryConfirm"
          @cancel="showIndustryPicker = false"
      />
    </van-popup>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { showConfirmDialog, showToast } from 'vant'
import request from '@/utils/request'

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
  tableData: [],
  industries: [],
  total: 0
})

const form = reactive({
  customerName: '',
  sex: '',
  businessIndustry: '',
  wechatNum: '',
  phoneNum: '',
  address: '',
  remark: ''
})

// 列表相关
const loading = ref(false)
const finished = ref(false)
const selectedIds = ref([])
const showAdvanced = ref(false)
const showForm = ref(false)
const showIndustryPicker = ref(false)
const editMode = ref(false)
const currentEditId = ref(null)
const originalWechat = ref('')
const originalPhone = ref('')

// 加载数据
const load = async () => {
  try {
    const res = await request.get('/customer/page', {
      params: data.queryParams
    })
    data.tableData = res.data.records
    data.total = res.data.total
    loading.value = false
    finished.value = data.queryParams.pageNum * data.queryParams.pageSize >= data.total
  } catch (error) {
    showToast('数据加载失败')
  }
}

// 加载行业数据
const loadIndustries = async () => {
  try {
    const mockData = [
      { name: '农、林、牧、渔业', id: 1 },
      {name: '采矿业', id: 2},
      {name: '制造业', id: 3},
      {name: '电力、热力、燃气及水生产和供应业', id: 4},
      {name: '建筑业', id: 5},
      {name: '批发和零售业', id: 6},
      {name: '交通运输、仓储和邮政业', id: 7},
      {name: '住宿和餐饮业', id: 8},
      {name: '信息传输、软件和信息技术服务业', id: 9},
      {name: '金融业', id: 10},
      {name: '房地产业', id: 11},
      {name: '租赁和商务服务业', id: 12},
      {name: '科学研究和技术服务业', id: 13},
      {name: '水利、环境和公共设施管理业', id: 14},
      {name: '居民服务、修理和其他服务业', id: 15},
      {name: '教育', id: 16},
      {name: '卫生和社会工作', id: 17},
      {name: '文化、体育和娱乐业', id: 18},
      {name: '公共管理、社会保障和社会组织', id: 19},
      {name: '国际组织', id: 20},
    ]
    data.industries = mockData.map(item => ({
      text: item.name,
      value: item.name
    }))
  } catch (error) {
    showToast('行业加载失败')
  }
}

// 表单验证
const checkWechat = async () => {
  if (!form.wechatNum || (editMode.value && form.wechatNum === originalWechat.value)) return

  const res = await request.get('/customer/checkWechat', {
    params: {
      wechatNum: form.wechatNum,
      excludeId: editMode.value ? currentEditId.value : undefined
    }
  })
  if (res.data.exist) {
    showToast('该微信号已存在')
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
    showToast('该手机号已存在')
    form.phoneNum = ''
  }
}

// 高级筛选应用
const applyFilter = () => {
  showAdvanced.value = false // 关闭弹窗
  data.queryParams.pageNum = 1 // 重置到第一页
  load()
}

const resetFilter = () => {
  data.queryParams = {
    ...data.queryParams,
    industry: '',
    gender: '',
    phone: '',
    wechatNum: ''
  }
  load()
  showAdvanced.value = false
}

// 表单操作
const handleAdd = () => {
  resetForm()
  editMode.value = false
  showForm.value = true
}

const handleEdit = (row) => {
  editMode.value = true
  currentEditId.value = row.customerId
  originalWechat.value = row.wechatNum
  originalPhone.value = row.phoneNum
  Object.assign(form, row)
  showForm.value = true
}

const resetForm = () => {
  Object.keys(form).forEach(key => form[key] = '')
  currentEditId.value = null
}

// 提交表单
const submitForm = async () => {
  try {
    const submitData = { ...form }
    if (editMode.value) submitData.customerId = currentEditId.value

    const apiUrl = editMode.value ? '/customer/update' : '/customer/add'
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
    message: '确定要删除该客户吗？'
  }).then(async () => {
    await request.post('/customer/delete/batch', { ids: [id] })
    showToast('删除成功')
    load()
  })
}

// 批量删除
const handleBatchDelete = () => {
  if (!selectedIds.value.length) {
    showToast('请至少选择一项')
    return
  }

  showConfirmDialog({
    title: '批量删除',
    message: `确定要删除选中的 ${selectedIds.value.length} 项吗？`
  }).then(async () => {
    await request.post('/customer/delete/batch', { ids: selectedIds.value })
    showToast('删除成功')
    selectedIds.value = []
    load()
  })
}

// 行业选择
const onIndustryConfirm = (value) => {
  form.businessIndustry = value.selectedOptions[0].text
  showIndustryPicker.value = false
}

// 初始化
onMounted(() => {
  load()
  loadIndustries()
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

.customer-info {
  flex: 1;
  margin-left: 12px;
}

.info-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 6px;
}

.name {
  font-weight: 500;
  font-size: 16px;
}

.info-detail {
  color: #666;
  font-size: 12px;
  line-height: 1.5;
}

.info-actions {
  margin-top: 8px;
  display: flex;
  gap: 6px;
}

.batch-actions {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: #fff;
  padding: 12px;
  box-shadow: 0 -2px 12px rgba(0, 0, 0, 0.1);
  text-align: center;
}

.filter-popup {
  padding: 16px;
}

.popup-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.form-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid #eee;
}

.form-actions {
  display: flex;
  gap: 8px;
}

.van-cell {
  align-items: flex-start;
}
</style>