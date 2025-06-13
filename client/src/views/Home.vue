<template>
  <div class="home">
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
            <span>{{ userStore.isTeacher ? '我教的课程' : '我的课程' }}</span>
          </el-menu-item>
          <el-menu-item index="reports" v-if="!userStore.isTeacher">
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
            <h2>{{ userStore.isTeacher ? '课程管理' : '我的课程' }}</h2>
            <div class="actions">
              <el-input
                v-model="searchText"
                placeholder="搜索课程"
                class="search-input"
                :prefix-icon="Search"
                clearable
              />
              <el-button v-if="userStore.isTeacher" type="primary" :icon="Plus" @click="handleCreateCourse">创建课程</el-button>
              <el-button v-else type="primary" :icon="Plus" @click="joinCourseDialogVisible = true">加入课程</el-button>
            </div>
          </div>
          
          <el-row :gutter="20" v-if="filteredCourses.length > 0">
            <el-col :span="6" v-for="course in filteredCourses" :key="course.id">
              <el-card class="course-card" :body-style="{ padding: '0px' }" @click="handleCourseClick(course.id)">
                <img :src="course.coverImage" class="course-image">
                <div class="course-info">
                  <h3>{{ course.title }}</h3>
                  
                  <!-- 学生视图 -->
                  <template v-if="!userStore.isTeacher">
                    教学老师：<h4 class="teacher" style="display: inline;">{{ course.teacherName }}</h4>
                  </template>

                  <!-- 教师视图 -->
                  <template v-else>
                     <div class="teacher course-code-container">
                        <span>课程码: {{ course.courseCode }}</span>
                        <el-tooltip content="复制课程码" placement="top">
                          <el-icon class="copy-icon" @click.stop="copyCourseCode(course.courseCode)"><CopyDocument /></el-icon>
                        </el-tooltip>
                     </div>
                     <div class="course-stats">
                        <el-icon><User /></el-icon>
                        <span>{{ course.studentCount }} 人报名</span>
                     </div>
                  </template>
                </div>
                <div class="card-footer">
                  <template v-if="userStore.isTeacher">
                    <el-button type="primary" link class="action-btn" @click.stop="openEditDialog(course)">编辑信息</el-button>
                    <el-button type="primary" link class="action-btn">内容管理</el-button>
                    <el-popconfirm
                      title="确定要删除这门课程吗？"
                      confirm-button-text="确定"
                      cancel-button-text="取消"
                      @confirm.stop="handleDeleteCourse(course.id)"
                    >
                      <template #reference>
                        <el-button type="danger" link class="action-btn" @click.stop>删除</el-button>
                      </template>
                    </el-popconfirm>
                  </template>
                  <template v-else>
                    <el-button type="primary" link class="action-btn">进入学习</el-button>
                  </template>
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
                    <!-- 这里可以添加趋势图表 -->
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

    <!-- 编辑课程信息对话框 -->
    <el-dialog v-model="editDialogVisible" title="编辑课程信息" width="600px">
      <el-form v-if="editingCourse" :model="editingCourse" label-width="100px">
        <el-form-item label="课程标题">
          <el-input v-model="editingCourse.title"></el-input>
        </el-form-item>
        <el-form-item label="课程描述">
          <el-input v-model="editingCourse.description" type="textarea" :rows="3"></el-input>
        </el-form-item>
        <el-form-item label="课程封面">
          <el-upload
            class="cover-uploader"
            :action="qiniuData.uploadUrl"
            :data="qiniuData"
            :show-file-list="false"
            :on-success="handleCoverUploadSuccess"
            :before-upload="beforeCoverUpload"
          >
            <img v-if="editingCourse.coverImage" :src="editingCourse.coverImage" class="cover-image-preview" />
            <el-icon v-else class="cover-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="editDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleUpdateCourse" :loading="isUpdating">
            保存
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
import { Reading, DataLine, Search, Plus, User, CopyDocument } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { getStudentCourses, getTeacherCourses, joinCourse, updateCourse, deleteCourse } from '@/api/course'
import { getUploadToken } from '@/api/courseChapter'
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

// --- 编辑课程信息 ---
const editDialogVisible = ref(false)
const editingCourse = ref(null)
const isUpdating = ref(false)

// --- 七牛云上传相关 ---
const QINIU_DOMAIN = 'http://sxh8oib6z.hb-bkt.clouddn.com';
const qiniuData = ref({
  token: '',
  key: '',
  uploadUrl: 'http://upload-z1.qiniup.com'
});

onMounted(() => {
  fetchCourses()
  fetchUploadToken()
})

const fetchUploadToken = async () => {
  try {
    if(userStore.isTeacher) {
      const res = await getUploadToken()
      qiniuData.value.token = res
    }
  } catch(e) {
    ElMessage.error("获取上传凭证失败");
  }
}

const fetchCourses = async () => {
  try {
    const apiCall = userStore.isTeacher ? getTeacherCourses : getStudentCourses
    const res = await apiCall({ page: 1, size: 10 })
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
  if (userStore.isTeacher) return;
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

const handleCreateCourse = () => {
  // 假设跳转到创建课程页面
  router.push('/course/create')
}

const openEditDialog = (course) => {
  editingCourse.value = JSON.parse(JSON.stringify(course)); // 深拷贝以避免直接修改列表中的数据
  editDialogVisible.value = true;
}

const handleUpdateCourse = async () => {
  if (!editingCourse.value) return;
  isUpdating.value = true;
  try {
    await updateCourse(editingCourse.value);
    ElMessage.success('课程信息更新成功');
    editDialogVisible.value = false;
    await fetchCourses(); // 更新成功后刷新列表
  } catch (error) {
    ElMessage.error(error.message || '更新失败');
  } finally {
    isUpdating.value = false;
  }
};

const handleDeleteCourse = async (courseId) => {
  try {
    await deleteCourse(courseId);
    ElMessage.success('课程删除成功');
    fetchCourses(); // 重新获取课程列表
  } catch (error) {
    ElMessage.error(error.message || '删除失败');
  }
};

const beforeCoverUpload = async (file) => {
  if (!qiniuData.value.token) {
    ElMessage.error('上传凭证获取失败，请重试');
    await fetchUploadToken();
    if (!qiniuData.value.token) return false;
  }
  const fileExt = file.name.split('.').pop() || 'png';
  qiniuData.value.key = `course-covers/${editingCourse.value.id}_${Date.now()}.${fileExt}`;
  return true;
};

const handleCoverUploadSuccess = (res) => {
    const newCoverUrl = `${QINIU_DOMAIN}/${res.key}`; 
    if(editingCourse.value) {
        editingCourse.value.coverImage = newCoverUrl;
    }
    ElMessage.success('封面上传成功!');
};

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
  if (userStore.isTeacher) {
    // 教师点击进入课程管理页面
    router.push(`/course/manage/${courseId}`);
  } else {
    // 学生点击进入课程学习页面
    router.push(`/course/learn/${courseId}`);
  }
}

const percentageFormat = (percentage) => {
  return percentage === 100 ? '已完成' : `${percentage}%`
}

const copyCourseCode = async (code) => {
  if (!code) return;
  try {
    await navigator.clipboard.writeText(code);
    ElMessage.success('课程码已复制');
  } catch (err) {
    ElMessage.error('复制失败，您的浏览器可能不支持该功能');
  }
};
</script>

<style scoped>
.home {
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

.course-code-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.copy-icon {
  visibility: hidden;
  cursor: pointer;
  color: var(--el-color-primary);
  transition: visibility 0.2s ease-in-out;
}

.course-code-container:hover .copy-icon {
  visibility: visible;
}

.teacher {
  margin: 0 0 10px;
  color: #909399;
  font-size: 14px;
  height: 21px; /* 预留空间防止视图切换时跳动 */
}

.course-progress {
  margin: 10px 0;
}

.course-stats {
  display: flex;
  align-items: center;
  color: #909399;
  font-size: 14px;
}

.course-stats .el-icon {
  margin-right: 5px;
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

/* --- 课程封面上传样式 --- */
.cover-uploader :deep(.el-upload) {
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
}

.cover-uploader :deep(.el-upload:hover) {
  border-color: var(--el-color-primary);
}

.cover-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  text-align: center;
}

.cover-image-preview {
  width: 178px;
  height: 178px;
  object-fit: cover;
  display: block;
}
</style>