import './assets/Global.css'
import './assets/main.css'

import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import {
    Form as VanForm,
    Field as VanField,
    RadioGroup as VanRadioGroup,
    Radio as VanRadio,
    Checkbox as VanCheckbox,
    Button as VanButton,
    Toast,
    Icon,
} from 'vant'

const app = createApp(App)

app.use(router)
app.use(VanForm)
    .use(VanField)
    .use(VanRadioGroup)
    .use(VanRadio)
    .use(VanCheckbox)
    .use(VanButton)
    .use(Toast)
    .use(Icon)

app.mount('#app')
