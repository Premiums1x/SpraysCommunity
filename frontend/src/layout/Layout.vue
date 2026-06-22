<template>
  <div class="app-shell">
    <header class="app-header">
      <div class="header-inner">
        <router-link to="/animals" class="brand">
          <span class="brand-mark">🐾</span>
          <span class="brand-text">校园动物图鉴</span>
        </router-link>

        <nav class="nav-links">
          <router-link to="/animals" class="nav-link" :class="{ active: $route.path === '/animals' }">
            动物图鉴
          </router-link>
          <router-link to="/checkin" class="nav-link" :class="{ active: $route.path === '/checkin' }">
            发布打卡
          </router-link>
          <router-link to="/my-checkins" class="nav-link" :class="{ active: $route.path === '/my-checkins' }">
            我的打卡
          </router-link>
          <router-link
            v-if="userStore.isAdmin"
            to="/admin/animals"
            class="nav-link nav-link--admin"
            :class="{ active: $route.path === '/admin/animals' }"
          >
            管理后台
          </router-link>
        </nav>

        <div class="user-zone">
          <span class="user-name">{{ userStore.userInfo?.nickname || userStore.userInfo?.username }}</span>
          <button class="btn-logout" @click="handleLogout">退出</button>
        </div>
      </div>
    </header>

    <main class="app-main">
      <router-view />
    </main>
  </div>
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

<style scoped>
.app-shell {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: var(--color-bg);
}

/* ── Header ── */
.app-header {
  position: sticky;
  top: 0;
  z-index: 100;
  background: oklch(1.00 0 0 / 0.85);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border-bottom: 1px solid var(--color-gray-100);
}

.header-inner {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 var(--space-6);
  height: 56px;
  display: flex;
  align-items: center;
  gap: var(--space-8);
}

.brand {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  text-decoration: none;
  color: var(--color-ink);
  flex-shrink: 0;
}

.brand-mark {
  font-size: 1.25rem;
  line-height: 1;
}

.brand-text {
  font-weight: 650;
  font-size: var(--text-md);
  letter-spacing: -0.01em;
}

/* ── Nav ── */
.nav-links {
  display: flex;
  align-items: center;
  gap: var(--space-1);
  flex: 1;
}

.nav-link {
  padding: var(--space-2) var(--space-4);
  border-radius: var(--radius-full);
  font-size: var(--text-sm);
  font-weight: 500;
  color: var(--color-ink-secondary);
  text-decoration: none;
  transition: all 0.2s var(--ease-out-quart);
}

.nav-link:hover {
  color: var(--color-ink);
  background-color: var(--color-surface);
}

.nav-link.active {
  color: var(--color-primary);
  background-color: var(--color-primary-pale);
}

.nav-link--admin.active {
  color: var(--color-accent);
  background-color: var(--color-accent-light);
}

/* ── User ── */
.user-zone {
  display: flex;
  align-items: center;
  gap: var(--space-4);
  flex-shrink: 0;
}

.user-name {
  font-size: var(--text-sm);
  color: var(--color-ink-secondary);
  font-weight: 500;
}

.btn-logout {
  padding: var(--space-2) var(--space-3);
  border: 1px solid var(--color-gray-200);
  border-radius: var(--radius-full);
  background: transparent;
  color: var(--color-ink-muted);
  font-size: var(--text-xs);
  font-family: var(--font-sans);
  cursor: pointer;
  transition: all 0.2s var(--ease-out-quart);
}

.btn-logout:hover {
  color: var(--color-danger);
  border-color: var(--color-danger);
}

/* ── Main ── */
.app-main {
  flex: 1;
  padding: var(--space-6);
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
}

@media (max-width: 768px) {
  .header-inner {
    gap: var(--space-4);
    padding: 0 var(--space-4);
  }

  .brand-text {
    display: none;
  }

  .nav-link {
    padding: var(--space-2) var(--space-3);
    font-size: var(--text-xs);
  }

  .user-name {
    display: none;
  }

  .app-main {
    padding: var(--space-4);
  }
}
</style>
