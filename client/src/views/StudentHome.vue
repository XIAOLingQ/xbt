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
                    <div class="stats-number">{{ (reportData.totalStudyDurationMinutes / 60).toFixed(1) }}<span class="stats-unit">小时</span></div>
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
                    <div class="stats-number">{{ reportData.completedCoursesCount }} / {{ reportData.totalCoursesCount }}</div>
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
                    <div class="stats-number">{{ reportData.averageScore.toFixed(1) }}<span class="stats-unit">分</span></div>
                    <div class="stats-compare">已完成 {{ reportData.completedHomeworkCount }} / {{ reportData.totalHomeworkCount }} 个作业</div>
                  </div>
                </el-card>
              </el-col>
            </el-row>

            <el-row :gutter="20" class="mt-20">
              <el-col :span="24">
                <el-card class="report-card">
                  <template #header>
                    <div class="card-header">
                      <span>学习趋势</span>
                      <el-radio-group v-model="timeRange" size="small">
                        <el-radio-button label="week">本周</el-radio-button>
                        <el-radio-button label="month">本月</el-radio-button>
                        <el-radio-button label="year">全年</el-radio-button>
                      </el-radio-group>
                    </div>
                  </template>
                  <div class="chart-container large">
                    <div class="placeholder-chart">学习时长趋势图 (功能开发中)</div>
                  </div>
                </el-card>
              </el-col>
            </el-row>
          </div>
          <el-empty v-if="!reportLoading && !reportData" description="暂无学习报告数据"></el-empty>
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
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Reading, DataLine, Search, Plus } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { getStudentCourses, joinCourse } from '@/api/course'
import { getMyReportSummary } from '@/api/report'

const router = useRouter()
const userStore = useUserStore()

const activeMenu = ref('courses')
const searchText = ref('')
const courses = ref([])
const timeRange = ref('week')

const reportData = ref(null);
const reportLoading = ref(false);

const joinCourseDialogVisible = ref(false)
const courseCodeInput = ref('')
const isJoining = ref(false)

onMounted(() => {
  fetchCourses()
})

const fetchCourses = async () => {
  try {
    const res = await getStudentCourses({ page: 1, size: 10 })
    courses.value = res.list
  } catch (error) {
    ElMessage.error('获取课程列表失败')
    console.error(error)
  }
}

const filteredCourses = computed(() => {
  if (!searchText.value) return courses.value
  const search = searchText.value.toLowerCase()
  return courses.value.filter(course => 
    course.title.toLowerCase().includes(search)
  )
})

const fetchReportData = async () => {
  reportLoading.value = true;
  try {
    const data = await getMyReportSummary();
    console.log('从接口获取到的原始报告数据:', data);
    reportData.value = data;
  } catch (error) {
    ElMessage.error('获取学习报告失败');
    console.error(error);
  } finally {
    reportLoading.value = false;
  }
}

const handleSelect = (key) => {
  activeMenu.value = key
  if (key === 'reports') {
    fetchReportData();
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
  height: 200px;
  display: flex;
  align-items: center;
  justify-content: center;
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
</style> 