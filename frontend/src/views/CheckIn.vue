<template>
  <div class="page-shell checkin-container">
    <aside class="checkin-intro">
      <p class="page-kicker">Share a sighting</p>
      <h1 class="page-heading">记录一次校园偶遇</h1>
      <p class="page-lead">尽量写清时间、位置与精神状态。可靠的小细节，比一句“今天也很好”更能帮助后续照护。</p>
      <div class="notice"><strong>温柔提醒</strong><span>不要公开精确窝点，也不要为了拍照追赶或投喂不适合的食物。</span></div>
    </aside>
    <el-card class="checkin-card" shadow="never">
      <template #header>
        <h2 class="page-title">填写近况</h2>
      </template>
      <CheckInForm show-animal-select @submitted="handleSubmitted" />
    </el-card>

    <!-- 提交成功提示 -->
    <el-dialog v-model="showSuccessDialog" title="打卡成功" width="min(400px, 92vw)" :close-on-click-modal="false">
      <p>这条记录已经加入动物的近况时间轴。</p>
      <template #footer>
        <el-button @click="continueCheckin">继续打卡</el-button>
        <el-button type="primary" @click="goDetail">查看该动物详情</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import CheckInForm from '../components/CheckInForm.vue'

const router = useRouter()
const showSuccessDialog = ref(false)
const lastAnimalId = ref(null)

const handleSubmitted = animalId => {
  lastAnimalId.value = animalId
  showSuccessDialog.value = true
}

const continueCheckin = () => {
  showSuccessDialog.value = false
}

const goDetail = () => {
  showSuccessDialog.value = false
  router.push(`/animals/${lastAnimalId.value}`)
}

</script>

<style scoped>
.checkin-container {
  display: grid;
  grid-template-columns: minmax(280px, .8fr) minmax(420px, 1.2fr);
  gap: clamp(28px, 6vw, 72px);
  align-items: start;
  padding-top: 42px;
}

.checkin-card {
  border-radius: var(--radius-card);
  box-shadow: var(--shadow-soft);
}

.page-title {
  margin: 0;
  color: var(--color-text);
  font-family: Georgia, 'Songti SC', serif;
  font-size: 24px;
}
.notice { margin-top: 34px; padding: 18px 0; border-top: 1px solid var(--color-border); border-bottom: 1px solid var(--color-border); }
.notice strong, .notice span { display: block; }
.notice strong { margin-bottom: 6px; color: var(--color-accent); font-size: 13px; }
.notice span { color: var(--color-text-muted); font-size: 13px; line-height: 1.7; }
@media (max-width: 780px) { .checkin-container { grid-template-columns: 1fr; padding-top: 20px; } }
</style>
