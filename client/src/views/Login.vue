<template>
  
  <div class="login-container">
    <Particles
      id="tsparticles"
      class="login-bg"
      :particlesInit="particlesInit"
      :options="particlesOptions"
    />
      
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
           <el-button text @click="$router.push('/register')">
            还没有账号？立即注册
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
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
};


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
    router.push('/')
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
</script>

<style scoped>
.login-bg {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: -1;  /* 一定要比登录框低 */
}

.login-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  /* background: linear-gradient(135deg, #72edf2 10%, #5151e5 100%); */
}

.login-card {
  width: 450px;
  border-radius: 15px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
  padding: 20px;
}

.card-header {
  text-align: center;
}
.card-header h2 {
  margin: 0;
  font-size: 24px;
}
.card-header p {
  margin-top: 5px;
  color: #888;
}

.login-btn {
  width: 100%;
}
.register-link {
  margin-top: -10px;
  text-align: center;
}
.register-link .el-button {
  width: auto;
}
</style> 