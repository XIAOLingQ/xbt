import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import request from '../utils/request'

export const useUserStore = defineStore('user', () => {
  // 用户信息
  const userInfo = ref({
    id: 1,
    username: 'student_user',
    role: 1, // 1-学生, 2-教师
    realName: '张三',
    avater: ''
  })

  const isTeacher = computed(() => userInfo.value.role === 2)

  const isLoggedIn = ref(false)

  // 登录
  const login = async (data) => {
    const res = await request.post('/user/login', data)
    userInfo.value = res
    console.log(res)
    isLoggedIn.value = true
    return res
  }

  // 注册
  const register = async (data) => {
    const res = await request.post('/user/register', data)
    return res
  }

  // 检查用户名是否存在
  const checkUsername = async (username) => {
    return await request.get(`/user/check-username?username=${username}`)
  }

  // 检查邮箱是否存在
  const checkEmail = async (email) => {
    return await request.get(`/user/check-email?email=${email}`)
  }

  // 退出登录
  const logout = async () => {
    await request.post('/user/logout')
    userInfo.value = null
    isLoggedIn.value = false
  }

  // 获取用户信息
  const getUserInfo = async () => {
    try {
      const res = await request.get('/user/info')
      userInfo.value = res
      isLoggedIn.value = true
      return res
    } catch (error) {
      userInfo.value = null
      isLoggedIn.value = false
      throw error
    }
  }

  // 更新用户信息
  const updateUserInfo = async (data) => {
    const res = await request.put('/user/info', data)
    userInfo.value = res
    return res
  }

  return {
    userInfo,
    isTeacher,
    isLoggedIn,
    login,
    register,
    logout,
    getUserInfo,
    updateUserInfo,
    checkUsername,
    checkEmail
  }
}) 