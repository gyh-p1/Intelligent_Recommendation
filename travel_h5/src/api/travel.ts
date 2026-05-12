import { getClientId } from '@/utils/clientId'
import { request } from './http'
import type {
  ChatRequest,
  CityOption,
  HotCity,
  RecommendRequest,
  RecommendResult,
} from '@/types/api'

export function getHotCities() {
  return request<HotCity[]>({
    url: '/api/travel/hot-cities',
    method: 'GET',
  })
}

export function getCities(keyword?: string) {
  return request<CityOption[]>({
    url: '/api/travel/cities',
    method: 'GET',
    params: { keyword },
  })
}

export function getQuickQuestions() {
  return request<string[]>({
    url: '/api/travel/chat/quick-questions',
    method: 'GET',
  })
}

export function recommendTravel(data: RecommendRequest) {
  return request<RecommendResult>({
    url: '/api/travel/recommend',
    method: 'POST',
    data,
  })
}

export async function streamTravelChat(
  data: ChatRequest,
  onMessage: (content: string) => void,
  onDone?: () => void,
) {
  const response = await fetch('/api/travel/chat', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      Accept: 'text/event-stream',
      'X-Client-Id': getClientId(),
    },
    body: JSON.stringify({
      ...data,
      stream: true,
    }),
  })

  if (!response.ok || !response.body) {
    throw new Error('AI 对话服务暂不可用')
  }

  const reader = response.body.getReader()
  const decoder = new TextDecoder('utf-8')
  let buffer = ''

  while (true) {
    const { done, value } = await reader.read()

    if (done) {
      onDone?.()
      break
    }

    buffer += decoder.decode(value, { stream: true })
    const chunks = buffer.split('\n\n')
    buffer = chunks.pop() || ''

    for (const chunk of chunks) {
      const event = chunk
        .split('\n')
        .find((line) => line.startsWith('event:'))
        ?.replace('event:', '')
        .trim()
      const dataLine = chunk
        .split('\n')
        .find((line) => line.startsWith('data:'))
        ?.replace('data:', '')
        .trim()

      if (!dataLine || event === 'start') {
        continue
      }

      if (event === 'done') {
        onDone?.()
        return
      }

      const parsed = JSON.parse(dataLine) as { content?: string; message?: string }

      if (event === 'error') {
        throw new Error(parsed.message || 'AI 对话失败')
      }

      if (parsed.content) {
        onMessage(parsed.content)
      }
    }
  }
}
