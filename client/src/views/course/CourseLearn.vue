<template>
  <div class="course-learn-container">
    <el-page-header @back="goBack" class="page-header">
        <template #content>
            <span class="text-large font-600 mr-3"> {{ courseInfo.title }} </span>
        </template>
    </el-page-header>
    
    <el-tabs v-model="activeTab">
      <!-- 章节内容 -->
      <el-tab-pane label="章节内容" name="chapters">
        <ChapterContent
          :tree-data="treeData"
          :tree-props="treeProps"
          :selected-node="selectedNode"
          @node-click="handleNodeClick"
          @video-ended="handleVideoEnded"
          ref="chapterContentRef"
        />
      </el-tab-pane>

      <!-- 作业列表 -->
      <el-tab-pane label="作业" name="homework">
        <HomeworkList
          :homework-list="homeworkList"
          :loading="homeworkLoading"
          @do-homework="handleDoHomework"
        />
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useCourseLearn } from '@/composables/useCourseLearn';
import { getHomeworkList } from '@/api/homework';
import { updateVideoProgress } from '@/api/progress';
import { ElMessage } from 'element-plus';
import ChapterContent from '@/components/course/ChapterContent.vue';
import HomeworkList from '@/components/course/HomeworkList.vue';

const route = useRoute();
const router = useRouter();
const courseId = ref(route.params.id);
const chapterContentRef = ref(null);

const { 
  courseInfo, 
  treeData, 
  selectedNode, 
  treeProps,
  fetchLearnData,
  handleNodeClick,
  handleVideoEnded
} = useCourseLearn(courseId, chapterContentRef);

const activeTab = ref('chapters');
const homeworkList = ref([]);
const homeworkLoading = ref(false);

watch(activeTab, (newTab) => {
    if (newTab === 'homework') {
        fetchHomeworkList();
    }
});

const fetchHomeworkList = async () => {
    if (!courseId.value) return;
    homeworkLoading.value = true;
    try {
        homeworkList.value = await getHomeworkList(courseId.value);
    } catch (error) {
        ElMessage.error('获取作业列表失败');
        console.error(error);
    } finally {
        homeworkLoading.value = false;
    }
};

const handleDoHomework = (homework) => {
    router.push({ name: 'HomeworkDo', params: { id: homework.id } });
};

onMounted(async () => {
  await fetchLearnData();
  fetchHomeworkList();
});

const goBack = () => {
  // 在返回前，最后再提交一次进度
  if (chapterContentRef.value?.videoPlayer && selectedNode.value && selectedNode.value.type === 'section') {
      const watchDuration = Math.round(chapterContentRef.value.videoPlayer.currentTime);
      if (watchDuration > 0) {
        updateVideoProgress({ videoId: selectedNode.value.id, watchDuration });
      }
  }
  router.back();
};
</script>

<style scoped>
.course-learn-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background-color: #f5f7fa;
}

.header {
  padding: 15px 20px;
  background-color: #fff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, .08);
  flex-shrink: 0;
}

.main-content {
  flex-grow: 1;
  padding: 20px;
  overflow: hidden;
}
</style> 