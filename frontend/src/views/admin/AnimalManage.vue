<template>
  <div class="animal-manage-container">
    <el-card shadow="never" class="manage-card">
      <template #header>
        <div class="card-header">
          <h2 class="page-title">🏠 动物档案管理</h2>
          <el-button type="primary" icon="Plus" @click="handleAdd">新增动物</el-button>
        </div>
      </template>

      <!-- 搜索栏 -->
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

      <!-- 表格 -->
      <el-table :data="animalList" v-loading="loading" stripe style="width: 100%">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="name" label="名字" width="120" />
        <el-table-column label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="row.type === 1 ? 'warning' : 'success'" size="small">
              {{ row.type === 1 ? '猫' : '狗' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="area" label="常驻区域" width="150" />
        <el-table-column label="封面" width="100">
          <template #default="{ row }">
            <el-image
              v-if="row.coverImage"
              :src="'/uploads/' + row.coverImage"
              style="width: 50px; height: 50px; border-radius: 6px;"
              fit="cover"
              :preview-src-list="['/uploads/' + row.coverImage]"
            />
            <span v-else style="color: #c0c4cc;">无</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="录入时间" width="180" />
        <el-table-column label="操作" fixed="right" width="160">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
            <el-popconfirm
              title="确认删除此动物档案？"
              confirm-button-text="确认"
              cancel-button-text="取消"
              @confirm="handleDelete(row.id)"
            >
              <template #reference>
                <el-button type="danger" link size="small">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          background
          @current-change="fetchAnimals"
          @size-change="handleSizeChange"
        />
      </div>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑动物' : '新增动物'"
      width="min(720px, 94vw)"
      @close="resetForm"
    >
      <el-form
        ref="formRef"
        :model="animalForm"
        :rules="formRules"
        label-width="80px"
      >
        <el-form-item label="名字" prop="name">
          <el-input v-model="animalForm.name" placeholder="请输入动物名字" />
        </el-form-item>
        <el-form-item label="类型" prop="type">
          <el-select v-model="animalForm.type" placeholder="请选择类型" style="width: 100%">
            <el-option label="猫" :value="1" />
            <el-option label="狗" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="区域" prop="area">
          <el-input v-model="animalForm.area" placeholder="请输入常驻区域" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input
            v-model="animalForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入描述信息"
          />
        </el-form-item>
        <div class="form-grid">
          <el-form-item label="别名">
            <el-input v-model="animalForm.aliases" placeholder="多个别名用逗号分隔" maxlength="255" />
          </el-form-item>
          <el-form-item label="性别">
            <el-select v-model="animalForm.gender" style="width: 100%">
              <el-option label="未知" :value="0" />
              <el-option label="公" :value="1" />
              <el-option label="母" :value="2" />
            </el-select>
          </el-form-item>
          <el-form-item label="健康状态">
            <el-select v-model="animalForm.healthStatus" style="width: 100%">
              <el-option label="状态良好" value="HEALTHY" />
              <el-option label="需要观察" value="OBSERVE" />
              <el-option label="需要帮助" value="NEEDS_HELP" />
            </el-select>
          </el-form-item>
          <el-form-item label="绝育情况">
            <el-switch v-model="animalForm.sterilized" active-text="已绝育" inactive-text="未确认" />
          </el-form-item>
          <el-form-item label="首次发现">
            <el-date-picker v-model="animalForm.firstSeenDate" type="date" value-format="YYYY-MM-DD" placeholder="选择日期" style="width: 100%" />
          </el-form-item>
          <el-form-item label="活跃时段">
            <el-input v-model="animalForm.activeTime" placeholder="如：傍晚 17:00 后" maxlength="100" />
          </el-form-item>
        </div>
        <el-form-item label="性格标签">
          <el-input v-model="animalForm.personalityTags" placeholder="如：亲人、怕生、爱晒太阳" maxlength="255" />
        </el-form-item>
        <el-form-item label="封面图片">
          <el-upload
            ref="uploadRef"
            :auto-upload="false"
            :limit="1"
            :on-change="handleFileChange"
            :on-exceed="handleExceed"
            accept="image/*"
            list-type="picture-card"
          >
            <el-icon><Plus /></el-icon>
            <template #tip>
              <div class="el-upload__tip">仅支持上传一张图片，JPG/PNG格式</div>
            </template>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">
          {{ isEdit ? '保存' : '新增' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { createAnimal, deleteAnimal, toAnimalFormData, updateAnimal } from '../../api/animals'
import { usePagedAnimals } from '../../composables/usePagedAnimals'
import { ElMessage } from 'element-plus'

const {
  animalList, loading, searchName, searchType, currentPage, pageSize, total,
  fetchAnimals, search: handleSearch, changePageSize: handleSizeChange
} = usePagedAnimals(10)

const dialogVisible = ref(false)
const isEdit = ref(false)
const submitLoading = ref(false)
const formRef = ref(null)
const uploadRef = ref(null)
const selectedFile = ref(null)
const editId = ref(null)

const animalForm = reactive({
  name: '',
  type: null,
  area: '',
  description: '',
  aliases: '',
  gender: 0,
  personalityTags: '',
  sterilized: false,
  healthStatus: 'HEALTHY',
  firstSeenDate: '',
  activeTime: ''
})

const formRules = {
  name: [{ required: true, message: '请输入动物名字', trigger: 'blur' }],
  type: [{ required: true, message: '请选择类型', trigger: 'change' }],
  area: [{ required: true, message: '请输入常驻区域', trigger: 'blur' }]
}

const handleAdd = () => {
  isEdit.value = false
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  editId.value = row.id
  animalForm.name = row.name
  animalForm.type = row.type
  animalForm.area = row.area
  animalForm.description = row.description || ''
  animalForm.aliases = row.aliases || ''
  animalForm.gender = row.gender ?? 0
  animalForm.personalityTags = row.personalityTags || ''
  animalForm.sterilized = Boolean(row.sterilized)
  animalForm.healthStatus = row.healthStatus || 'HEALTHY'
  animalForm.firstSeenDate = row.firstSeenDate || ''
  animalForm.activeTime = row.activeTime || ''
  dialogVisible.value = true
}

const handleFileChange = (file) => {
  selectedFile.value = file.raw
}

const handleExceed = () => {
  ElMessage.warning('只能上传一张图片')
}

const resetForm = () => {
  animalForm.name = ''
  animalForm.type = null
  animalForm.area = ''
  animalForm.description = ''
  animalForm.aliases = ''
  animalForm.gender = 0
  animalForm.personalityTags = ''
  animalForm.sterilized = false
  animalForm.healthStatus = 'HEALTHY'
  animalForm.firstSeenDate = ''
  animalForm.activeTime = ''
  selectedFile.value = null
  editId.value = null
  if (uploadRef.value) {
    uploadRef.value.clearFiles()
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    submitLoading.value = true
    try {
      const formData = toAnimalFormData(animalForm, selectedFile.value)
      if (isEdit.value) {
        await updateAnimal(editId.value, formData)
        ElMessage.success('修改成功')
      } else {
        await createAnimal(formData)
        ElMessage.success('新增成功')
      }
      dialogVisible.value = false
      fetchAnimals()
    } catch (error) {
      // handled by interceptor
    } finally {
      submitLoading.value = false
    }
  })
}

const handleDelete = async (id) => {
  try {
    await deleteAnimal(id)
    ElMessage.success('删除成功')
    fetchAnimals()
  } catch (error) {
    // handled by interceptor
  }
}

onMounted(() => {
  fetchAnimals()
})
</script>

<style scoped>
.animal-manage-container {
  max-width: 1200px;
  margin: 0 auto;
  min-height: calc(100vh - 108px);
  display: flex;
  flex-direction: column;
}

.manage-card {
  border-radius: 12px;
  flex: 1;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.page-title {
  margin: 0;
  color: #303133;
}

.search-row {
  display: grid;
  grid-template-columns: minmax(0, 1fr) minmax(0, 1fr) 140px;
  align-items: center;
  gap: 16px;
  width: 100%;
  margin-bottom: 20px;
}

.search-row :deep(.el-select) {
  width: 100%;
}

.search-button {
  width: 100%;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  column-gap: 18px;
}

@media (max-width: 768px) {
  .search-row {
    grid-template-columns: 1fr;
  }
  .form-grid {
    grid-template-columns: 1fr;
  }
}
</style>
