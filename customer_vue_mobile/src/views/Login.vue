<template>
  <!-- 移动端适配容器 -->
  <div class="mobile-container">
    <!-- 头部LOGO -->
    <div class="header">
      <!--      <img class="logo" src="@/assets/logo-mobile.png">-->
      <h2 class="title">客户商品管理系统</h2>
    </div>

    <!-- 登录表单 -->
    <van-form
        ref="loginForm"
        @submit="handleSubmit"
        validate-trigger="onBlur"
        :show-error-message="true"
    >
      <!-- 用户名 -->
      <van-field
          v-model="form.username"
          name="username"
          placeholder="请输入用户名"
          left-icon="user-o"
          clearable
          :rules="rules.username"
      />

      <!-- 密码 -->
      <van-field
          v-model="form.password"
          name="password"
          type="password"
          placeholder="请输入密码"
          left-icon="lock"
          clearable
          :rules="rules.password"
      />

      <!-- 角色选择 -->
      <van-radio-group
          v-model="form.roleId"
          direction="horizontal"
          class="role-group"
      >
        <van-radio :name="1">普通人员</van-radio>
        <van-radio :name="0">管理员</van-radio>
      </van-radio-group>

      <!-- 记住我 -->
      <van-checkbox v-model="form.rememberMe" shape="square">
        记住我
      </van-checkbox>

      <!-- 登录按钮 -->
      <div class="submit-btn">
        <van-button
            round
            block
            type="primary"
            native-type="submit"
            :loading="loading"
            loading-text="登录中..."
        >
          立即登录
        </van-button>
      </div>
    </van-form>
  </div>
</template>

<script setup>
import {ref, reactive} from 'vue'
import {showToast, showDialog} from 'vant'
import request from "@/utils/request"
import router from "@/router/index.js";

// 表单数据（保持不变）
const form = reactive({
  username: '',
  password: '',
  roleId: 0,
  rememberMe: false
})

// 验证规则调整
const rules = {
  username: [
    {required: true, message: '请输入用户名'},
    {validator: (val) => val.length >= 3 && val.length <= 12, message: '长度3-12个字符'}
  ],
  password: [
    {required: true, message: '请输入密码'},
    {validator: (val) => val.length >= 6 && val.length <= 18, message: '长度6-18个字符'}
  ]
}

// 表单引用
const loginForm = ref(null)
const loading = ref(false)

// 提交逻辑调整
const handleSubmit = async () => {
  try {
    // Vant表单验证
    await loginForm.value.validate()

    loading.value = true

    // 记住我逻辑（保持不变）
    if (form.rememberMe) {
      localStorage.setItem('rememberedUser', JSON.stringify({
        username: form.username,
        roleId: form.roleId
      }))
    }

    // 请求逻辑（保持不变）
    const data = await request.post('/auth/login', {
      username: form.username,
      password: form.password,
      roleId: form.roleId
    })
    console.log(data)
    if (data.code !== '200') {
      const errorMap = {
        '401': '用户名或密码错误',
        '402': '没有权限',
        '403': '账户已被禁用'
      }
      const msg = errorMap[data.code] || '登录失败'
      showToast({
        message: msg,
        icon: 'fail',
        duration: 1500,
        position: 'middle',
      })
      return
    }
    // 登录成功处理
    localStorage.setItem('accessToken', data.data.accessToken)
    localStorage.setItem('username', data.data.username)
    showToast({
      message: '登录成功',
      icon: 'success',
      duration: 1500, // 显示1.5秒
      position: 'middle',
      onClose: () => {
        router.replace(form.roleId === 0 ? '/manager/home' : '/404')
      }
    })

  } catch (error) {
    // 错误处理优化
    console.error('请求错误:', error)
    showToast(error.message || '网络异常')
  } finally {
    loading.value = false
  }
}

// 初始化逻辑（保持不变）
</script>

<style lang="less">
@import '~vant/lib/style/var.less';

/* 确保Toast容器正确继承视口 */
body {
  position: relative;
  overflow-x: hidden;
  min-height: 100vh;
}

.mobile-container {
  overflow: visible !important;
  position: static !important;
  min-height: 100vh; /* 确保全屏高度 */
}

.mobile-container {
  padding: 30px 20px;
  min-height: 100vh;
  background: linear-gradient(160deg, #00dfd8 0%, #007cf0 100%);

  .header {
    text-align: center;
    margin-bottom: 40px;

    .logo {
      width: 80px;
      height: 32px;
      margin-bottom: 15px;
    }

    .title {
      color: white;
      font-size: 18px;
      margin: 0;
    }
  }

  /* 表单样式覆盖 */

  .van-form {
    .van-cell {
      border-radius: 8px;
      margin-bottom: 16px;
      background: rgba(255, 255, 255, 0.9);

      &::after {
        display: none;
      }
    }

    .van-field__left-icon {
      margin-right: 8px;
    }
  }

  /* 错误提示样式 */
  .van-field__error-message {
    color: #ee0a24;
    font-size: 12px;
    padding: 4px 0 0 8px;
    text-align: left !important;
  }

  /* 角色选择组 */

  .role-group {
    margin: 15px 0;
    display: flex;
    justify-content: space-around;

    .van-radio {
      background: rgba(255, 255, 255, 0.9);
      padding: 8px 15px;
      border-radius: 20px;
    }
  }

  /* 记住我样式 */

  .van-checkbox {
    margin: 15px 0;
    color: white;

    &__icon--checked .van-icon {
      background: @blue;
      border-color: @blue;
    }
  }

  /* 登录按钮 */

  .submit-btn {
    margin-top: 30px;

    .van-button {
      height: 44px;
      font-size: 16px;
    }
  }
}

@media (max-width: 768px) {
  .van-toast {
    width: 70vw !important;
    min-width: auto !important;
    max-width: 300px !important;
  }
}

.van-toast {
  position: fixed !important;
  left: 50% !important;
  top: 50% !important;
  transform: translate(-50%, -50%) !important;
  z-index: 9999;
  margin: 0 !important;
  min-width: 180px;
  max-width: 80vw;
  box-sizing: border-box;
  backdrop-filter: blur(8px);
  border-radius: 12px !important;
  box-shadow: 0 6px 20px rgba(0,0,0,0.12) !important;
  padding: 18px 24px !important;

  &__text {
    font-size: 15px !important;
    font-weight: 500 !important;
    color: #333 !important;
    line-height: 1.5;
    text-align: center !important;
  }

  &__icon {
    font-size: 36px !important;
    margin-bottom: 12px !important;
  }

  // 成功状态
  &--success {
    background: rgba(255,255,255,0.98) !important;
    border: 1px solid #e6f7e6;
    .van-toast__icon {
      color: #34d399 !important;
    }
  }

  // 失败状态
  &--fail {
    background: rgba(255,255,255,0.98) !important;
    border: 1px solid #ffe6e6;
    .van-toast__icon {
      color: #ef4444 !important;
    }
  }

  @media (max-width: 768px) {
    min-width: 200px;
    max-width: 90%;
    border-radius: 16px !important;
    box-shadow: 0 8px 32px rgba(0,0,0,0.15) !important;

    @supports (padding: max(0px)) {
      padding-bottom: max(12px, env(safe-area-inset-bottom)) !important;
    }
  }
}

// 入场动画修正
@keyframes toastSlideIn {
  from {
    opacity: 0;
    transform: translate(-50%, -40%);
  }
  to {
    opacity: 1;
    transform: translate(-50%, -50%);
  }
}

// 添加入场动画
.van-popup-enter-active {
  animation: toastSlideIn 0.3s ease;
}

@keyframes toastSlideIn {
  from {
    opacity: 0;
    transform: translate(-50%, -50%) translateY(20px);
  }
  to {
    opacity: 1;
    transform: translate(-50%, -50%);
  }
}
</style>