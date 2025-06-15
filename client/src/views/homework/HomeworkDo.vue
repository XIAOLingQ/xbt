<template>
  <div class="homework-do-container">
    <el-page-header @back="goBack" class="page-header">
      <template #content>
        <span class="text-large font-600 mr-3">
          {{ isSubmitted ? '查看作业' : '作业作答' }}
        </span>
      </template>
    </el-page-header>

    <el-card v-if="!loading && homeworkDetail" class="homework-card">
      <template #header>
        <div class="card-header">
          <h2>{{ homeworkDetail.homework.title }}</h2>
          <p>截止时间：{{ homeworkDetail.homework.endTime }}</p>
        </div>
      </template>

      <div class="questions-list">
        <div v-for="(question, index) in homeworkDetail.questions" :key="question.id" class="question-item">
          <h4 class="question-title">
            <span>{{ index + 1 }}. {{ question.questionText }} ({{ question.score }}分)</span>
            <span v-if="isSubmitted && question.questionType === 'choice'" class="correctness-indicator">
              <el-tag v-if="isCorrect(question)" type="success" effect="dark">回答正确</el-tag>
              <el-tag v-else type="danger" effect="dark">回答错误</el-tag>
            </span>
          </h4>

          <!-- 选择题渲染 -->
          <div v-if="question.questionType === 'choice'">
            <el-radio-group v-model="answers[question.id]" :disabled="isSubmitted">
              <el-radio
                v-for="(option, optIndex) in question.options"
                :key="optIndex"
                :label="String.fromCharCode(65 + optIndex)"
                class="choice-radio"
                :class="{
                  'correct-answer': isSubmitted && String.fromCharCode(65 + optIndex) === question.answer,
                  'wrong-answer': isSubmitted && getSubmittedAnswer(question.id) === String.fromCharCode(65 + optIndex) && getSubmittedAnswer(question.id) !== question.answer
                }"
              >
                {{ String.fromCharCode(65 + optIndex) }}. {{ option.text }}
              </el-radio>
            </el-radio-group>
            <div v-if="isSubmitted" class="answer-info">
              <div v-if="getSubmittedAnswer(question.id)" class="submitted-answer">
                你的答案：<span :class="{'text-danger': getSubmittedAnswer(question.id) !== question.answer}">{{ getSubmittedAnswer(question.id) }}</span>
              </div>
              <div class="correct-answer-text">
                正确答案：{{ question.answer }}
              </div>
            </div>
          </div>

          <!-- 编程题/其他题型渲染 -->
          <div v-else>
            <div v-if="parsedOptions(question).description" class="question-description" v-html="formatDescription(parsedOptions(question).description)"></div>
            <div v-if="parsedOptions(question).questionFileUrl" class="attachment">
                <strong>题目附件: </strong>
                <a :href="parsedOptions(question).questionFileUrl" target="_blank">{{ parsedOptions(question).questionFileName || '点击下载' }}</a>
            </div>

            <div class="answer-section">
              <el-input
                type="textarea"
                :rows="8"
                placeholder="请在此处输入你的代码或答案"
                v-model="answers[question.id].text"
                :disabled="isSubmitted"
                class="answer-textarea"
              ></el-input>

              <div class="upload-container">
                <el-upload
                  v-if="!isSubmitted"
                  :action="qiniuData.uploadUrl"
                  :data="qiniuData"
                  :before-upload="beforeUpload"
                  :on-success="handleUploadSuccess(question.id)"
                  :on-error="handleUploadError"
                  :show-file-list="false"
                  :limit="1"
                  class="upload-component"
                >
                  <el-button type="primary">上传答案文件</el-button>
                </el-upload>
                <span v-if="answers[question.id]?.file" class="file-name">
                  <a :href="answers[question.id].file.url" target="_blank">{{ answers[question.id].file.name }}</a>
                </span>
              </div>
            </div>

            <div v-if="isSubmitted" class="correct-answer-section">
                <h4>参考答案</h4>
                <div v-if="parsedAnswer(question).answerDescription" v-html="formatDescription(parsedAnswer(question).answerDescription)" class="answer-description"></div>
                <div v-if="parsedAnswer(question).answerFileUrl" class="attachment">
                    <strong>答案附件: </strong>
                    <a :href="parsedAnswer(question).answerFileUrl" target="_blank">{{ parsedAnswer(question).answerFileName || '点击下载' }}</a>
                </div>
                <el-empty v-if="!parsedAnswer(question).answerDescription && !parsedAnswer(question).answerFileUrl" description="该题没有提供参考答案"></el-empty>
            </div>
          </div>
        </div>
      </div>

      <div class="submission-actions" v-if="!isSubmitted">
        <el-button type="primary" @click="submitHomework">提交作业</el-button>
      </div>
    </el-card>

    <el-skeleton :rows="10" animated v-if="loading" />
    <el-empty v-if="!loading && !homeworkDetail" description="作业加载失败或不存在" />

    <!-- 仅在提交后显示AI助手 -->
    <AiAssistant v-if="isSubmitted" />
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import { getHomeworkDetail, submitHomework as apiSubmitHomework } from '@/api/homework';
import { getUploadToken } from '@/api/courseChapter';
import { v4 as uuidv4 } from 'uuid';
import AiAssistant from '@/components/AiAssistant.vue';

const route = useRoute();
const router = useRouter();
const homeworkId = ref(route.params.id);
const loading = ref(true);
const homeworkDetail = ref(null);
const answers = ref({});

const isSubmitted = computed(() => {
  return homeworkDetail.value?.submission?.status === 2 || homeworkDetail.value?.submission?.status === 3;
});

const isCorrect = (question) => {
  if (!homeworkDetail.value?.submission) return false;
  // 对于非选择题，这里只做展示，不判断对错
  if (question.questionType !== 'choice') return false; 
  const studentAnswer = answers.value[question.id];
  return studentAnswer?.toLowerCase() === question.answer?.toLowerCase();
};

const getSubmittedAnswer = (questionId) => {
  if (!homeworkDetail.value?.submission?.answers) return null;
  try {
    const submittedAnswers = JSON.parse(homeworkDetail.value.submission.answers);
    // 遍历所有提交的答案，找到对应的答案
    for (const [key, value] of Object.entries(submittedAnswers)) {
      if (key === String(questionId)) {
        return value;
      }
    }
    return null;
  } catch (e) {
    console.warn('解析提交答案失败:', e);
    return null;
  }
};

const parsedOptions = (question) => {
    try {
        const options = JSON.parse(question.options);
        return options || {};
    } catch(e) {
        return {};
    }
};

const parsedAnswer = (question) => {
    try {
        const answer = JSON.parse(question.answer);
        return answer || {};
    } catch(e) {
        return {};
    }
}

const formatDescription = (text) => {
    if (typeof text !== 'string') {
        return '';
    }
    return text.replace(/\\n/g, '<br>');
}

const qiniuData = ref({
  domain: 'http://sxh8oib6z.hb-bkt.clouddn.com',
  uploadUrl: 'http://upload-z1.qiniup.com',
  token: '',
  key: '',
});

const beforeUpload = async (file) => {
  try {
    qiniuData.value.token = await getUploadToken();
    const fileExtension = file.name.split('.').pop() || 'tmp';
    qiniuData.value.key = `homework/${uuidv4()}.${fileExtension}`;
    return true;
  } catch(e) {
    ElMessage.error("获取上传凭证失败");
    return false;
  }
};

const handleUploadSuccess = (questionId) => (res, file) => {
  const fileInfo = {
    url: `${qiniuData.value.domain}/${res.key}`,
    name: file.name
  };
  // 如果之前的答案是字符串，需要转换为对象
  let answer = answers.value[questionId];
  if (typeof answer === 'string') {
    answer = { text: answer };
  } else if (!answer) {
    answer = {};
  }
  answer.file = fileInfo;
  answers.value[questionId] = answer;
  ElMessage.success('文件上传成功');
};

const handleUploadError = () => {
  ElMessage.error('上传失败，请检查网络或联系管理员');
};

const fetchHomework = async () => {
  loading.value = true;
  try {
    const response = await getHomeworkDetail(homeworkId.value);
    homeworkDetail.value = response;

    // 初始化答案
    if (response.questions) {
      const initialAnswers = {};
      response.questions.forEach(q => {
        // 确保选项被正确解析
        if (q.questionType === 'choice' && typeof q.options === 'string') {
          try {
            q.options = JSON.parse(q.options);
          } catch (e) {
            console.warn('选项解析失败:', e);
            q.options = [];
          }
        }
        
        // 初始化答案结构
        if (q.questionType === 'choice') {
          initialAnswers[q.id] = '';
        } else {
          initialAnswers[q.id] = { text: '', file: null };
        }
      });

      // 如果有提交的答案，则合并现有答案
      if (isSubmitted.value && homeworkDetail.value.submission?.answers) {
        try {
          const submittedAnswers = JSON.parse(homeworkDetail.value.submission.answers);
          answers.value = {
            ...initialAnswers,
            ...submittedAnswers
          };
        } catch (e) {
          console.warn('提交答案解析失败:', e);
          answers.value = initialAnswers;
        }
      } else {
        answers.value = initialAnswers;
      }
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
        answers: answers.value,
      };
      await apiSubmitHomework(payload);
      ElMessage.success('作业提交成功！');
      // 重新加载数据以显示批改结果
      fetchHomework();
    } catch (error) {
      ElMessage.error('提交失败，请稍后重试');
      console.error('Submission error:', error);
    }
  }).catch(() => {
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
  position: relative;
  min-height: calc(100vh - 93px);
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
.question-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}
.choice-radio {
  display: block;
  margin: 15px 0;
  padding: 10px;
  border-radius: 4px;
  transition: all 0.2s;
}
.choice-radio.correct-answer {
  background-color: #f0f9eb;
  border: 1px solid #e1f3d8;
}
.choice-radio.wrong-answer {
  background-color: #fef0f0;
  border: 1px solid #fde2e2;
}
.correct-answer-text {
  color: #67c23a;
  font-weight: bold;
  margin-top: 10px;
}
.submission-actions {
  text-align: center;
  margin-top: 30px;
}
.question-description {
    background-color: #f9f9f9;
    padding: 15px;
    border-radius: 4px;
    margin-bottom: 15px;
    line-height: 1.6;
    color: #333;
}

.answer-description {
    background-color: #f0f9eb;
    padding: 15px;
    border-radius: 4px;
    margin-bottom: 15px;
    line-height: 1.6;
}

.attachment {
    margin: 10px 0;
}

.attachment a {
    color: #409eff;
    text-decoration: none;
}
.attachment a:hover {
    text-decoration: underline;
}

.answer-textarea {
    margin-top: 15px;
}

.correct-answer-section {
    margin-top: 20px;
    padding-top: 20px;
    border-top: 1px solid #ebeef5;
}

.answer-section {
  margin-top: 15px;
}

.upload-container {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-top: 12px;
}

.file-name {
  color: #606266;
}

.file-name a {
  color: #409EFF;
  text-decoration: none;
}

.file-name a:hover {
  text-decoration: underline;
}

.text-danger {
  color: #f56c6c;
  font-weight: bold;
}

.answer-info {
  margin-top: 10px;
  padding: 10px;
  background-color: #f8f9fa;
  border-radius: 4px;
}

.submitted-answer {
  margin-bottom: 5px;
}
</style> 