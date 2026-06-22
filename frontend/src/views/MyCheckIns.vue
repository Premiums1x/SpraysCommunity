<template>
  <div class="my-checkins-container">
    <el-card shadow="never" class="checkins-card">
      <template #header>
        <h2 class="page-title">📋 我的打卡记录</h2>
      </template>
      <div v-loading="loading">
        <el-empty v-if="!loading && checkinList.length === 0" description="还没有打卡记录哦~" />
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
              <span class="checkin-time">{{ checkin.createTime }}</span>
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
import request from '../utils/request'

const router = useRouter()
const checkinList = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const fetchMyCheckins = async () => {
  loading.value = true
  try {
    const res = await request.get('/api/checkins/my', {
      params: { page: currentPage.value, size: pageSize.value }
    })
    checkinList.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    // handled by interceptor
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
  max-width: 800px;
  margin: 0 auto;
}

.checkins-card {
  border: 1px solid var(--color-gray-100);
}

.page-title {
  margin: 0;
  color: var(--color-ink);
  font-size: var(--text-xl);
  font-weight: 650;
}

.checkin-list {
  display: flex;
  flex-direction: column;
  gap: var(--space-3);
}

.checkin-item {
  border: 1px solid var(--color-gray-100);
  border-radius: var(--radius-md);
  transition: box-shadow 0.2s var(--ease-out-quart);
}

.checkin-item:hover {
  box-shadow: var(--shadow-sm);
}

.checkin-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: var(--space-3);
}

.animal-tag {
  cursor: pointer;
  font-weight: 500;
}

.checkin-time {
  font-size: var(--text-xs);
  color: var(--color-ink-muted);
}

.checkin-content {
  margin: 0;
  color: var(--color-ink-secondary);
  line-height: 1.7;
  font-size: var(--text-sm);
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: var(--space-6);
}
</style>
