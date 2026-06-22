<template>
  <div class="animal-list-container">
    <!-- 搜索栏 -->
    <el-card class="search-bar" shadow="never">
      <div class="search-row">
        <el-input
          v-model="searchName"
          placeholder="搜索动物名字"
          prefix-icon="Search"
          clearable
          @clear="handleSearch"
        />
        <el-select v-model="searchType" placeholder="全部类型" clearable>
          <el-option label="全部" value="" />
          <el-option label="猫" :value="1" />
          <el-option label="狗" :value="2" />
        </el-select>
        <el-button class="search-button" type="primary" icon="Search" @click="handleSearch">
          搜索
        </el-button>
      </div>
    </el-card>

    <!-- 动物卡片列表 -->
    <div v-loading="loading" class="animal-grid">
      <el-empty v-if="!loading && animalList.length === 0" description="暂无动物数据" />
      <el-row :gutter="20">
        <el-col
          v-for="animal in animalList"
          :key="animal.id"
          :xs="24"
          :sm="12"
          :md="8"
          :lg="6"
        >
          <el-card
            class="animal-card"
            shadow="hover"
            :body-style="{ padding: '0' }"
            @click="goDetail(animal.id)"
          >
            <div class="animal-cover">
              <img
                v-if="animal.coverImage"
                :src="'/uploads/' + animal.coverImage"
                :alt="animal.name"
              />
              <div v-else class="no-image">
                <el-icon :size="48"><Picture /></el-icon>
                <span>暂无图片</span>
              </div>
            </div>
            <div class="animal-info">
              <h3 class="animal-name">{{ animal.name }}</h3>
              <div class="animal-meta">
                <el-tag
                  :type="animal.type === 1 ? 'warning' : 'success'"
                  size="small"
                >
                  {{ animal.type === 1 ? '🐱 猫' : '🐶 狗' }}
                </el-tag>
                <span class="animal-area">
                  <el-icon><Location /></el-icon>
                  {{ animal.area || '未知区域' }}
                </span>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 分页 -->
    <div class="pagination-wrapper">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[8, 12, 16, 24]"
        layout="total, sizes, prev, pager, next, jumper"
        background
        @current-change="fetchAnimals"
        @size-change="handleSizeChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import request from '../utils/request'

const router = useRouter()

const animalList = ref([])
const loading = ref(false)
const searchName = ref('')
const searchType = ref('')
const currentPage = ref(1)
const pageSize = ref(8)
const total = ref(0)

const fetchAnimals = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value
    }
    if (searchName.value) params.name = searchName.value
    if (searchType.value !== '') params.type = searchType.value
    const res = await request.get('/api/animals', { params })
    animalList.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    // handled by interceptor
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
  fetchAnimals()
}

const handleSizeChange = () => {
  currentPage.value = 1
  fetchAnimals()
}

const goDetail = (id) => {
  router.push(`/animals/${id}`)
}

onMounted(() => {
  fetchAnimals()
})
</script>

<style scoped>
.animal-list-container {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: var(--space-6);
}

/* ── Search Bar ── */
.search-bar {
  border: 1px solid var(--color-gray-100);
  border-radius: var(--radius-lg);
}

.search-row {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  width: 100%;
}

.search-row :deep(.el-input) {
  flex: 1;
  min-width: 0;
}

.search-row :deep(.el-select) {
  width: 160px;
  flex-shrink: 0;
}

.search-button {
  flex-shrink: 0;
}

/* ── Card Grid ── */
.animal-grid {
  min-height: 300px;
}

.animal-grid :deep(.el-row) {
  row-gap: var(--space-5);
}

.animal-card {
  border: 1px solid var(--color-gray-100);
  border-radius: var(--radius-lg);
  cursor: pointer;
  overflow: hidden;
  transition: transform 0.3s var(--ease-out-quart),
              box-shadow 0.3s var(--ease-out-quart);
}

.animal-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-hover);
}

.animal-cover {
  height: 220px;
  overflow: hidden;
  background-color: var(--color-surface);
  display: flex;
  align-items: center;
  justify-content: center;
}

.animal-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.no-image {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: var(--color-ink-muted);
  gap: var(--space-2);
  font-size: var(--text-sm);
}

.animal-info {
  padding: var(--space-4) var(--space-5);
}

.animal-name {
  margin: 0 0 var(--space-3);
  font-size: var(--text-base);
  font-weight: 600;
  color: var(--color-ink);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.animal-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.animal-area {
  font-size: var(--text-sm);
  color: var(--color-ink-muted);
  display: flex;
  align-items: center;
  gap: var(--space-1);
}

/* ── Pagination ── */
.pagination-wrapper {
  display: flex;
  justify-content: center;
  padding-top: var(--space-4);
  padding-bottom: var(--space-4);
}

@media (max-width: 768px) {
  .search-row {
    flex-direction: column;
  }

  .search-row :deep(.el-select) {
    width: 100%;
  }
}
</style>

