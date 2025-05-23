import './assets/main.css'

import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
//引入Element-plus
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import '@/assets/icon/iconfont.css'
import '@/assets/base.scss'
//引入VueCookies
import { VueCookies } from 'vue-cookies'


import Verify from './utils/Verify'
import Message from './utils/Message'
import Confirm from './utils/Confirm'
import Utils from './utils/Utils'
// 引入全局函数
import common from '@/libs/globalFunction/common.js'
// 引入自定义的全局配置
import config from '@/config/index.js'
import store from '@/store/index.js'

const app = createApp(App)

app.use(ElementPlus)
app.use(router)
app.use(store)
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
}


//配置全局组件
app.config.globalProperties.Verify=Verify;
app.config.globalProperties.Message=Message;
app.config.globalProperties.Request=Request;
app.config.globalProperties.Confirm=Confirm;
app.config.globalProperties.Utils=Utils;
app.config.globalProperties.VueCookies=VueCookies;
app.config.globalProperties.$config = config
app.config.globalProperties.$common = common
app.mount('#app')
