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
            <span>我教的课程</span>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <!-- 主要内容区域 -->
      <el-main class="main-content">
        <!-- 课程列表 -->
        <div v-if="activeMenu === 'courses'">
          <div class="section-header">
            <h2>课程管理</h2>
            <div class="actions">
              <el-input
                v-model="searchText"
                placeholder="搜索课程"
                class="search-input"
                :prefix-icon="Search"
                clearable
              />
              <el-button type="primary" :icon="Plus" @click="handleCreateCourse">创建课程</el-button>
            </div>
          </div>
          
          <el-row :gutter="20" v-if="filteredCourses.length > 0">
            <el-col :span="6" v-for="course in filteredCourses" :key="course.id">
              <el-card class="course-card" :body-style="{ padding: '0px' }" @click="handleCourseClick(course.id)">
                <img :src="course.coverImage" class="course-image">
                <div class="course-info">
                  <h3>{{ course.title }}</h3>
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
                </div>
                <div class="card-footer">
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
                </div>
              </el-card>
            </el-col>
          </el-row>
          <el-empty v-else description="暂无课程" />
        </div>
      </el-main>
    </div>

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
  <ChatContainer v-if="courseId" :course-id="courseId" />
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Reading, Search, Plus, User, CopyDocument } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { getTeacherCourses, updateCourse, deleteCourse } from '@/api/course'
import { getUploadToken } from '@/api/courseChapter'
import ChatContainer from '@/components/common/ChatContainer.vue';
const router = useRouter()
const userStore = useUserStore()

const activeMenu = ref('courses')
const searchText = ref('')
const courses = ref([])

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
    const res = await getUploadToken()
    const r = await getUploadToken()
    qiniuData.value.token = r
  } catch(e) {
    ElMessage.error("获取上传凭证失败");
  }
}

const fetchCourses = async () => {
  try {
    const res = await getTeacherCourses({ page: 1, size: 10 })
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

const handleSelect = (key) => {
  activeMenu.value = key
}

const handleCreateCourse = () => {
  router.push('/course/create')
}

const openEditDialog = (course) => {
  editingCourse.value = JSON.parse(JSON.stringify(course));
  editDialogVisible.value = true;
}

const handleUpdateCourse = async () => {
  if (!editingCourse.value) return;
  isUpdating.value = true;
  try {
    await updateCourse(editingCourse.value);
    ElMessage.success('课程信息更新成功');
    editDialogVisible.value = false;
    await fetchCourses();
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
    fetchCourses();
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

const handleCourseClick = (courseId) => {
  router.push(`/course/manage/${courseId}`);
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
}
.actions {
  display: flex;
  align-items: center;
  gap: 15px;
}
.search-input {
  width: 250px;
  transition: all 0.3s ease;
}

.search-input :deep(.el-input__wrapper) {
  background: rgba(255, 255, 255, 0.9);
  border: 1px solid rgba(105, 150, 248, 0.3);
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
}

.search-input :deep(.el-input__wrapper:hover) {
  border-color: rgba(105, 150, 248, 0.5);
  box-shadow: 0 2px 6px rgba(105, 150, 248, 0.1);
}

.search-input :deep(.el-input__wrapper.is-focus) {
  border-color: rgba(105, 150, 248, 0.8);
  box-shadow: 0 2px 8px rgba(105, 150, 248, 0.15);
  transform: translateY(-1px);
}

.search-input :deep(.el-input__inner::placeholder) {
  color: rgba(100, 116, 139, 0.6);
}
.course-card {
  margin-bottom: 20px;
  border-radius: 12px;
  border: none;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(8px);
  overflow: hidden;
  position: relative;
  cursor: pointer;
}

.course-card:hover {
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
  transform: translateY(-2px);
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
}

.course-card:hover:before {
  width: 6px;
  background: #1d4ed8;
  transition: all 0.3s ease;
}

.course-image:hover {
  transform: scale(1.02);
  transition: transform 0.3s ease;
}

.course-card:before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 4px;
  height: 100%;
  background: #6996f8;
}

.course-image {
  width: 100%;
  height: 180px;
  object-fit: cover;
  display: block;
}

.course-info {
  padding: 16px;
}

.course-info h3 {
  margin: 0 0 10px;
  font-size: 16px;
  color: #1e293b;
  font-weight: 500;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.course-code-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.copy-icon {
  cursor: pointer;
  color: #6996f8;
  transition: all 0.3s;
}

.copy-icon:hover {
  color: #1d4ed8;
  transform: scale(1.2);
  transition: all 0.2s ease;
}

.teacher {
  color: #64748b;
  font-size: 14px;
}

.course-stats {
  display: flex;
  align-items: center;
  color: #64748b;
  font-size: 14px;
}

.course-stats .el-icon {
  margin-right: 6px;
  color: #6996f8;
}

.card-footer {
  border-top: 1px solid #e2e8f0;
  padding: 12px 16px;
  display: flex;
  justify-content: space-between;
}

.action-btn {
  padding: 0;
  height: auto;
  color: #6996f8;
  position: relative;
  transition: all 0.3s ease;
}

.action-btn:hover {
  color: #1d4ed8;
  transform: translateX(2px);
}

.action-btn:after {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 0;
  width: 0;
  height: 1px;
  background: #1d4ed8;
  transition: width 0.3s ease;
}

.action-btn:hover:after {
  width: 100%;
}
/* --- 编辑对话框样式 --- */
:deep(.el-dialog) {
  border-radius: 12px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.15);
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
}

:deep(.el-dialog__header) {
  border-bottom: 1px solid rgba(105, 150, 248, 0.2);
  padding: 16px 20px;
  margin-right: 0;
  background: rgba(255, 255, 255, 0.8);
}

:deep(.el-dialog__title) {
  font-size: 18px;
  font-weight: 500;
  color: #1e293b;
}

:deep(.el-dialog__body) {
  padding: 20px;
}

:deep(.el-form-item__label) {
  color: #64748b;
  font-weight: 500;
}

:deep(.el-dialog .el-input__wrapper) {
  background: rgba(255, 255, 255, 0.9);
  border: 1px solid rgba(105, 150, 248, 0.3);
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
}

:deep(.el-dialog .el-input__wrapper:hover) {
  border-color: rgba(105, 150, 248, 0.5);
  box-shadow: 0 2px 6px rgba(105, 150, 248, 0.1);
}

:deep(.el-dialog .el-input__wrapper.is-focus) {
  border-color: rgba(105, 150, 248, 0.8);
  box-shadow: 0 2px 8px rgba(105, 150, 248, 0.15);
  transform: translateY(-1px);
}

:deep(.el-dialog .el-textarea__inner) {
  background: rgba(255, 255, 255, 0.9);
  border: 1px solid rgba(105, 150, 248, 0.3);
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
}

:deep(.el-dialog .el-textarea__inner:hover) {
  border-color: rgba(105, 150, 248, 0.5);
  box-shadow: 0 2px 6px rgba(105, 150, 248, 0.1);
}

:deep(.el-dialog .el-textarea__inner:focus) {
  border-color: rgba(105, 150, 248, 0.8);
  box-shadow: 0 2px 8px rgba(105, 150, 248, 0.15);
}

:deep(.el-dialog__footer) {
  border-top: 1px solid rgba(105, 150, 248, 0.2);
  padding: 16px 20px;
  text-align: right;
  background: rgba(255, 255, 255, 0.8);
}

:deep(.el-dialog__footer .el-button) {
  background: rgba(105, 150, 248, 0.1);
  backdrop-filter: blur(5px);
  border: 1px solid rgba(105, 150, 248, 0.2);
  color: #1d4ed8;
  transition: all 0.3s ease;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
}

:deep(.el-dialog__footer .el-button:hover) {
  background: rgba(105, 150, 248, 0.2);
  transform: translateY(-2px);
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.1);
}

:deep(.el-dialog__footer .el-button--primary) {
  background: rgba(105, 150, 248, 0.2);
  color: white;
}

:deep(.el-dialog__footer .el-button--primary:hover) {
  background: rgba(105, 150, 248, 0.3);
}

/* --- 课程封面上传样式 --- */
.cover-uploader :deep(.el-upload) {
  border: 1px dashed #e2e8f0;
  border-radius: 8px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: all 0.3s;
  background-color: #f8fafc;
}

.cover-uploader :deep(.el-upload:hover) {
  border-color: #6996f8;
  background-color: rgba(105, 150, 248, 0.05);
}

.cover-uploader-icon {
  font-size: 28px;
  color: #64748b;
  width: 178px;
  height: 178px;
  line-height: 178px;
  text-align: center;
  transition: all 0.3s;
}

.cover-uploader-icon:hover {
  color: #6996f8;
  transform: scale(1.05);
  transition: all 0.3s ease;
}

.cover-image-preview {
  width: 178px;
  height: 178px;
  object-fit: cover;
  display: block;
  border-radius: 6px;
}
</style> 