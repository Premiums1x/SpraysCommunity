import { createApp } from 'vue'
import { createPinia } from 'pinia'
import {
  ElAlert, ElAvatar, ElButton, ElCard, ElContainer, ElDatePicker, ElDialog,
  ElDrawer, ElEmpty, ElForm, ElFormItem, ElHeader, ElIcon, ElImage, ElInput,
  ElLoading, ElMain, ElOption, ElPagination, ElPopconfirm, ElResult, ElSelect,
  ElSkeleton, ElSkeletonItem, ElSwitch, ElTable, ElTableColumn, ElTag, ElUpload
} from 'element-plus'
import 'element-plus/dist/index.css'
import { EditPen, Location, Lock, Picture, Plus, Search, User, UserFilled } from '@element-plus/icons-vue'
import App from './App.vue'
import router from './router'
import './style.css'

const app = createApp(App)

const icons = { EditPen, Location, Lock, Picture, Plus, Search, User, UserFilled }
Object.entries(icons).forEach(([name, component]) => app.component(name, component))

const elementComponents = [
  ElAlert, ElAvatar, ElButton, ElCard, ElContainer, ElDatePicker, ElDialog,
  ElDrawer, ElEmpty, ElForm, ElFormItem, ElHeader, ElIcon, ElImage, ElInput,
  ElMain, ElOption, ElPagination, ElPopconfirm, ElResult, ElSelect, ElSkeleton,
  ElSkeletonItem, ElSwitch, ElTable, ElTableColumn, ElTag, ElUpload
]
elementComponents.forEach(component => app.use(component))
app.use(ElLoading)

app.use(createPinia())
app.use(router)
app.mount('#app')
