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
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Reading, Search, Plus, User, CopyDocument } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { getTeacherCourses, updateCourse, deleteCourse } from '@/api/course'
import { getUploadToken } from '@/api/courseChapter'

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
    qiniuData.value.token = res
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