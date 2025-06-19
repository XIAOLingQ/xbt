<template>
  <div class="register-container">
    <Particles
      id="tsparticles"
      class="register-particles"
      :particlesInit="particlesInit"
      :options="particlesOptions"
    />
    
    <!-- 中央图片容器 - 与登录页一致 -->
    <div class="image-container">
      <img src="@/assets/login-illustration.png" alt="背景图" class="center-image" />
      
      <!-- 半透明注册卡片 - 位于图片左侧50%区域 -->
      <el-card class="register-card">
        <template #header>
          <div class="card-header">
            <h2>创建您的账号</h2>
            <p>开启您的学习之旅</p>
          </div>
        </template>
        
        <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          label-width="80px"
          status-icon
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

          <el-form-item label="确认密码" prop="confirmPassword">
            <el-input
              v-model="form.confirmPassword"
              type="password"
              placeholder="请确认密码"
              show-password
            />
          </el-form-item>

          <el-form-item label="邮箱" prop="email">
            <el-input v-model="form.email" placeholder="请输入邮箱" />
          </el-form-item>

          <el-form-item label="角色" prop="role">
            <el-radio-group v-model="form.role">
              <el-radio :label="1">学生</el-radio>
              <el-radio :label="2">教师</el-radio>
            </el-radio-group>
          </el-form-item>

          <el-form-item label="真实姓名" prop="realName">
            <el-input v-model="form.realName" placeholder="请输入真实姓名" />
          </el-form-item>

          <el-form-item>
            <el-button type="primary" @click="handleSubmit" :loading="loading" class="login-btn">
              注册
            </el-button>
          </el-form-item>
          
          <el-form-item class="register-link">
            <el-button text @click="$router.push('/login')">
              已有账号？返回登录
            </el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { ElMessage } from 'element-plus'
import { loadFull } from 'tsparticles'

// 粒子背景初始化函数
const particlesInit = async (engine) => {
  await loadFull(engine)
}

// 粒子背景配置 - 与登录页完全一致
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
};

const router = useRouter()
const userStore = useUserStore()
const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  email: '',
  role: 1,
  realName: ''
})

// 表单验证规则 - 保持不变
const validatePass = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请输入密码'))
  } else {
    if (form.confirmPassword !== '') {
      formRef.value?.validateField('confirmPassword')
    }
    callback()
  }
}

const validatePass2 = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== form.password) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

const validateUsername = async (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请输入用户名'))
  } else {
    try {
      const res = await userStore.checkUsername(value)
      if (res) {
        callback(new Error('用户名已存在'))
      } else {
        callback()
      }
    } catch (error) {
      callback(new Error('验证用户名失败'))
    }
  }
}

const validateEmail = async (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请输入邮箱'))
  } else {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
    if (!emailRegex.test(value)) {
      callback(new Error('请输入有效的邮箱地址'))
    } else {
      try {
        const res = await userStore.checkEmail(value)
        if (res) {
          callback(new Error('邮箱已被使用'))
        } else {
          callback()
        }
      } catch (error) {
        callback(new Error('验证邮箱失败'))
      }
    }
  }
}

const rules = {
  username: [
    { required: true, validator: validateUsername, trigger: 'blur' }
  ],
  password: [
    { required: true, validator: validatePass, trigger: 'blur' },
    { min: 6, message: '密码长度不能小于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, validator: validatePass2, trigger: 'blur' }
  ],
  email: [
    { required: true, validator: validateEmail, trigger: 'blur' }
  ],
  role: [
    { required: true, message: '请选择角色', trigger: 'change' }
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
    
    const { confirmPassword, ...registerData } = form
    await userStore.register(registerData)
    
    ElMessage.success('注册成功')
    router.push('/login')
  } catch (error) {
    if (error.message) {
      ElMessage.error(error.message)
    }
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
/* 继承登录页样式，保持一致性 */
.register-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f5f7fa;
  position: relative;
}

.register-particles {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 0;
}

.image-container {
  position: relative;
  width: 800px;
  height: 600px;
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

.register-card {
  position: absolute;
  top: 0;
  left: 0;
  width: 50%;
  height: 100%;
  border-radius: 15px 0 0 15px;
  background-color: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(10px);
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

/* 响应式设计 - 与登录页一致 */
@media (max-width: 768px) {
  .image-container {
    width: 90%;
    height: auto;
    min-height: 400px;
  }
  
  .register-card {
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

/* 表单元素样式 - 保持一致 */
:deep(.el-form-item__label) {
  font-weight: 500;
}

:deep(.el-input__wrapper) {
  box-shadow: 0 0 0 1px #dcdfe6 inset;
}

:deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #c0c4cc inset;
}

:deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #409eff inset;
}
</style>