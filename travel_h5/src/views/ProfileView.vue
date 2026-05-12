<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { showDialog } from 'vant'
import { getUserProfile } from '@/api/user'
import type { UserProfile } from '@/types/api'

const profile = ref<UserProfile>({
  userId: 'guest',
  nickname: '旅行用户',
  avatar: 'https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg',
  favoriteCount: 0,
  historyCount: 0,
})

async function loadProfile() {
  try {
    profile.value = await getUserProfile()
  } catch {
    // 匿名用户初始化阶段允许后端未启动。
  }
}

function showAbout() {
  showDialog({
    title: '关于我们',
    message: '旅游景点推荐智能助手，基于 Vue 3、Vant、Spring Boot、Redis 与 AI 能力构建。',
  })
}

onMounted(loadProfile)
</script>

<template>
  <main class="page page-with-tab">
    <section class="profile-header">
      <van-image round width="72" height="72" :src="profile.avatar" />
      <div>
        <h1>{{ profile.nickname }}</h1>
        <p>ID：{{ profile.userId }}</p>
      </div>
    </section>

    <van-cell-group inset>
      <van-cell title="我的收藏" icon="star-o" :value="`${profile.favoriteCount}`" is-link />
      <van-cell title="历史记录" icon="clock-o" :value="`${profile.historyCount}`" is-link />
      <van-cell title="设置" icon="setting-o" is-link />
      <van-cell title="关于我们" icon="info-o" is-link @click="showAbout" />
    </van-cell-group>
  </main>
</template>
