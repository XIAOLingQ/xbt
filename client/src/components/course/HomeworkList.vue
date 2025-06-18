<template>
  <div class="homework-list">
    <div class="tab-header">
      <h3>作业列表</h3>
    </div>
    <el-table :data="homeworkList" style="width: 100%" v-loading="loading">
      <el-table-column prop="title" label="作业标题" />
      <el-table-column prop="startTime" label="开始时间" width="200"/>
      <el-table-column prop="endTime" label="截止时间" width="200"/>
      <el-table-column label="状态与得分" width="180">
        <template #default="scope">
          <el-tag :type="getStatusTag(scope.row.status)">
            {{ getStatusText(scope.row) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150">
        <template #default="scope">
          <el-button size="small" type="primary" @click="handleDoHomework(scope.row)">
            {{ scope.row.status === 1 ? '开始答题' : '查看提交' }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-empty v-if="!loading && homeworkList.length === 0" description="该课程暂无作业"></el-empty>
  </div>
</template>

<script setup>
import { defineProps, defineEmits } from 'vue';

const props = defineProps({
  homeworkList: {
    type: Array,
    default: () => []
  },
  loading: {
    type: Boolean,
    default: false
  }
});

const emit = defineEmits(['do-homework']);

const getStatusTag = (status) => {
  switch(status) {
    case 1: return 'info'; // 未开始
    case 2: return 'warning'; // 已提交
    case 3: return 'success'; // 已批改
    default: return 'info';
  }
};

const getStatusText = (row) => {
  switch(row.status) {
    case 1: return '未开始';
    case 2: return '已提交';
    case 3: 
      return row.score !== null ? `已批改 (${row.score}分)` : '已批改';
    default: return '未知';
  }
};

const handleDoHomework = (homework) => {
  emit('do-homework', homework);
};
</script>

<style scoped>
.homework-list {
  width: 100%;
}

.tab-header {
  margin-bottom: 20px;
}

.tab-header h3 {
  margin: 0;
  font-size: 18px;
  color: #333;
}
</style> 