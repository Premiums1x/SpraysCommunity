<template>
  <div class="page-shell animal-detail-container">
    <el-skeleton v-if="loading" animated :rows="8" class="detail-skeleton" />
    <el-result v-else-if="error" icon="error" title="档案暂时无法打开" :sub-title="error">
      <template #extra><el-button type="primary" @click="loadPage">重新加载</el-button></template>
    </el-result>
    <template v-else-if="animal">
      <section class="profile-card">
        <div class="detail-cover">
          <img v-if="animal.coverImage" :src="animalImageUrl(animal.coverImage)" :alt="animal.name" />
          <div v-else class="no-image"><el-icon :size="64"><Picture /></el-icon><span>暂无图片</span></div>
        </div>
        <div class="detail-info">
          <p class="page-kicker">Animal profile · #{{ animal.id }}</p>
          <h1>{{ animal.name }}</h1>
          <div class="detail-tags">
            <span>{{ animal.type === 1 ? '校园猫' : '校园犬' }}</span>
            <span><el-icon><Location /></el-icon>{{ animal.area || '区域未知' }}</span>
          </div>
          <div class="passport-grid">
            <div><small>性别</small><strong>{{ genderLabel(animal.gender) }}</strong></div>
            <div><small>绝育</small><strong>{{ animal.sterilized ? '已确认' : '未确认' }}</strong></div>
            <div><small>健康</small><strong :class="`health-${animal.healthStatus || 'HEALTHY'}`">{{ healthLabel(animal.healthStatus) }}</strong></div>
            <div><small>首次发现</small><strong>{{ animal.firstSeenDate || '待补充' }}</strong></div>
          </div>
          <dl v-if="animal.aliases || animal.activeTime" class="profile-notes">
            <div v-if="animal.aliases"><dt>常用别名</dt><dd>{{ animal.aliases }}</dd></div>
            <div v-if="animal.activeTime"><dt>活跃时段</dt><dd>{{ animal.activeTime }}</dd></div>
          </dl>
          <div v-if="personalityTags.length" class="personality-tags">
            <span v-for="tag in personalityTags" :key="tag">{{ tag }}</span>
          </div>
          <div class="detail-desc">
            <h2>关于它</h2>
            <p>{{ animal.description || '这份档案还在持续完善，欢迎通过打卡补充近期观察。' }}</p>
          </div>
          <el-button v-if="userStore.isLogin" type="primary" :icon="EditPen" @click="showCheckinDialog = true">发布打卡</el-button>
          <el-button v-else type="primary" plain @click="goLogin">登录后打卡</el-button>
        </div>
      </section>

      <section class="timeline-section">
        <div class="section-heading">
          <div><p class="page-kicker">Recent sightings</p><h2>近况时间轴</h2></div>
          <span>{{ checkinTotal }} 条记录</span>
        </div>
        <div v-loading="checkinLoading && checkins.length === 0" class="timeline-body">
          <el-empty v-if="!checkinLoading && checkins.length === 0" description="还没有打卡记录">
            <el-button v-if="userStore.isLogin" type="primary" plain @click="showCheckinDialog = true">记录第一次遇见</el-button>
          </el-empty>
          <ol v-else class="timeline-list">
            <li v-for="checkin in checkins" :key="checkin.id" class="timeline-item">
              <div class="timeline-dot" />
              <article>
                <header>
                  <div class="checkin-user">
                    <el-avatar :size="34" :src="checkin.userAvatar ? animalImageUrl(checkin.userAvatar) : ''" icon="UserFilled" />
                    <strong>{{ checkin.userDisplayName || '匿名用户' }}</strong>
                  </div>
                  <time :datetime="checkin.createTime">{{ formatDateTime(checkin.createTime) }}</time>
                </header>
                <p>{{ checkin.content }}</p>
              </article>
            </li>
          </ol>
          <div v-if="checkins.length < checkinTotal" class="load-more">
            <el-button :loading="checkinLoading" @click="loadMoreCheckins">加载更多</el-button>
          </div>
        </div>
      </section>
    </template>

    <el-dialog v-model="showCheckinDialog" title="发布打卡" width="min(500px, 92vw)">
      <CheckInForm :animal-id="animalId" @submitted="handleCheckInSubmitted" />
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { EditPen } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { getAnimal } from '../api/animals'
import { getAnimalCheckIns } from '../api/checkins'
import CheckInForm from '../components/CheckInForm.vue'
import { useUserStore } from '../stores/user'
import { animalImageUrl, formatDateTime } from '../utils/format'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const animalId = route.params.id
const animal = ref(null)
const loading = ref(false)
const error = ref('')
const checkins = ref([])
const checkinTotal = ref(0)
const checkinPage = ref(1)
const checkinLoading = ref(false)
const showCheckinDialog = ref(false)
const personalityTags = computed(() => (animal.value?.personalityTags || '').split(/[,，]/).map(tag => tag.trim()).filter(Boolean))
const genderLabel = gender => ({ 1: '公', 2: '母' }[gender] || '未知')
const healthLabel = status => ({ HEALTHY: '状态良好', OBSERVE: '需要观察', NEEDS_HELP: '需要帮助' }[status] || '状态良好')

const fetchAnimalDetail = async () => {
  const response = await getAnimal(animalId)
  animal.value = response.data
}
const fetchCheckins = async (reset = false) => {
  if (reset) {
    checkinPage.value = 1
    checkins.value = []
  }
  checkinLoading.value = true
  try {
    const response = await getAnimalCheckIns(animalId, { page: checkinPage.value, size: 10 })
    checkins.value = reset ? response.data.records : [...checkins.value, ...response.data.records]
    checkinTotal.value = response.data.total
  } finally {
    checkinLoading.value = false
  }
}
const loadPage = async () => {
  loading.value = true
  error.value = ''
  try {
    await fetchAnimalDetail()
    await fetchCheckins(true)
  } catch (requestError) {
    error.value = requestError.response?.data?.message || requestError.message || '请稍后重试'
  } finally {
    loading.value = false
  }
}
const loadMoreCheckins = async () => {
  checkinPage.value += 1
  try {
    await fetchCheckins()
  } catch {
    checkinPage.value -= 1
  }
}
const handleCheckInSubmitted = () => {
  ElMessage.success('打卡成功')
  showCheckinDialog.value = false
  fetchCheckins(true)
}
const goLogin = () => router.push({ path: '/login', query: { redirect: route.fullPath } })

onMounted(loadPage)
</script>

<style scoped>
.animal-detail-container { padding-top: 20px; }
.detail-skeleton { max-width: 900px; margin: 30px auto; padding: 30px; background: var(--color-surface); border: 1px solid var(--color-border); }
.profile-card { display: grid; grid-template-columns: minmax(300px, 1.05fr) minmax(320px, .95fr); overflow: hidden; background: var(--color-surface); border: 1px solid var(--color-border); border-radius: 24px; box-shadow: var(--shadow-soft); }
.detail-cover { min-height: 480px; display: grid; place-items: center; overflow: hidden; background: var(--color-surface-muted); }
.detail-cover img { width: 100%; height: 100%; object-fit: cover; }
.no-image { display: grid; justify-items: center; gap: 10px; color: var(--color-text-muted); }
.detail-info { padding: clamp(30px, 5vw, 62px); align-self: center; }
.detail-info h1 { margin: 10px 0 18px; font-family: Georgia, 'Songti SC', serif; font-size: clamp(40px, 5vw, 64px); font-weight: 500; letter-spacing: -.04em; }
.detail-tags { display: flex; flex-wrap: wrap; gap: 8px; }
.detail-tags span { display: inline-flex; align-items: center; gap: 5px; padding: 6px 10px; color: var(--color-text-muted); background: var(--color-surface-muted); border-radius: 999px; font-size: 13px; }
.detail-desc { margin: 32px 0; padding-top: 26px; border-top: 1px solid var(--color-border); }
.detail-desc h2 { margin-bottom: 9px; font-size: 14px; }
.detail-desc p { color: var(--color-text-muted); line-height: 1.85; white-space: pre-wrap; }
.passport-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 1px; margin-top: 28px; overflow: hidden; background: var(--color-border); border: 1px solid var(--color-border); border-radius: 12px; }
.passport-grid div { padding: 13px 14px; background: var(--color-surface); }
.passport-grid small, .passport-grid strong { display: block; }
.passport-grid small { margin-bottom: 5px; color: var(--color-text-muted); font-size: 11px; }
.passport-grid strong { font-size: 14px; }
.health-OBSERVE { color: #a5662f; }
.health-NEEDS_HELP { color: #bd4949; }
.profile-notes { margin-top: 20px; }
.profile-notes div { display: grid; grid-template-columns: 78px 1fr; gap: 10px; padding: 7px 0; font-size: 13px; }
.profile-notes dt { color: var(--color-text-muted); }
.profile-notes dd { margin: 0; }
.personality-tags { display: flex; flex-wrap: wrap; gap: 7px; margin-top: 18px; }
.personality-tags span { padding: 5px 9px; color: var(--color-brand-strong); background: var(--color-brand-soft); border-radius: 999px; font-size: 12px; }
.timeline-section { margin-top: 34px; padding: clamp(24px, 4vw, 42px); background: var(--color-surface); border: 1px solid var(--color-border); border-radius: var(--radius-card); }
.section-heading { display: flex; align-items: end; justify-content: space-between; gap: 16px; padding-bottom: 24px; border-bottom: 1px solid var(--color-border); }
.section-heading h2 { margin-top: 6px; font-family: Georgia, 'Songti SC', serif; font-size: 28px; }
.section-heading > span { color: var(--color-text-muted); font-size: 13px; }
.timeline-body { min-height: 160px; padding-top: 24px; }
.timeline-list { list-style: none; }
.timeline-item { position: relative; display: grid; grid-template-columns: 20px 1fr; gap: 12px; padding-bottom: 22px; }
.timeline-item::before { content: ''; position: absolute; left: 5px; top: 12px; bottom: -2px; width: 1px; background: var(--color-border); }
.timeline-item:last-child::before { display: none; }
.timeline-dot { z-index: 1; width: 11px; height: 11px; margin-top: 8px; background: var(--color-brand); border: 3px solid var(--color-surface); border-radius: 50%; box-shadow: 0 0 0 1px var(--color-brand); }
.timeline-item article { padding: 18px; background: var(--color-surface-muted); border-radius: 12px; }
.timeline-item header { display: flex; align-items: center; justify-content: space-between; gap: 16px; }
.checkin-user { display: flex; align-items: center; gap: 10px; }
.checkin-user strong { font-size: 14px; }
.timeline-item time { color: var(--color-text-muted); font-size: 12px; }
.timeline-item p { margin-top: 13px; color: var(--color-text); line-height: 1.75; white-space: pre-wrap; }
.load-more { text-align: center; }
@media (max-width: 760px) {
  .profile-card { grid-template-columns: 1fr; }
  .detail-cover { min-height: 0; aspect-ratio: 4 / 3; }
  .detail-info { padding: 28px 22px 32px; }
  .timeline-section { padding: 22px 16px; }
  .timeline-item header { align-items: start; flex-direction: column; gap: 8px; }
}
</style>
