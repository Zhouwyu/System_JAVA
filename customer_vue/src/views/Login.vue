<template>
  <!-- 渐变背景容器 -->
  <div class="background-gradient"></div>
  <!-- 登录框  -->
  <div class="login" id="login">
    <a href="javascript:;" class="log-close"><i class="icons close"></i></a>
    <div class="log-bg">
      <div class="log-logo">后台管理系统登录</div>
      <div class="log-text"></div>
    </div>

    <!-- 登录表单区域 -->
    <el-form
        :model="form"
        :rules="rules"
        ref="loginForm"
        class="log-email"
        @submit.prevent="handleSubmit"
    >
      <el-form-item prop="username">
        <el-input
            v-model="form.username"
            placeholder="用户名"
            prefix-icon="User"
            size="default"
            clearable
        />
      </el-form-item>

      <el-form-item prop="password">
        <el-input
            v-model="form.password"
            placeholder="密码"
            prefix-icon="Lock"
            size="default"
            show-password
            clearable
        />
      </el-form-item>

      <!-- 新增角色选择 -->
      <el-form-item prop="roleId">
        <el-radio-group v-model="form.roleId">
          <el-radio :label="1">普通人员</el-radio>
          <el-radio :label="0">管理员</el-radio>
        </el-radio-group>
      </el-form-item>

      <!-- 记住我 & 忘记密码 -->
      <div class="form-options">
        <el-checkbox v-model="form.rememberMe">记住我</el-checkbox>
        <el-link type="primary" @click="handleForgetPassword">忘记密码？</el-link>
      </div>

      <el-button
          type="primary"
          native-type="submit"
          size="large"
          class="log-btn"
          :loading="loading"
      >
        立即登录
      </el-button>
    </el-form>
  </div>
</template>

<script setup>
import {ref, reactive, onMounted} from 'vue'
import {ElMessage, ElMessageBox} from 'element-plus'
import request from "@/utils/request.js";
import router from "@/router/index.js";

// 表单数据
const form = reactive({
  username: '',
  password: '',
  roleId: 0,       // 新增角色字段 1-普通 0-管理员
  rememberMe: false // 新增记住我
})

// 验证规则
const rules = reactive({
  username: [
    {required: true, message: '请输入用户名', trigger: 'blur'},
    {min: 3, max: 12, message: '长度在 3 到 12 个字符', trigger: 'blur'}
  ],
  password: [
    {required: true, message: '请输入密码', trigger: 'blur'},
    {min: 6, max: 18, message: '长度在 6 到 18 个字符', trigger: 'blur'}
  ],
  roleId: [
    {
      required: true,
      message: '请选择登录身份',
      trigger: 'change'
    }
  ]
})

// 引用表单实例
const loginForm = ref(null)
// 加载状态
const loading = ref(false)

// 提交处理
const handleSubmit = async () => {
  // 1、表单验证
  await loginForm.value.validate()
  loading.value = true

  // 处理记住我
  if (form.rememberMe) {
    localStorage.setItem('rememberedUser', JSON.stringify({
      username: form.username,
      roleId: form.roleId
    }))
  } else {
    localStorage.removeItem('rememberedUser')
  }

  // 2. 发送登录请求
  const data = await request.post('/auth/login', {
    username: form.username,
    password: form.password,
    roleId: form.roleId
  })


  if (data.code !== '200') {
    let errorMessage = '登录失败，请重试';
    switch (data.code) {
      case '401':
        errorMessage = '用户名或密码错误';
        break;
      case '402':
        errorMessage = '没有权限';
        break;
      case '403':
        errorMessage = '账户已被禁用';
        break;
    }
    ElMessage.error(errorMessage)
    loading.value = false
  } else {
    // 4. 存储Token（根据实际返回字段调整）
    localStorage.setItem('accessToken', data.data.accessToken)
    localStorage.setItem('username', data.data.username)
    // 跳转逻辑
    const targetPath = form.roleId === 0 ? '/manager/home' : '/user/home'
    await router.replace(targetPath) // 等待跳转完成

    ElMessage.success('登录成功')

  }
}

// 新增状态管理
const verifyCode = ref('')
const countdown = ref(0)
let timer = null

// 修改后的忘记密码处理
const handleForgetPassword = () => {
  ElMessageBox.prompt('请输入注册邮箱', '找回密码', {
    confirmButtonText: '发送验证码',
    cancelButtonText: '取消',
    inputPattern: /[\w-]+@([\w-]+\.)+[\w-]+/,
    inputErrorMessage: '邮箱格式不正确'
  }).then(async ({value}) => {
    // 第一步：发送验证码
    try {
      await request.post('/auth/send-verify-code', {email: value})
      ElMessage.success('验证码已发送')
      // 显示验证码输入框
      showVerifyCodeInput(value)
    } catch (error) {
      ElMessage.error(error.message || '发送失败')
    }
  }).catch(() => {
  })
}

// 显示验证码输入框
const showVerifyCodeInput = (email) => {
  // 开始倒计时
  startCountdown()

  ElMessageBox.prompt(
      `验证码已发送至 ${email}，请输入6位验证码`,
      '验证身份',
      {
        confirmButtonText: `确认验证（${countdown.value}s）`,
        cancelButtonText: '重新发送',
        inputPattern: /^\d{6}$/,
        inputErrorMessage: '请输入6位数字验证码',
        // 自定义内容区域
        message: `
        <div style="margin: 10px 0">
          <el-input
            v-model="verifyCode"
            placeholder="请输入6位验证码"
            maxlength="6"
            style="width: 200px"
          />
          <div style="color: #999; margin-top: 8px; font-size: 12px">
            没有收到？60秒后可重新发送
          </div>
        </div>
      `,
        // 动态更新确认按钮文本
        beforeClose: (action, instance, done) => {
          if (action === 'confirm') {
            if (!/^\d{6}$/.test(verifyCode.value)) {
              ElMessage.error('验证码格式错误')
              return false
            }
            // 实际应调用验证接口
            verifyCodeHandler(email, verifyCode.value).then(done)
            return false
          }
          // 点击重新发送
          if (action === 'cancel') {
            if (countdown.value > 0) {
              ElMessage.warning(`${countdown.value}秒后可重新发送`)
              return false
            }
            request.post('/auth/resend-code', {email})
                .then(() => {
                  ElMessage.success('验证码已重新发送')
                  startCountdown()
                  instance.inputValue = '' // 清空输入
                })
            return false
          }
        }
      }
  ).then(() => {
    // 验证通过后的处理
    ElMessageBox.prompt('请输入新密码', '重置密码', {
      inputType: 'password',
      confirmButtonText: '确认重置',
    }).then(({value}) => {
      // 调用重置密码接口
      request.post('/auth/reset-password', {
        email,
        code: verifyCode.value,
        newPassword: value
      }).then(() => {
        ElMessage.success('密码重置成功')
      })
    })
  })
}

// 验证码处理
const verifyCodeHandler = async (email, code) => {
  try {
    await request.post('/auth/verify-code', {email, code})
    return true
  } catch (error) {
    ElMessage.error(error.message || '验证码错误')
    return Promise.reject()
  }
}

// 倒计时逻辑
const startCountdown = (seconds = 60) => {
  countdown.value = seconds
  timer = setInterval(() => {
    if (countdown.value <= 0) {
      clearInterval(timer)
      return
    }
    countdown.value--
  }, 1000)
}

// 初始化时读取记住的用户信息
onMounted(() => {
  const remembered = localStorage.getItem('rememberedUser')
  if (remembered) {
    const user = JSON.parse(remembered)
    form.username = user.username
    form.roleId = user.roleId
    form.rememberMe = true
  }
})
</script>

<style scoped>
.login {
  position: fixed;
  overflow: hidden;
  left: 50%;
  margin-left: -250px;
  top: 50%;
  margin-top: -350px;
  width: 500px;
  min-height: 555px;
  z-index: 1;
  right: 140px;
  -webkit-border-radius: 5px;
  -moz-border-radius: 5px;
  -ms-border-radius: 5px;
  -o-border-radius: 5px;
  border-radius: 5px;
  -webkit-box-shadow: 0px 3px 16px -5px #070707;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1); /* 增加投影 */
  background: rgba(255, 255, 255, 0.98); /* 轻微半透明白色 */
}

.log-close {
  display: block;
  position: absolute;
  top: 12px;
  right: 12px;
  opacity: 1;
}

.log-close:hover .icons {
  transform: rotate(180deg);
}

.log-close .icons {
  opacity: 1;
  transition: all .3s
}

.log-bg {
  width: 100%;
  height: 312px;
  overflow: hidden;
}

.log-logo {
  height: 80px;
  margin: 120px auto 25px;
  text-align: center;
  color: #1fcab3;
  font-weight: bold;
  font-size: 40px;
}

.log-text {
  color: #57d4c3;
  font-size: 13px;
  text-align: center;
  margin: 0 auto;
}

.log-logo, .log-text {
  z-index: 2
}

.icons {
  display: inline-block;
}

.close {
  height: 16px;
  width: 16px;
  background-position: -13px 0;
}

.log-btn {
  width: 100px;
  display: block;
  text-align: left;
  line-height: 50px;
  margin: 0 auto 15px;
  height: 50px;
  color: #fff;
  font-size: 13px;
  -webkit-border-radius: 5px;
  background-color: #3B5999;
  -moz-border-radius: 5px;
  -ms-border-radius: 5px;
  -o-border-radius: 5px;
  border-radius: 5px;
  position: relative;
}

.log-btn:hover, .log-btn:focus {
  color: #fff;
  opacity: .8;
}

.log-email {
  text-align: center;
  max-width: 420px;
  margin: 20px auto 0;
}

.log-email .log-btn {
  background-color: #50c8e3;
  text-align: center;
}

@-webkit-keyframes cloud1 {
  0% {
    left: 200px
  }
  100% {
    left: -130px;
  }
}

@keyframes cloud1 {
  0% {
    left: 200px
  }
  100% {
    left: -130px;
  }
}

@-webkit-keyframes cloud2 {
  0% {
    left: 500px;
  }
  100% {
    left: -90px;
  }
}

@keyframes cloud2 {
  0% {
    left: 500px;
  }
  100% {
    left: -90px;
  }
}

@-webkit-keyframes cloud3 {
  0% {
    left: 620px;
  }
  100% {
    left: -70px;
  }
}

@keyframes cloud3 {
  0% {
    left: 620px;
  }
  100% {
    left: -70px;
  }
}

@-webkit-keyframes cloud4 {
  0% {
    left: 100px;
  }
  100% {
    left: -70px;
  }
}

@keyframes cloud4 {
  0% {
    left: 100px;
  }
  100% {
    left: -70px;
  }
}

/* 调整Element组件样式 */
:deep(.el-input__wrapper) {
  padding: 0 15px !important;
  width: 300px;
  height: 48px;
  box-shadow: 0 0 0 1px #EBEBEB;
  transition: all 0.3s ease;
}

/* 聚焦状态效果 */
:deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #50c8e3 !important;
  border-radius: 5px;
}

:deep(.el-input__inner) {
  font-size: 15px;
  letter-spacing: 0.5px;
  color: #666;
}

:deep(.el-form-item) {
  margin-bottom: 25px;
}

:deep(.el-input) {
  width: 100% !important;
}

:deep(.el-icon) {
  font-size: 18px !important;
  color: #50c8e3; /* 统一图标颜色 */
}

/* 新增样式 */
.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;
}

:deep(.el-radio-group) {
  width: 100%;
  display: flex;
  justify-content: center;
  gap: 30px;
}

:deep(.el-radio) {
  margin-right: 0;
}

:deep(.el-link) {
  font-size: 14px;
}

:deep(.log-btn) {
  line-height: 50px !important; /* 与按钮高度一致 */
  padding-top: 0;
  padding-bottom: 0;
  transition: all 0.3s ease-in-out !important; /* 所有属性过渡动画 */
  transform-origin: center; /* 缩放中心点 */
}

:deep(.log-btn):hover {
  transform: scale(1.05); /* 放大5% */
  box-shadow: 0 2px 12px rgba(80, 227, 206, 0.3); /* 同步增加阴影效果 */
}

/* 登录按钮样式 */
.log-btn {
  width: 100px !important;
  height: 50px !important;
  font-size: 16px !important;
  background-color: #50c8e3 !important;
  border: none !important;
}

.log-btn:hover {
  opacity: 0.9;
  transform: scale(1.02);
  transition: all 0.3s;
}

/* 错误状态 */
:deep(.is-error .el-input__wrapper) {
  box-shadow: 0 0 0 1px #f37474 !important;
}

/* 渐变色背景 */
.background-gradient {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: linear-gradient(
      160deg, #00dfd8 0%, #007cf0 100%
  );
  z-index: 0;
}
</style>