<template>
  <div class="home-container">
    <div class="content-wrapper">
      <!-- 侧边栏 -->
      <el-aside width="240px" class="sidebar">
        <el-menu
          :default-active="activeMenu"
          class="menu"
          @select="handleSelect"
        >
          <el-menu-item index="courses">
            <el-icon><Reading /></el-icon>
            <span>我的课程</span>
          </el-menu-item>
          <el-menu-item index="reports">
            <el-icon><DataLine /></el-icon>
            <span>学习报告</span>
          </el-menu-item>
          <el-menu-item index="ai_playground">
            <el-icon><Aim /></el-icon>
            <span>AI练题</span>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <!-- 主要内容区域 -->
      <el-main class="main-content">
        <!-- 课程列表 -->
        <div v-if="activeMenu === 'courses'">
          <div class="section-header">
            <h2>我的课程</h2>
            <div class="actions">
              <el-input
                v-model="searchText"
                placeholder="搜索课程"
                class="search-input"
                :prefix-icon="Search"
                clearable
              />
              <el-button type="primary" :icon="Plus" @click="joinCourseDialogVisible = true">加入课程</el-button>
            </div>
          </div>
          
          <el-row :gutter="20" v-if="filteredCourses.length > 0">
            <el-col :span="6" v-for="course in filteredCourses" :key="course.id">
              <el-card class="course-card" :body-style="{ padding: '0px' }" @click="handleCourseClick(course.id)">
                <img :src="course.coverImage" class="course-image">
                <div class="course-info">
                  <h3>{{ course.title }}</h3>
                    教学老师：<h4 class="teacher" style="display: inline;">{{ course.teacherName }}</h4>
                </div>
                <div class="card-footer">
                    <el-button type="primary" link class="action-btn">进入学习</el-button>
                </div>
              </el-card>
            </el-col>
          </el-row>
          <el-empty v-else description="暂无课程" />
        </div>

        <!-- 学习报告 (学生视图) -->
        <div v-if="activeMenu === 'reports'">
          <div class="section-header">
            <h2>学习报告</h2>
          </div>
          
          <div v-if="reportLoading">
            <el-skeleton :rows="5" animated />
          </div>

          <div v-if="!reportLoading && reportData">
            <el-row :gutter="20">
              <el-col :span="8">
                <el-card class="report-card">
                  <template #header>
                    <div class="card-header">
                      <span>总学习时长</span>
                    </div>
                  </template>
                  <div class="stats-info">
                    <div class="stats-number">{{ (reportData.totalStudyDurationMinutes || 0).toFixed(1) }}<span class="stats-unit">分钟</span></div>
                  </div>
                </el-card>
              </el-col>
              <el-col :span="8">
                <el-card class="report-card">
                  <template #header>
                    <div class="card-header">
                      <span>课程完成情况</span>
                    </div>
                  </template>
                  <div class="stats-info">
                    <div class="stats-number">{{ reportData.completedCoursesCount || 0 }} / {{ reportData.totalCoursesCount || 0 }}</div>
                    <div class="stats-compare">已完成课程数 / 总课程数</div>
                  </div>
                </el-card>
              </el-col>
              <el-col :span="8">
                <el-card class="report-card">
                  <template #header>
                    <div class="card-header">
                      <span>作业平均分</span>
                    </div>
                  </template>
                  <div class="stats-info">
                    <div class="stats-number">{{ (reportData.averageScore || 0).toFixed(1) }}<span class="stats-unit">分</span></div>
                    <div class="stats-compare">已完成 {{ reportData.completedHomeworkCount || 0 }} / {{ reportData.totalHomeworkCount || 0 }} 个作业</div>
                  </div>
                </el-card>
              </el-col>
            </el-row>

            <el-row :gutter="20" class="mt-20">
              <el-col :span="24">
                <el-card class="report-card">
                  <template #header>
                    <div class="card-header">
                      <span>近30日学习趋势</span>
                    </div>
                  </template>
                  <div ref="studyTrendChart" class="chart-container large"></div>
                </el-card>
              </el-col>
            </el-row>
          </div>
          <el-empty v-if="!reportLoading && !reportData" description="暂无学习报告数据"></el-empty>
        </div>

        <!-- AI靶场 -->
        <div v-if="activeMenu === 'ai_playground'">
          <div class="section-header">
            <h2>AI靶场 - 练习主题</h2>
            <el-button type="primary" :icon="Plus" @click="openGenerateDialog">生成新题目</el-button>
          </div>
          
          <div v-if="historyLoading" class="loading-container">
              <el-skeleton :rows="5" animated />
          </div>

          <el-row :gutter="20" v-if="!historyLoading && topicHistory.length > 0">
            <el-col :span="6" v-for="topic in topicHistory" :key="topic.batchId">
              <el-card class="topic-card" @click="goToPractice(topic)">
                <div class="topic-info">
                  <h3>{{ topic.topic }}</h3>
                  <p class="topic-meta">
                    创建于 {{ new Date(topic.createdAt).toLocaleDateString() }}
                  </p>
                </div>
                <div class="topic-footer">
                  <div class="topic-progress">
                    <el-progress :percentage="topic.totalCount > 0 ? (topic.completedCount / topic.totalCount * 100) : 0" :show-text="false"/>
                    <span class="progress-text">{{ topic.completedCount }} / {{ topic.totalCount }} 题</span>
                  </div>
                  <el-tag :type="getTopicStatus(topic).type" size="small">{{ getTopicStatus(topic).text }}</el-tag>
                </div>
              </el-card>
            </el-col>
          </el-row>
          <el-empty v-if="!historyLoading && topicHistory.length === 0" description="您还没有生成过题目，快去生成新的题目吧！"></el-empty>
        </div>
      </el-main>
    </div>

    <!-- 加入课程对话框 -->
    <el-dialog v-model="joinCourseDialogVisible" title="加入新课程" width="400px">
      <el-form @submit.prevent>
        <el-form-item label="课程码">
          <el-input v-model="courseCodeInput" placeholder="请输入教师提供的6位课程码" clearable></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="joinCourseDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleJoinCourse" :loading="isJoining">
            立即加入
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 生成新题目对话框 -->
    <el-dialog v-model="generateDialogVisible" title="生成新题目" width="500px">
        <el-input
            v-model="aiTopic"
            placeholder="请输入您想练习的知识点，例如：Java并发、二叉树"
            class="topic-input"
        >
            <template #append>
                <el-button @click="handleGenerate" :loading="isGenerating">立即生成</el-button>
            </template>
        </el-input>
        <p class="dialog-tip">生成成功后，请关闭对话框查看您的新题目列表。</p>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElNotification } from 'element-plus'
import { Reading, DataLine, Search, Plus, Aim } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { getStudentCourses, joinCourse } from '@/api/course'
import { getMyReport } from '@/api/report'
import { getTopicHistory, generateAiQuestion } from '@/api/ai'
import * as echarts from 'echarts'

const router = useRouter()
const userStore = useUserStore()

const activeMenu = ref('courses')
const searchText = ref('')
const courses = ref([])
const coursesLoaded = ref(false)
const timeRange = ref('week')

const reportData = ref(null);
const reportLoading = ref(false);
const reportDataLoaded = ref(false);
const studyTrendChart = ref(null);
let chartInstance = null;

const joinCourseDialogVisible = ref(false)
const courseCodeInput = ref('')
const isJoining = ref(false)

// --- AI靶场相关 ---
const aiTopic = ref('')
const isGenerating = ref(false)
const historyLoading = ref(false)
const topicHistory = ref([])
const generateDialogVisible = ref(false)

onMounted(() => {
  const tab = router.currentRoute.value.query.tab;
  if (tab) {
      handleSelect(tab);
  } else {
      handleSelect('courses');
  }
  window.addEventListener('resize', () => {
    if (chartInstance) {
      chartInstance.resize();
    }
  });
})

const fetchCourses = async () => {
  if (coursesLoaded.value) return;
  try {
    const res = await getStudentCourses({ page: 1, size: 10 })
    courses.value = res.list
    coursesLoaded.value = true;
  } catch (error) {
    ElMessage.error('获取课程列表失败')
    console.error(error)
  } finally {
    reportLoading.value = false;
  }
}

const filteredCourses = computed(() => {
  if (!searchText.value) return courses.value
  const search = searchText.value.toLowerCase()
  return courses.value.filter(course => 
    course.title.toLowerCase().includes(search)
  )
})

const fetchStudentReport = async () => {
  reportLoading.value = true;
  try {
    const data = await getMyReport();
    reportData.value = data;
    await nextTick();
    initChart();
  } catch (error) {
    console.error("获取学习报告失败", error);
    ElMessage.error('获取学习报告失败');
  } finally {
    reportLoading.value = false;
  }
};

const initChart = () => {
  if (studyTrendChart.value && reportData.value && reportData.value.studyTrend) {
    if (chartInstance) {
        chartInstance.dispose();
    }
    chartInstance = echarts.init(studyTrendChart.value);

    // 生成最近30天的日期
    const dates = [];
    for (let i = 29; i >= 0; i--) {
      const d = new Date();
      d.setDate(d.getDate() - i);
      dates.push(d.toISOString().slice(0, 10));
    }

    const trendData = new Map(reportData.value.studyTrend.map(item => [item.date.slice(0,10), (item.duration || 0).toFixed(1)]));

    const seriesData = dates.map(date => trendData.get(date) || 0);

    const option = {
      tooltip: {
        trigger: 'axis',
        formatter: '{b}: {c} 分钟'
      },
      xAxis: {
        type: 'category',
        data: dates,
        axisLabel: {
          rotate: 30
        }
      },
      yAxis: {
        type: 'value',
        name: '学习时长 (分钟)'
      },
      series: [{
        data: seriesData,
        type: 'line',
        smooth: true
      }],
      grid: {
        left: '3%',
        right: '4%',
        bottom: '10%',
        containLabel: true
      }
    };
    chartInstance.setOption(option);
  }
};

const handleSelect = (key) => {
  activeMenu.value = key
  if (key === 'courses') {
    fetchCourses();
  } else if (key === 'reports') {
    fetchStudentReport();
  } else if (key === 'ai_playground') {
    fetchTopicHistory();
  }
}

const handleJoinCourse = async () => {
  if (!courseCodeInput.value) {
    ElMessage.warning('请输入课程码');
    return;
  }
  isJoining.value = true
  try {
    await joinCourse({ courseCode: courseCodeInput.value });
    ElMessage.success(`成功加入课程！`);
    joinCourseDialogVisible.value = false;
    courseCodeInput.value = '';
    fetchCourses(); // 重新获取课程列表
  } catch (error) {
    ElMessage.error(error.message || '加入课程失败');
  } finally {
    isJoining.value = false
  }
};

const handleCourseClick = (courseId) => {
  router.push(`/course/learn/${courseId}`);
}

const fetchTopicHistory = async () => {
    historyLoading.value = true;
    try {
        const data = await getTopicHistory();
        topicHistory.value = data;
    } catch (error) {
        ElMessage.error('获取主题历史失败');
        topicHistory.value = [];
    } finally {
        historyLoading.value = false;
    }
}

const openGenerateDialog = () => {
    aiTopic.value = '';
    generateDialogVisible.value = true;
}

const handleGenerate = async () => {
  if (!aiTopic.value) {
    ElMessage.warning('请输入知识点主题');
    return;
  }
  isGenerating.value = true;
  try {
    await generateAiQuestion(aiTopic.value);
    ElMessage.success('新题目生成成功！');
    generateDialogVisible.value = false;
    await fetchTopicHistory(); // 刷新主题列表
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '题目生成失败，请稍后重试');
  } finally {
    isGenerating.value = false;
  }
}

const goToPractice = (topic) => {
    router.push({ name: 'AiQuestionView', params: { batchId: topic.batchId }, query: { topic: topic.topic } });
}

const getTopicStatus = (topic) => {
    if (topic.completedCount === 0) {
        return { text: '待完成', type: 'info' };
    } else if (topic.completedCount < topic.totalCount) {
        return { text: '进行中', type: 'warning' };
    } else {
        return { text: '已完成', type: 'success' };
    }
};

watch(activeMenu, (newVal) => {
  if (newVal === 'reports' && !reportData.value) {
    fetchStudentReport();
  }
});
</script>

<style scoped>
.home-container {
  padding: 0px 20px 20px;
  --primary-color: #6996f8;
  --primary-light: #ebf2ff;
  --primary-lighter: #f5f8ff;
  --border-color: #e2e8f0;
  --text-primary: #1e293b;
  --text-secondary: #64748b;
  --background-color: #f8fafc;
  --card-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
  --card-shadow-hover: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
  min-height: 100vh;
}

.content-wrapper {
  display: flex;
  min-height: calc(100vh - 60px);
}

.sidebar {
  background-color: #fff;
  border-right: 1px solid var(--border-color);
  padding: 24px 0;
  display: flex;
  flex-direction: column;
  background: linear-gradient(to bottom, #ffffff, #f8fafc);
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.05);
}

.menu {
  border-right: none;
  background: transparent;
  padding: 0 16px;
  margin-top: 16px;
}

.menu :deep(.el-menu-item) {
  height: 48px;
  line-height: 48px;
  margin: 8px 0;
  border-radius: 8px;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.menu :deep(.el-menu-item.is-active) {
  background-color: var(--primary-light);
  color: var(--primary-color);
  font-weight: 500;
  box-shadow: 0 2px 8px rgba(99, 102, 241, 0.1);
}

.menu :deep(.el-menu-item.is-active)::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  height: 100%;
  width: 3px;
  background: var(--primary-color);
}

.menu :deep(.el-menu-item:not(.is-active):hover) {
  background-color: var(--primary-lighter);
}

.menu :deep(.el-icon) {
  font-size: 18px;
  margin-right: 10px;
  transition: transform 0.2s ease;
}

.menu :deep(.el-menu-item:hover .el-icon) {
  transform: scale(1.1);
}

.main-content {
  flex: 1;
  padding: 20px;
  background-color: var(--background-color);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-header h2 {
  margin: 0;
  font-size: 20px;
  font-weight: 500;
  position: relative;
  padding-bottom: 10px;
}

.section-header h2:after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 60px;
  height: 3px;
  background: var(--primary-color);
  border-radius: 3px;
}

.actions {
  display: flex;
  align-items: center;
  gap: 15px;
}

.search-input {
  width: 250px;
}

.course-card {
  margin-bottom: 20px;
  border-radius: 12px;
  border: none;
  box-shadow: var(--card-shadow);
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(8px);
  overflow: hidden;
  position: relative;
  height: 340px; /* 固定卡片高度 */
  display: flex;
  flex-direction: column;
}

.course-card:hover {
  box-shadow: var(--card-shadow-hover);
  transform: translateY(-2px);
}

.course-card:before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 4px;
  height: 100%;
  background: var(--primary-color);
}

.course-image {
  width: 100%;
  height: 180px; /* 固定图片高度 */
  object-fit: cover;
  display: block;
  flex-shrink: 0;
}

.course-info {
  padding: 15px;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.course-info h3 {
  margin: 0 0 8px;
  font-size: 16px;
  color: var(--text-primary);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.teacher {
  margin: 0 0 10px;
  color: var(--text-secondary);
  font-size: 14px;
  height: 21px;
}

.card-footer {
  border-top: 1px solid var(--border-color);
  padding: 10px 15px;
  text-align: right;
}

.action-btn {
  padding: 0;
  height: auto;
}

.report-card {
  border-radius: 12px;
  border: none;
  box-shadow: var(--card-shadow);
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(8px);
  overflow: hidden;
  position: relative;
}

.report-card:hover {
  box-shadow: var(--card-shadow-hover);
  transform: translateY(-2px);
}

.report-card:before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 4px;
  height: 100%;
  background: var(--primary-color);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 20px;
  border-bottom: 1px solid var(--border-color);
}

.stats-info {
  padding: 24px;
  text-align: center;
}

.stats-number {
  font-size: 36px;
  font-weight: 700;
  color: var(--primary-color);
  line-height: 1.2;
  margin-bottom: 4px;
}

.stats-unit {
  font-size: 16px;
  color: var(--text-secondary);
  margin-left: 4px;
}

.stats-compare {
  margin-top: 8px;
  font-size: 14px;
  color: var(--text-secondary);
}

.chart-container {
  padding: 20px;
  height: 400px;
}

.chart-container.large {
  height: 400px;
}

.loading-container {
  padding: 32px;
  background: #ffffff;
  border-radius: 16px;
  box-shadow: var(--card-shadow);
}

:deep(.el-card) {
  border: none !important;
}

:deep(.el-skeleton) {
  padding: 20px;
}

.mt-20 {
  margin-top: 20px;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .stats-number {
    font-size: 28px;
  }
  
  .stats-unit {
    font-size: 14px;
  }
  
  .chart-container {
    height: 300px;
  }
}

.ai-playground {
  background: linear-gradient(135deg, #0f172a 0%, #1e293b 100%);
  border-radius: 20px;
  padding: 30px;
  box-shadow: 
    0 20px 40px rgba(0, 0, 0, 0.2),
    inset 0 0 0 1px rgba(255, 255, 255, 0.1);
  position: relative;
  overflow: hidden;
}

.ai-playground::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: 
    radial-gradient(circle at 20% 20%, rgba(99, 102, 241, 0.15) 0%, transparent 50%),
    radial-gradient(circle at 80% 80%, rgba(129, 140, 248, 0.1) 0%, transparent 50%);
  pointer-events: none;
}

.ai-playground .topic-input {
  margin-bottom: 30px;
  position: relative;
}

.ai-playground :deep(.el-input__wrapper) {
  background: rgba(30, 41, 59, 0.8);
  border: 1px solid rgba(99, 102, 241, 0.3);
  box-shadow: 
    0 0 20px rgba(99, 102, 241, 0.1),
    inset 0 0 0 1px rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(10px);
  transition: all 0.3s ease;
}

.ai-playground :deep(.el-input__wrapper:hover) {
  border-color: rgba(99, 102, 241, 0.5);
  box-shadow: 
    0 0 30px rgba(99, 102, 241, 0.15),
    inset 0 0 0 1px rgba(255, 255, 255, 0.1);
}

.ai-playground :deep(.el-input__wrapper.is-focus) {
  border-color: rgba(99, 102, 241, 0.8);
  box-shadow: 
    0 0 40px rgba(99, 102, 241, 0.2),
    inset 0 0 0 1px rgba(255, 255, 255, 0.15);
}

.ai-playground :deep(.el-input__inner) {
  color: #e2e8f0;
  font-size: 16px;
}

.ai-playground :deep(.el-input__inner::placeholder) {
  color: #64748b;
}

.question-container {
  padding: 25px;
  background: rgba(30, 41, 59, 0.6);
  border-radius: 16px;
  border: 1px solid rgba(99, 102, 241, 0.2);
  box-shadow: 
    0 10px 30px rgba(0, 0, 0, 0.1),
    inset 0 0 0 1px rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(10px);
  margin-bottom: 20px;
  transition: all 0.3s ease;
}

.question-container:hover {
  transform: translateY(-2px);
  border-color: rgba(99, 102, 241, 0.4);
  box-shadow: 
    0 15px 40px rgba(0, 0, 0, 0.15),
    inset 0 0 0 1px rgba(255, 255, 255, 0.1);
}

.question-text {
  font-size: 16px;
  line-height: 1.6;
  color: #e2e8f0;
  margin-bottom: 25px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.options-group {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.options-group :deep(.el-radio) {
  margin-right: 0;
  padding: 15px 20px;
  border-radius: 12px;
  background: rgba(30, 41, 59, 0.4);
  border: 1px solid rgba(99, 102, 241, 0.2);
  transition: all 0.3s ease;
}

.options-group :deep(.el-radio:hover) {
  background: rgba(30, 41, 59, 0.6);
  border-color: rgba(99, 102, 241, 0.4);
}

.options-group :deep(.el-radio__label) {
  color: #e2e8f0;
  font-size: 15px;
}

.options-group :deep(.el-radio__inner) {
  background: rgba(30, 41, 59, 0.6);
  border-color: rgba(99, 102, 241, 0.4);
}

.options-group :deep(.el-radio__inner::after) {
  background: #818cf8;
  box-shadow: 0 0 10px rgba(129, 140, 248, 0.4);
}

.submit-action {
  margin-top: 30px;
  text-align: right;
}

.submit-action :deep(.el-button) {
  background: linear-gradient(135deg, #6366f1 0%, #818cf8 100%);
  border: none;
  padding: 12px 30px;
  font-size: 16px;
  font-weight: 500;
  box-shadow: 
    0 10px 20px rgba(99, 102, 241, 0.2),
    inset 0 0 0 1px rgba(255, 255, 255, 0.1);
  transition: all 0.3s ease;
}

.submit-action :deep(.el-button:hover) {
  transform: translateY(-2px);
  box-shadow: 
    0 15px 30px rgba(99, 102, 241, 0.3),
    inset 0 0 0 1px rgba(255, 255, 255, 0.2);
}

.result-alert {
  margin-top: 25px;
  padding: 20px;
  border-radius: 12px;
  background: rgba(30, 41, 59, 0.6);
  border: 1px solid rgba(99, 102, 241, 0.2);
  box-shadow: 
    0 10px 30px rgba(0, 0, 0, 0.1),
    inset 0 0 0 1px rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(10px);
}

.result-alert p {
  margin: 8px 0;
  color: #e2e8f0;
  font-size: 15px;
  line-height: 1.6;
}

.result-alert :deep(.el-alert__title) {
  font-size: 16px;
  font-weight: 600;
  color: #e2e8f0;
}

.result-alert :deep(.el-alert__content) {
  color: #94a3b8;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.question-container {
  animation: fadeIn 0.6s ease-out forwards;
}

.question-container:nth-child(2) {
  animation-delay: 0.1s;
}

.question-container:nth-child(3) {
  animation-delay: 0.2s;
}

@media (max-width: 768px) {
  .ai-playground {
    padding: 20px;
  }
  
  .question-container {
    padding: 20px;
  }
  
  .options-group :deep(.el-radio) {
    padding: 12px 15px;
  }
  
  .submit-action :deep(.el-button) {
    padding: 10px 25px;
    font-size: 15px;
  }
}
</style> 