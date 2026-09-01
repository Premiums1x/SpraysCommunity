<template>
  <main class="auth-page">
    <section class="auth-context">
      <router-link to="/animals" class="auth-brand">🐾 校园流浪动物图鉴</router-link>
      <div>
        <p class="page-kicker">Campus neighbors</p>
        <h1>让每次遇见，<br>都成为照护记录。</h1>
        <p>认识校园里的动物邻居，留下可靠近况，也让后来的人知道如何温柔地接近它们。</p>
      </div>
      <ul>
        <li>公开浏览动物档案</li>
        <li>持续记录校园近况</li>
        <li>匿名发布也能保护隐私</li>
      </ul>
    </section>
    <section class="auth-form-panel">
      <div class="auth-form-wrap">
        <p class="page-kicker">{{ eyebrow }}</p>
        <h2>{{ title }}</h2>
        <p class="auth-description">{{ description }}</p>
        <slot />
        <div class="auth-footer"><slot name="footer" /></div>
      </div>
    </section>
  </main>
</template>

<script setup>
defineProps({
  eyebrow: { type: String, required: true },
  title: { type: String, required: true },
  description: { type: String, required: true }
})
</script>

<style scoped>
.auth-page { min-height: 100vh; display: grid; grid-template-columns: minmax(360px, .9fr) minmax(420px, 1.1fr); background: var(--color-canvas); }
.auth-context { padding: clamp(36px, 6vw, 80px); display: flex; flex-direction: column; justify-content: space-between; background: #244c38; color: #f2f7f3; overflow: hidden; position: relative; }
.auth-context::after { content: '🐾'; position: absolute; right: -34px; bottom: -58px; font-size: 240px; opacity: .06; transform: rotate(-14deg); }
.auth-brand { color: #f2f7f3; font-family: Georgia, 'Songti SC', serif; font-weight: 700; }
.auth-brand:hover { text-decoration: none; }
.auth-context h1 { margin: 14px 0 20px; max-width: 560px; font-family: Georgia, 'Songti SC', serif; font-size: clamp(42px, 5.5vw, 72px); font-weight: 500; line-height: 1.08; letter-spacing: -.04em; }
.auth-context p { max-width: 540px; color: #cfddd3; line-height: 1.85; }
.auth-context .page-kicker { color: #b8d7c2; }
.auth-context ul { display: flex; flex-wrap: wrap; gap: 10px 24px; list-style: none; color: #cfddd3; font-size: 13px; }
.auth-context li::before { content: '·'; margin-right: 8px; color: #e2b07c; }
.auth-form-panel { display: grid; place-items: center; padding: 36px 24px; }
.auth-form-wrap { width: min(420px, 100%); }
.auth-form-wrap h2 { margin: 8px 0; font-family: Georgia, 'Songti SC', serif; font-size: 34px; }
.auth-description { margin-bottom: 30px; color: var(--color-text-muted); line-height: 1.7; }
.auth-footer { margin-top: 22px; color: var(--color-text-muted); text-align: center; font-size: 14px; }
@media (max-width: 800px) {
  .auth-page { grid-template-columns: 1fr; }
  .auth-context { min-height: 300px; padding: 28px 24px; gap: 54px; }
  .auth-context h1 { font-size: 40px; }
  .auth-context ul { display: none; }
}
</style>
