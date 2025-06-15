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
            <span>AI靶场</span>
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
const topicHistoryLoaded = ref(false);
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
    if (topicHistoryLoaded.value) return;
    historyLoading.value = true;
    try {
        const data = await getTopicHistory();
        topicHistory.value = data;
        topicHistoryLoaded.value = true;
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
    ElMessage.error('题目生成失败，请稍后重试');
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
  height: 100%;
}
.content-wrapper {
  display: flex;
  min-height: calc(100vh - 60px);
}
.sidebar {
  background-color: #fff;
  border-right: 1px solid #e6e6e6;
}
.menu {
  border-right: none;
}
.main-content {
  flex: 1;
  padding: 20px;
  background-color: #f5f7fa;
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
  transition: all 0.3s;
  cursor: pointer;
}
.course-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}
.course-image {
  width: 100%;
  height: 150px;
  object-fit: cover;
  display: block;
}
.course-info {
  padding: 15px;
}
.course-info h3 {
  margin: 0 0 8px;
  font-size: 16px;
  color: #303133;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.teacher {
  margin: 0 0 10px;
  color: #909399;
  font-size: 14px;
  height: 21px; /* 预留空间防止视图切换时跳动 */
}
.card-footer {
  border-top: 1px solid #e4e7ed;
  padding: 10px 15px;
  text-align: right;
}
.action-btn {
  padding: 0;
  height: auto;
}
.report-card {
  margin-bottom: 20px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.chart-container {
  width: 100%;
  height: 400px;
}
.chart-container.large {
  height: 400px;
}
.stats-info {
  text-align: center;
}
.stats-number {
  font-size: 36px;
  color: #303133;
  font-weight: bold;
}
.stats-unit {
  font-size: 16px;
  margin-left: 4px;
}
.stats-compare {
  margin-top: 10px;
  color: #67c23a;
  font-size: 14px;
}
.placeholder-chart {
  color: #909399;
  font-size: 14px;
}
.mt-20 {
  margin-top: 20px;
}
.ai-playground .topic-input {
  margin-bottom: 20px;
}
.question-container {
  padding: 20px 0;
}
.question-text {
  font-size: 16px;
  margin-bottom: 20px;
  line-height: 1.6;
}
.options-group {
  display: flex;
  flex-direction: column;
  gap: 15px;
}
.submit-action {
  margin-top: 20px;
  text-align: right;
}
.question-card {
  margin-bottom: 20px;
}
.questions-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}
.result-alert {
  margin-top: 20px;
}
.result-alert p {
  margin: 5px 0;
}
.loading-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 200px;
}
.dialog-tip {
    margin-top: 15px;
    font-size: 12px;
    color: #909399;
    text-align: center;
}
.topic-card {
  margin-bottom: 20px;
  cursor: pointer;
  transition: all 0.3s;
}
.topic-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}
.topic-info h3 {
  margin: 0 0 8px;
  font-size: 16px;
}
.topic-meta {
  font-size: 12px;
  color: #909399;
  margin: 0 0 15px;
}
.topic-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.topic-progress {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-grow: 1;
  margin-right: 10px;
}
.progress-text {
  font-size: 12px;
  color: #606266;
  width: 70px; /* 防止文字换行 */
}
</style> 