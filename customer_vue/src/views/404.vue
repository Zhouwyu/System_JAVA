<template>
  <div class="container">
    <div class="content">
      <!-- 动态背景粒子 -->
      <div class="particles">
        <div class="particle"></div>
        <div class="particle"></div>
        <div class="particle"></div>
        <div class="particle"></div>
        <div class="particle"></div>
      </div>

      <!-- 主要内容 -->
      <div class="error-card">
        <!-- 动态SVG插图 -->
        <svg class="error-illustration" viewBox="0 0 512 512">
          <path fill="#3B82F6" d="M256 48C141.1 48 48 141.1 48 256s93.1 208 208 208 208-93.1 208-208S370.9 48 256 48zm0 384c-97.2 0-176-78.8-176-176S158.8 80 256 80s176 78.8 176 176-78.8 176-176 176z"/>
          <path fill="#3B82F6" d="M256 160c-8.8 0-16 7.2-16 16v128c0 8.8 7.2 16 16 16s16-7.2 16-16V176c0-8.8-7.2-16-16-16z"/>
          <circle fill="#3B82F6" cx="256" cy="336" r="16"/>
        </svg>

        <h1>404</h1>
        <p class="subtitle">页面消失不见啦！</p>
        <p class="description">
          您访问的页面可能已被移动、删除或暂时不可用
        </p>

        <!-- 返回按钮 -->
        <button @click="goHome" class="action-btn">
          <span>返回登录页面</span>
          <svg class="arrow-icon" viewBox="0 0 24 24">
            <path d="M5 12H19M12 5l7 7-7 7"/>
          </svg>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
const goHome = () => {
  // 导航回首页逻辑
  localStorage.removeItem('accessToken')
  localStorage.removeItem('username')
  window.location.href = '/login'
}
</script>

<style scoped>
.container {
  min-height: 100vh;
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 2rem;
}

.content {
  position: relative;
  max-width: 600px;
  width: 100%;
}

/* 动态粒子效果 */
.particles {
  position: absolute;
  width: 100%;
  height: 100%;
}

.particle {
  position: absolute;
  background: #3B82F6;
  border-radius: 50%;
  animation: float 6s infinite ease-in-out;
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-20px); }
}

.error-card {
  background: white;
  border-radius: 1.5rem;
  padding: 3rem 2rem;
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1);
  text-align: center;
  position: relative;
  overflow: hidden;
}

.error-illustration {
  width: 200px;
  height: 200px;
  margin: 0 auto 2rem;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.05); }
}

h1 {
  font-size: 4rem;
  color: #1e293b;
  margin-bottom: 1rem;
}

.subtitle {
  font-size: 1.5rem;
  color: #64748b;
  margin-bottom: 1.5rem;
}

.description {
  color: #94a3b8;
  margin-bottom: 2rem;
}

.action-btn {
  background: #3B82F6;
  color: white;
  border: none;
  padding: 1rem 2rem;
  border-radius: 0.5rem;
  font-size: 1rem;
  cursor: pointer;
  transition: all 0.3s ease;
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
}

.action-btn:hover {
  background: #2563eb;
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(59, 130, 246, 0.3);
}

.arrow-icon {
  width: 20px;
  height: 20px;
  stroke: currentColor;
  stroke-width: 2;
  stroke-linecap: round;
  stroke-linejoin: round;
}

/* 响应式设计 */
@media (max-width: 640px) {
  .error-card {
    padding: 2rem 1.5rem;
  }

  h1 {
    font-size: 3rem;
  }

  .error-illustration {
    width: 150px;
    height: 150px;
  }
}
</style>