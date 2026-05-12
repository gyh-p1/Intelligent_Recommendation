export interface ApiResponse<T> {
  code: number
  message: string
  data: T
}

export interface PageResult<T> {
  list: T[]
  total: number
  page: number
  pageSize: number
}

export interface HotCity {
  cityCode: string
  cityName: string
  province: string
  cover: string
  tags: string[]
}

export interface CityOption {
  label: string
  value: string
  province: string
}

export interface RecommendRequest {
  city: string
  cityCode?: string
  budget: number
  days: number
  travelers?: number
  preferences?: string[]
  startDate?: string
}

export interface RecommendResult {
  requestId: string
  city: string
  summary: string
  budgetAdvice: {
    totalBudget: number
    estimatedCost: number
    currency: string
    details: Array<{
      name: string
      amount: number
    }>
  }
  spots: Array<{
    spotId: string
    name: string
    level?: string
    address: string
    description: string
    recommendedDuration: string
    ticketPrice: number
    tags: string[]
    reason: string
  }>
  itinerary: Array<{
    day: number
    title: string
    items: Array<{
      time: string
      activity: string
    }>
  }>
  tips: string[]
  cached: boolean
  createdAt: string
}

export interface ChatRequest {
  sessionId?: string
  message: string
  city?: string
  model?: string
  stream?: boolean
}

export interface UserProfile {
  userId: string
  nickname: string
  avatar: string
  favoriteCount: number
  historyCount: number
}
