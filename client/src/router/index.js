import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '../stores/user'
import BasicLayout from '../components/layout/BasicLayout.vue'
import { ElMessage } from 'element-plus'

const router = createRouter({
  history: createWebHistory(),
  routes: [
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
          component: () => import('../views/course/CourseManage.vue'),
          meta: { requiresTeacher: true },
          props: true
        },
        {
          path: 'course/learn/:id',
          name: 'CourseLearn',
          component: () => import('../views/course/CourseLearn.vue'),
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
          path: '/homework/do/:id',
          name: 'HomeworkDo',
          component: () => import('../views/homework/HomeworkDo.vue'),
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
  const isLoggedIn = userStore.isLoggedIn

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