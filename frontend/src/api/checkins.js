import request from '../utils/request'

export const createCheckIn = payload => request.post('/api/checkins', payload)
export const getAnimalCheckIns = (animalId, params) =>
  request.get(`/api/animals/${animalId}/checkins`, { params })
export const getMyCheckIns = params => request.get('/api/checkins/my', { params })
