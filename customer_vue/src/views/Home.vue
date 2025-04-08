<template>
  <!-- 主内容区 -->
  <main class="content">
    <h1>欢迎使用后台管理系统</h1>
    <div class="features">
      <el-card v-for="(feat, index) in features" :key="index">
        <h3>{{ feat.title }}</h3>
        <p>{{ feat.desc }}</p>
      </el-card>
    </div>
  </main>

</template>

<script setup>
import {onMounted, ref} from 'vue'
import {ElMessage} from 'element-plus'


onMounted(() => {
  const token = localStorage.getItem('accessToken')
  if (!token) {
    ElMessage.error('请先登录')
    this.$router.push('/login')
  }
})


// 功能列表数据
const features = ref([
  {title: '智能订单中心', desc: '自动化订单分单规则+多平台订单聚合+异常订单智能拦截'},
  {title: '支付对账系统', desc: '多渠道支付集成+自动对账核销+支付差错智能处理'},
  {title: '物流监控平台', desc: '多快递公司对接+电子面单自动生成+实时物流轨迹追踪'},
  {title: '库存联动系统', desc: '实时库存水位预警+自动同步渠道库存+智能预售占库机制'},
  {title: '售后管理模块', desc: '智能退换货审批+补偿方案自动生成+供应商逆向物流对接'},
  {title: '客户自助门户', desc: '订单生命周期查询+电子发票自助申请+物流催单服务'},
  {title: '订单分析引擎', desc: '爆款商品预测模型+客户购买行为分析+渠道销售效能评估'},
  {title: '多端协同系统', desc: 'ERP系统深度集成+移动端审核流程+智能客服工单对接'},
  {title: '合规审计中心', desc: '订单全流程追溯+敏感操作留痕审计+电子数据存证对接'},
  {title: '促销管理系统', desc: '智能优惠券核销+满减规则引擎+跨平台促销策略同步'},
  {title: '跨境订单模块', desc: '多语言订单处理+自动关税计算+跨境支付合规管理'},
  {title: 'B2B订货系统', desc: '客户分级定价策略+电子合同签章+账期信用智能管控'},
  {title: '智能拆单系统', desc: '多仓库就近分单+包裹最优组合算法+运输成本预测'},
  {title: '虚拟商品管理', desc: '电子凭证自动发放+核销状态实时同步+使用期限提醒'},
  {title: '供应链看板', desc: '供应商交付时效监控+采购需求智能预测+产能波动可视化预警'},
])


</script>

<style scoped>
.logo img {
  height: 40px;
  max-width: 120px;
}

.content {
  margin-top: 80px;
  padding: 2rem;
}

.features {
  display: grid;
  grid-template-columns: repeat(5, minmax(300px, 1fr));
  gap: 2rem;
  margin-top: 2rem;
}

:deep(.el-card) {
  background: linear-gradient(
      160deg, #00dfd8 0%, #007cf0 100%
  );
  color: white !important;
  border: none;
  border-radius: 12px !important;
  transition: all 0.3s ease;
  min-height: 180px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

/* 卡片悬停效果 */
:deep(.el-card:hover) {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.2);
}

/* 卡片标题样式 */
:deep(.el-card h3) {
  color: white !important;
  font-size: 1.5rem;
  margin-bottom: 0.5rem;
}

/* 卡片描述样式 */
:deep(.el-card p) {
  color: rgba(255, 255, 255, 0.9) !important;
  line-height: 1.6;
}

/* 响应式布局 */
@media (max-width: 1200px) {
  .features {
    grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  }
}

@media (max-width: 768px) {
  .features {
    grid-template-columns: 1fr;
    max-width: 500px;
    margin: 0 auto;
  }
}
</style>