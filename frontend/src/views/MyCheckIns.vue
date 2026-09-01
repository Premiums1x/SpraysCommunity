<template>
  <div class="page-shell my-checkins-container">
    <header class="page-intro"><p class="page-kicker">Personal log</p><h1 class="page-heading">我的打卡记录</h1><p class="page-lead">你留下的每条观察，都在补全校园动物的生活轨迹。</p></header>
    <el-card shadow="never" class="checkins-card">
      <template #header>
        <h2 class="page-title">全部记录</h2>
      </template>
      <el-alert v-if="error" :title="error" type="error" show-icon :closable="false" class="state-alert" />
      <div v-loading="loading">
        <el-empty v-if="!loading && !error && checkinList.length === 0" description="还没有打卡记录">
          <el-button type="primary" plain @click="router.push('/checkin')">发布第一次打卡</el-button>
        </el-empty>
        <div v-else class="checkin-list">
          <el-card
            v-for="checkin in checkinList"
            :key="checkin.id"
            class="checkin-item"
            shadow="hover"
          >
            <div class="checkin-header">
              <el-tag
                type="primary"
                size="small"
                class="animal-tag"
                @click="goDetail(checkin.animalId)"
              >
                {{ checkin.animalName || '未知动物' }}
              </el-tag>
              <time class="checkin-time" :datetime="checkin.createTime">{{ formatDateTime(checkin.createTime) }}</time>
            </div>
            <p class="checkin-content">{{ checkin.content }}</p>
          </el-card>
        </div>
      </div>
      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 30]"
          layout="total, sizes, prev, pager, next"
          background
          @current-change="fetchMyCheckins"
          @size-change="handleSizeChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getMyCheckIns } from '../api/checkins'
import { formatDateTime } from '../utils/format'

const router = useRouter()
const checkinList = ref([])
const loading = ref(false)
const error = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const fetchMyCheckins = async () => {
  loading.value = true
  error.value = ''
  try {
    const res = await getMyCheckIns({ page: currentPage.value, size: pageSize.value })
    checkinList.value = res.data.records
    total.value = res.data.total
  } catch (requestError) {
    error.value = requestError.response?.data?.message || requestError.message || '加载失败'
  } finally {
    loading.value = false
  }
}

const handleSizeChange = () => {
  currentPage.value = 1
  fetchMyCheckins()
}

const goDetail = (animalId) => {
  if (animalId) {
    router.push(`/animals/${animalId}`)
  }
}

onMounted(() => {
  fetchMyCheckins()
})
</script>

<style scoped>
.my-checkins-container {
  max-width: 900px;
}
.page-intro { margin: 28px 0 30px; }

.checkins-card {
  border-radius: var(--radius-card);
}

.page-title {
  margin: 0;
  color: var(--color-text);
  font-family: Georgia, 'Songti SC', serif;
}

.checkin-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.checkin-item {
  border-radius: 12px;
  background: var(--color-surface-muted);
}

.checkin-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 10px;
}

.animal-tag {
  cursor: pointer;
}

.checkin-time {
  font-size: 13px;
  color: var(--color-text-muted);
}

.checkin-content {
  margin: 0;
  color: var(--color-text);
  line-height: 1.6;
}
.state-alert { margin-bottom: 18px; }

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}
</style>
