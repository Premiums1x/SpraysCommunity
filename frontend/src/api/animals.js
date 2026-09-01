import request from '../utils/request'

export const getAnimals = params => request.get('/api/animals', { params })
export const getAnimal = id => request.get(`/api/animals/${id}`)
export const createAnimal = formData => request.post('/api/animals', formData)
export const updateAnimal = (id, formData) => request.put(`/api/animals/${id}`, formData)
export const deleteAnimal = id => request.delete(`/api/animals/${id}`)

export const getAllAnimalOptions = async () => {
  const first = await getAnimals({ page: 1, size: 50 })
  const records = [...first.data.records]
  const pageCount = Math.min(first.data.pages || 1, 20)
  for (let page = 2; page <= pageCount; page += 1) {
    const response = await getAnimals({ page, size: 50 })
    records.push(...response.data.records)
  }
  return records
}

export const toAnimalFormData = (animal, file) => {
  const formData = new FormData()
  formData.append('name', animal.name.trim())
  formData.append('type', String(animal.type))
  formData.append('area', animal.area.trim())
  if (animal.description?.trim()) formData.append('description', animal.description.trim())
  if (animal.aliases?.trim()) formData.append('aliases', animal.aliases.trim())
  formData.append('gender', String(animal.gender ?? 0))
  if (animal.personalityTags?.trim()) formData.append('personalityTags', animal.personalityTags.trim())
  formData.append('sterilized', String(Boolean(animal.sterilized)))
  formData.append('healthStatus', animal.healthStatus || 'HEALTHY')
  if (animal.firstSeenDate) formData.append('firstSeenDate', animal.firstSeenDate)
  if (animal.activeTime?.trim()) formData.append('activeTime', animal.activeTime.trim())
  if (file) formData.append('file', file)
  return formData
}
