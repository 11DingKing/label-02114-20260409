<template>
  <div class="app">
    <router-view />
    <nav class="tab-bar" v-if="showTabBar">
      <router-link to="/" class="tab-item" :class="{ active: route.path === '/' }">
        <span class="icon">🍽️</span>
        <span class="text">点餐</span>
      </router-link>
      <router-link to="/random" class="tab-item" :class="{ active: route.path === '/random' }">
        <span class="icon">🎲</span>
        <span class="text">随机</span>
      </router-link>
      <router-link to="/history" class="tab-item" :class="{ active: route.path === '/history' }">
        <span class="icon">📜</span>
        <span class="text">记录</span>
      </router-link>
      <router-link to="/wishlist" class="tab-item" :class="{ active: route.path === '/wishlist' }">
        <span class="icon">💝</span>
        <span class="text">心愿</span>
      </router-link>
    </nav>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const showTabBar = computed(() => route.path !== '/login')
</script>
