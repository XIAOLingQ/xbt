<template>
  <div class="basic-layout">
    <Particles
      id="tsparticles"
      class="layout-bg"
      :particlesInit="particlesInit"
      :options="particlesOptions"
    />
    <!-- 顶部导航栏 -->
    <el-header class="header" height="60px">
  <Particles
    id="tsparticles"
    class="header-particles"
    :particlesInit="particlesInit"
    :options="particlesOptions"
  />
      <div class="header-left">
        <h1 class="logo">学习平台</h1>
      </div>
      <div class="header-right" v-if="userStore.isLoggedIn">
        <el-dropdown @command="handleCommand">
          <span class="user-info">
            <el-avatar :size="32" :src="userStore.userInfo.avatar" class="user-avatar">
              <!-- 头像加载失败或未设置时的后备内容 -->
              <el-icon><UserFilled /></el-icon>
            </el-avatar>
            <span class="user-name">{{ userStore.userInfo?.realName }}</span>
            <el-icon class="arrow-down"><ArrowDown /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="profile">个人信息</el-dropdown-item>
              <el-dropdown-item command="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </el-header>

    <!-- 主要内容区域 -->
    <div class="main-container">
      <router-view></router-view>
    </div>
  </div>
</template>

<script setup>
import { useUserStore } from '../../stores/user'
import { useRouter } from 'vue-router'
import { ArrowDown, UserFilled } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { loadFull } from 'tsparticles'

const userStore = useUserStore()
const router = useRouter()

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
      value: 20,
      density: {
        enable: true,
        value_area: 400
      }
    },
    color: {
      value: "#BFE9FF"
    },
    shape: {
      type: "circle"
    },
    opacity: {
      value: 0.8,
      random: true
    },
    size: {
      value: 2,
      random: true
    },
    links: {
      enable: true,
      color: "#BFE9FF",
      distance: 80,
      opacity: 0.5,
      width: 1
    },
    move: {
      enable: true,
      speed: 1.5,
      direction: "none",
      out_mode: "bounce"
    }
  },
  interactivity: {
    detect_on: "window",
    events: {
      onhover: {
        enable: true,
        mode: "bubble"
      },
      onclick: {
        enable: true,
        mode: "push"
      }
    },
    modes: {
      bubble: {
        distance: 100,
        size: 6,
        duration: 0.3,
        opacity: 0.8,
        speed: 3
      }
    }
  }
}

const handleCommand = async (command) => {
  if (command === 'profile') {
    router.push('/profile')
  } else if (command === 'logout') {
    try {
      await userStore.logout()
      ElMessage.success('退出成功')
      router.push('/login')
    } catch (error) {
      ElMessage.error('退出失败')
    }
  }
}
</script>

<style scoped>
.header-particles {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 0;
}

.basic-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: rgba(255, 255, 255, 0.7);
}

.header {
    background: linear-gradient(45deg, #516790, #2a92fa, #4582f4, #6397f8, #1E90FF, #316cdb);
    box-shadow: 0 2px 8px rgba(245, 244, 244, 0.1);
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0 40px;
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    z-index: 1000;
}

.logo {
  margin: 0;
  font-size: 20px;
  color: #f3f6f8;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  color: #e9ebf3;
}

.user-avatar {
  margin-right: 10px;
}

.user-name {
  margin-right: 4px;
}

.arrow-down {
  margin-left: 4px;
}

.main-container {
  flex: 1;
  margin-top: 60px;
  padding: 20px 40px;
  background-color: #f4f9ff;
  min-width: 1200px;
}
</style> 