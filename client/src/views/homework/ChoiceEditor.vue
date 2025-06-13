<template>
    <div class="choice-editor">
        <el-form :model="questionData" label-width="80px">
            <el-form-item label="题目内容">
                <el-input v-model="questionData.questionText" type="textarea" autosize placeholder="请输入题目内容"></el-input>
            </el-form-item>
            <el-form-item
                v-for="(option, index) in questionData.options"
                :key="index"
                :label="'选项 ' + String.fromCharCode(65 + index)">
                <el-input v-model="option.text" placeholder="请输入选项内容" style="width: 80%; margin-right: 10px;"></el-input>
                <el-button type="danger" size="small" @click="removeOption(index)">删除</el-button>
            </el-form-item>
            <el-form-item>
                <el-button type="primary" plain @click="addOption">添加选项</el-button>
            </el-form-item>
            <el-form-item label="正确答案">
                <el-radio-group v-model="questionData.answer">
                    <el-radio 
                        v-for="(option, index) in questionData.options"
                        :key="index"
                        :label="String.fromCharCode(65 + index)">
                        {{ String.fromCharCode(65 + index) }}
                    </el-radio>
                </el-radio-group>
            </el-form-item>
            <el-form-item label="分值">
                <el-input-number v-model="questionData.score" :min="1" :max="100"></el-input-number>
            </el-form-item>
        </el-form>
    </div>
</template>

<script setup>
import { computed } from 'vue';

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

const addOption = () => {
    if (!questionData.value.options) {
        questionData.value.options = [];
    }
    questionData.value.options.push({ text: '' });
};

const removeOption = (index) => {
    questionData.value.options.splice(index, 1);
};
</script>

<style scoped>
.choice-editor {
    padding: 20px;
}
</style> 