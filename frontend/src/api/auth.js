import request from '../utils/request'

export const login = credentials => request.post('/api/auth/login', credentials)
export const register = profile => request.post('/api/auth/register', profile)
export const getCurrentUser = () => request.get('/api/auth/info')
