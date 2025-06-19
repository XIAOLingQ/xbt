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
  <ChatContainer v-if="courseId" :course-id="courseId" />
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
import ChatContainer from '@/components/common/ChatContainer.vue';

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
  background-color: var(--background-color);
  padding: 0px 20px 20px;
}

.page-header {
  margin-bottom: 10px;
  padding-bottom: 15px;
  position: relative;
}

.page-header:after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 60px;
  height: 3px;
  background: var(--primary-color);
  border-radius: 3px;
}

.main-content {
  flex-grow: 1;
  padding: 20px;
  overflow: hidden;
  background-color: var(--background-color);
}
</style> 