<template>
  <div class="homework-do-container">
    <el-page-header @back="goBack" class="page-header">
      <template #content>
        <span class="text-large font-600 mr-3"> 作业作答 </span>
      </template>
    </el-page-header>

    <el-card v-if="!loading && homeworkDetail" class="homework-card">
      <template #header>
        <div class="card-header">
          <h2>{{ homeworkDetail.homework.title }}</h2>
          <p>截止时间：{{ homeworkDetail.homework.endTime }}</p>
        </div>
      </template>

      <div class="homework-description" v-if="homeworkDetail.homework.description">
        <p>{{ homeworkDetail.homework.description }}</p>
      </div>

      <el-divider />

      <div class="questions-list">
        <div v-for="(question, index) in homeworkDetail.questions" :key="question.id" class="question-item">
          <h4>{{ index + 1 }}. {{ question.questionText }} ({{question.score}}分)</h4>

          <!-- 选择题渲染 -->
          <div v-if="question.questionType === 'choice'" class="choice-options">
            <el-radio-group v-model="answers[question.id]">
              <el-radio
                  v-for="(option, optIndex) in question.options"
                  :key="optIndex"
                  :label="String.fromCharCode(65 + optIndex)"
                  class="choice-radio"
              >
                {{ String.fromCharCode(65 + optIndex) }}. {{ option.text }}
              </el-radio>
            </el-radio-group>
          </div>

          <!-- 编程题/其他题型渲染 -->
          <div v-else>
            <el-input
                type="textarea"
                :rows="6"
                placeholder="请输入你的答案"
                v-model="answers[question.id]"
            ></el-input>
          </div>

        </div>
      </div>

      <div class="submission-actions">
        <el-button type="primary" @click="submitHomework">提交作业</el-button>
      </div>
    </el-card>

    <el-skeleton :rows="10" animated v-if="loading" />
    <el-empty v-if="!loading && !homeworkDetail" description="作业加载失败或不存在" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import { getHomeworkDetail, submitHomework as apiSubmitHomework } from '@/api/homework';

const route = useRoute();
const router = useRouter();
const homeworkId = ref(route.params.id);
const loading = ref(true);
const homeworkDetail = ref(null);
const answers = ref({});

const fetchHomework = async () => {
  loading.value = true;
  try {
    const response = await getHomeworkDetail(homeworkId.value);
    homeworkDetail.value = response;
    if (homeworkDetail.value && homeworkDetail.value.questions) {
      // 解析选择题的 options 字符串
      homeworkDetail.value.questions.forEach(q => {
        if (q.questionType === 'choice' && typeof q.options === 'string') {
          try {
            q.options = JSON.parse(q.options);
          } catch (e) {
            console.error('Failed to parse options for question:', q);
            q.options = []; // 解析失败则置为空数组
          }
        }
      });

      const initialAnswers = {};
      homeworkDetail.value.questions.forEach(q => {
        initialAnswers[q.id] = '';
      });
      answers.value = initialAnswers;
    }
  } catch (error) {
    ElMessage.error('获取作业详情失败');
    console.error(error);
  } finally {
    loading.value = false;
  }
};

const submitHomework = () => {
  ElMessageBox.confirm('确认要提交作业吗？提交后将无法修改。', '提示', {
    confirmButtonText: '确认提交',
    cancelButtonText: '再检查一下',
    type: 'warning',
  }).then(async () => {
    try {
      const payload = {
        homeworkId: homeworkId.value,
        answers: answers.value
      };
      await apiSubmitHomework(payload);
      ElMessage.success('作业提交成功！');
      router.back();
    } catch (error) {
      ElMessage.error('提交失败，请稍后重试');
      console.error('Submission error:', error);
    }
  }).catch(() => {
    // 用户点击了"再检查一下"
    ElMessage.info('已取消提交');
  });
};

const goBack = () => {
  router.back();
};

onMounted(fetchHomework);
</script>

<style scoped>
.homework-do-container {
  padding: 20px;
}
.homework-card {
  max-width: 900px;
  margin: 20px auto;
}
.card-header p {
  font-size: 14px;
  color: #909399;
}
.question-item {
  margin-bottom: 25px;
}
.choice-radio {
  display: block;
  margin-bottom: 10px;
}
.submission-actions {
  text-align: center;
  margin-top: 30px;
}
</style>