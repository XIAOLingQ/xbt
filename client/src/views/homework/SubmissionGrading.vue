<template>
    <div class="submission-grading-container">
        <el-page-header @back="goBack">
            <template #content>
                <span class="text-large font-600 mr-3"> 作业批改: {{ submissionInfo.homeworkTitle }} - {{ submissionInfo.studentName }} </span>
            </template>
        </el-page-header>
        <el-card class="box-card">
            <div v-if="loading">加载中...</div>
            <div v-else>
                <div v-for="(question, index) in questions" :key="question.id" class="question-item">
                    <h4>{{ index + 1 }}. {{ question.questionText }} <span class="score-text">(分值: {{ question.score }})</span></h4>
                    
                    <div v-if="question.questionType === 'choice'" class="answer-block">
                        <p><strong>正确答案:</strong> {{ question.answer }}</p>
                        <p><strong>学生答案:</strong> {{ getStudentAnswer(question.id) }}</p>
                    </div>

                    <div v-else class="answer-block">
                        <div class="answer-section">
                            <strong>参考答案:</strong>
                            <div v-if="parsedAnswer(question.answer).answerDescription" v-html="formatDescription(parsedAnswer(question.answer).answerDescription)" class="answer-content"></div>
                            <div v-if="parsedAnswer(question.answer).answerFileUrl" class="attachment">
                                <strong>附件: </strong>
                                <a :href="parsedAnswer(question.answer).answerFileUrl" target="_blank">{{ parsedAnswer(question.answer).answerFileName || '点击下载' }}</a>
                            </div>
                            <p v-if="!parsedAnswer(question.answer).answerDescription && !parsedAnswer(question.answer).answerFileUrl">该题没有提供参考答案</p>
                        </div>

                        <div class="answer-section">
                            <strong>学生答案:</strong>
                            <div v-if="getStudentAnswer(question.id) !== '未作答'">
                                <div v-if="parseStudentAnswer(question.id).text" class="answer-content">
                                    {{ parseStudentAnswer(question.id).text }}
                                </div>
                                <div v-if="parseStudentAnswer(question.id).file && parseStudentAnswer(question.id).file.url" class="attachment">
                                    <strong>附件: </strong>
                                    <a :href="parseStudentAnswer(question.id).file.url" target="_blank">{{ parseStudentAnswer(question.id).file.name }}</a>
                                </div>
                            </div>
                            <p v-else>未作答</p>
                        </div>
                    </div>

                    <div v-if="question.questionType !== 'choice'" class="grading-input">
                        <el-form-item label="打分">
                            <el-input-number v-model="scores[question.id]" :min="0" :max="parseInt(question.score || 0)" />
                        </el-form-item>
                    </div>
                    <el-divider></el-divider>
                </div>
                <div class="footer-actions">
                    <div class="total-score">
                        <strong>总分: </strong> {{ totalScore }} / {{ totalPossibleScore }}
                    </div>
                    <el-button type="primary" @click="submitGrading" :loading="isSubmitting">提交批改</el-button>
                </div>
            </div>
        </el-card>
    </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { getSubmissionDetail, gradeSubmission } from '@/api/homework';

const route = useRoute();
const router = useRouter();

const submissionId = route.params.submissionId;
const loading = ref(true);
const isSubmitting = ref(false);

const submissionInfo = ref({});
const questions = ref([]);
const studentAnswers = ref({});
const scores = ref({});

onMounted(async () => {
    try {
        const data = await getSubmissionDetail(submissionId);
        submissionInfo.value = { homeworkTitle: data.homeworkTitle, studentName: data.studentName };
        questions.value = data.questions;
        studentAnswers.value = data.studentAnswers;
        
        // Initialize scores for non-choice questions
        questions.value.forEach(q => {
            if (q.questionType !== 'choice') {
                scores.value[q.id] = 0;
            }
        });
    } catch (error) {
        ElMessage.error('获取提交详情失败');
    } finally {
        loading.value = false;
    }
});

const getStudentAnswer = (questionId) => {
    return studentAnswers.value[questionId] || '未作答';
};

const parseStudentAnswer = (questionId) => {
    const answer = getStudentAnswer(questionId);
    if (answer === '未作答') {
        return {};
    }

    // 如果答案已经是对象，则直接返回
    if (typeof answer === 'object' && answer !== null) {
        return answer;
    }

    // 如果是字符串，则尝试解析
    if (typeof answer === 'string') {
        try {
            const parsed = JSON.parse(answer);
            return parsed || {};
        } catch (e) {
            // 解析失败，说明它可能就是一个纯文本答案
            return { text: answer };
        }
    }

    // 对于其他意外情况，返回空对象
    return {};
};

const parsedAnswer = (answerJson) => {
    if (!answerJson) return {};
    try {
        return JSON.parse(answerJson) || {};
    } catch(e) {
        return {};
    }
};

const formatDescription = (text) => {
    if (typeof text !== 'string') return '';
    return text.replace(/\\n/g, '<br>');
}

const totalPossibleScore = computed(() => {
    return questions.value.reduce((sum, q) => sum + parseInt(q.score || 0), 0);
});

const totalScore = computed(() => {
    let total = 0;
    questions.value.forEach(q => {
        if (q.questionType === 'choice') {
            if (String(getStudentAnswer(q.id)) === String(q.answer)) {
                total += parseInt(q.score || 0);
            }
        } else {
            total += scores.value[q.id] || 0;
        }
    });
    return total;
});

const submitGrading = async () => {
    isSubmitting.value = true;
    try {
        await gradeSubmission(submissionId, { finalScore: totalScore.value });
        ElMessage.success('批改成功!');
        router.back();
    } catch (error) {
        ElMessage.error('提交批改失败');
    } finally {
        isSubmitting.value = false;
    }
};

const goBack = () => {
    router.back();
};
</script>

<style scoped>
.submission-grading-container {
    padding: 20px;
}
.box-card {
    margin-top: 20px;
}
.question-item {
    margin-bottom: 20px;
}
.score-text {
    color: #909399;
    font-size: 14px;
    margin-left: 10px;
}
.answer-block {
    margin: 10px 0;
}
.answer-section {
    background-color: #f8f9fa;
    padding: 15px;
    border-radius: 4px;
    margin-top: 10px;
}
.answer-content {
    white-space: pre-wrap;
    line-height: 1.6;
    color: #333;
    margin-top: 8px;
}
.attachment {
    margin-top: 10px;
}
.attachment a {
    color: #409eff;
    text-decoration: none;
}
.grading-input {
    margin-top: 15px;
}
.footer-actions {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-top: 20px;
}
.total-score {
    font-size: 18px;
}
</style> 