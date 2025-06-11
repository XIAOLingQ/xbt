<template>
  <div class="programming-editor">
    <el-form :model="questionData" label-width="80px">
      <el-form-item label="题目内容">
        <el-input v-model="questionData.questionText" type="textarea" autosize placeholder="请输入题目内容"></el-input>
      </el-form-item>
      <el-form-item label="题目描述">
        <el-input v-model="questionData.description" type="textarea" :rows="5" placeholder="请输入题目详细描述"></el-input>
      </el-form-item>

      <el-form-item label="题目附件 (可选)">
        <el-upload
            :action="qiniuData.uploadUrl"
            :data="qiniuData"
            :before-upload="beforeUpload"
            :on-success="handleQuestionUploadSuccess"
            :on-error="handleUploadError"
            :on-remove="handleQuestionFileRemove"
            :on-change="handleQuestionFileChange"
            :file-list="questionFileList"
            :limit="1"
        >
          <el-button type="primary">点击上传</el-button>
          <template #tip>
            <div class="el-upload__tip">
              只能上传一个文件，新文件会覆盖旧文件。
            </div>
          </template>
        </el-upload>
      </el-form-item>

      <el-form-item label="参考答案描述">
        <el-input type="textarea" :rows="5" v-model="questionData.answerDescription" placeholder="请填写文字版参考答案..."></el-input>
      </el-form-item>

      <el-form-item label="答案附件 (可选)">
        <el-upload
            :action="qiniuData.uploadUrl"
            :data="qiniuData"
            :before-upload="beforeUpload"
            :on-success="handleAnswerUploadSuccess"
            :on-error="handleUploadError"
            :on-remove="handleAnswerFileRemove"
            :on-change="handleAnswerFileChange"
            :file-list="answerFileList"
            :limit="1"
        >
          <el-button type="primary">点击上传</el-button>
        </el-upload>
      </el-form-item>

      <el-form-item label="分值">
        <el-input-number v-model="questionData.score" :min="1" :max="100"></el-input-number>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref, watch, computed } from 'vue';
import { ElMessage } from 'element-plus';
import { getUploadToken } from '@/api/courseChapter';
import { v4 as uuidv4 } from 'uuid';

const qiniuData = ref({
  domain: 'http://sxh8oib6z.hb-bkt.clouddn.com',
  uploadUrl: 'http://upload-z1.qiniup.com',
  token: '',
  key: '',
});

const props = defineProps({
  modelValue: {
    type: Object,
    required: true
  }
});
const emit = defineEmits(['update:modelValue']);

// 使用 computed 属性来创建双向绑定
const questionData = computed({
  get: () => props.modelValue,
  set: (value) => {
    emit('update:modelValue', value);
  }
});

const questionFileList = computed(() => {
  if (props.modelValue.questionFileUrl) {
    return [{ name: props.modelValue.questionFileName, url: props.modelValue.questionFileUrl, status: 'success' }];
  }
  return [];
});

const answerFileList = computed(() => {
  if (props.modelValue.answerFileUrl) {
    return [{ name: props.modelValue.answerFileName, url: props.modelValue.answerFileUrl, status: 'success' }];
  }
  return [];
});

const handleQuestionFileChange = (uploadFile, uploadFiles) => {
  // Implementation needed
};

const handleAnswerFileChange = (uploadFile, uploadFiles) => {
  // Implementation needed
};

const beforeUpload = async (file) => {
  try {
    qiniuData.value.token = await getUploadToken();
    const fileExtension = file.name.split('.').pop() || 'tmp';
    qiniuData.value.key = `homework/${uuidv4()}.${fileExtension}`;
    return true; // allow upload
  } catch(e) {
    ElMessage.error("获取上传凭证失败");
    return false; // prevent upload
  }
};

const handleQuestionUploadSuccess = (res, file) => {
  questionData.value.questionFileUrl = `${qiniuData.value.domain}/${res.key}`;
  questionData.value.questionFileName = file.name;
  ElMessage.success('题目附件上传成功');
};

const handleAnswerUploadSuccess = (res, file) => {
  questionData.value.answerFileUrl = `${qiniuData.value.domain}/${res.key}`;
  questionData.value.answerFileName = file.name;
  ElMessage.success('答案附件上传成功');
};

const handleUploadError = () => {
  ElMessage.error('上传失败，请检查网络或联系管理员');
}

const handleQuestionFileRemove = (file, fileList) => {
  questionData.value.questionFileUrl = '';
  questionData.value.questionFileName = '';
};

const handleAnswerFileRemove = (file, fileList) => {
  questionData.value.answerFileUrl = '';
  questionData.value.answerFileName = '';
};
</script>

<style scoped>
.programming-editor {
  padding: 20px;
}
</style> 