<template>
  <div class="animal-detail-container">
    <div v-loading="loading">
      <!-- 基本信息 -->
      <el-card v-if="animal" class="info-card" shadow="never">
        <el-row :gutter="30">
          <el-col :span="10">
            <div class="detail-cover">
              <img
                v-if="animal.coverImage"
                :src="'/uploads/' + animal.coverImage"
                :alt="animal.name"
              />
              <div v-else class="no-image">
                <el-icon :size="64"><Picture /></el-icon>
                <span>暂无图片</span>
              </div>
            </div>
          </el-col>
          <el-col :span="14">
            <div class="detail-info">
              <h1 class="detail-name">{{ animal.name }}</h1>
              <div class="detail-tags">
                <el-tag
                  :type="animal.type === 1 ? 'warning' : 'success'"
                  size="large"
                >
                  {{ animal.type === 1 ? '🐱 猫' : '🐶 狗' }}
                </el-tag>
                <el-tag type="info" size="large">
                  <el-icon><Location /></el-icon>
                  {{ animal.area || '未知区域' }}
                </el-tag>
              </div>
              <div class="detail-desc">
                <h4>简介</h4>
                <p>{{ animal.description || '暂无描述信息' }}</p>
              </div>
              <el-button type="primary" icon="EditPen" @click="showCheckinDialog = true">
                发布打卡
              </el-button>
            </div>
          </el-col>
        </el-row>
      </el-card>

      <!-- 打卡时间轴 -->
      <el-card class="timeline-card" shadow="never">
        <template #header>
          <div class="timeline-header">
            <h3>📋 近况时间轴</h3>
          </div>
        </template>
        <el-empty v-if="checkins.length === 0" description="还没有打卡记录，快来发布第一条吧~" />
        <el-timeline v-else>
          <el-timeline-item
            v-for="checkin in checkins"
            :key="checkin.id"
            :timestamp="checkin.createTime"
            placement="top"
          >
            <el-card shadow="hover" class="checkin-card">
              <div class="checkin-content">
                <div class="checkin-user">
                  <el-avatar :size="32" icon="UserFilled" />
                  <span class="checkin-nickname">{{ checkin.nickname || checkin.username || '匿名用户' }}</span>
                </div>
                <p class="checkin-text">{{ checkin.content }}</p>
              </div>
            </el-card>
          </el-timeline-item>
        </el-timeline>
        <div v-if="checkins.length < checkinTotal" class="load-more">
          <el-button :loading="checkinLoading" @click="loadMoreCheckins">加载更多</el-button>
        </div>
      </el-card>
    </div>

    <!-- 发布打卡对话框 -->
    <el-dialog v-model="showCheckinDialog" title="发布打卡" width="500px">
      <el-form :model="checkinForm" :rules="checkinRules" ref="checkinFormRef">
        <el-form-item label="打卡内容" prop="content">
          <el-input
            v-model="checkinForm.content"
            type="textarea"
            :rows="4"
            placeholder="记录你与这只小动物的故事吧..."
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCheckinDialog = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="submitCheckin">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import request from '../utils/request'
import { ElMessage } from 'element-plus'

const route = useRoute()
const animalId = route.params.id

const animal = ref(null)
const loading = ref(false)
const checkins = ref([])
const checkinTotal = ref(0)
const checkinPage = ref(1)
const checkinLoading = ref(false)
const showCheckinDialog = ref(false)
const submitLoading = ref(false)
const checkinFormRef = ref(null)

const checkinForm = reactive({
  content: ''
})

const checkinRules = {
  content: [{ required: true, message: '请输入打卡内容', trigger: 'blur' }]
}

const fetchAnimalDetail = async () => {
  loading.value = true
  try {
    const res = await request.get(`/api/animals/${animalId}`)
    animal.value = res.data
  } catch (error) {
    // handled by interceptor
  } finally {
    loading.value = false
  }
}

const fetchCheckins = async (reset = false) => {
  if (reset) {
    checkinPage.value = 1
    checkins.value = []
  }
  checkinLoading.value = true
  try {
    const res = await request.get(`/api/animals/${animalId}/checkins`, {
      params: { page: checkinPage.value, size: 10 }
    })
    checkins.value = reset ? res.data.records : [...checkins.value, ...res.data.records]
    checkinTotal.value = res.data.total
  } catch (error) {
    // handled by interceptor
  } finally {
    checkinLoading.value = false
  }
}

const loadMoreCheckins = () => {
  checkinPage.value++
  fetchCheckins()
}

const submitCheckin = async () => {
  if (!checkinFormRef.value) return
  await checkinFormRef.value.validate(async (valid) => {
    if (!valid) return
    submitLoading.value = true
    try {
      await request.post('/api/checkins', {
        animalId: Number(animalId),
        content: checkinForm.content
      })
      ElMessage.success('打卡成功！')
      showCheckinDialog.value = false
      checkinForm.content = ''
      fetchCheckins(true)
    } catch (error) {
      // handled by interceptor
    } finally {
      submitLoading.value = false
    }
  })
}

onMounted(() => {
  fetchAnimalDetail()
  fetchCheckins(true)
})
</script>

<style scoped>
.animal-detail-container {
  max-width: 1000px;
  margin: 0 auto;
}

.info-card {
  margin-bottom: 24px;
  border-radius: 12px;
}

.detail-cover {
  height: 320px;
  border-radius: 8px;
  overflow: hidden;
  background-color: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
}

.detail-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.no-image {
  display: flex;
  flex-direction: column;
  align-items: center;
  color: #c0c4cc;
  gap: 8px;
}

.detail-info {
  padding: 10px 0;
}

.detail-name {
  font-size: 28px;
  color: #303133;
  margin: 0 0 16px;
}

.detail-tags {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.detail-desc {
  margin-bottom: 20px;
}

.detail-desc h4 {
  color: #606266;
  margin: 0 0 8px;
}

.detail-desc p {
  color: #909399;
  line-height: 1.6;
  margin: 0;
}

.timeline-card {
  border-radius: 12px;
}

.timeline-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.timeline-header h3 {
  margin: 0;
  color: #303133;
}

.checkin-card {
  border-radius: 8px;
}

.checkin-content {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.checkin-user {
  display: flex;
  align-items: center;
  gap: 10px;
}

.checkin-nickname {
  font-weight: 500;
  color: #303133;
  font-size: 14px;
}

.checkin-text {
  margin: 0;
  color: #606266;
  line-height: 1.6;
}

.load-more {
  text-align: center;
  margin-top: 16px;
}
</style>
