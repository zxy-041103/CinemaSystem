import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import axios from 'axios'
import './assets/global.css'

Vue.use(ElementUI, { size: 'small' })
Vue.config.productionTip = false

// axios全局配置
axios.defaults.baseURL = ''
Vue.prototype.$http = axios
Vue.prototype.$baseUrl = ''

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')
