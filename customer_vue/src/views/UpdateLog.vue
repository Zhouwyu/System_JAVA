<template>
  <div class="changelog-container">
    <el-card shadow="never" class="log-card">
      <template #header>
        <div class="card-header">
          <h2>系统更新日志</h2>
          <el-tag type="info">当前版本: v1.3.1</el-tag>
        </div>
      </template>

      <el-timeline>
        <el-timeline-item
            v-for="(item, index) in changelog"
            :key="index"
            :timestamp="item.date"
            placement="top"
            :color="getTimelineColor(index)"
        >
          <div class="version-block">
            <div class="version-header">
              <span class="version-tag">{{ item.version }}</span>
              <span class="publish-date">{{ item.date }}</span>
            </div>
            <el-scrollbar height="220px">
              <div class="changes-list">
                <div
                    v-for="(change, cIndex) in item.changes"
                    :key="cIndex"
                    class="change-item"
                >
                  <el-tag
                      :type="getChangeType(change.type)"
                      size="small"
                      effect="plain"
                      class="type-tag"
                  >
                    {{ change.type }}
                  </el-tag>
                  <span class="change-desc">{{ change.desc }}</span>
                </div>
              </div>
            </el-scrollbar>
          </div>
        </el-timeline-item>
      </el-timeline>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue';

const changelog = ref([
  {
    version: 'v1.3.1',
    date: '2025-04-14',
    changes: [
      { type: '新增', desc: '禁止修改用户名逻辑' },
      { type: '新增', desc: '新建订单下单时可以自定义成交价功能' },
      { type: '新增', desc: '标记出货成功后不能修改订单功能' },
      { type: '新增', desc: '订单出货时间校验功能' },
      { type: '优化', desc: '订单详情显示逻辑' },

    ]
  },
  {
    version: 'v1.3.0',
    date: '2025-04-08',
    changes: [
      { type: '新增', desc: '用户管理模块' },
      { type: '优化', desc: '界面加载性能提升30%' },
      { type: '修复', desc: '表单验证逻辑错误' },

    ]
  },
  {
    version: 'v1.2.0',
    date: '2025-03-30',
    changes: [
      { type: '新增', desc: '订单管理模块' },
      { type: '修复', desc: '订单查看详情问题' },
      { type: '新增', desc: '标记出货功能' },
      { type: '新增', desc: '数据导出功能' }
    ]
  },
  {
    version: 'v1.1.0',
    date: '2025-03-27',
    changes: [
      { type: '新增', desc: '商品管理模块' },
      { type: '优化', desc: '移动端适配方案' },
      { type: '修复', desc: '上传图片显示问题' },
    ]
  },
  {
    version: 'v1.0.0',
    date: '2025-03-25',
    changes: [
      { type: '新增', desc: '客户管理模块' },
      { type: '优化', desc: '客户管理界面交互体验' }
    ]
  }
]);

const getChangeType = (type) => {
  const typeMap = {
    '新增': 'success',
    '优化': 'primary',
    '修复': 'warning',
    '功能': 'info'
  };
  return typeMap[type] || 'info';
};

const getTimelineColor = (index) => {
  return index === 0 ? '#409EFF' : '#E4E7ED';
};
</script>

<style scoped>
.changelog-container {
  max-width: 800px;
  margin: 20px auto;
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
}

.version-block {
  background: #f8f9fc;
  border-radius: 8px;
  padding: 15px;
  transition: box-shadow 0.3s;
}

.version-block:hover {
  box-shadow: 0 2px 12px rgba(0,0,0,0.08);
}

.version-header {
  margin-bottom: 12px;
  border-bottom: 1px solid #ebeef5;
  padding-bottom: 8px;
}

.version-tag {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin-right: 15px;
}

.publish-date {
  font-size: 13px;
  color: #909399;
}

.changes-list {
  padding: 8px 0;
}

.change-item {
  display: flex;
  align-items: center;
  padding: 8px 0;
  border-radius: 4px;
  transition: background 0.3s;
}

.change-item:hover {
  background: rgba(64,158,255,0.05);
}

.type-tag {
  margin-right: 12px;
  width: 50px;
  justify-content: center;
}

.change-desc {
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
}
</style>