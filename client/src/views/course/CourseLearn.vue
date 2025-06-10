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
        <el-row :gutter="20">
          <el-col :span="8">
            <el-card class="box-card chapter-list">
              <template #header>
                课程目录
              </template>
              <el-tree
                v-if="treeData.length > 0"
                :data="treeData"
                :props="treeProps"
                @node-click="handleNodeClick"
                :expand-on-click-node="false"
                default-expand-all
              >
                <template #default="{ node, data }">
                  <span class="custom-tree-node">
                    <span>
                      <el-icon v-if="data.type === 'section'"><VideoCamera /></el-icon>
                      <el-icon v-else-if="data.type === 'homework'"><Document /></el-icon>
                      {{ node.label }}
                    </span>
                    <el-tag v-if="data.isCompleted" type="success" size="small" effect="dark">已完成</el-tag>
                  </span>
                </template>
              </el-tree>
              <el-empty v-else description="目录加载中..."></el-empty>
            </el-card>
          </el-col>
          <el-col :span="16">
            <el-card class="box-card video-player-card">
              <div class="video-content-wrapper">
                <div class="main-video-area">
                  <div v-if="selectedNode">
                    <div v-if="selectedNode.type === 'section'">
                      <h3>{{ selectedNode.label }}</h3>
                      <div v-if="selectedNode.url" class="video-container">
                        <video
                          ref="videoPlayer"
                          :key="selectedNode.id"
                          :src="selectedNode.url" 
                          controls 
                          class="video-player"
                          @timeupdate="handleTimeUpdate"
                          @ended="handleVideoEnded"
                        ></video>
                      </div>
                      <div class="video-actions" v-if="selectedNode.url" style="margin-top: 15px;">
                        <el-button @click="toggleAskPanel" type="primary" :icon="ChatDotRound">
                           {{ isAskPanelVisible ? '收起问答' : '问一问' }}
                        </el-button>
                      </div>
                      <el-empty v-else description="该课时暂无视频"></el-empty>
                    </div>
                    <div v-else-if="selectedNode.type === 'homework'">
                      <h3>作业：{{ selectedNode.label }}</h3>
                      <el-empty description="作业功能开发中..."></el-empty>
                    </div>
                  </div>
                  <el-empty v-else description="请从左侧选择一个课时或作业开始学习"></el-empty>
                </div>

                <el-drawer
                  v-model="isAskPanelVisible"
                  direction="ltr"
                  :with-header="true"
                  :modal="false"
                  :size="drawerWidth"
                  class="ask-drawer"
                  @close="isAskPanelVisible = false"
                >
                  <template #header>
                    <div class="drawer-header">
                      <span>AI 助教</span>
                      <el-button type="danger" :icon="Delete" circle @click="clearChatHistory" />
                    </div>
                  </template>
                  <div class="resize-handle" @mousedown="initResize"></div>
                  <div class="ask-panel">
                    <div class="ask-result-display" ref="chatDisplay">
                        <div v-for="(message, index) in chatHistory" :key="index" :class="['chat-message', message.type]">
                            <template v-if="message.type === 'ai'">
                                <el-avatar :icon="Service" class="chat-avatar" />
                                <div class="message-content markdown-body" v-html="renderMarkdown(message.content)"></div>
                            </template>
                            <template v-else>
                                <el-avatar :icon="User" class="chat-avatar" />
                                <div class="message-content">{{ message.content }}</div>
                            </template>
                        </div>
                        <div v-if="isAsking" class="chat-message ai">
                            <el-avatar :icon="Service" class="chat-avatar" />
                            <div class="message-content loading-content">
                                <el-icon class="is-loading"><Loading /></el-icon>
                                <span>思考中...</span>
                            </div>
                        </div>
                        <el-empty v-if="!chatHistory.length && !isAsking" description="你好！对于这个视频，有什么可以帮你的吗？"></el-empty>
                    </div>
                    <div class="ask-input-area">
                      <el-input
                        v-model="userQuestion"
                        :rows="3"
                        type="textarea"
                        placeholder="请输入你的问题..."
                        @keyup.enter.native="submitQuestion"
                      />
                      <div class="ask-options">
                        <el-checkbox v-model="includeVideoContext" label="结合当前视频内容提问" border />
                        <el-button type="primary" @click="submitQuestion" :disabled="isAsking">
                          发送
                        </el-button>
                      </div>
                    </div>
                  </div>
                </el-drawer>

              </div>
            </el-card>
          </el-col>
        </el-row>
      </el-tab-pane>

      <!-- 作业列表 -->
      <el-tab-pane label="作业" name="homework">
        <div class="tab-header">
            <h3>作业列表</h3>
        </div>
        <el-table :data="homeworkList" style="width: 100%" v-loading="homeworkLoading">
            <el-table-column prop="title" label="作业标题" />
            <el-table-column prop="endTime" label="截止时间" width="200"/>
            <el-table-column label="状态" width="120">
                <template #default="scope">
                    <el-tag :type="getStatusTag(scope.row.status)">
                        {{ getStatusText(scope.row.status) }}
                    </el-tag>
                </template>
            </el-table-column>
             <el-table-column label="得分" prop="score" width="100">
                <template #default="scope">
                    {{ scope.row.score !== null ? scope.row.score : '-' }}
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
        <el-empty v-if="!homeworkLoading && homeworkList.length === 0" description="该课程暂无作业"></el-empty>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, nextTick } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { VideoCamera, Document, ChatDotRound, Loading, Delete, User, Service } from '@element-plus/icons-vue';
import { useCourseLearn } from '@/composables/useCourseLearn';
import { getHomeworkList } from '@/api/homework';
import { updateVideoProgress } from '@/api/progress';
import { ElMessage } from 'element-plus';
import request from '@/utils/request';
import { marked } from 'marked';
import DOMPurify from 'dompurify';

const route = useRoute();
const router = useRouter();
const courseId = ref(route.params.id);
const videoPlayer = ref(null);

// --- 视频播放进度相关 ---
let lastUpdateTime = 0;
const UPDATE_INTERVAL = 15 * 1000; // 每15秒更新一次

const handleTimeUpdate = (event) => {
    const currentTime = Date.now();
    if (currentTime - lastUpdateTime < UPDATE_INTERVAL) {
        return; // 未达到更新间隔，直接返回
    }
    
    lastUpdateTime = currentTime;
    
    const videoElement = event.target;
    const watchDuration = Math.round(videoElement.currentTime);
    
    if (selectedNode.value && selectedNode.value.id && watchDuration > 0) {
        updateVideoProgress({
            videoId: selectedNode.value.id,
            watchDuration: watchDuration
        }).catch(err => {
            console.error('更新学习进度失败:', err);
        });
    }
};

const { 
  courseInfo, 
  treeData, 
  selectedNode, 
  treeProps,
  fetchLearnData,
  handleNodeClick,
  handleVideoEnded
} = useCourseLearn(courseId, videoPlayer);

const activeTab = ref('chapters');
const homeworkList = ref([]);
const homeworkLoading = ref(false);

// --- 视频问答功能 ---
const isAskPanelVisible = ref(false);
const userQuestion = ref('');
const isAsking = ref(false);
const chatHistory = ref([]);
const includeVideoContext = ref(true);
const chatDisplay = ref(null);
const drawerWidth = ref('400px');
const isResizing = ref(false);
const startPos = ref({ x: 0, y: 0 });
const startWidth = ref(400);

// --- 清除聊天记录 ---
const clearChatHistory = () => {
    chatHistory.value = [];
};

// 配置marked选项
marked.setOptions({
  gfm: true, // 启用GitHub风格的Markdown
  breaks: true, // 启用换行符
  sanitize: false, // 允许HTML标签
  highlight: function (code, lang) {
    // 这里可以集成代码高亮库，如Prism.js或highlight.js
    return code;
  }
});

// 添加Markdown渲染函数
const renderMarkdown = (content) => {
  try {
    return DOMPurify.sanitize(marked(content || ''));
  } catch (error) {
    console.error('Markdown rendering error:', error);
    return content;
  }
};

// --- 面板缩放 ---
const handleResize = (e) => {
  if (!isResizing.value) return;
  
  const deltaX = e.clientX - startPos.value.x;
  let newWidth = startWidth.value + deltaX;
  
  // 限制宽度范围
  newWidth = Math.max(300, Math.min(800, newWidth));
  drawerWidth.value = `${newWidth}px`;
};

const initResize = (e) => {
  e.preventDefault();
  isResizing.value = true;
  startPos.value = { x: e.clientX, y: e.clientY };
  startWidth.value = parseInt(drawerWidth.value);
  
  document.addEventListener('mousemove', handleResize);
  document.addEventListener('mouseup', stopResize);
};

const stopResize = () => {
  isResizing.value = false;
  document.removeEventListener('mousemove', handleResize);
  document.removeEventListener('mouseup', stopResize);
};

const scrollToBottom = () => {
    nextTick(() => {
        const display = chatDisplay.value;
        if (display) {
            display.scrollTop = display.scrollHeight;
        }
    });
};

const toggleAskPanel = () => {
    isAskPanelVisible.value = !isAskPanelVisible.value;
    if (isAskPanelVisible.value) {
        // 清空上一次的记录
        chatHistory.value = [];
        userQuestion.value = '';
    }
};

const submitQuestion = async () => {
    const question = userQuestion.value.trim();
    if (!question) {
        ElMessage.error('问题不能为空');
        return;
    }
    if (includeVideoContext.value && (!selectedNode.value || !selectedNode.value.url)) {
        ElMessage.error('没有可提问的视频');
        return;
    }

    isAsking.value = true;
    chatHistory.value.push({ type: 'user', content: question });
    userQuestion.value = ''; // Clear input immediately
    scrollToBottom();

    try {
        const payload = {
            userInput: question,
            videoUrl: includeVideoContext.value ? selectedNode.value.url : ""
        };

        const result = await request.post('/workflow/run', payload);
        chatHistory.value.push({ type: 'ai', content: result });
    } catch (error) {
        const errorMsg = '抱歉，处理您的问题时出错了。';
        chatHistory.value.push({ type: 'ai', content: errorMsg });
        ElMessage.error('提问失败，请稍后再试');
        console.error('Error asking question:', error);
    } finally {
        isAsking.value = false;
        scrollToBottom();
    }
};

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

const getStatusTag = (status) => {
    switch(status) {
        case 1: return 'info'; // 未开始
        case 2: return 'warning'; // 已提交
        case 3: return 'success'; // 已批改
        default: return 'info';
    }
};

const getStatusText = (status) => {
    switch(status) {
        case 1: return '未开始';
        case 2: return '已提交';
        case 3: return '已批改';
        default: return '未知';
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
  if (videoPlayer.value && selectedNode.value && selectedNode.value.type === 'section') {
      const watchDuration = Math.round(videoPlayer.value.currentTime);
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

.chapter-aside {
  height: 100%;
}

.chapter-aside .el-card, .content-main .el-card {
  height: calc(100vh - 120px);
  display: flex;
  flex-direction: column;
}

.chapter-aside :deep(.el-card__body), .content-main :deep(.el-card__body) {
  overflow-y: auto;
  flex-grow: 1;
}

.content-main {
  padding-left: 20px;
  height: 100%;
}

.custom-tree-node {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 14px;
  padding-right: 8px;
}

.custom-tree-node .el-icon {
  margin-right: 5px;
}

.video-container {
  position: relative;
  width: 100%;
  max-width: 1000px;
  margin: 20px auto;
  background: #000;
  padding-top: 56.25%; /* 16:9 宽高比 */
}

.video-player {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.video-content-wrapper {
  display: flex;
  width: 100%;
}
.main-video-area {
  flex-grow: 1;
  transition: width 0.3s ease;
}
.ask-panel {
  display: flex;
  flex-direction: column;
  height: 100%;
}
.ask-result-display {
  flex-grow: 1;
  overflow-y: auto;
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 4px;
}
.chat-message {
  margin-bottom: 20px;
  max-width: 85%;
  word-break: break-word;
  display: flex;
  align-items: flex-start;
  position: relative;
  background: transparent;
}
.chat-avatar {
  flex-shrink: 0;
  margin: 0 8px;
}
.chat-message.user {
  margin-left: auto;
  flex-direction: row-reverse;
  background: transparent;
}
.chat-message.ai {
  margin-right: auto;
  flex-direction: row;
  background: transparent;
}
.message-content {
  padding: 12px 16px;
  border-radius: 12px;
  font-size: 14px;
  line-height: 1.6;
  max-width: 100%;
}
.chat-message.user .message-content {
  background: linear-gradient(135deg, #3B82F6 0%, #2563EB 100%);
  color: white;
  border-radius: 12px 12px 2px 12px;
  box-shadow: 0 2px 8px rgba(37, 99, 235, 0.2);
}
.chat-message.ai .message-content {
  background: white;
  color: #1F2937;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
  border-radius: 12px 12px 12px 2px;
  border: 1px solid #E5E7EB;
}
.ask-input-area {
  padding-top: 15px;
}
.ask-options {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-top: 10px;
}
.loading-content {
    display: flex;
    align-items: center;
}
.loading-content .el-icon {
    margin-right: 5px;
}
.ask-drawer {
  position: relative;
  box-shadow: 5px 0 15px rgba(0,0,0,0.1);
}
.resize-handle {
  position: absolute;
  top: 0;
  right: -5px;
  width: 10px;
  height: 100%;
  cursor: col-resize;
  z-index: 100;
  background: transparent;
  transition: background-color 0.2s;
}
.resize-handle:hover {
  background: rgba(0, 0, 0, 0.1);
}
.resize-handle:active {
  background: rgba(0, 0, 0, 0.2);
}
.drawer-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

/* Markdown样式 */
.markdown-body {
  font-size: 14px;
  line-height: 1.6;
  word-wrap: break-word;
}

.markdown-body :deep(h1),
.markdown-body :deep(h2),
.markdown-body :deep(h3),
.markdown-body :deep(h4),
.markdown-body :deep(h5),
.markdown-body :deep(h6) {
  margin-top: 16px;
  margin-bottom: 8px;
  font-weight: 600;
  line-height: 1.25;
}

.markdown-body :deep(h1) {
  font-size: 1.5em;
}

.markdown-body :deep(h2) {
  font-size: 1.3em;
}

.markdown-body :deep(p) {
  margin-top: 0;
  margin-bottom: 10px;
}

.markdown-body :deep(a) {
  color: #0366d6;
  text-decoration: none;
}

.markdown-body :deep(a:hover) {
  text-decoration: underline;
}

.markdown-body :deep(code) {
  padding: 0.2em 0.4em;
  margin: 0;
  font-size: 85%;
  background-color: rgba(27,31,35,0.05);
  border-radius: 3px;
  font-family: "SFMono-Regular", Consolas, "Liberation Mono", Menlo, monospace;
}

.markdown-body :deep(pre) {
  padding: 16px;
  overflow: auto;
  font-size: 85%;
  line-height: 1.45;
  background-color: #f6f8fa;
  border-radius: 3px;
  margin-bottom: 16px;
}

.markdown-body :deep(pre code) {
  padding: 0;
  margin: 0;
  background-color: transparent;
  border: 0;
  word-break: normal;
  white-space: pre;
  font-size: 100%;
  display: inline;
  max-width: auto;
  overflow: visible;
  line-height: inherit;
}

.markdown-body :deep(ul),
.markdown-body :deep(ol) {
  padding-left: 2em;
  margin-top: 0;
  margin-bottom: 16px;
}

.markdown-body :deep(li) {
  margin-top: 0.25em;
}

.markdown-body :deep(table) {
  border-spacing: 0;
  border-collapse: collapse;
  margin-bottom: 16px;
  width: 100%;
  overflow: auto;
}

.markdown-body :deep(table th),
.markdown-body :deep(table td) {
  padding: 6px 13px;
  border: 1px solid #dfe2e5;
}

.markdown-body :deep(table tr) {
  background-color: #fff;
  border-top: 1px solid #c6cbd1;
}

.markdown-body :deep(table tr:nth-child(2n)) {
  background-color: #f6f8fa;
}

.markdown-body :deep(blockquote) {
  margin: 0 0 16px;
  padding: 0 1em;
  color: #6a737d;
  border-left: 0.25em solid #dfe2e5;
}

.markdown-body :deep(hr) {
  height: 0.25em;
  padding: 0;
  margin: 24px 0;
  background-color: #e1e4e8;
  border: 0;
}

.markdown-body :deep(img) {
  max-width: 100%;
  box-sizing: border-box;
}
</style> 