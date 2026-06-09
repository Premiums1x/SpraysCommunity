<template>
  <div class="animal-list-container">
    <!-- 搜索栏 -->
    <el-card class="search-bar" shadow="never">
      <el-row :gutter="16" align="middle">
        <el-col :span="8">
          <el-input
            v-model="searchName"
            placeholder="搜索动物名字"
            prefix-icon="Search"
            clearable
            @clear="handleSearch"
          />
        </el-col>
        <el-col :span="6">
          <el-select v-model="searchType" placeholder="全部类型" clearable style="width: 100%">
            <el-option label="全部" value="" />
            <el-option label="猫" :value="1" />
            <el-option label="狗" :value="2" />
          </el-select>
        </el-col>
        <el-col :span="4">
          <el-button type="primary" icon="Search" @click="handleSearch">搜索</el-button>
        </el-col>
      </el-row>
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
}

.search-bar {
  margin-bottom: 20px;
  border-radius: 8px;
}

.animal-grid {
  min-height: 400px;
}

.animal-card {
  margin-bottom: 20px;
  border-radius: 12px;
  cursor: pointer;
  transition: transform 0.3s, box-shadow 0.3s;
  overflow: hidden;
}

.animal-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.1);
}

.animal-cover {
  height: 200px;
  overflow: hidden;
  background-color: #f5f7fa;
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
  color: #c0c4cc;
  gap: 8px;
}

.animal-info {
  padding: 16px;
}

.animal-name {
  margin: 0 0 10px;
  font-size: 16px;
  color: #303133;
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
  font-size: 13px;
  color: #909399;
  display: flex;
  align-items: center;
  gap: 4px;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 24px;
  padding-bottom: 20px;
}
</style>
