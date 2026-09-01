<template>
  <div class="page-shell animal-list-container">
    <header class="gallery-intro">
      <div>
        <p class="page-kicker">Campus field guide</p>
        <h1 class="page-heading">认识我们的动物邻居</h1>
        <p class="page-lead">每一份档案都来自校园里的真实遇见。先了解它们的活动区域和个性，再以合适的距离表达善意。</p>
      </div>
      <div class="gallery-count"><strong>{{ total }}</strong><span>份公开档案</span></div>
    </header>

    <el-card class="search-bar" shadow="never">
      <div class="search-row">
        <el-input v-model="searchName" placeholder="搜索动物名字" :prefix-icon="Search" clearable @clear="handleSearch" />
        <el-select v-model="searchType" placeholder="全部类型" clearable>
          <el-option label="全部" value="" />
          <el-option label="猫" :value="1" />
          <el-option label="狗" :value="2" />
        </el-select>
        <el-button class="search-button" type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
      </div>
    </el-card>

    <el-alert v-if="error" :title="error" type="error" show-icon :closable="false" class="state-alert">
      <template #default><el-button link type="primary" @click="fetchAnimals">重新加载</el-button></template>
    </el-alert>
    <div v-if="loading" class="animal-grid" aria-label="正在加载动物档案">
      <el-skeleton v-for="index in 8" :key="index" animated class="animal-skeleton">
        <template #template>
          <el-skeleton-item variant="image" class="skeleton-image" />
          <div class="skeleton-copy"><el-skeleton-item variant="h3" /><el-skeleton-item variant="text" /></div>
        </template>
      </el-skeleton>
    </div>
    <el-empty v-else-if="!error && animalList.length === 0" description="没有找到符合条件的动物">
      <el-button type="primary" plain @click="resetSearch">清除筛选</el-button>
    </el-empty>
    <div v-else-if="!error" class="animal-grid">
      <article
        v-for="animal in animalList"
        :key="animal.id"
        class="animal-card"
        role="link"
        tabindex="0"
        @click="goDetail(animal.id)"
        @keyup.enter="goDetail(animal.id)"
      >
        <div class="animal-cover">
          <img v-if="animal.coverImage" :src="animalImageUrl(animal.coverImage)" :alt="animal.name" loading="lazy" />
          <div v-else class="no-image"><el-icon :size="48"><Picture /></el-icon><span>暂无图片</span></div>
        </div>
        <div class="animal-info">
          <h2 class="animal-name">{{ animal.name }}</h2>
          <p v-if="animal.personalityTags" class="animal-traits">{{ animal.personalityTags }}</p>
          <div class="animal-meta">
            <span class="animal-type">{{ animal.type === 1 ? '猫' : '狗' }}</span>
            <span class="animal-area"><el-icon><Location /></el-icon>{{ animal.area || '未知区域' }}</span>
          </div>
        </div>
      </article>
    </div>

    <div v-if="!loading && total > 0" class="pagination-wrapper">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[8, 12, 16, 24]"
        layout="total, sizes, prev, pager, next"
        background
        @current-change="fetchAnimals"
        @size-change="handleSizeChange"
      />
    </div>
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Search } from '@element-plus/icons-vue'
import { usePagedAnimals } from '../composables/usePagedAnimals'
import { animalImageUrl } from '../utils/format'

const router = useRouter()
const {
  animalList, loading, error, searchName, searchType, currentPage, pageSize, total,
  fetchAnimals, search: handleSearch, changePageSize: handleSizeChange
} = usePagedAnimals(8)

const goDetail = id => router.push(`/animals/${id}`)
const resetSearch = () => {
  searchName.value = ''
  searchType.value = ''
  handleSearch()
}

onMounted(fetchAnimals)
</script>

<style scoped>
.animal-list-container { min-height: calc(100vh - 150px); }
.gallery-intro { display: flex; align-items: end; justify-content: space-between; gap: 28px; margin: 28px 0 30px; }
.gallery-count { flex: 0 0 auto; padding: 14px 18px; border-left: 2px solid var(--color-accent); }
.gallery-count strong, .gallery-count span { display: block; }
.gallery-count strong { font-family: Georgia, serif; font-size: 30px; }
.gallery-count span { margin-top: 2px; color: var(--color-text-muted); font-size: 12px; }
.search-bar { margin-bottom: 28px; border-radius: var(--radius-card); background: var(--color-surface); }
.search-row { display: grid; grid-template-columns: minmax(0, 1.4fr) minmax(180px, .7fr) 130px; gap: 14px; }
.search-row :deep(.el-select), .search-button { width: 100%; }
.state-alert { margin-bottom: 20px; }
.animal-grid { min-height: 300px; display: grid; grid-template-columns: repeat(4, minmax(0, 1fr)); gap: 22px; }
.animal-card { overflow: hidden; background: var(--color-surface); border: 1px solid var(--color-border); border-radius: var(--radius-card); cursor: pointer; transition: transform .2s ease, box-shadow .2s ease, border-color .2s ease; }
.animal-card:hover, .animal-card:focus-visible { transform: translateY(-3px); border-color: color-mix(in srgb, var(--color-brand) 55%, var(--color-border)); box-shadow: var(--shadow-soft); }
.animal-cover { aspect-ratio: 4 / 3; overflow: hidden; display: grid; place-items: center; background: var(--color-surface-muted); }
.animal-cover img { width: 100%; height: 100%; object-fit: cover; transition: transform .35s ease; }
.animal-card:hover img { transform: scale(1.025); }
.no-image { display: grid; justify-items: center; gap: 8px; color: var(--color-text-muted); }
.animal-info { padding: 17px 18px 19px; }
.animal-name { margin-bottom: 13px; overflow: hidden; color: var(--color-text); font-family: Georgia, 'Songti SC', serif; font-size: 20px; white-space: nowrap; text-overflow: ellipsis; }
.animal-traits { margin: -5px 0 13px; overflow: hidden; color: var(--color-text-muted); font-size: 12px; white-space: nowrap; text-overflow: ellipsis; }
.animal-meta { display: flex; align-items: center; justify-content: space-between; gap: 8px; }
.animal-type { padding: 4px 9px; color: var(--color-brand-strong); background: var(--color-brand-soft); border-radius: 999px; font-size: 12px; font-weight: 700; }
.animal-area { min-width: 0; display: flex; align-items: center; gap: 4px; overflow: hidden; color: var(--color-text-muted); font-size: 13px; white-space: nowrap; text-overflow: ellipsis; }
.animal-skeleton { overflow: hidden; border: 1px solid var(--color-border); border-radius: var(--radius-card); }
.skeleton-image { width: 100%; height: 190px; border-radius: 0; }
.skeleton-copy { display: grid; gap: 12px; padding: 16px; }
.pagination-wrapper { display: flex; justify-content: center; margin-top: 36px; padding-bottom: 20px; }
@media (max-width: 980px) { .animal-grid { grid-template-columns: repeat(3, minmax(0, 1fr)); } }
@media (max-width: 740px) {
  .gallery-intro { align-items: start; }
  .gallery-count { display: none; }
  .search-row { grid-template-columns: 1fr; }
  .animal-grid { grid-template-columns: repeat(2, minmax(0, 1fr)); gap: 14px; }
  .pagination-wrapper { justify-content: start; overflow-x: auto; }
}
@media (max-width: 470px) { .animal-grid { grid-template-columns: 1fr; } }
</style>
