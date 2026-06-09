<template>
  <div class="checkin-container">
    <el-card class="checkin-card" shadow="never">
      <template #header>
        <h2 class="page-title">📝 发布打卡</h2>
      </template>
      <el-form
        ref="formRef"
        :model="checkinForm"
        :rules="rules"
        label-position="top"
      >
        <el-form-item label="选择动物" prop="animalId">
          <el-select
            v-model="checkinForm.animalId"
            placeholder="请选择要打卡的动物"
            filterable
            style="width: 100%"
            :loading="animalsLoading"
          >
            <el-option
              v-for="animal in animalOptions"
              :key="animal.id"
              :label="animal.name + (animal.type === 1 ? ' 🐱' : ' 🐶')"
              :value="animal.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="打卡内容" prop="content">
          <el-input
            v-model="checkinForm.content"
            type="textarea"
            :rows="6"
            placeholder="记录你与这只小动物的故事吧..."
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="submitLoading" @click="handleSubmit">
            提交打卡
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 提交成功提示 -->
    <el-dialog v-model="showSuccessDialog" title="打卡成功 🎉" width="400px" :close-on-click-modal="false">
      <p>你的打卡已成功提交！接下来你想？</p>
      <template #footer>
        <el-button @click="continueCheckin">继续打卡</el-button>
        <el-button type="primary" @click="goDetail">查看该动物详情</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import request from '../utils/request'
import { ElMessage } from 'element-plus'

const router = useRouter()
const formRef = ref(null)
const submitLoading = ref(false)
const animalsLoading = ref(false)
const animalOptions = ref([])
const showSuccessDialog = ref(false)
const lastAnimalId = ref(null)

const checkinForm = reactive({
  animalId: null,
  content: ''
})

const rules = {
  animalId: [{ required: true, message: '请选择动物', trigger: 'change' }],
  content: [{ required: true, message: '请输入打卡内容', trigger: 'blur' }]
}

const fetchAnimals = async () => {
  animalsLoading.value = true
  try {
    const res = await request.get('/api/animals', { params: { page: 1, size: 100 } })
    animalOptions.value = res.data.records
  } catch (error) {
    // handled by interceptor
  } finally {
    animalsLoading.value = false
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    submitLoading.value = true
    try {
      await request.post('/api/checkins', {
        animalId: checkinForm.animalId,
        content: checkinForm.content
      })
      lastAnimalId.value = checkinForm.animalId
      showSuccessDialog.value = true
    } catch (error) {
      // handled by interceptor
    } finally {
      submitLoading.value = false
    }
  })
}

const continueCheckin = () => {
  showSuccessDialog.value = false
  checkinForm.content = ''
}

const goDetail = () => {
  showSuccessDialog.value = false
  router.push(`/animals/${lastAnimalId.value}`)
}

onMounted(() => {
  fetchAnimals()
})
</script>

<style scoped>
.checkin-container {
  max-width: 700px;
  margin: 0 auto;
}

.checkin-card {
  border-radius: 12px;
}

.page-title {
  margin: 0;
  color: #303133;
}
</style>
