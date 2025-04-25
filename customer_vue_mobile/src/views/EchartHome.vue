<template>
  <div class="mobile-home">
    <!-- 顶部导航 -->
    <van-nav-bar
        title="运营看板"
        fixed
        placeholder
        safe-area-inset-top
    >
      <template #right>
        <div class="date-info">
          {{ currentDate }}
        </div>
      </template>
    </van-nav-bar>

    <!-- 主内容区 -->
    <div class="content">
      <!-- 数据概览 -->
      <div class="data-overview">
        <van-grid :gutter="16" :column-num="2">
          <van-grid-item>
            <div class="data-card blue">
              <van-icon name="orders-o" size="24"/>
              <div class="data-content">
                <div class="value">1,234</div>
                <div class="label">今日订单</div>
              </div>
            </div>
          </van-grid-item>
          <van-grid-item>
            <div class="data-card green">
              <van-icon name="cash-back-record" size="24"/>
              <div class="data-content">
                <div class="value">¥89,500</div>
                <div class="label">今日销售额</div>
              </div>
            </div>
          </van-grid-item>
        </van-grid>
      </div>

      <!-- 快捷入口 -->
      <div class="quick-actions">
        <h3 class="section-title">常用功能</h3>
        <van-grid :gutter="20" :column-num="4">
          <van-grid-item
              v-for="(action, index) in quickActions"
              :key="index"
              @click="handleAction(action)"
          >
            <div class="action-item">
              <van-icon :name="action.icon" size="28"/>
              <div class="action-name">{{ action.name }}</div>
            </div>
          </van-grid-item>
        </van-grid>
      </div>

      <!-- 数据趋势 -->
      <div class="data-trend">
        <h3 class="section-title">销售趋势</h3>
        <div class="chart-container">
          <!-- 这里可以接入图表库 -->
          <van-empty description="图表加载中" image-size="80"/>
        </div>
      </div>

      <!-- 系统公告 -->
      <div class="notice">
        <h3 class="section-title">最新公告</h3>
        <van-cell
            v-for="(notice, index) in notices"
            :key="index"
            :title="notice.title"
            :label="notice.date"
            is-link
        >
          <template #extra>
            <div class="notice-content">
              {{ notice.content }}
            </div>
          </template>
        </van-cell>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

// 当前日期
const currentDate = computed(() => {
  const date = new Date()
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    weekday: 'long'
  })
})

// 快捷操作
const quickActions = ref([
  { name: '新建订单', icon: 'plus' },
  { name: '客户管理', icon: 'friends-o' },
  { name: '商品入库', icon: 'logistics' },
  { name: '数据报表', icon: 'chart-trending-o' }
])

// 系统公告
const notices = ref([
  {
    title: '系统维护通知',
    date: '2024-06-25',
    content: '计划于今晚23:00-24:00进行系统维护升级，请提前保存工作内容。'
  },
  {
    title: '新功能上线',
    date: '2024-06-24',
    content: '新增智能报表模块，支持多维度数据分析。'
  }
])

const handleAction = (action) => {
  console.log('触发操作:', action.name)
}
</script>

<style lang="less">
@import '~vant/lib/style/var.less';

.mobile-home {
  --van-nav-bar-height: 50px;
  --van-grid-item-content-padding: 0;

  .date-info {
    color: @gray-7;
    font-size: 12px;
    padding-right: 12px;
  }

  .content {
    padding: 16px;
    background: #f8f9fa;
  }

  // 数据卡片
  .data-card {
    padding: 16px;
    border-radius: 12px;
    color: white;
    display: flex;
    align-items: center;
    gap: 12px;

    &.blue {
      background: linear-gradient(135deg, #00dfd8, #007cf0);
    }

    &.green {
      background: linear-gradient(135deg, #00d87c, #00b07c);
    }

    .data-content {
      .value {
        font-size: 20px;
        font-weight: 600;
        line-height: 1.2;
      }

      .label {
        font-size: 12px;
        opacity: 0.9;
      }
    }
  }

  // 功能入口
  .quick-actions {
    margin-top: 24px;

    .action-item {
      text-align: center;

      .van-icon {
        color: @blue;
        margin-bottom: 8px;
      }

      .action-name {
        font-size: 12px;
        color: @gray-8;
      }
    }
  }

  // 趋势图表
  .data-trend {
    margin-top: 24px;

    .chart-container {
      background: white;
      border-radius: 12px;
      padding: 16px;
      box-shadow: 0 2px 8px rgba(0,0,0,0.05);
    }
  }

  // 系统公告
  .notice {
    margin-top: 24px;

    .van-cell {
      margin-bottom: 8px;
      border-radius: 8px;
      box-shadow: 0 2px 8px rgba(0,0,0,0.05);

      &::after {
        display: none;
      }
    }

    .notice-content {
      color: @gray-7;
      font-size: 13px;
      .ellipsis();
    }
  }

  // 通用样式
  .section-title {
    color: @gray-8;
    font-size: 16px;
    margin: 16px 0;
    padding-left: 8px;
    position: relative;

    &::before {
      content: '';
      position: absolute;
      left: 0;
      top: 50%;
      transform: translateY(-50%);
      width: 3px;
      height: 16px;
      background: @blue;
      border-radius: 2px;
    }
  }

  .ellipsis() {
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
}
</style>