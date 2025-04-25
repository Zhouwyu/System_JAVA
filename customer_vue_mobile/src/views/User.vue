<template>
  <div class="user-management">
    <!-- 搜索栏 -->
    <van-search
        v-model="searchKeyword"
        placeholder="搜索用户"
        shape="round"
        @search="handleSearch"
    >
      <template #left>
        <van-button
            icon="plus"
            type="primary"
            size="small"
            @click="handleCreate"
        >新建</van-button>
      </template>
    </van-search>

    <!-- 用户列表 -->
    <van-list
        v-model:loading="loading"
        :finished="finished"
        finished-text="没有更多了"
        @load="loadUsers"
    >
      <van-swipe-cell
          v-for="user in userList"
          :key="user.userId"
          :before-close="beforeClose"
          :right-width="100"
      >
        <van-cell-group>
          <van-cell
              :title="user.userName"
              :label="`${user.fullName} | ${user.phone}`"
          >
            <template #value>
              <div class="cell-right">
                <van-tag
                    :color="getRoleColor(user.roleName)"
                    plain
                    size="small"
                >
                  {{ user.roleName }}
                </van-tag>
<!--                <van-switch-->
<!--                    v-model="user.status"-->
<!--                    :active-value="1"-->
<!--                    :inactive-value="0"-->
<!--                    size="20"-->
<!--                    @change="handleStatusChange(user)"-->
<!--                />-->
              </div>
            </template>
          </van-cell>
        </van-cell-group>
        <template #right>
          <van-button
              square
              type="primary"
              icon="edit"
              style="width: 50px; height: 100%"
              @click="handleEdit(user)"
          />
          <van-button
              square
              type="danger"
              icon="delete"
              style="width: 50px; height: 100%"
              @click="handleDelete(user.userId)"
          />
        </template>
      </van-swipe-cell>
    </van-list>

    <!-- 新增/编辑弹窗 -->
    <van-popup
        v-model:show="dialogVisible"
        position="bottom"
        :style="{ height: '80%' }"
    >
      <van-nav-bar
          :title="dialogType === 'create' ? '新增用户' : '编辑用户'"
          left-text="取消"
          right-text="保存"
          class="form-nav-bar"
          @click-left="dialogVisible = false"
          @click-right="submitForm"
      />

      <van-form ref="formRef" class="form-container">
        <van-field
            v-model="currentUser.userName"
            label="用户名"
            placeholder="请输入用户名"
            :rules="rules.userName"
            :disabled="dialogType === 'edit'"
        >
          <template #button>
            <van-loading
                v-if="validating"
                size="16px"
                type="spinner"
            />
            <van-icon
                v-else-if="usernameValid === true"
                name="passed"
                color="#07c160"
            />
            <van-icon
                v-else-if="usernameValid === false"
                name="warning-o"
                color="#ee0a24"
            />
          </template>
        </van-field>

        <van-field
            v-model="currentUser.fullName"
            label="姓名"
            placeholder="请输入姓名"
            :rules="rules.fullName"
        />

        <van-field
            v-model="currentUser.email"
            label="邮箱"
            placeholder="请输入邮箱"
            :rules="rules.email"
        />

        <van-field
            v-model="currentUser.phone"
            label="手机号"
            placeholder="请输入手机号"
            :rules="rules.phone"
        />

        <van-field
            readonly
            clickable
            name="role"
            label="角色"
            v-model="currentUser.roleName"
            placeholder="请选择角色"
            :rules="rules.roleId"
            @click="showRolePicker = true"
        />

        <van-field
            v-if="dialogType === 'create'"
            v-model="currentUser.password"
            label="密码"
            type="password"
            placeholder="请输入密码"
            :rules="rules.password"
        />
      </van-form>

      <!-- 角色选择器 -->
      <van-popup
          v-model:show="showRolePicker"
          round
          position="bottom"
      >
        <van-picker
            :columns="roleOptions"
            value-key="text"
            @confirm="onRoleConfirm"
            @cancel="showRolePicker = false"
        />
      </van-popup>
    </van-popup>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { showConfirmDialog, showToast } from 'vant'
import request from '@/utils/request'

// 用户数据
const userList = ref([])
const loading = ref(false)
const finished = ref(false)
const searchKeyword = ref('')

// 角色数据
const roleOptions = ref([])
const showRolePicker = ref(false)

// 弹窗相关
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
  password: ''
})

const data = reactive({
  queryParams: {
    userName: '',
    pageNum: 1,
    pageSize: 10
  },
  total: 0,
})

// 表单验证
const formRef = ref(null)
const validating = ref(false)
const usernameValid = ref(null)

// 防抖函数首先定义
const debouncePromise = (fn, delay = 500) => {
  let timer;
  return (...args) => new Promise((resolve) => {
    clearTimeout(timer);
    timer = setTimeout(() => {
      resolve(fn.apply(this, args));
    }, delay);
  });
};

// 然后定义检查用户名的函数
const checkUsername = debouncePromise(async (val) => {
  if (!val) return false;

  validating.value = true;
  try {
    const params = {
      username: val,
      userId: dialogType.value === 'edit' ? currentUser.userId : null
    };
    const res = await request.get('/user/checkUsername', { params });
    return !res.data.exists;
  } catch (error) {
    console.error('校验失败:', error);
    return false;
  } finally {
    validating.value = false;
  }
});

const rules = reactive({
  userName: [
    { required: true, message: '请输入用户名' },
    { validator: val => val.length >= 3 && val.length <= 20, message: '长度3-20位' },
    { validator: async (val) => {
        if (!val) return true;
        return await checkUsername(val);
      },
      message: '用户名已存在'
    }
  ],
  fullName: [
    { required: true, message: '请输入姓名' }
  ],
  email: [
    { required: true, message: '请输入邮箱' },
    { type: 'email', message: '邮箱格式不正确' }
  ],
  phone: [
    { required: true, message: '请输入手机号' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式错误' }
  ],
  roleId: [
    { required: true, message: '请选择角色' }
  ],
  password: [
    { required: true, message: '请输入密码' },
    { validator: val => val.length >= 6 && val.length <= 20, message: '长度6-20位' }
  ]
})

// 生命周期
onMounted(() => {
  loadRoles()
})

const handleSearch = () => {
  // 同步搜索关键词到查询参数
  data.queryParams.userName = searchKeyword.value
  data.queryParams.pageNum = 1
  userList.value = []
  finished.value = false
  loadUsers()
}

// 加载用户
const loadUsers = async () => {
  try {
    loading.value = true

    // 当加载第一页时清空列表
    if (data.queryParams.pageNum === 1) {
      userList.value = []
    }
    const params = {
      userName: searchKeyword.value,
      pageNum: data.queryParams.pageNum,
      pageSize: data.queryParams.pageSize
    }

    const res = await request.get('/user/page', { params })
    userList.value = [...userList.value, ...res.data.records]
    data.total = res.data.total
    data.queryParams.pageNum++

    // 判断是否加载完成
    if (userList.value.length >= data.total) {
      finished.value = true
    }
  } finally {
    loading.value = false
  }
}

// 加载角色
const loadRoles = async () => {
  try {
    const res = await request.get('/role/load')
    roleOptions.value = res.data.map(role => ({
      value: role.roleId,
      text: role.roleName,
    }))
  } catch (error) {
    showToast({
      message: '加载角色失败',
      icon: 'fail',
      duration: 1500,
      position: 'middle',
    })
  }
}

// 提交表单
const submitForm = async () => {
  try {
    // 表单验证
    await formRef.value.validate()

    // 构建请求参数
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

    // 发送请求
    const res = await request.post(apiUrl, params)

    if (res.code === '200') {
      showToast({
        message: isCreate ? '创建成功' : '修改成功',
        icon: 'success',
        duration: 1500,
        position: 'middle',
      })
      dialogVisible.value = false
      resetList()
    }
  } catch (error) {
    console.error('提交失败:', error)
  }
}

// 删除用户
const handleDelete = (userId) => {
  showConfirmDialog({
    title: '确认删除',
    message: '确定要删除该用户吗？'
  }) .then(async () => {
    await request.post('/user/delete/batch', { ids: [userId] })
    showToast({
      message: '删除成功',
      icon: 'success',
      duration: 1500,
      position: 'middle',
    })
    resetList()
  })
}

// 重置列表
const resetList = () => {
  userList.value = []
  data.queryParams.pageNum = 1
  finished.value = false
  loadUsers()
}

// 角色选择
const onRoleConfirm = ({ selectedOptions }) => {
  currentUser.roleId = selectedOptions[0].value
  currentUser.roleName = selectedOptions[0].text
  showRolePicker.value = false
}

// 滑动关闭处理
const beforeClose = ({ position }) => {
  if (position === 'right') {
    return { close: false }
  }
  return true
}

// 角色颜色映射
const getRoleColor = computed(() => (roleName) => {
  const colorMap = {
    '超级管理员': '#ff4d4f',
    '用户管理员': '#409eff',
    '技术支持': '#67c23a',
    '访客': '#909399'
  }
  return colorMap[roleName] || '#999'
})

// 打开创建弹窗
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
    password: ''
  })
  dialogVisible.value = true
}

// 打开编辑弹窗
const handleEdit = (user) => {
  dialogType.value = 'edit'
  Object.assign(currentUser, {
    ...user,
  })
  dialogVisible.value = true
}
</script>

<style scoped>
.user-management {
  padding: 12px;
  min-height: 100vh;
  background: #f7f8fa;
}

.cell-right {
  display: flex;
  align-items: center;
  gap: 8px;
}

.form-container {
  padding: 20px 16px;
}

:deep(.van-field__button) {
  display: flex;
  align-items: center;
  gap: 4px;
}

.van-switch {
  transform: scale(0.8);
}

/* 添加全局样式 */
:deep(.form-nav-bar) {
  --van-nav-bar-title-text-color: #333;  /* 标题颜色 */
  --van-nav-bar-text-color: #666;       /* 默认文字颜色 */
  --van-nav-bar-height: 46px;          /* 导航栏高度 */
}

/* 自定义按钮颜色 */
:deep(.form-nav-bar .van-nav-bar__left .van-nav-bar__text) {
  color: #ffffff !important;  /* 取消按钮灰色 */
}

:deep(.form-nav-bar .van-nav-bar__right .van-nav-bar__text) {
  color: #d1f0f6 !important;   /* 保存按钮主题色 */
}
</style>