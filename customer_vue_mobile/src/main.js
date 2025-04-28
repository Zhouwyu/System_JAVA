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
    Collapse, CollapseItem,
    DatePicker, TimePicker
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
    .use(Collapse)
    .use(CollapseItem)
    .use(DatePicker)
    .use(TimePicker)

app.mount('#app')
