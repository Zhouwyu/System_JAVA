<template>
  <div class="user-management-container">
    <!-- 搜索和操作栏 -->
    <div class="operation-bar">
      <el-input
          v-model="searchKeyword"
          placeholder="搜索用户"
          clearable
          style="width: 300px"
          @keyup.enter="handleSearch"
      >
        <template #append>
          <el-button :icon="Search" @click="handleSearch" />
        </template>
      </el-input>

      <el-button
          type="primary"
          :icon="Plus"
          @click="handleCreate"
      >
        新增用户
      </el-button>
    </div>

    <!-- 用户表格 -->
    <el-table
        :data="userList"
        v-loading="loading"
        style="width: 100%"
        border
        stripe
    >
      <el-table-column prop="userName" label="用户名" width="180" />
      <el-table-column prop="fullName" label="姓名" width="150" />
      <el-table-column prop="email" label="邮箱" width="220" />
      <el-table-column prop="phone" label="手机号" width="150" />
      <el-table-column label="角色" width="150">
        <template #default="{row}">
          <el-tag
              :type="getRoleTagType(row.roleName)"
              size="small"
              effect="dark"
              :color="getRoleColor(row.roleName)"
              class="role-tag"
          >
            <span class="role-text">{{ row.roleName }}</span>
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="{row}">
          <el-switch
              v-model="row.status"
              :active-value="1"
              :inactive-value="0"
              @change="handleStatusChange(row)"
          />
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{row}">
          <el-button
              type="primary"
              size="small"
              :icon="Edit"
              @click="handleEdit(row)"
          />
          <el-button
              type="danger"
              size="small"
              :icon="Delete"
              @click="handleDelete(row.userId)"
          />
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination">
      <el-pagination
          v-model:current-page="data.queryParams.pageNum"
          v-model:page-size="data.queryParams.pageSize"
          :total="data.total"
          layout="total, sizes, prev, pager, next, jumper"
          :page-sizes="[10, 20, 50, 100]"
          @size-change="loadUsers"
          @current-change="loadUsers"
      />
    </div>

    <!-- 用户编辑/新增对话框 -->
    <el-dialog
        v-model="dialogVisible"
        :title="dialogType === 'create' ? '新增用户' : '编辑用户'"
        width="600px"
    >
      <el-form
          ref="formRef"
          :model="currentUser"
          :rules="rules"
          label-width="100px"
          label-position="right"
      >
        <el-form-item label="用户名" prop="userName">
          <el-input
              v-model="currentUser.userName"
              placeholder="请输入用户名"
              :validate-event="false"
          @blur="formRef.validateField('userName')"
          >
          <template #suffix>
            <el-icon
                v-if="formRef?.fields?.userName?.validateState === 'validating'"
                class="is-loading"
            >
              <Loading />
            </el-icon>
            <el-icon
                v-if="formRef?.fields?.userName?.validateState === 'error'"
                style="color: #F56C6C"
            >
              <CircleCloseFilled />
            </el-icon>
            <el-icon
                v-if="formRef?.fields?.userName?.validateState === 'success'"
                style="color: #67C23A"
            >
              <CircleCheckFilled />
            </el-icon>
          </template>
          </el-input>
        </el-form-item>

        <el-form-item label="姓名" prop="fullName">
          <el-input
              v-model="currentUser.fullName"
              placeholder="请输入姓名"
          />
        </el-form-item>

        <el-form-item label="邮箱" prop="email">
          <el-input
              v-model="currentUser.email"
              placeholder="请输入邮箱"
          />
        </el-form-item>

        <el-form-item label="手机号" prop="phone">
          <el-input
              v-model="currentUser.phone"
              placeholder="请输入手机号"
          />
        </el-form-item>

        <el-form-item label="角色" prop="roles">
          <el-select
              v-model="currentUser.roleId"
              placeholder="请选择角色"
              style="width: 100%"
          >
            <el-option
                v-for="role in roleOptions"
                :key="role.id"
                :label="role.name"
                :value="role.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="密码" prop="password" v-if="dialogType === 'create'">
          <el-input
              v-model="currentUser.password"
              type="password"
              placeholder="请输入密码"
              show-password
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Search, Plus, Edit, Delete } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from "@/utils/request.js";

// 用户数据
const userList = ref([])
const loading = ref(false)
const searchKeyword = ref('')
const roleOptions = ref([])

// 添加表单应用
const formRef = ref(null)

// 对话框相关
const dialogVisible = ref(false)
const dialogType = ref('create')
const currentUser = reactive({
  userId: null,
  userName: '',
  fullName: '',
  email: '',
  phone: '',
  roleId: null,
  roleName: '',
  password: '',
  status: ''
})

const data = reactive({
  queryParams: {
    userName: '',
    pageNum: 1,
    pageSize: 10
  },
  total: 0,
})

// 表单验证规则
const rules = reactive({
  userName: [
    {
      required: true,
      message: '请输入用户名',
      trigger: 'blur'
    },
    {
      min: 3,
      max: 20,
      message: '长度在3到20个字符',
      trigger: 'blur'
    },
    {
      validator: async (rule, value, callback) => {
        if (!value) return callback();

        const userId = dialogType.value === 'edit' ? currentUser.userId : null;

        try {
          const valid = await checkUsername(value, userId);
          valid ? callback() : callback(new Error('用户名已存在'));
        } catch (e) {
          callback(new Error('校验服务不可用'));
        }
      },
      trigger: 'blur'
    }
  ],
  fullName: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  phone: [
    { required: true, pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  roleId: [
    { required: true, message: '请至少选择一个角色', trigger: 'change' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '长度在6到20个字符', trigger: 'blur' }
  ]
})

// 生命周期
onMounted(() => {
  loadUsers()
  loadRoles()
})

// 加载用户数据
const loadUsers = async () => {
  try {
    loading.value = true
    // 这里替换为实际API调用
    const params = {
      ...data.queryParams,
    }
    const res = await request.get('/user/page', {params})
    userList.value = res.data.records
    data.total = res.data.total
  } finally {
    loading.value = false
  }
}

// 加载角色选项
const loadRoles = async () => {
  // 替换为实际API调用
  try {
    const res = await request.get('/role/load')
    // 转换数据结构适配前端
    roleOptions.value = res.data.map(role => ({
      id: role.roleId,       // 对应数据库role_id
      code: role.roleCode,   // 角色代码
      name: role.roleName,   // 角色名称
      description: role.description
    }))

  } catch (error) {
    console.error('角色加载失败:', error)
    ElMessage.error('角色列表加载失败')
    roleOptions.value = [] // 确保有默认空数组
  }
}

// 搜索处理
const handleSearch = () => {
  // 同步搜索关键词到查询参数
  data.queryParams.userName = searchKeyword.value
  data.queryParams.pageNum = 1
  loadUsers()
}

// 状态修改
const handleStatusChange = async (user) => {
  try {
    // 调用API修改状态
    // await mockApiUpdateStatus(user.id, user.status)
    // ElMessage.success('状态更新成功')
  } catch {
    user.status = user.status === 1 ? 0 : 1
  }
}

// 删除用户
const handleDelete = (userId) => {
  ElMessageBox.confirm('确定删除该用户？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await request.post('/user/delete/batch', {ids: [userId]})
    ElMessage.success('删除成功')
    loadUsers()
  })
}

// 提交表单
const submitForm = async () => {
  try {
    // 1. 表单验证
    await formRef.value.validate()

    // 最终用户名校验
    const finalCheck = await checkUsername(
        currentUser.userName,
        dialogType.value === 'edit' ? currentUser.userId : null
    );

    if (!finalCheck) {
      ElMessage.warning('用户名已被占用，请修改后提交');
      return;
    }

    // 2. 准备API参数
    const isCreate = dialogType.value === 'create'
    const apiUrl = isCreate ? '/user/create' : '/user/update'
    const params = {
      userId: currentUser.userId,
      username: currentUser.userName,
      fullName: currentUser.fullName,
      email: currentUser.email,
      mobile: currentUser.phone,
      roleId: currentUser.roleId, // 根据后端字段要求可能需要调整
      ...(isCreate && { password: currentUser.password }) // 仅创建时传密码
    }

    // 3. 执行API调用
    const res = await request.post(apiUrl, params)

    // 4. 处理响应
    if (res.code === '200') {
      ElMessage.success(isCreate ? '用户创建成功' : '用户更新成功')
      dialogVisible.value = false
      data.queryParams.pageNum = 1 // 新增数据后回到第一页
      loadUsers() // 刷新列表
    } else {
      ElMessage.error(res.msg || '操作失败')
    }
  } catch (error) {
    // 错误处理
    if (error.name === 'ValidationError') {
      ElMessage.warning('请正确填写表单')
    } else {
      console.error('API错误:', error)
      ElMessage.error(error.response?.data?.msg || '请求失败')
    }
  }
}

// 添加防抖函数
const debouncePromise = (fn, delay = 500) => {
  let timer;
  return (...args) => new Promise((resolve) => {
    clearTimeout(timer);
    timer = setTimeout(() => {
      resolve(fn.apply(this, args));
    }, delay);
  });
};

// 用户名校验方法
const checkUsername = debouncePromise(async (username, userId = null) => {
  try {
    const params = { username };
    if (userId) params.userId = userId;

    const res = await request.get('/user/checkUsername', { params });
    return !res.data.exists;
  } catch (error) {
    console.error('校验失败:', error);
    return false;
  }
});

// 对话框处理
const handleCreate = () => {
  dialogType.value = 'create'
  Object.assign(currentUser, {
    userId: null,
    userName: '',
    fullName: '',
    email: '',
    phone: '',
    roleId: null,
    roleName: '',
    password: '',
    status: ''
  })
  dialogVisible.value = true
}

const handleEdit = (user) => {
  dialogType.value = 'edit'
  Object.assign(currentUser, {
    ...user,
    // roleIds: user.roles.map(r => r.id)
  })
  dialogVisible.value = true
}

// 添加角色颜色映射方法
const getRoleTagType = (roleName) => {
  const typeMap = {
    '超级管理员': 'danger',
    '用户管理员': 'primary',
    '技术支持': 'success',
    '访客': 'info'
  }
  return typeMap[roleName] || 'info'
}

const getRoleColor = (roleName) => {
  const colorMap = {
    '超级管理员': '#ff4d4f',
    '用户管理员': '#409eff',
    '技术支持': '#67c23a',
    '访客': '#909399'
  }
  return colorMap[roleName]
}
</script>

<style scoped>
.user-management-container {
  padding: 20px;
  background: #fff;
  border-radius: 4px;
}

.operation-bar {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
}

.role-tag {
  margin: 2px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.el-table {
  margin-top: 20px;
}

.el-form-item {
  margin-bottom: 22px;
}
</style>