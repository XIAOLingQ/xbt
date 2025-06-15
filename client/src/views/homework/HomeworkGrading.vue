<template>
    <div class="grading-container">
        <el-card>
            <template #header>
                <el-page-header @back="goBack" :content="`批改作业 - ${homeworkTitle}`" />
            </template>
            <el-table :data="submissions" v-loading="loading">
                <el-table-column prop="studentName" label="学生姓名" width="180" />
                <el-table-column prop="status" label="状态" width="120">
                    <template #default="scope">
                        <el-tag :type="getStatusTag(scope.row.status)">{{ formatStatus(scope.row.status) }}</el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="submitTime" label="提交时间">
                    <template #default="scope">{{ scope.row.submitTime ? new Date(scope.row.submitTime).toLocaleString() : 'N/A' }}</template>
                </el-table-column>
                <el-table-column prop="score" label="得分" width="100" />
                <el-table-column label="操作" width="120">
                    <template #default="scope">
                        <el-button 
                            size="small" 
                            type="primary" 
                            :disabled="scope.row.status === 1"
                            @click="viewSubmission(scope.row)">
                            {{ scope.row.status === 3 ? '查看' : '批改' }}
                        </el-button>
                    </template>
                </el-table-column>
            </el-table>
        </el-card>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { getHomeworkSubmissions } from '@/api/homework';

const route = useRoute();
const router = useRouter();

const loading = ref(true);
const submissions = ref([]);
const homeworkTitle = ref('');

const homeworkId = route.params.homeworkId;
const courseId = route.params.courseId;

onMounted(async () => {
    await fetchSubmissions();
});

const fetchSubmissions = async () => {
    loading.value = true;
    try {
        const response = await getHomeworkSubmissions(homeworkId);
        submissions.value = response;
        if (response.length > 0) {
            // This is a temporary way to get the homework title.
            // Ideally, the API should return it along with the submissions.
            // For now, let's just use a placeholder.
            homeworkTitle.value = `作业ID: ${homeworkId}`; 
        }
    } catch (error) {
        ElMessage.error('获取提交列表失败');
    } finally {
        loading.value = false;
    }
};

const goBack = () => {
    router.push({ name: 'CourseManage', params: { courseId: courseId }, query: { tab: 'homework' } });
};

const formatStatus = (status) => {
    const map = { 1: '未提交', 2: '已提交', 3: '已批改' };
    return map[status] || '未知';
};

const getStatusTag = (status) => {
    const map = { 1: 'info', 2: 'warning', 3: 'success' };
    return map[status] || 'info';
};

const viewSubmission = (submission) => {
    router.push({ name: 'SubmissionGrading', params: { submissionId: submission.id } });
};

</script>

<style scoped>
.grading-container {
    padding: 20px;
}
</style> 