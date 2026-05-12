import { request } from './http'
import type { PageResult, UserProfile } from '@/types/api'

export interface FavoriteItem {
  favoriteId: string
  type: string
  targetId: string
  title: string
  description: string
  cover: string
  createdAt: string
}

export interface HistoryItem {
  historyId: string
  type: string
  title: string
  summary: string
  createdAt: string
}

export function getUserProfile() {
  return request<UserProfile>({
    url: '/api/user/profile',
    method: 'GET',
  })
}

export function getFavorites(page = 1, pageSize = 10) {
  return request<PageResult<FavoriteItem>>({
    url: '/api/user/favorites',
    method: 'GET',
    params: { page, pageSize },
  })
}

export function getHistory(page = 1, pageSize = 10) {
  return request<PageResult<HistoryItem>>({
    url: '/api/user/history',
    method: 'GET',
    params: { page, pageSize },
  })
}
