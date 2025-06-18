<template>
  <div class="create-course-container">
    <Particles
      id="tsparticles"
      class="course-bg"
      :particlesInit="particlesInit"
      :options="particlesOptions"
    />
    <el-page-header @back="goBack" class="page-header">
  <template #content>
    <span class="page-title">创建新课程</span>
  </template>
</el-page-header>
    
    <el-card class="form-card">
      <el-form :model="courseForm" :rules="rules" ref="courseFormRef" label-width="100px">
        <el-form-item label="课程标题" prop="title">
          <el-input v-model="courseForm.title" placeholder="请输入课程标题"></el-input>
        </el-form-item>
        <el-form-item label="课程描述" prop="description">
          <el-input
            type="textarea"
            :rows="4"
            v-model="courseForm.description"
            placeholder="请输入课程描述"
          ></el-input>
        </el-form-item>
        <el-form-item label="课程封面" prop="coverImage">
          <el-upload
            class="cover-uploader"
            :action="qiniuData.uploadUrl"
            :data="qiniuData"
            :show-file-list="false"
            :on-success="handleCoverSuccess"
            :before-upload="beforeCoverUpload"
          >
            <img v-if="courseForm.coverImage" :src="courseForm.coverImage" class="cover-image" />
            <el-icon v-else class="cover-uploader-icon"><Plus /></el-icon>
          </el-upload>
           <div class="el-upload__tip">
            只能上传jpg/png文件，且不超过2MB
          </div>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submitForm" :loading="loading">立即创建</el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { Plus } from '@element-plus/icons-vue';
import { createCourse } from '@/api/course';
import { getUploadToken } from '@/api/file';
import { loadFull } from 'tsparticles'

const router = useRouter();
const courseFormRef = ref(null);
const loading = ref(false);

const particlesInit = async (engine) => {
  await loadFull(engine)
}

const particlesOptions = {
  background: {
    color: {
      value: "transparent"
    }
  },
  particles: {
    number: {
      value: 80
    },
    color: {
      value: "#6996f8"
    },
    shape: {
      type: "circle"
    },
    opacity: {
      value: 0.7
    },
    size: {
      value: 3,
      random: true
    },
    links: {
      enable: true,
      color: "#6996f8",
      distance: 120,
      opacity: 0.5,
      width: 1
    },
    move: {
      enable: true,
      speed: 2,
      direction: "none",
      random: true,
      straight: false,
      out_mode: "out"
    }
  },
  interactivity: {
    events: {
      onHover: {
        enable: true,
        mode: "grab"
      },
      onClick: {
        enable: true,
        mode: "push"
      }
    }
  }
};

// --- 七牛云上传相关 ---
const QINIU_DOMAIN = 'http://sxh8oib6z.hb-bkt.clouddn.com';
const qiniuData = ref({
  token: '',
  key: '',
  uploadUrl: 'http://upload-z1.qiniup.com'
});

const courseForm = ref({
  title: '',
  description: '',
  coverImage: '',
});

const rules = {
  title: [{ required: true, message: '请输入课程标题', trigger: 'blur' }],
  description: [{ required: true, message: '请输入课程描述', trigger: 'blur' }],
};

const goBack = () => {
  router.back();
};

const handleCoverSuccess = (res, uploadFile) => {
  const newCoverUrl = `${QINIU_DOMAIN}/${res.key}`;
  courseForm.value.coverImage = newCoverUrl;
  ElMessage.success('封面上传成功!');
};

const beforeCoverUpload = async (rawFile) => {
  const isJpgOrPng = rawFile.type === 'image/jpeg' || rawFile.type === 'image/png';
  const isLt2M = rawFile.size / 1024 / 1024 < 2;

  if (!isJpgOrPng) {
    ElMessage.error('封面图片只能是 JPG/PNG 格式!');
    return false;
  }
  if (!isLt2M) {
    ElMessage.error('封面图片大小不能超过 2MB!');
    return false;
  }
  
  try {
    const token = await getUploadToken();
    qiniuData.value.token = token;
    const fileExt = rawFile.name.split('.').pop() || 'png';
    // 文件名可以取得更唯一，例如加上用户ID，但此处为简化流程
    qiniuData.value.key = `course-covers/temp_${Date.now()}.${fileExt}`;
    return true;
  } catch(e) {
    ElMessage.error('获取上传凭证失败，请重试');
    return false;
  }
};

const submitForm = () => {
  courseFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true;
      try {
        const res = await createCourse(courseForm.value);
        ElMessage.success(`课程《${res.title}》创建成功！课程码为：${res.courseCode}`);
        router.push('/');
      } catch (error) {
        ElMessage.error(error.message || '创建失败');
      } finally {
        loading.value = false;
      }
    } else {
      return false;
    }
  });
};

const resetForm = () => {
  courseFormRef.value.resetFields();
};
</script>

<style scoped>
.create-course-container {
  padding: 100px 40px 40px;
  --primary-color: #6996f8;
  --primary-light: #ebf2ff;
  --primary-lighter: #f5f8ff;
  --border-color: #e2e8f0;
  --text-primary: #1e293b;
  --text-secondary: #64748b;
  --background-color: #e8f1f8;
  --card-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
  --card-shadow-hover: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
  min-height: 100vh;
}

.course-bg {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: -1;
}

.create-course-container {
  position: relative;
  z-index: 2;
  background: #e8f1f8(248, 250, 252, 0.7);
}

.page-header {
  margin-bottom: 80px;
  padding-bottom: 15px;
  position: relative;
}

.page-header:after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 60px;
  height: 3px;
  background: var(--primary-color);
  border-radius: 3px;
}

.page-header {
  position: fixed;
  top: 60px;
  left: 0;
  right: 0;
  margin: 0;
  padding: 15px 20px;
  border-radius: 0 0 12px 12px;
  box-shadow: 0 2px 12px 0 rgba(255, 255, 255, 0.1);
  transition: all 0.3s ease;
  z-index: 10;
}

.page-header:hover {
  box-shadow: 0 2px 12px 0 rgba(255, 255, 255, 0.1);
}

.page-header:after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 20px;
  width: 60px;
  height: 3px;
  background: var(--primary-color);
  border-radius: 3px;
  transition: all 0.3s ease;
}

.page-header :deep(.el-page-header__content) {
  display: flex;
  align-items: center;
}

.page-header :deep(.el-page-header__back) {
  font-size: 1.2rem;
  margin-right: 12px;
  color: var(--primary-color);
  transition: all 0.3s ease;
}

.page-header :deep(.el-page-header__back):hover {
  color: var(--primary-color);
}

.page-title {
  color: var(--text-primary);
  font-weight: 600;
  font-size: 1.2rem;
  margin-right: 12px;
}

.form-card {
  max-width: 720px;
  margin: 0 auto;
  border-radius: 12px;
  border: none;
  box-shadow: var(--card-shadow);
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(8px);
  overflow: hidden;
  position: relative;
  z-index: 1;
}

.form-card:before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 4px;
  height: 100%;
  background: var(--primary-color);
}

.form-card:hover {
  box-shadow: var(--card-shadow-hover);
  transform: translateY(-2px);
}

.el-form {
  padding: 30px 40px;
}

.el-form-item {
  margin-bottom: 28px;
  transition: all 0.3s ease;
}

.el-form-item:hover {
  transform: translateX(4px);
}

.el-form-item:deep(.el-form-item__label) {
  font-weight: 500;
  color: var(--text-primary);
  padding-bottom: 8px;
}

.el-form-item :deep(.el-input__wrapper) {
  background: rgba(255, 255, 255, 0.9);
  border: 1px solid var(--border-color);
  box-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
}

.el-form-item :deep(.el-input__wrapper:hover) {
  border-color: var(--primary-color);
  background-color: rgba(105, 150, 248, 0.05);
  box-shadow: 0 4px 6px -1px rgba(105, 150, 248, 0.1), 0 2px 4px -1px rgba(105, 150, 248, 0.06);
}

.el-form-item :deep(.el-input__wrapper.is-focus) {
  border-color: var(--primary-color);
  box-shadow: 0 4px 6px -1px rgba(105, 150, 248, 0.1), 0 2px 4px -1px rgba(105, 150, 248, 0.06);
}

.el-form-item :deep(.el-textarea__inner) {
  background: rgba(255, 255, 255, 0.9);
  border: 1px solid var(--border-color);
  box-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
}

.el-form-item :deep(.el-textarea__inner:hover) {
  border-color: var(--primary-color);
  background-color: rgba(105, 150, 248, 0.05);
  box-shadow: 0 4px 6px -1px rgba(105, 150, 248, 0.1), 0 2px 4px -1px rgba(105, 150, 248, 0.06);
}

.el-form-item :deep(.el-textarea__inner:focus) {
  border-color: var(--primary-color);
  box-shadow: 0 4px 6px -1px rgba(105, 150, 248, 0.1), 0 2px 4px -1px rgba(105, 150, 248, 0.06);
}

.cover-uploader :deep(.el-upload) {
  border: 2px dashed var(--border-color);
  border-radius: 12px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  background-color: rgba(255, 255, 255, 0.8);
  box-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.05);
}

.cover-uploader :deep(.el-upload:hover) {
  border-color: var(--primary-color);
  background-color: rgba(37, 99, 235, 0.05);
  box-shadow: 0 4px 6px -1px rgba(37, 99, 235, 0.1), 0 2px 4px -1px rgba(37, 99, 235, 0.06);
}

.cover-uploader :deep(.el-upload):after {
  content: '点击或拖拽上传封面';
  position: absolute;
  bottom: 10px;
  left: 0;
  width: 100%;
  text-align: center;
  color: var(--text-secondary);
  font-size: 14px;
  transition: all 0.3s ease;
}

.cover-uploader-icon {
  font-size: 32px;
  color: var(--text-secondary);
  width: 200px;
  height: 200px;
  line-height: 200px;
  text-align: center;
  transition: all 0.3s ease;
}

.cover-image {
  width: 100%;
  height: 200px;
  display: block;
  object-fit: cover;
}

.el-upload__tip {
  font-size: 13px;
  color: var(--text-secondary);
  margin-top: 8px;
}

.el-button {
  padding: 12px 28px;
  border-radius: 8px;
  font-weight: 500;
  letter-spacing: 0.025em;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}

.el-button--primary {
  min-width: 140px;
  background-color: var(--primary-color);
  border-color: var(--primary-color);
  box-shadow: 0 4px 6px -1px rgba(37, 99, 235, 0.3), 0 2px 4px -1px rgba(37, 99, 235, 0.1);
}

.el-button--primary:hover {
  background-color: #1d4ed8;
  border-color: #1d4ed8;
  box-shadow: 0 10px 15px -3px rgba(37, 99, 235, 0.3), 0 4px 6px -2px rgba(37, 99, 235, 0.1);
  transform: translateY(-1px);
}

.el-button--primary:active {
  transform: translateY(0);
}

.el-button--primary:after {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: rgba(255, 255, 255, 0.1);
  transform: rotate(30deg);
  transition: all 0.3s ease;
}

.el-button--primary:hover:after {
  left: 100%;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .create-course-container {
    padding: 20px 15px;
    background: var(--background-color);
  }
  
  .page-header :deep(.el-page-header__content) {
    font-size: 1.3rem;
  }
  
  .form-card {
    border-radius: 0;
    box-shadow: none;
    background: white;
  }
  
  .form-card:before {
    display: none;
  }
  
  .el-form {
    padding: 20px;
  }
  
  .cover-uploader :deep(.el-upload) {
    width: 100%;
  }
  
  .cover-uploader-icon,
  .cover-image {
    width: 100%;
    height: 180px;
    line-height: 180px;
  }
  
  .el-button {
    width: 100%;
    margin-bottom: 10px;
  }
}
</style>