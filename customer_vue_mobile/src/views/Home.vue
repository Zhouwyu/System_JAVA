<template>
  <!-- 移动端首页布局 -->
  <div class="mobile-home">
    <!-- 导航栏 -->
    <van-nav-bar
        title="首页"
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
      <!-- 欢迎标题 -->
      <h2 class="welcome-title">欢迎使用后台管理系统</h2>

      <!-- 功能卡片网格 -->
      <van-grid
          :gutter="16"
          :column-num="2"
          :border="false"
          class="features-grid"
      >
        <van-grid-item
            v-for="(feat, index) in features"
            :key="index"
            class="feature-card"
        >
          <div class="card-content">
            <h3>{{ feat.title }}</h3>
            <p>{{ feat.desc }}</p>
          </div>
        </van-grid-item>
      </van-grid>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'

const router = useRouter()

onMounted(() => {
  const token = localStorage.getItem('accessToken')
  if (!token) {
    showToast('请先登录')
    router.push('/login')
  }
})

// 保持原有 features 数据不变
const features = ref([
  {title: '智能订单中心', desc: '自动化订单分单规则+多平台订单聚合+异常订单智能拦截'},
  {title: '支付对账系统', desc: '多渠道支付集成+自动对账核销+支付差错智能处理'},
  {title: '物流监控平台', desc: '多快递公司对接+电子面单自动生成+实时物流轨迹追踪'},
  {title: '库存联动系统', desc: '实时库存水位预警+自动同步渠道库存+智能预售占库机制'},
  {title: '售后管理模块', desc: '智能退换货审批+补偿方案自动生成+供应商逆向物流对接'},
  {title: '客户自助门户', desc: '订单生命周期查询+电子发票自助申请+物流催单服务'},
  // {title: '订单分析引擎', desc: '爆款商品预测模型+客户购买行为分析+渠道销售效能评估'},
  // {title: '多端协同系统', desc: 'ERP系统深度集成+移动端审核流程+智能客服工单对接'},
  // {title: '合规审计中心', desc: '订单全流程追溯+敏感操作留痕审计+电子数据存证对接'},
  // {title: '促销管理系统', desc: '智能优惠券核销+满减规则引擎+跨平台促销策略同步'},
  // {title: '跨境订单模块', desc: '多语言订单处理+自动关税计算+跨境支付合规管理'},
  // {title: 'B2B订货系统', desc: '客户分级定价策略+电子合同签章+账期信用智能管控'},
  // {title: '智能拆单系统', desc: '多仓库就近分单+包裹最优组合算法+运输成本预测'},
  // {title: '虚拟商品管理', desc: '电子凭证自动发放+核销状态实时同步+使用期限提醒'},
  // {title: '供应链看板', desc: '供应商交付时效监控+采购需求智能预测+产能波动可视化预警'},
])

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
</script>

<style lang="less">
@import '~vant/lib/style/var.less';

.mobile-home {
  --van-nav-bar-height: 50px;
  --van-grid-item-content-padding: 0;

  .content {
    padding: 16px;
    min-height: 100vh;
    background: #f5f7fa;
  }

  .welcome-title {
    color: @gray-8;
    font-size: 20px;
    text-align: center;
    margin: 24px 0;
    font-weight: 500;
  }

  .features-grid {
    margin: 0 -8px;

    .van-grid-item {
      padding: 0 8px;
      margin-bottom: 16px;
    }
  }

  .feature-card {
    .card-content {
      background: linear-gradient(135deg, #00dfd8, #007cf0);
      border-radius: 12px;
      padding: 16px;
      width: 100%;
      height: 100%;
      color: white;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
      transition: transform 0.2s ease;

      h3 {
        font-size: 16px;
        margin-bottom: 8px;
        line-height: 1.4;
        font-weight: 500;
      }

      p {
        font-size: 12px;
        line-height: 1.5;
        opacity: 0.9;
        display: -webkit-box;
        -webkit-box-orient: vertical;
        -webkit-line-clamp: 3;
        overflow: hidden;
      }
    }

    &:active .card-content {
      transform: scale(0.98);
    }
  }

  @media (max-width: 375px) {
    .card-content {
      padding: 12px;

      h3 {
        font-size: 15px;
      }

      p {
        font-size: 11px;
      }
    }
  }
}
.date-info {
  color: @gray-8;
  font-size: 12px;
  padding-right: 12px;
}

</style>