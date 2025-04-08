<template>
  <div>
    <!--   头部 -->
    <div style="height: 60px; background: linear-gradient(to right, #00dfd8, #007cf0); display: flex; align-items: center">
      <div style="width: 360px; display: flex; align-items: center; padding-left: 20px">
        <img style="width: 100px; height: 40px" src="@/assets/logo.png">
        <span style="font-size: 24px; color: white; margin-left: 15px">客户商品管理系统</span>
      </div>
      <div style="flex: 1"></div>
      <div style="width: fit-content; display: flex; align-items: center; padding-right: 10px">
<!--        <img src="@/assets/dyy.png" style="width: 40px; height: 40px">-->
        <span style="color: white; margin-left: 5px">{{ username }}</span>
      </div>
    </div>

    <!--    下面部分-->
    <div style="display: flex">

      <!--  左侧导航菜单    -->
      <div style="width: 200px; border-right: 1px solid #ddf0f4; min-height: calc(100vh - 60px) ">
        <!-- 给当前点击并处于的位置高亮       -->
        <el-menu router :default-active="router.currentRoute.value.path" style="border: 0">
          <el-menu-item index="/manager/home">
            <el-icon>
              <HomeFilled/>
            </el-icon>
            系统首页
          </el-menu-item>
          <el-menu-item index="/manager/customer">
            <el-icon>
              <User/>
            </el-icon>
            客户管理
          </el-menu-item>
<!--          <el-sub-menu index="1">-->
<!--            <template #title>-->
<!--              <el-icon>-->
<!--                <User/>-->
<!--              </el-icon>-->
<!--              <span>用户管理</span>-->
<!--            </template>-->
<!--              <el-menu-item index="/manager/employee">员工信息</el-menu-item>-->
<!--          </el-sub-menu>-->

          <el-menu-item index="/manager/commodity">
            <el-icon><Goods /></el-icon>
            商品管理
          </el-menu-item>
          <el-menu-item index="/manager/order">
            <el-icon><Tickets /></el-icon>
            订单管理
          </el-menu-item>
          <el-menu-item index="/manager/user">
            <el-icon><UserFilled /></el-icon>
            用户管理
          </el-menu-item>
          <el-menu-item index="/manager/updatelog">
            <el-icon><DataLine /></el-icon>
            更新日志
          </el-menu-item>
          <el-menu-item @click="logout">
            <el-icon>
              <CircleCloseFilled/>
            </el-icon>
            退出登录
          </el-menu-item>
        </el-menu>
      </div>

      <!--  右侧展示区域    -->
      <div style="flex: 1; width: 0; background-color: #ebf1f3">
        <!--   子路由页面显示位置     -->
        <RouterView/>
      </div>
    </div>

  </div>
</template>

<script setup>

import router from "@/router/index.js";
import {onMounted, ref} from "vue";
import {ElMessage, ElMessageBox} from "element-plus";

// 响应式用户名
const username = ref('')

// 组件挂载时读取用户名
onMounted(() => {
  username.value = localStorage.getItem('username') || '未登录用户'
})

const logout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '退出提示', {
    confirmButtonText: '确定退出',
    cancelButtonText: '取消',
    type: 'warning',
    confirmButtonClass: 'el-button--danger'
  }).then(() => {
    // 用户确认退出
    localStorage.removeItem('accessToken')
    localStorage.removeItem('username')
    location.href = '/login'
  }).catch(() => {
    // 用户取消退出
    ElMessage.info('已取消退出')
  })
}

</script>