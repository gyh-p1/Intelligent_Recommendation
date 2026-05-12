<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import { getHotCities, recommendTravel } from '@/api/travel'
import type { HotCity, RecommendResult } from '@/types/api'

const router = useRouter()
const city = ref('杭州')
const budget = ref(1500)
const days = ref(3)
const loading = ref(false)
const hotCities = ref<HotCity[]>([])
const result = ref<RecommendResult>()

const active = ref<number>(0)
const showCity = ref<boolean>(false)

const allCities = ref<string[]>(['北京', '上海', '杭州', '广州', '深圳', '成都', '重庆', '西安', '南京', '苏州'])

const cityColumns = computed(() => {
  return allCities.value.map(city => ({ text: city, value: city }))
})

const handleCityConfirm = ({ selectedValues }: { selectedValues: string[] }) => {
  city.value = selectedValues[0]
  showCity.value = false
}

const canSubmit = computed(() => city.value.trim() && budget.value > 0 && days.value > 0)

async function loadHotCities() {
  try {
    hotCities.value = await getHotCities()
  } catch {
    hotCities.value = [
      { cityCode: 'beijing', cityName: '北京', province: '北京市', cover: '', tags: ['历史文化'] },
      { cityCode: 'shanghai', cityName: '上海', province: '上海市', cover: '', tags: ['都市夜景'] },
      { cityCode: 'chengdu', cityName: '成都', province: '四川省', cover: '', tags: ['美食休闲'] },
      { cityCode: 'hangzhou', cityName: '杭州', province: '浙江省', cover: '', tags: ['自然风光'] },
    ]
  }
}

async function handleRecommend() {
  if (!canSubmit.value) {
    showToast('请完善目的地、预算和天数')
    return
  }

  loading.value = true

  try {
    result.value = await recommendTravel({
      city: city.value,
      budget: budget.value,
      days: days.value,
      travelers: 1,
      preferences: ['轻松', '景点推荐'],
    })
  } finally {
    loading.value = false
  }
}

onMounted(loadHotCities)
</script>

<template>

  <van-nav-bar title="旅游景点推荐智能助手" />

  <main class="page page-with-tab">

    <van-notice-bar left-icon="info-o" text="选择目的地、预算和天数，快速生成适合移动端查看的旅行建议"></van-notice-bar>

    <section class="panel">
      <van-field is-link readonly v-model="city" label="目的地" placeholder="请输入城市，例如杭州" @click="showCity = true"
        style="border-radius: 8px;" />
      <van-field v-model.number="budget" label="预算" type="number" placeholder="请输入预算" style="border-radius: 8px;">
        <template #right-icon>元</template>
      </van-field>
      <van-field v-model.number="days" label="天数" type="number" placeholder="请输入预算" style="border-radius: 8px;">
        <template #right-icon>天</template>
      </van-field>
      <van-button type="primary" block :loading="loading" @click="handleRecommend">生成推荐</van-button>
    </section>

    <section class="quick-actions">
      <van-button icon="chat-o" plain type="primary" @click="router.push('/chat')">AI 对话</van-button>
      <van-button icon="user-o" plain @click="router.push('/profile')">个人中心</van-button>
    </section>

    <section class="section">
      <h2>热门目的地</h2>
      <div class="city-grid">
        <button v-for="item in hotCities" :key="item.cityCode" class="city-card" type="button"
          @click="city = item.cityName">
          <strong>{{ item.cityName }}</strong>
          <span>{{ item.tags.join(' / ') }}</span>
        </button>
      </div>
    </section>

    <section v-if="result" class="section result-section">
      <h2>{{ result.city }}推荐</h2>
      <p class="result-summary">{{ result.summary }}</p>
      <van-cell-group inset>
        <van-cell v-for="spot in result.spots" :key="spot.spotId" :title="spot.name" :label="spot.reason"
          :value="spot.recommendedDuration" />
      </van-cell-group>
    </section>

    <van-popup v-model:show="showCity" position="bottom" round>
      <van-picker title="请选择目的地" :columns="cityColumns" @confirm="handleCityConfirm" @cancel="showCity = false" />
    </van-popup>

  </main>

  <section class="home-footer">
    <van-tabbar route v-model="active" :fixed="false">
      <van-tabbar-item to="/" icon="home-o">首页</van-tabbar-item>
      <van-tabbar-item to="/chat" icon="chat-o">对话</van-tabbar-item>
      <van-tabbar-item to="/profile" icon="user-o">我的</van-tabbar-item>
    </van-tabbar>
  </section>



</template>
