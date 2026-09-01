<template>
  <el-container class="app-frame">
    <el-header class="site-header">
      <router-link to="/animals" class="brand" aria-label="校园流浪动物图鉴首页">
        <span class="brand-mark">🐾</span>
        <span><strong>校园流浪动物图鉴</strong><small>Campus Neighbors</small></span>
      </router-link>
      <nav class="desktop-nav" aria-label="主导航">
        <router-link to="/animals">动物图鉴</router-link>
        <router-link v-if="userStore.isLogin" to="/checkin">发布打卡</router-link>
        <router-link v-if="userStore.isLogin" to="/my-checkins">我的打卡</router-link>
        <router-link v-if="userStore.isAdmin" to="/admin/animals">管理后台</router-link>
      </nav>
      <div class="account-actions">
        <template v-if="userStore.isLogin">
          <span class="account-name">{{ userStore.userInfo?.nickname || userStore.userInfo?.username }}</span>
          <el-button text @click="handleLogout">退出</el-button>
        </template>
        <template v-else>
          <el-button text @click="router.push('/login')">登录</el-button>
          <el-button type="primary" @click="router.push('/register')">加入社区</el-button>
        </template>
      </div>
      <button class="mobile-menu-button" type="button" aria-label="打开导航" @click="drawerVisible = true">
        <MenuIcon />
      </button>
    </el-header>
    <el-main>
      <router-view />
    </el-main>
    <el-drawer v-model="drawerVisible" direction="rtl" size="min(320px, 86vw)" title="导航">
      <nav class="mobile-nav" aria-label="移动端导航" @click="drawerVisible = false">
        <router-link to="/animals">动物图鉴</router-link>
        <router-link v-if="userStore.isLogin" to="/checkin">发布打卡</router-link>
        <router-link v-if="userStore.isLogin" to="/my-checkins">我的打卡</router-link>
        <router-link v-if="userStore.isAdmin" to="/admin/animals">管理后台</router-link>
        <router-link v-if="!userStore.isLogin" to="/login">登录</router-link>
        <router-link v-if="!userStore.isLogin" to="/register">加入社区</router-link>
        <button v-if="userStore.isLogin" type="button" @click="handleLogout">退出登录</button>
      </nav>
    </el-drawer>
  </el-container>
</template>

<script setup>
import { ref } from 'vue'
import { useUserStore } from '../stores/user'
import { useRouter } from 'vue-router'
import { Menu as MenuIcon } from '@element-plus/icons-vue'

const userStore = useUserStore()
const router = useRouter()
const drawerVisible = ref(false)

const handleLogout = () => {
  userStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.app-frame { min-height: 100vh; }
.site-header {
  position: sticky;
  top: 0;
  z-index: 30;
  height: 72px;
  padding: 0 clamp(18px, 4vw, 56px);
  display: grid;
  grid-template-columns: auto 1fr auto;
  align-items: center;
  gap: 32px;
  background: color-mix(in srgb, var(--color-surface) 94%, transparent);
  border-bottom: 1px solid var(--color-border);
  backdrop-filter: blur(12px);
}
.brand { display: flex; align-items: center; gap: 10px; color: var(--color-text); }
.brand:hover { text-decoration: none; }
.brand-mark { font-size: 26px; }
.brand strong, .brand small { display: block; }
.brand strong { font-family: Georgia, 'Songti SC', serif; font-size: 17px; }
.brand small { margin-top: 2px; color: var(--color-text-muted); font-size: 9px; letter-spacing: .16em; text-transform: uppercase; }
.desktop-nav { display: flex; justify-content: center; gap: 28px; }
.desktop-nav a { position: relative; color: var(--color-text-muted); font-size: 14px; font-weight: 650; }
.desktop-nav a:hover { color: var(--color-text); text-decoration: none; }
.desktop-nav a.router-link-active { color: var(--color-brand-strong); }
.desktop-nav a.router-link-active::after { content: ''; position: absolute; left: 0; right: 0; bottom: -25px; height: 2px; background: var(--color-brand); }
.account-actions { display: flex; align-items: center; gap: 8px; }
.account-name { max-width: 120px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; color: var(--color-text-muted); font-size: 13px; }
.mobile-menu-button { display: none; width: 40px; height: 40px; padding: 9px; color: var(--color-text); background: transparent; border: 1px solid var(--color-border); border-radius: 10px; }
.mobile-nav { display: grid; gap: 8px; }
.mobile-nav a, .mobile-nav button { padding: 14px 4px; color: var(--color-text); text-align: left; font: inherit; background: none; border: 0; border-bottom: 1px solid var(--color-border); }
.mobile-nav a:hover { text-decoration: none; }
@media (max-width: 880px) {
  .site-header { grid-template-columns: 1fr auto; height: 64px; }
  .desktop-nav, .account-actions { display: none; }
  .mobile-menu-button { display: inline-grid; place-items: center; }
  .desktop-nav a.router-link-active::after { display: none; }
}
@media (max-width: 480px) {
  .brand strong { font-size: 15px; }
  .brand small { display: none; }
}
</style>
