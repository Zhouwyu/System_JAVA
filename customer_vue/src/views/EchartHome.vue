<template>
  <div class="dashboard-container">
    <!-- 顶部统计指标 -->
    <div class="statistic-row">
      <el-card shadow="hover" v-for="(item,index) in stats" :key="index">
        <div class="stat-item">
          <div class="icon-box" :style="{background:item.color}">
            <component :is="item.icon" style="width:1.2em;height:1.2em"/>
          </div>
          <div class="data">
            <span class="value">{{ item.value }}</span>
            <span class="label">{{ item.label }}</span>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 主体内容 -->
    <div class="main-content">
      <!-- 左栏 -->
      <div class="left-column">
        <!-- 订单趋势图 -->
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <div class="card-header">
              <span>订单趋势</span>
              <el-select v-model="chartDateType" size="small">
                <el-option label="近7天" value="7d"/>
                <el-option label="本月" value="1m"/>
              </el-select>
            </div>
          </template>
          <div ref="chart" style="height:300px"></div>
        </el-card>

        <!-- 近期订单 -->
        <el-card shadow="hover" class="order-list">
          <template #header>
            <span>最新订单</span>
            <el-button type="text" @click="gotoOrder">查看全部</el-button>
          </template>
          <el-table :data="recentOrders" style="width:100%">
            <el-table-column prop="orderNo" label="订单号" width="180"/>
            <el-table-column prop="customer" label="客户"/>
            <el-table-column prop="amount" label="金额" width="100"/>
            <el-table-column label="状态" width="120">
              <template #default="{row}">
                <el-tag :type="statusMap[row.status].type" size="small">
                  {{ statusMap[row.status].label }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </div>

      <!-- 右栏 -->
      <div class="right-column">
        <!-- 系统公告 -->
        <el-card shadow="hover" class="notice-card">
          <template #header>
            <div class="notice-header">
              <i class="el-icon-megaphone"></i>
              <span>系统公告</span>
            </div>
          </template>
          <ul class="notice-list">
            <li v-for="notice in notices" :key="notice.id" class="notice-item">
              <div class="notice-title">
                <el-tag size="mini" effect="dark" type="danger" v-if="notice.important">重要</el-tag>
                {{ notice.title }}
                <span class="notice-time">{{ notice.time }}</span>
              </div>
              <div class="notice-content">{{ notice.content }}</div>
            </li>
          </ul>
        </el-card>

        <!-- 客户动态 -->
        <el-card shadow="hover" class="customer-activity">
          <template #header>
            <span>客户动态</span>
          </template>
          <el-timeline>
            <el-timeline-item
                v-for="activity in activities"
                :key="activity.time"
                :timestamp="activity.time">
              {{ activity.content }}
            </el-timeline-item>
          </el-timeline>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup>
import {ref, onMounted} from 'vue'
import * as echarts from 'echarts'
import {
  ShoppingCart,
  Wallet,
  User,
  TrendCharts
} from '@element-plus/icons-vue'

// 统计指标数据
const stats = ref([
  {
    icon: ShoppingCart,
    label: '今日订单',
    value: '1,234',
    color: '#409EFF'
  },
  {
    icon: Wallet,
    label: '待处理订单',
    value: '56',
    color: '#E6A23C'
  },
  {
    icon: User,
    label: '新增客户',
    value: '89',
    color: '#67C23A'
  },
  {
    icon: TrendCharts,
    label: '本月销售额',
    value: '¥234,567',
    color: '#F56C6C'
  }
])

// 订单状态映射
const statusMap = {
  1: {label: '待付款', type: 'warning'},
  2: {label: '已付款', type: 'success'},
  3: {label: '已发货', type: ''},
  4: {label: '已完成', type: 'info'}
}

// 系统公告数据
const notices = ref([
  {
    id: 1,
    title: '系统维护通知',
    content: '计划于8月15日 00:00-02:00进行系统维护升级',
    time: '2023-08-10',
    important: true
  },
  {
    id: 2,
    title: '订单流程优化',
    content: '新增批量发货功能，支持电子面单直接打印',
    time: '2023-08-09'
  }
])

// 客户动态数据
const activities = ref([
  {
    content: '客户「XX公司」创建了新订单 #123456',
    time: '2023-08-10 14:00'
  },
  {
    content: '客户「张三」更新了收货地址',
    time: '2023-08-10 10:30'
  }
])

// 图表相关
const chart = ref(null)
const chartDateType = ref('7d')
let chartInstance = null

// 初始化图表
const initChart = () => {
  chartInstance = echarts.init(chart.value)
  const option = {
    tooltip: {trigger: 'axis'},
    xAxis: {
      type: 'category',
      data: ['08-04', '08-05', '08-06', '08-07', '08-08', '08-09', '08-10']
    },
    yAxis: {type: 'value'},
    series: [{
      data: [120, 200, 150, 80, 70, 110, 130],
      type: 'line',
      smooth: true,
      areaStyle: {},
      color: '#409EFF'
    }]
  }
  chartInstance.setOption(option)
}

onMounted(() => {
  initChart()
  window.addEventListener('resize', () => chartInstance.resize())
})

// 近期订单数据
const recentOrders = ref([
  {orderNo: 'DD20230810001', customer: '张三', amount: '¥1,234.00', status: 1},
  {orderNo: 'DD20230810002', customer: '李四科技', amount: '¥5,678.00', status: 2},
  {orderNo: 'DD20230810003', customer: '王五集团', amount: '¥9,876.00', status: 3}
])
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
  background: #f5f7fa;
}

.statistic-row {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 15px;
  margin-bottom: 20px;
}

.stat-item {
  display: flex;
  align-items: center;
  padding: 15px;
}

.icon-box {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
  color: white;
}

.data {
  display: flex;
  flex-direction: column;
}

.value {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 4px;
}

.label {
  color: #909399;
  font-size: 12px;
}

.main-content {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.notice-header {
  display: flex;
  align-items: center;
  font-weight: bold;

  i {
    margin-right: 8px;
    color: #F56C6C;
  }
}

.notice-list {
  list-style: none;
  padding: 0;
}

.notice-item {
  padding: 12px 0;
  border-bottom: 1px solid #ebeef5;

  &:last-child {
    border-bottom: none;
  }
}

.notice-title {
  font-weight: 500;
  margin-bottom: 6px;
  display: flex;
  align-items: center;

  .el-tag {
    margin-right: 8px;
  }
}

.notice-time {
  color: #909399;
  font-size: 12px;
  margin-left: auto;
}

.notice-content {
  color: #606266;
  font-size: 13px;
  line-height: 1.5;
}

.customer-activity {
  margin-top: 20px;
}

.order-list {
  margin-top: 20px;
}

@media (max-width: 1200px) {
  .main-content {
    grid-template-columns: 1fr;
  }
}
</style>