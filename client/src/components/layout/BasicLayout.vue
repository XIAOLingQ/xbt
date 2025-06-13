<template>
  <div class="basic-layout">
    <!-- 顶部导航栏 -->
    <el-header class="header" height="60px">
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

const userStore = useUserStore()
const router = useRouter()

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
.basic-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.header {
  background-color: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
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
  color: #409EFF;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  color: #606266;
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
  background-color: #f5f7fa;
  min-width: 1200px;
}
</style> 