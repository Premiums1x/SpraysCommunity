<template>
  <el-container style="min-height: 100vh">
    <el-header style="background-color: #409eff; display: flex; align-items: center; justify-content: space-between;">
      <div style="display: flex; align-items: center;">
        <h2 style="color: white; margin: 0; margin-right: 40px;">🐾 校园流浪动物图鉴</h2>
        <el-menu
          :default-active="$route.path"
          mode="horizontal"
          background-color="#409eff"
          text-color="#fff"
          active-text-color="#ffd04b"
          router
        >
          <el-menu-item index="/animals">动物图鉴</el-menu-item>
          <el-menu-item index="/checkin">发布打卡</el-menu-item>
          <el-menu-item index="/my-checkins">我的打卡</el-menu-item>
          <el-menu-item v-if="userStore.isAdmin" index="/admin/animals">管理后台</el-menu-item>
        </el-menu>
      </div>
      <div style="display: flex; align-items: center;">
        <span style="color: white; margin-right: 16px;">{{ userStore.userInfo?.nickname || userStore.userInfo?.username }}</span>
        <el-button type="warning" size="small" @click="handleLogout">退出登录</el-button>
      </div>
    </el-header>
    <el-main>
      <router-view />
    </el-main>
  </el-container>
</template>

<script setup>
import { useUserStore } from '../stores/user'
import { useRouter } from 'vue-router'

const userStore = useUserStore()
const router = useRouter()

const handleLogout = () => {
  userStore.logout()
  router.push('/login')
}
</script>
