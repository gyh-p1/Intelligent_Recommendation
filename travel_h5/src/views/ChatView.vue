<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { showToast } from 'vant'
import { getQuickQuestions, streamTravelChat } from '@/api/travel'

interface ChatMessage {
  role: 'user' | 'assistant'
  content: string
}

const input = ref('')
const loading = ref(false)
const quickQuestions = ref<string[]>([])
const messages = ref<ChatMessage[]>([
  {
    role: 'assistant',
    content: '你好，我可以帮你规划路线、推荐景点，也可以按预算整理行程。',
  },
])

async function loadQuickQuestions() {
  try {
    quickQuestions.value = await getQuickQuestions()
  } catch {
    quickQuestions.value = ['帮我规划杭州 3 日游', '成都 1500 元预算怎么玩？', '北京亲子景点推荐']
  }
}

async function sendMessage(text = input.value) {
  const content = text.trim()

  if (!content || loading.value) {
    return
  }

  messages.value.push({ role: 'user', content })
  input.value = ''
  loading.value = true

  const assistantMessage: ChatMessage = { role: 'assistant', content: '' }
  messages.value.push(assistantMessage)

  try {
    await streamTravelChat(
      { message: content, model: 'default' },
      (chunk) => {
        assistantMessage.content += chunk
      },
      () => {
        loading.value = false
      },
    )
  } catch (error) {
    assistantMessage.content = error instanceof Error ? error.message : 'AI 对话失败，请稍后再试'
    showToast(assistantMessage.content)
  } finally {
    loading.value = false
  }
}

onMounted(loadQuickQuestions)
</script>

<template>
  <main class="chat-page">
    <van-nav-bar title="AI 对话" left-arrow @click-left="$router.back()" />

    <section class="question-strip">
      <button
        v-for="question in quickQuestions"
        :key="question"
        class="question-chip"
        type="button"
        @click="sendMessage(question)"
      >
        {{ question }}
      </button>
    </section>

    <section class="message-list">
      <div v-for="(message, index) in messages" :key="index" class="message-row" :class="message.role">
        <div class="bubble">{{ message.content || '...' }}</div>
      </div>
    </section>

    <footer class="chat-input">
      <van-field v-model="input" rows="1" autosize type="textarea" placeholder="问我旅行计划..." />
      <van-button type="primary" :loading="loading" @click="sendMessage()">发送</van-button>
    </footer>
  </main>
</template>
