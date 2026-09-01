import { ref } from 'vue'
import { getAnimals } from '../api/animals'

export const usePagedAnimals = (initialPageSize = 8) => {
  const animalList = ref([])
  const loading = ref(false)
  const error = ref('')
  const searchName = ref('')
  const searchType = ref('')
  const currentPage = ref(1)
  const pageSize = ref(initialPageSize)
  const total = ref(0)

  const fetchAnimals = async () => {
    loading.value = true
    error.value = ''
    try {
      const params = { page: currentPage.value, size: pageSize.value }
      if (searchName.value.trim()) params.name = searchName.value.trim()
      if (searchType.value !== '') params.type = searchType.value
      const response = await getAnimals(params)
      animalList.value = response.data.records
      total.value = response.data.total
    } catch (requestError) {
      error.value = requestError.response?.data?.message || requestError.message || '加载失败'
    } finally {
      loading.value = false
    }
  }

  const search = () => {
    currentPage.value = 1
    return fetchAnimals()
  }

  const changePageSize = () => {
    currentPage.value = 1
    return fetchAnimals()
  }

  return {
    animalList,
    loading,
    error,
    searchName,
    searchType,
    currentPage,
    pageSize,
    total,
    fetchAnimals,
    search,
    changePageSize
  }
}
