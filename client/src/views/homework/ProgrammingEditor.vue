<template>
    <div class="programming-editor">
        <el-form :model="props.modelValue" label-width="80px">
            <el-form-item label="题目内容">
                <el-input :model-value="props.modelValue.questionText" @update:model-value="updateField('questionText', $event)" type="textarea" autosize placeholder="请输入题目内容"></el-input>
            </el-form-item>
            <el-form-item label="题目描述">
                <el-input v-model="description" type="textarea" :rows="5" placeholder="请输入题目详细描述"></el-input>
            </el-form-item>
            
            <el-form-item label="题目附件 (可选)">
                <div class="upload-container">
                    <el-upload
                        :action="qiniuData.uploadUrl"
                        :data="qiniuData"
                        :before-upload="beforeUpload"
                        :on-success="handleQuestionUploadSuccess"
                        :on-error="handleUploadError"
                        :show-file-list="false"
                        :limit="1"
                        class="upload-component"
                    >
                        <el-button type="primary">点击上传</el-button>
                    </el-upload>
                    <span v-if="questionFileList.length > 0" class="file-name">
                        <a :href="questionFileList[0].url" target="_blank">{{ questionFileList[0].name }}</a>
                    </span>
                </div>
                <div class="el-upload__tip">
                    只能上传一个文件，新文件会覆盖旧文件。
                </div>
            </el-form-item>

            <el-form-item label="参考答案描述">
                <el-input type="textarea" :rows="5" v-model="answerDescription" placeholder="请填写文字版参考答案..."></el-input>
            </el-form-item>

            <el-form-item label="答案附件 (可选)">
                <div class="upload-container">
                    <el-upload
                        :action="qiniuData.uploadUrl"
                        :data="qiniuData"
                        :before-upload="beforeUpload"
                        :on-success="handleAnswerUploadSuccess"
                        :on-error="handleUploadError"
                        :show-file-list="false"
                        :limit="1"
                        class="upload-component"
                    >
                        <el-button type="primary">点击上传</el-button>
                    </el-upload>
                    <span v-if="answerFileList.length > 0" class="file-name">
                        <a :href="answerFileList[0].url" target="_blank">{{ answerFileList[0].name }}</a>
                    </span>
                </div>
                <div class="el-upload__tip">
                    只能上传一个文件，新文件会覆盖旧文件。
                </div>
            </el-form-item>

            <el-form-item label="分值">
                <el-input-number :model-value="props.modelValue.score" @update:model-value="updateField('score', $event)" :min="1" :max="100"></el-input-number>
            </el-form-item>
        </el-form>
    </div>
</template>

<script setup>
import { ref, watch, computed } from 'vue';
import { ElMessage } from 'element-plus';
import { getUploadToken } from '@/api/courseChapter';
import { v4 as uuidv4 } from 'uuid';

const props = defineProps({
    modelValue: {
        type: Object,
        required: true
    }
});
const emit = defineEmits(['update:modelValue']);

const qiniuData = ref({
  domain: 'http://sxh8oib6z.hb-bkt.clouddn.com',
  uploadUrl: 'http://upload-z1.qiniup.com',
  token: '',
  key: '',
});

// Local state for complex properties that will be stored in JSON
const description = ref('');
const answerDescription = ref('');

const updateField = (key, value) => {
    emit('update:modelValue', { ...props.modelValue, [key]: value });
};

const updateOptions = (data) => {
    try {
        const currentOptions = props.modelValue.options ? JSON.parse(props.modelValue.options) : {};
        const newOptions = { ...currentOptions, ...data };
        const newOptionsString = JSON.stringify(newOptions);
        if (newOptionsString !== props.modelValue.options) {
            updateField('options', newOptionsString);
        }
    } catch(e) {
        // On error, start fresh
        updateField('options', JSON.stringify(data));
    }
};

// Sync from prop's 'options' field to local state
watch(() => props.modelValue.options, (newOptions) => {
    try {
        const options = newOptions ? JSON.parse(newOptions) : {};
        description.value = options.description || '';
        answerDescription.value = options.answerDescription || '';
    } catch (e) {
        description.value = '';
        answerDescription.value = '';
    }
}, { immediate: true });

// Sync from local state back to prop's 'options' field
watch(description, (val) => updateOptions({ description: val }));
watch(answerDescription, (val) => updateOptions({ answerDescription: val }));

const questionFileList = computed(() => {
    try {
        const options = props.modelValue.options ? JSON.parse(props.modelValue.options) : {};
        if (options.questionFileUrl) {
            return [{ name: options.questionFileName, url: options.questionFileUrl, status: 'success' }];
        }
    } catch(e) {}
    return [];
});

const answerFileList = computed(() => {
    try {
        const answer = props.modelValue.answer ? JSON.parse(props.modelValue.answer) : {};
        if (answer.answerFileUrl) {
            return [{ name: answer.answerFileName, url: answer.answerFileUrl, status: 'success' }];
        }
    } catch (e) {}
    return [];
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

const handleQuestionUploadSuccess = (res, file) => {
  updateOptions({
      questionFileUrl: `${qiniuData.value.domain}/${res.key}`,
      questionFileName: file.name
  });
  ElMessage.success('题目附件上传成功');
};

const handleAnswerUploadSuccess = (res, file) => {
  const answerData = {
      answerFileUrl: `${qiniuData.value.domain}/${res.key}`,
      answerFileName: file.name
  };
  updateField('answer', JSON.stringify(answerData));
  ElMessage.success('答案附件上传成功');
};

const handleUploadError = () => {
  ElMessage.error('上传失败，请检查网络或联系管理员');
}

const handlePreview = (uploadFile) => {
    if (uploadFile.url) {
        window.open(uploadFile.url);
    }
};


</script>

<style scoped>
.programming-editor {
    padding: 20px;
}

.upload-container {
    display: flex;
    align-items: center;
    gap: 12px;
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

.el-upload__tip {
    margin-top: 8px;
    font-size: 12px;
    color: #909399;
}
</style> 