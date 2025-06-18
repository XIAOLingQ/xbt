import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '../stores/user'
import BasicLayout from '../components/layout/BasicLayout.vue'
import { ElMessage } from 'element-plus'
import Home from '../views/Home.vue'
import Profile from '../views/Profile.vue'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import CreateCourse from '../views/course/CreateCourse.vue'
import ManageCourse from '../views/course/CourseManage.vue'
import LearnCourse from '../views/course/CourseLearn.vue'
// import HomeworkDetail from '../views/homework/HomeworkDetail.vue'
import AiQuestionView from '../views/ai/QuestionView.vue'
import Splash from '../views/Splash.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/splash',
      name: 'Splash',
      component: Splash,
      meta: { requiresGuest: true }
    },
    {
      path: '/login',
      name: 'Login',
      component: () => import('../views/Login.vue'),
      meta: { requiresGuest: true }
    },
    {
      path: '/register',
      name: 'Register',
      component: () => import('../views/Register.vue'),
      meta: { requiresGuest: true }
    },
    {
      path: '/',
      component: BasicLayout,
      meta: { requiresAuth: true },
      children: [
        {
          path: '',
          name: 'home',
          component: () => import('../views/Home.vue')
        },
        {
          path: 'profile',
          name: 'Profile',
          component: () => import('../views/Profile.vue')
        },
        {
          path: 'course/create',
          name: 'CreateCourse',
          component: () => import('../views/course/CreateCourse.vue'),
          meta: { requiresTeacher: true }
        },
        {
          path: 'course/manage/:courseId',
          name: 'CourseManage',
          component: ManageCourse,
          meta: { requiresTeacher: true },
          props: true
        },
        {
          path: 'course/learn/:id',
          name: 'CourseLearn',
          component: LearnCourse,
          meta: { requiresStudent: true },
          props: true
        },
        {
          path: '/course/:courseId/homework/edit/:homeworkId',
          name: 'HomeworkEditor',
          component: () => import('../views/homework/HomeworkEditor.vue'),
          props: true
        },
        {
          path: '/course/:courseId/homework/:homeworkId/grading',
          name: 'HomeworkGrading',
          component: () => import('../views/homework/HomeworkGrading.vue'),
          props: true,
          meta: { requiresTeacher: true }
        },
        {
          path: '/homework/submission/:submissionId/grade',
          name: 'SubmissionGrading',
          component: () => import('../views/homework/SubmissionGrading.vue'),
          props: true,
          meta: { requiresTeacher: true }
        },
        {
          path: '/homework/do/:id',
          name: 'HomeworkDo',
          component: () => import('../views/homework/HomeworkDo.vue'),
          props: true,
          meta: { requiresAuth: true }
        },
        {
          path: '/ai/history/:batchId',
          name: 'AiQuestionView',
          component: AiQuestionView,
          props: true,
          meta: { requiresAuth: true }
        }
      ]
    }
  ]
})

// 路由守卫
router.beforeEach(async (to, from, next) => {
  const userStore = useUserStore()
  // 首次进入应用时，尝试获取用户信息
  if (!userStore.isLoggedIn && localStorage.getItem('token')) {
    try {
      await userStore.getUserInfo()
    } catch (error) {
      // 获取失败，token无效，停留在guest页面
      localStorage.removeItem('token')
    }
  }
  const isLoggedIn = userStore.isLoggedIn

  // 如果未登录，且目标路由不是 guest 路由，则重定向到 splash
  if (!isLoggedIn && to.path !== '/splash' && to.path !== '/login' && to.path !== '/register') {
    return next('/splash');
  }

  // 检查教师权限
  if (to.meta.requiresTeacher && !userStore.isTeacher) {
    ElMessage.error('您没有权限访问此页面');
    return next(from.path || '/'); // 从哪里来回哪里去，或返回首页
  }

  // 检查学生权限
  if (to.meta.requiresStudent && userStore.isTeacher) {
    ElMessage.error('教师不能访问该页面');
    return next(from.path || '/');
  }

  // 如果路由需要登录
  if (to.meta.requiresAuth) {
    if (!isLoggedIn) {
      next('/login')
      return
    }
  }

  // 如果路由是给未登录用户的（如登录页）
  if (to.meta.requiresGuest) {
    if (isLoggedIn) {
      next('/')
      return
    }
  }

  next()
})

export default router 