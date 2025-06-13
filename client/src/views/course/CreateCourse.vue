<template>
  <div class="create-course-container">
    <el-page-header @back="goBack" content="创建新课程" class="page-header"></el-page-header>
    
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
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { Plus } from '@element-plus/icons-vue';
import { createCourse } from '@/api/course';
import { getUploadToken } from '@/api/file';

const router = useRouter();
const courseFormRef = ref(null);
const loading = ref(false);

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
  padding: 20px;
}
.page-header {
  margin-bottom: 20px;
}
.form-card {
  max-width: 600px;
  margin: 0 auto;
}
.cover-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: .2s;
}
.cover-uploader .el-upload:hover {
  border-color: #409eff;
}
.cover-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  text-align: center;
}
.cover-image {
  width: 178px;
  height: 178px;
  display: block;
}
</style> 