<template>
  <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
    <el-alert v-if="optionsError" :title="optionsError" type="error" show-icon :closable="false" class="options-error" />
    <el-form-item v-if="showAnimalSelect" label="选择动物" prop="animalId">
      <el-select
        v-model="form.animalId"
        placeholder="请选择要打卡的动物"
        filterable
        class="full-width"
        :loading="animalsLoading"
      >
        <el-option
          v-for="animal in animalOptions"
          :key="animal.id"
          :label="`${animal.name} · ${animal.type === 1 ? '猫' : '狗'}`"
          :value="animal.id"
        />
      </el-select>
    </el-form-item>
    <el-form-item label="打卡内容" prop="content">
      <el-input
        v-model="form.content"
        type="textarea"
        :rows="5"
        placeholder="记录你看到的状态、位置和时间，帮助大家持续照看它。"
        maxlength="500"
        show-word-limit
      />
    </el-form-item>
    <el-form-item>
      <el-switch v-model="form.anonymous" active-text="匿名发布" inactive-text="显示昵称" />
    </el-form-item>
    <el-button type="primary" :loading="submitting" @click="submit">
      提交打卡
    </el-button>
  </el-form>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { createCheckIn } from '../api/checkins'
import { getAllAnimalOptions } from '../api/animals'

const props = defineProps({
  animalId: { type: [Number, String], default: null },
  showAnimalSelect: { type: Boolean, default: false }
})
const emit = defineEmits(['submitted'])

const formRef = ref(null)
const submitting = ref(false)
const animalsLoading = ref(false)
const animalOptions = ref([])
const optionsError = ref('')
const form = reactive({
  animalId: props.animalId ? Number(props.animalId) : null,
  content: '',
  anonymous: false
})
const rules = {
  animalId: [{ required: true, message: '请选择动物', trigger: 'change' }],
  content: [
    { required: true, message: '请输入打卡内容', trigger: 'blur' },
    { max: 500, message: '打卡内容不能超过500个字符', trigger: 'blur' }
  ]
}

const reset = () => {
  form.content = ''
  form.anonymous = false
  if (props.showAnimalSelect) form.animalId = null
  formRef.value?.clearValidate()
}

const submit = async () => {
  if (!formRef.value) return
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  submitting.value = true
  try {
    await createCheckIn({
      animalId: Number(form.animalId),
      content: form.content.trim(),
      anonymous: form.anonymous
    })
    const submittedAnimalId = form.animalId
    reset()
    emit('submitted', submittedAnimalId)
  } catch {
    // 统一请求层已向用户展示错误。
  } finally {
    submitting.value = false
  }
}

onMounted(async () => {
  if (!props.showAnimalSelect) return
  animalsLoading.value = true
  optionsError.value = ''
  try {
    animalOptions.value = await getAllAnimalOptions()
  } catch (requestError) {
    optionsError.value = requestError.response?.data?.message || requestError.message || '动物列表加载失败'
  } finally {
    animalsLoading.value = false
  }
})

defineExpose({ reset })
</script>

<style scoped>
.full-width {
  width: 100%;
}
.options-error { margin-bottom: 16px; }
</style>
