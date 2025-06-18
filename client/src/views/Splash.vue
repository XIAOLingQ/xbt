<template>
  <div class="splash-page">
    <header class="splash-header">
      <div class="logo">
        <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg"><path d="M12 2L2 7V17L12 22L22 17V7L12 2Z" stroke="#212529" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/><path d="M2 7L12 12" stroke="#212529" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/><path d="M22 7L12 12" stroke="#212529" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/><path d="M12 22V12" stroke="#212529" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/><path d="M17 4.5L7 9.5" stroke="#212529" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
        <span>XBT 在线学习平台</span>
      </div>
      <button class="login-btn" @click="goToLogin">立即登录</button>
    </header>

    <main class="content-wrapper">
      <section class="hero-section">
        <div class="hero-content">
          <h1 class="hero-title">AI 赋能的新一代学习体验</h1>
          <p class="hero-subtitle">结合前沿 AI 技术，为您提供个性化、高效率的在线学习环境。</p>
          <button class="cta-button" @click="goToLogin">开启学习之旅</button>
        </div>
      </section>

      <section class="features-section">
        <div 
          class="feature-card" 
          v-for="feature in features" 
          :key="feature.title"
          :ref="el => { if (el) featureCards.push(el) }"
          :style="{'--feature-color': feature.color}"
        >
          <div class="feature-icon" v-html="feature.icon"></div>
          <div class="feature-content">
            <h3 class="feature-title">{{ feature.title }}</h3>
            <p class="feature-description">{{ feature.description }}</p>
          </div>
        </div>
      </section>

      <section class="courses-section">
        <h2 class="section-title">精选课程</h2>
        <div class="courses-grid">
          <div 
            class="course-item" 
            v-for="course in courses" 
            :key="course.id"
            :ref="el => { if (el) courseItems.push(el) }"
          >
            <img :src="course.coverImage" alt="" class="course-cover">
            <div class="course-info">
              <h4 class="course-title">{{ course.title }}</h4>
              <p class="course-teacher">{{ course.teacherName }}</p>
            </div>
          </div>
        </div>
      </section>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUpdate, watch } from 'vue'
import { useRouter } from 'vue-router'
import { getCourseList } from '../api/course'
import { useIntersectionObserver } from '../composables/useIntersectionObserver'

const router = useRouter()
const courses = ref([])
const featureCards = ref([])
const courseItems = ref([])

const { observe: observeFeatures } = useIntersectionObserver()
const { observe: observeCourses } = useIntersectionObserver({ threshold: 0.05 })

onBeforeUpdate(() => {
  featureCards.value = []
  courseItems.value = []
})

const features = ref([
  {
    title: '学习赋能',
    description: '集直播录播双模式与资料测试于一体，我们的平台既灵活又高效。',
    icon: `<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="m12 1-3.14 6.26L1 9.27l5.26 5.14L4.82 22 12 18.26 19.18 22l-1.44-7.59L23 9.27l-7.86-2.01L12 1z"></path></svg>`,
    color: '#007bff'
  },
  {
    title: '社交互联',
    description: '聚社区讨论与私信互助于一身，我们的生态既活跃又温暖。',
    icon: `<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12.22 2h-4.44a2 2 0 0 0-2 2v.78a2 2 0 0 1-1 1.73l-1.83.91a2 2 0 0 0-1 1.73V11a2 2 0 0 0 2 2h4.44a2 2 0 0 0 2-2v-.78a2 2 0 0 1 1-1.73l1.83-.91a2 2 0 0 0 1-1.73V4a2 2 0 0 0-2-2z"></path><path d="M22 12.78v.78a2 2 0 0 1-2 2h-4.44a2 2 0 0 1-2-2v-1.56a2 2 0 0 0-1-1.73l-1.83-.91a2 2 0 0 1-1-1.73V4a2 2 0 0 1 2-2h4.44a2 2 0 0 1 2 2v.78a2 2 0 0 0 1 1.73l1.83.91a2 2 0 0 1 1 1.73z"></path></svg>`,
    color: '#28a745'
  },
  {
    title: '管理智控',
    description: '融个人定制与课程管控为一体，我们的系统既便捷又可靠。',
    icon: `<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"></path></svg>`,
    color: '#ffc107'
  }
])

watch(featureCards, (newVal) => {
  if (newVal && newVal.length) {
    observeFeatures(newVal);
  }
}, { flush: 'post' });

watch(courseItems, (newVal) => {
  if (newVal && newVal.length) {
    observeCourses(newVal);
  }
}, { flush: 'post' });

onMounted(async () => {
  try {
    const response = await getCourseList({ page: 1, pageSize: 4 });
    courses.value = response.list;
  } catch (error) {
    console.error('获取课程列表失败:', error)
  }
})

const goToLogin = () => {
  router.push('/login')
}
</script>

<style scoped>
:root {
  --text-primary: #edeff1;
  --text-secondary: #6c757d;
  --bg-primary: #cacada;
  --bg-secondary: #f8f9fa;
  --accent-primary: #007bff;
  --border-color: #e9ecef;
  --soft-shadow: 0 4px 6px rgba(245, 240, 240, 0.921), 0 1px 3px rgba(0, 0, 0, 0.06);
  --hover-shadow: 0 10px 20px rgba(0,0,0,0.04), 0 4px 8px rgba(0,0,0,0.08);
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

.splash-page {
  /* 根容器背景：从hero区域边缘向四周渐变褪色（颜色变深） */
  background-image: radial-gradient(
    ellipse at center,
    rgba(18, 80, 212, 0.5) 0%,     /* 中心颜色：深蓝，透明度0.5（更不透明） */
    rgba(15, 40, 120, 0.2) 60%,    /* 中间过渡色：深紫蓝，透明度0.2 */
    rgba(8, 18, 58, 0.1) 90%      /* 边缘颜色：深灰蓝，透明度0.1（保留轻微色调） */
  );
  background-size: cover;
  background-position: center;
  background-attachment: fixed;
  color: var(--text-primary);
  height: 100vh;
  width: 100%;
  overflow-y: auto;
  overflow-x: hidden;
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
  animation: fadeIn 1s ease-out;
}

.splash-header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 2rem;
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(10px);
  z-index: 100;
  border-bottom: 1px solid var(--border-color);
}

.logo {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  font-size: 1.1rem;
  font-weight: 600;
  color: var(--text-primary);
}

.login-btn {
  background-color: #e9ecef;
  color: #495057;
  border: none;
  padding: 0.6rem 1.2rem;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 600;
  transition: background-color 0.3s, transform 0.2s;
}

.login-btn:hover {
  background-color: #dee2e6;
  transform: scale(1.05);
}

.content-wrapper {
  padding: 8rem 2rem 4rem;
  max-width: 1140px;
  margin: 0 auto;
}

.hero-section {
  text-align: center;
  padding: 4rem 0;
  opacity: 0;
  animation: fadeInUp 0.8s ease-out forwards;

  /* 添加背景图片 */
  background-image: url('./b2.png');
  background-size: cover;
  background-position: center;
  border-radius: 16px; /* 可选：圆角 */
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1); /* 可选：阴影 */
}

@keyframes fadeInUp {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

.hero-title {
  font-size: clamp(2.8rem, 5vw, 4.2rem);
  font-weight: 700;
  color: #ffffff; /* 改为白色 */
  margin-bottom: 1rem;
  letter-spacing: -1.5px;
}

.hero-subtitle {
  font-size: clamp(1.1rem, 2vw, 1.25rem);
  color: #ffffff; /* 改为白色 */
  max-width: 600px;
  margin: 0 auto 2.5rem;
  line-height: 1.7;
}

.cta-button {
  background: #007bff;
  color: white;
  border: none;
  padding: 1rem 2.5rem;
  border-radius: 10px;
  font-size: 1.1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(0, 123, 255, 0.2);
}

.cta-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(0, 123, 255, 0.3);
  background-color: #0056b3;
}

.features-section {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 2rem;
  margin: 6rem 0;
}

.feature-card {
  background: #ffffff; /* 改为白色，完全不透明 */
  /* 或使用半透明：background: rgba(255, 255, 255, 0.9); */
  
  border-radius: 16px;
  padding: 2rem;
  border: 1px solid var(--border-color);
  border-left: 4px solid var(--feature-color);
  transition: transform 0.3s, box-shadow 0.3s;
  box-shadow: var(--soft-shadow);
  
  opacity: 0;
  transform: translateY(30px);
  transition: opacity 0.6s ease-out, transform 0.6s ease-out;
}

.feature-card.visible {
  opacity: 1;
  transform: translateY(0);
}

.feature-card:hover {
  transform: translateY(-5px);
  box-shadow: var(--hover-shadow);
}

.feature-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 48px;
  height: 48px;
  border-radius: 12px;
  margin-bottom: 1.5rem;
  background: color-mix(in srgb, var(--feature-color) 10%, transparent);
  color: var(--feature-color);
}

.feature-title {
  font-size: 1.25rem;
  font-weight: 600;
  margin-bottom: 0.75rem;
  color: var(--text-primary);
}

.feature-description {
  color: var(--text-secondary);
  line-height: 1.7;
  font-size: 0.95rem;
}

.courses-section {
  text-align: center;
  margin: 6rem 0;
}

.section-title {
  font-size: 2.5rem;
  font-weight: 700;
  margin-bottom: 3rem;
  color: var(--text-primary);
}

.courses-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 2rem;
}

.course-item {
  /* 从顶部透明到底部纯白的渐变 */
  background: linear-gradient(
    to bottom,
    rgba(255, 255, 255, 0) 0%,   /* 顶部完全透明 */
    rgba(255, 255, 255, 1) 100%   /* 底部完全白色 */
  );
  
  border-radius: 12px;
  overflow: hidden;
  border: 1px solid var(--border-color);
  transition: transform 0.3s, box-shadow 0.3s, opacity 0.6s, transform 0.6s;
  box-shadow: var(--soft-shadow);
  opacity: 0;
  transform: translateY(30px);
}

.course-item.visible {
  opacity: 1;
  transform: translateY(0);
}

.course-item:hover {
  transform: translateY(-8px);
  box-shadow: var(--hover-shadow);
}

.course-cover {
  width: 100%;
  height: 180px;
  object-fit: cover;
}

.course-info {
  padding: 1.25rem;
  text-align: left;
}

.course-title {
  font-size: 1.1rem;
  font-weight: 600;
  margin-bottom: 0.5rem;
  color: var(--text-primary);
}

.course-teacher {
  font-size: 0.9rem;
  color: var(--text-secondary);
}
</style>