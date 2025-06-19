<template>
    <div class="question-view">
        <el-card>
            <template #header>
                <el-page-header @back="goBack" :content="`${isPracticeMode ? '进行练习' : '练习回顾'} - ${topic}`">
                </el-page-header>
            </template>
            <div v-if="loading">加载中...</div>
            <div v-if="error">{{ error }}</div>

            <!-- Practice Mode -->
            <div v-if="!loading && isPracticeMode">
                <div v-for="(question, index) in questions" :key="question.id" class="question-item">
                    <p><strong>{{ index + 1 }}. ({{ question.questionType }}) {{ question.questionText }}</strong></p>
                    <el-radio-group 
                        v-if="question.questionType === 'single_choice'" 
                        v-model="studentAnswers[question.id]" 
                        class="options-group"
                    >
                        <el-radio 
                            v-for="opt in parseOptions(question.options)" 
                            :key="opt.option" 
                            :label="opt.option"
                            class="option-radio"
                            border
                        >
                            {{ opt.option }}. {{ opt.description }}
                        </el-radio>
                    </el-radio-group>
                    <el-divider v-if="index < questions.length - 1"></el-divider>
                </div>
                <div class="submit-action">
                    <el-button type="primary" @click="submitAnswers" :loading="isSubmitting">提交答案</el-button>
                </div>
            </div>

            <!-- Review Mode -->
            <div v-if="!loading && !isPracticeMode && questions.length > 0">
                <div v-for="(question, index) in questions" :key="question.id" class="question-item">
                    <p><strong>{{ index + 1 }}. ({{ question.questionType }}) {{ question.questionText }}</strong></p>
                    <div v-if="question.options" class="options-display">
                        <div v-for="opt in parseOptions(question.options)" :key="opt.option">
                            {{ opt.option }}. {{ opt.description }}
                        </div>
                    </div>
                    <p><strong>正确答案:</strong> {{ question.correctAnswer }}</p>
                    <p><strong>你的答案:</strong> {{ question.studentAnswer || '未作答' }} <el-tag :type="question.isCorrect ? 'success' : 'danger'">{{ question.isCorrect ? '正确' : '错误' }}</el-tag></p>
                    <p><strong>解析:</strong> {{ question.analysis }}</p>
                    <el-divider></el-divider>
                </div>
            </div>
        </el-card>
    </div>
    <AiAssistant v-if="!isPracticeMode" />
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import api from '../../api/ai';
import { ElMessage, ElCard, ElPageHeader, ElTag, ElDivider, ElRadioGroup, ElRadio, ElButton } from 'element-plus';
import AiAssistant from '@/components/AiAssistant.vue';
const route = useRoute();
const router = useRouter();
const questions = ref([]);
const loading = ref(true);
const error = ref('');
const batchId = ref(route.params.batchId);
const topic = ref(route.query.topic || '练习详情');

const studentAnswers = ref({});
const isSubmitting = ref(false);

const isPracticeMode = computed(() => {
    return questions.value.length > 0 && questions.value[0].studentAnswer === null;
});

const parseOptions = (optionsStr) => {
    if (!optionsStr) return [];
    try {
        const options = JSON.parse(optionsStr);
        return Array.isArray(options) ? options : [];
    } catch (e) {
        console.error('Failed to parse options:', e);
        return [];
    }
};

const fetchQuestions = async () => {
    loading.value = true;
    try {
        const response = await api.getQuestionsByBatchId(batchId.value);
        questions.value = response;
        if (isPracticeMode.value) {
            questions.value.forEach(q => {
                studentAnswers.value[q.id] = '';
            });
        }
    } catch (err) {
        error.value = err.message;
        ElMessage.error(err.message || '获取题目失败');
    } finally {
        loading.value = false;
    }
}

onMounted(() => {
    if (!batchId.value) {
        error.value = '无效的批次ID';
        loading.value = false;
        return;
    }
    fetchQuestions();
});

const submitAnswers = async () => {
    isSubmitting.value = true;
    try {
        const answersPayload = Object.keys(studentAnswers.value)
            .filter(qid => studentAnswers.value[qid])
            .map(questionId => ({
                questionId: parseInt(questionId),
                studentAnswer: studentAnswers.value[questionId]
            }));
        
        if (answersPayload.length === 0) {
            ElMessage.warning('您还没有回答任何问题。');
            isSubmitting.value = false;
            return;
        }

        await api.submitAiAnswers(batchId.value, answersPayload);
        ElMessage.success('提交成功！');
        
        await fetchQuestions();

    } catch (err) {
        ElMessage.error(err.message || '提交失败');
    } finally {
        isSubmitting.value = false;
    }
};

const goBack = () => {
    router.push({ name: 'home', query: { tab: 'ai_playground' } });
};

</script>

<style scoped>
.question-view {
    padding: 20px;
}
.question-item {
    margin-bottom: 20px;
}
.options-group {
    display: flex;
    flex-direction: column;
    margin-top: 15px;
}
.option-radio {
    width: 100%;
    height: auto;
    white-space: normal;
    padding: 15px;
    margin: 0 0 10px 0 !important;
    border-radius: 8px;
    transition: all 0.2s ease-in-out;
}
.option-radio:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(0,0,0,0.08);
}
.option-radio .el-radio__label {
    white-space: normal;
    padding-left: 10px;
    font-size: 14px;
    line-height: 1.5;
}
.option-radio.is-checked {
    border-color: var(--el-color-primary);
    background-color: var(--el-color-primary-light-9);
}
.option-radio.is-checked .el-radio__label {
    color: var(--el-color-primary);
}
.submit-action {
    margin-top: 20px;
    text-align: right;
}
.options-display {
    background-color: #f5f5f5;
    padding: 10px;
    border-radius: 4px;
    white-space: pre-wrap;
    word-wrap: break-word;
    margin-bottom: 10px;
}
pre {
    background-color: #f5f5f5;
    padding: 10px;
    border-radius: 4px;
    white-space: pre-wrap;
    word-wrap: break-word;
}
</style> 