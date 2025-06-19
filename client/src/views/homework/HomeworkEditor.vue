<template>
    <div class="homework-editor-container">
        <el-page-header @back="goBack" class="page-header">
            <template #content>
                <span class="text-large font-600 mr-3"> 作业编辑器 </span>
            </template>
        </el-page-header>
        
        <el-card>
            <template #header>
                <div class="card-header">
                    <span>{{ homeworkData.title || '加载中...' }}</span>
                    <el-button type="primary" @click="saveAll">保存全部修改</el-button>
                </div>
            </template>
            
            <el-alert title="此页面下的所有修改，都需要点击右上角的'保存全部修改'按钮才会生效。" type="info" show-icon :closable="false" />
            
            <el-form :model="homeworkData" label-width="100px" class="homework-info-form">
                <el-row>
                    <el-col :span="12">
                        <el-form-item label="作业标题">
                            <el-input v-model="homeworkData.title" placeholder="请输入作业标题"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                         <el-form-item label="作业时间">
                            <el-date-picker
                                v-model="timeRange"
                                type="datetimerange"
                                range-separator="至"
                                start-placeholder="开始日期"
                                end-placeholder="结束日期"
                                style="width: 100%;"
                                value-format="YYYY-MM-DDTHH:mm:ss"
                            />
                        </el-form-item>
                    </el-col>
                </el-row>
            </el-form>
            
            <div class="main-content">
                <div v-for="(question, index) in homeworkData.questions" :key="question.id" class="question-item">
                    <el-card class="question-card">
                        <template #header>
                            <div class="card-header">
                                <span>第 {{ index + 1 }} 题 ({{ getQuestionTypeName(question.questionType) }})</span>
                                <el-button type="danger" size="small" @click="removeQuestion(index)">删除</el-button>
                            </div>
                        </template>
                        
                        <!-- 动态组件渲染 -->
                        <component :is="getEditorComponent(question.questionType)" v-model="homeworkData.questions[index]"></component>
                        
                    </el-card>
                </div>

                <el-empty v-if="!homeworkData.questions || homeworkData.questions.length === 0" description="该作业下暂无题目"></el-empty>

                <div class="add-question-buttons">
                    <el-button type="primary" plain @click="addQuestion('choice')">
                        <el-icon><Plus /></el-icon> 添加选择题
                    </el-button>
                    <el-button type="success" plain @click="addQuestion('programming')">
                        <el-icon><Plus /></el-icon> 添加编程题
                    </el-button>
                </div>
            </div>
        </el-card>
    </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { getHomeworkDetail, saveHomework } from '@/api/homework';
import { Plus } from '@element-plus/icons-vue';
import { v4 as uuidv4 } from 'uuid';
import ChoiceEditor from './ChoiceEditor.vue';
import ProgrammingEditor from './ProgrammingEditor.vue';

const props = defineProps({
    courseId: {
        type: String,
        required: true
    },
    homeworkId: {
        type: String,
        required: true
    }
});

const router = useRouter();

const homeworkData = ref({ title:'', questions: [] }); // 改进初始状态

const timeRange = computed({
    get() {
        if (homeworkData.value.startTime && homeworkData.value.endTime) {
            return [homeworkData.value.startTime, homeworkData.value.endTime];
        }
        return [];
    },
    set(val) {
        if (val && val.length === 2) {
            homeworkData.value.startTime = val[0];
            homeworkData.value.endTime = val[1];
        } else {
            homeworkData.value.startTime = null;
            homeworkData.value.endTime = null;
        }
    }
});

onMounted(() => {
    if (props.homeworkId !== 'new') {
        fetchHomeworkData();
    }
});

const fetchHomeworkData = async () => {
    try {
        // 根据 request.js 的响应拦截器, API 调用成功后直接返回 res.data.data
        const homeworkAndQuestions = await getHomeworkDetail(props.homeworkId);
        
        const homework = homeworkAndQuestions.homework;
        const questions = homeworkAndQuestions.questions || [];

        const processedQuestions = questions.map(q => {
            if (q.questionType === 'choice' && typeof q.options === 'string') {
                try {
                    q.options = JSON.parse(q.options);
                } catch (e) {
                    console.error('Failed to parse options for question:', q);
                    q.options = [];
                }
            }
            return q;
        });

        homeworkData.value = {
            ...homework,
            questions: processedQuestions
        };

    } catch (error) {
        ElMessage.error('加载作业信息失败');
        console.error(error);
    }
};

const goBack = () => {
    router.push({ name: 'CourseManage', params: { courseId: props.courseId }, query: { tab: 'homework' } });
};

const saveAll = async () => {
    try {
        // 创建一个干净的、可序列化的对象用于提交
        const payload = {
            id: homeworkData.value.id,
            courseId: homeworkData.value.courseId,
            title: homeworkData.value.title,
            description: homeworkData.value.description,
            startTime: homeworkData.value.startTime,
            endTime: homeworkData.value.endTime,
            answer:homeworkData.value.answer,
            questions: (homeworkData.value.questions || []).map(q => {
                // 深拷贝一份，避免修改原始数据
                const questionToSend = JSON.parse(JSON.stringify(q));

                // 如果 id 是字符串 (UUID), 说明是新创建的题目，置为 null
                if (typeof questionToSend.id === 'string') {
                    questionToSend.id = null;
                }

                // 如果是选择题且 options 是数组，则序列化为 JSON 字符串
                if (questionToSend.questionType === 'choice' && Array.isArray(questionToSend.options)) {
                    questionToSend.options = JSON.stringify(questionToSend.options);
                }

                return questionToSend;
            })
        };
        await saveHomework(payload);
        ElMessage.success('作业保存成功!');
    } catch (error) {
        ElMessage.error('保存失败');
        console.error(error);
    }
};

const getQuestionTypeName = (type) => {
    if (type === 'choice') return '选择题';
    if (type === 'programming') return '编程题';
    return '未知题型';
};

const getEditorComponent = (type) => {
    if (type === 'choice') return ChoiceEditor;
    if (type === 'programming') return ProgrammingEditor;
    return null; // or a default placeholder component
};

const addQuestion = (type) => {
    let newQuestion;
    if (type === 'choice') {
        newQuestion = {
            id: uuidv4(),
            questionType: 'choice',
            questionText: '新的选择题',
            options: [{text:''}, {text:''}, {text:''}, {text:''}], // 默认4个选项
            answer: 'A',
            score: 5
        };
    } else {
        newQuestion = {
            id: uuidv4(),
            questionType: 'programming',
            questionText: '新的编程题',
            description: '',
            questionFileName: '',
            questionFileUrl: '',
            answerDescription: '',
            answerFileName: '',
            answerFileUrl: '',
            score: 10
        };
    }
    
    if (!homeworkData.value.questions) {
        homeworkData.value.questions = [];
    }
    homeworkData.value.questions.push(newQuestion);
};

const removeQuestion = (index) => {
    homeworkData.value.questions.splice(index, 1);
};
</script>

<style scoped>
.homework-editor-container {
    padding: 20px;
}
.page-header {
    margin-bottom: 20px;
}
.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
}
.homework-info-form {
    margin-top: 20px;
    padding: 20px;
    border: 1px solid #ebeef5;
    border-radius: 4px;
    margin-bottom: 20px;
}
.main-content {
    margin-top: 20px;
}
.question-item {
    margin-bottom: 20px;
}
.question-card {
    border-left: 5px solid #409EFF;
}
.add-question-buttons {
    margin-top: 20px;
    border-top: 1px solid #ebeef5;
    padding-top: 20px;
    text-align: center;
}
</style> 