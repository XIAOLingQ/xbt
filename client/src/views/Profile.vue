<template>
  <div class="profile-container">
    <el-card class="profile-card">
      <template #header>
        <div class="header">
          <h2>个人信息</h2>
          <el-button type="primary" @click="handleLogout">返回主页</el-button>
        </div>
      </template>
      
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="80px"
        @submit.prevent="handleSubmit"
      >
        <el-form-item label="用户名">
          <el-input v-model="form.username" disabled />
        </el-form-item>
        
        <el-form-item label="角色">
          <el-tag :type="form.role === 1 ? 'success' : 'warning'">
            {{ form.role === 1 ? '学生' : '教师' }}
          </el-tag>
        </el-form-item>
        
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
        
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="form.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        
        <el-form-item label="头像">
          <el-upload
            class="avatar-uploader"
            :action="qiniuData.uploadUrl"
            :data="qiniuData"
            :show-file-list="false"
            :on-success="handleAvatarSuccess"
            :before-upload="beforeAvatarUpload"
          >
            <img v-if="form.avatar" :src="form.avatar" class="avatar" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" native-type="submit" :loading="loading">
            保存修改
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getUploadToken } from '@/api/file'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref(null)
const loading = ref(false)

// --- 七牛云上传相关 ---
const QINIU_DOMAIN = 'http://sxh8oib6z.hb-bkt.clouddn.com'
const qiniuData = ref({
  token: '',
  key: '',
  uploadUrl: 'http://upload-z1.qiniup.com'
})

const form = reactive({
  username: '',
  role: 1,
  email: '',
  realName: '',
  avatar: ''
})

const validateEmail = async (rule, value, callback) => {
  if (!value) {
    callback(new Error('请输入邮箱'))
    return
  }
  
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  if (!emailRegex.test(value)) {
    callback(new Error('请输入正确的邮箱格式'))
    return
  }
  
  if (value !== userStore.userInfo?.email) {
    try {
      const res = await userStore.checkEmail(value)
      if (res.data) {
        callback(new Error('邮箱已存在'))
      } else {
        callback()
      }
    } catch (error) {
      callback()
    }
  } else {
    callback()
  }
}

const rules = {
  email: [
    { required: true, validator: validateEmail, trigger: 'blur' }
  ],
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' }
  ]
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    loading.value = true
    
    await userStore.updateUserInfo({
      email: form.email,
      realName: form.realName,
      avatar: form.avatar
    })
    ElMessage.success('修改成功')
  } catch (error) {
    if (error.response?.data?.message) {
      ElMessage.error(error.response.data.message)
    } else {
      ElMessage.error('修改失败')
    }
  } finally {
    loading.value = false
  }
}

const handleLogout = () => {
  router.push('/')
}

const handleAvatarSuccess = (res) => {
  const newAvatarUrl = `${QINIU_DOMAIN}/${res.key}`
  form.avatar = newAvatarUrl
  ElMessage.success('头像上传成功!')
  handleSubmit()
}

const beforeAvatarUpload = async (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('上传头像图片只能是图片格式!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('上传头像图片大小不能超过 2MB!')
    return false
  }

  try {
    const token = await getUploadToken()
    qiniuData.value.token = token
    const fileExt = file.name.split('.').pop() || 'png'
    qiniuData.value.key = `avatars/${userStore.userInfo.id}_${Date.now()}.${fileExt}`
    return true
  } catch (e) {
    ElMessage.error('获取上传凭证失败，请重试')
    return false
  }
}

onMounted(async () => {
  try {
    await userStore.getUserInfo()
    Object.assign(form, userStore.userInfo)
  } catch (error) {
    ElMessage.error('获取用户信息失败')
    router.push('/login')
  }
})
</script>

<style scoped>
.profile-container {
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
}

.profile-card {
  max-width: 600px;
  margin: 0 auto;
  border-radius: 12px;
  border: none;
  box-shadow: var(--card-shadow);
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(8px);
  overflow: hidden;
  position: relative;
}

.profile-card:hover {
  box-shadow: var(--card-shadow-hover);
  transform: translateY(-2px);
}

.profile-card:before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 4px;
  height: 100%;
  background: var(--primary-color);
  transition: all 0.3s ease;
}

.profile-card:hover:before {
  width: 6px;
}

:deep(.el-button) {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

:deep(.el-button:hover) {
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(99, 102, 241, 0.2);
}

:deep(.el-input__wrapper) {
  transition: all 0.2s ease;
}

:deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px var(--primary-color) inset;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid var(--border-color);
}

.header h2 {
  margin: 0;
  position: relative;
  padding-bottom: 8px;
}

.header h2:after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 40px;
  height: 3px;
  background: var(--primary-color);
  border-radius: 3px;
}

.avatar-uploader {
  text-align: center;
}

.avatar-uploader .avatar {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  object-fit: cover;
}

.avatar-uploader .el-upload {
  border: 1px dashed var(--el-border-color);
  border-radius: 50%;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
}

.avatar-uploader .el-upload:hover {
  border-color: var(--el-color-primary);
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 100px;
  height: 100px;
  text-align: center;
  line-height: 100px;
}
</style> 