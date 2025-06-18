<template>
  <div class="home">
    <TeacherHome v-if="userStore.isTeacher" />
    <StudentHome v-else />
  </div>
</template>

<script setup>
import { useUserStore } from '@/stores/user'
import TeacherHome from './TeacherHome.vue'
import StudentHome from './StudentHome.vue'

const userStore = useUserStore()
</script>

<style scoped>
.home {
  height: 100%;
}
</style>