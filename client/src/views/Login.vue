<template>
  <div class="login-container">
    <Particles
      id="tsparticles"
      class="login-particles"
      :particlesInit="particlesInit"
      :options="particlesOptions"
    />
    
    <!-- 中央图片容器 -->
    <div class="image-container">
      <img src="@/assets/login-illustration.png" alt="背景图" class="center-image" />
      
      <!-- 半透明登录卡片 - 位于图片左侧50%区域 -->
      <el-card class="login-card">
        <template #header>
          <div class="card-header">
            <h2>欢迎回来</h2>
            <p>登录您的账号</p>
          </div>
        </template>
        
        <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          label-width="80px"
          @submit.prevent="handleSubmit"
        >
          <el-form-item label="用户名" prop="username">
            <el-input v-model="form.username" placeholder="请输入用户名" />
          </el-form-item>
          
          <el-form-item label="密码" prop="password">
            <el-input
              v-model="form.password"
              type="password"
              placeholder="请输入密码"
              show-password
            />
          </el-form-item>
          
          <el-form-item>
            <el-button type="primary" native-type="submit" :loading="loading" class="login-btn">
              登录
            </el-button>
          </el-form-item>
          
          <el-form-item class="register-link">
            <el-button text @click="handleRegister">
              还没有账号？立即注册
            </el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { ElMessage } from 'element-plus'
import { loadFull } from 'tsparticles'

const particlesInit = async (engine) => {
  await loadFull(engine)
}

const router = useRouter()
const userStore = useUserStore()
const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  username: '',
  password: ''
})

const particlesOptions = {
  background: {
    color: {
      value: "transparent"
    }
  },
  particles: {
    number: {
      value: 50
    },
    color: {
      value: "#888888"
    },
    shape: {
      type: "circle"
    },
    opacity: {
      value: 0.5
    },
    size: {
      value: 2
    },
    links: {
      enable: true,
      color: "#cccccc",
      distance: 150,
      opacity: 0.4,
      width: 1
    },
    move: {
      enable: true,
      speed: 3
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
}

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    loading.value = true
    
    await userStore.login(form)
    ElMessage.success('登录成功')
    router.push({ name: 'home' })
  } catch (error) {
    if (error.response?.data?.message) {
      ElMessage.error(error.response.data.message)
    } else {
      ElMessage.error('登录失败')
    }
  } finally {
    loading.value = false
  }
}

const handleRegister = () => {
  router.push('/register')
}
</script>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f5f7fa;
  position: relative;
}

.login-particles {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 0;
}

.image-container {
  position: relative;
  width: 600px;
  height: 400px;
  border-radius: 15px;
  overflow: hidden;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15);
  z-index: 10;
}

.center-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.login-card {
  position: absolute;
  top: 0;
  left: 0;
  width: 50%;
  height: 100%;
  border-radius: 15px 0 0 15px;
  background-color: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(10px);
  /* 减少顶部内边距，将表单元素上移 */
  padding: 20px 40px 40px 40px; 
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.card-header {
  text-align: center;
  margin-bottom: 30px;
}

.card-header h2 {
  margin: 0;
  font-size: 24px;
  color: #333;
  font-weight: 600;
}

.card-header p {
  margin-top: 10px;
  color: #666;
  font-size: 14px;
}

.login-btn {
  width: 100%;
  height: 44px;
  font-size: 16px;
  border-radius: 6px;
  margin-top: 15px;
}

.register-link {
  margin-top: 15px;
  text-align: center;
}

.register-link .el-button {
  color: #409EFF;
  font-size: 14px;
  padding: 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .image-container {
    width: 90%;
    height: auto;
    min-height: 400px;
  }
  
  .login-card {
    position: static;
    width: 100%;
    height: auto;
    border-radius: 15px;
    background-color: rgba(255, 255, 255, 0.95);
    padding: 30px 20px;
  }
  
  .center-image {
    display: none; /* 在移动设备上隐藏背景图 */
  }
}
</style>