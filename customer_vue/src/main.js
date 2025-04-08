import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
// 引入element-plus
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
// 引入icon图标
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
// 国际化
import zhCn from 'element-plus/es/locale/lang/zh-cn'
// 引入全局CSS文件
import './assets/global.css'

const app = createApp(App)

app.use(router)
app.use(ElementPlus, {
    locale: zhCn,
})

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
}

app.mount('#app')


