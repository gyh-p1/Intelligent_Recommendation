import axios from 'axios'
import { showToast } from 'vant'
import type { ApiResponse } from '@/types/api'
import { getClientId } from '@/utils/clientId'

const http = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '',
  timeout: 30000,
})

http.interceptors.request.use((config) => {
  config.headers['X-Client-Id'] = getClientId()
  return config
})

http.interceptors.response.use(
  (response) => {
    const result = response.data as ApiResponse<unknown>

    if (typeof result?.code === 'number' && result.code !== 200) {
      showToast(result.message || '请求失败')
      return Promise.reject(result)
    }

    return response
  },
  (error) => {
    showToast(error?.response?.data?.message || '网络异常，请稍后重试')
    return Promise.reject(error)
  },
)

export async function request<T>(config: Parameters<typeof http.request>[0]) {
  const response = await http.request<ApiResponse<T>>(config)
  return response.data.data
}

export default http
