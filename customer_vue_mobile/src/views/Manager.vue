<template>
  <!-- 移动端整体容器 -->
  <div class="mobile-container">
    <!-- 顶部导航栏 -->
    <van-nav-bar
        title="客户商品管理系统"
        fixed
        placeholder
        safe-area-inset-top
    >
      <template #right>
        <van-popover
            v-model:show="showUserMenu"
            trigger="click"
            :actions="userActions"
            @select="onSelectUserAction"
        >
          <template #reference>
            <div class="user-info">
              <van-icon name="user-circle-o" size="24" color="#fff"/>
              <span class="username">{{ username }}</span>
            </div>
          </template>
        </van-popover>
      </template>
    </van-nav-bar>

    <!-- 页面内容区域 -->
    <div class="content">
      <router-view v-slot="{ Component }">
        <keep-alive>
          <component :is="Component" />
        </keep-alive>
      </router-view>
    </div>

    <!-- 底部导航 -->
    <van-tabbar
        v-model="activeTab"
        route
        fixed
        placeholder
        safe-area-inset-bottom
    >
      <van-tabbar-item
          v-for="tab in tabs"
          :key="tab.path"
          :to="tab.path"
          :icon="tab.icon"
      >
        {{ tab.title }}
      </van-tabbar-item>
    </van-tabbar>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { showConfirmDialog } from 'vant'

const router = useRouter()

// 用户信息
const username = ref(localStorage.getItem('username') || '未登录用户')
const showUserMenu = ref(false)

// 用户操作菜单
const userActions = computed(() => [
  { text: '退出登录', icon: 'revoke' }
])

// 底部导航配置
const tabs = [
  { path: '/manager/home', title: '首页', icon: 'home-o' },
  { path: '/manager/customer', title: '客户', icon: 'friends-o' },
  { path: '/manager/commodity', title: '商品', icon: 'shop-o' },
  { path: '/manager/order', title: '订单', icon: 'orders-o' },
  { path: '/manager/user', title: '用户', icon: 'user-o' }
]

const activeTab = ref(0)

// 用户菜单操作
const onSelectUserAction = (action) => {
  if (action.text === '退出登录') {
    showConfirmDialog({
      title: '退出确认',
      message: '确定要退出登录吗？',
      confirmButtonColor: '#ee0a24',
    }).then(() => {
      localStorage.removeItem('accessToken')
      localStorage.removeItem('username')
      router.replace('/login')
    }).catch(() => {
      // 取消操作
    })
  }
}
</script>

<style lang="less">
@import '~vant/lib/style/var.less';

.mobile-container {
  --van-nav-bar-height: 50px;
  --van-tabbar-height: 60px;

  .user-info {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 0 12px;

    .username {
      color: #fff;
      font-size: 14px;
      max-width: 80px;
      .ellipsis();
    }
  }

  .content {
    padding: 16px;
    min-height: calc(100vh - var(--van-nav-bar-height) - var(--van-tabbar-height));
    background: #f5f7fa;

    @media (max-width: 375px) {
      padding: 12px;
    }
  }

  // 美化导航栏
  .van-nav-bar {
    background: linear-gradient(135deg, #00dfd8, #007cf0);

    &__title {
      color: #fff;
      font-weight: 500;
    }
  }

  // 底部导航美化
  .van-tabbar {
    box-shadow: 0 -2px 12px rgba(0,0,0,0.06);

    &-item {
      &__icon {
        font-size: 22px;
      }

      &__text {
        font-size: 12px;
        margin-top: 4px;
      }

      &--active {
        color: @blue;
      }
    }
  }
}

// 通用样式
.ellipsis() {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>