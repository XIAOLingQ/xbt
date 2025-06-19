<template>
  <div class="ai-assistant">
    <div class="ai-fab" @click="toggleAiChat">
      <div class="ai-fab-icon">
        <div class="ai-fab-corner"></div>
        <div class="ai-fab-eyes">
          <div class="ai-fab-eye"></div>
          <div class="ai-fab-eye"></div>
        </div>
        <div class="ai-fab-dots">
          <div class="ai-fab-dot"></div>
          <div class="ai-fab-dot"></div>
          <div class="ai-fab-dot"></div>
        </div>
      </div>

      <!-- AI聊天界面 -->
      <div class="ai-chat-container" v-show="chatVisible" :style="chatContainerStyle">
        <!-- 添加8个方向的拖拽手柄 -->
        <div class="resize-handle resize-handle-n" @mousedown="(e) => startResize('n', e)"></div>
        <div class="resize-handle resize-handle-e" @mousedown="(e) => startResize('e', e)"></div>
        <div class="resize-handle resize-handle-s" @mousedown="(e) => startResize('s', e)"></div>
        <div class="resize-handle resize-handle-w" @mousedown="(e) => startResize('w', e)"></div>
        <div class="resize-handle resize-handle-ne" @mousedown="(e) => startResize('ne', e)"></div>
        <div class="resize-handle resize-handle-se" @mousedown="(e) => startResize('se', e)"></div>
        <div class="resize-handle resize-handle-sw" @mousedown="(e) => startResize('sw', e)"></div>
        <div class="resize-handle resize-handle-nw" @mousedown="(e) => startResize('nw', e)"></div>
        
        <div class="chat-header">
          <span>AI助手</span>
          <el-icon class="close-icon" @click.stop="chatVisible = false">
            <Close />
          </el-icon>
        </div>
        <div class="chat-messages" ref="chatMessagesRef">
          <div v-for="(msg, index) in chatMessages" :key="index" 
            :class="['message', msg.type === 'user' ? 'user-message' : 'ai-message']">
            <div v-if="msg.type === 'user'" v-text="msg.content"></div>
            <div v-else class="markdown-body" v-html="renderMarkdown(msg.content)"></div>
          </div>
          <!-- 添加思考中动画 -->
          <div v-if="loading" class="message ai-message">
            <div class="thinking">
              <span class="dot"></span>
              <span class="dot"></span>
              <span class="dot"></span>
            </div>
          </div>
        </div>
        <div class="chat-input">
          <el-input
            v-model="userInput"
            placeholder="请输入你的问题"
            @keyup.enter="sendMessage"
            :disabled="loading"
          >
            <template #append>
              <el-button @click="sendMessage" :loading="loading">发送</el-button>
            </template>
          </el-input>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick } from 'vue';
import { ElMessage } from 'element-plus';
import { Close } from '@element-plus/icons-vue';
import { marked } from 'marked';
import request from '@/utils/request';

const loading = ref(false);
const chatVisible = ref(false);
const userInput = ref('');
const chatMessages = ref([]);
const chatMessagesRef = ref(null);

const resizeDirection = ref('');
const isResizing = ref(false);
const startPos = ref({ x: 0, y: 0 });
const startSize = ref({ width: 0, height: 0 });
const chatContainerStyle = ref({ width: '360px', height: '480px' });

// 配置marked选项
marked.setOptions({
  gfm: true,
  breaks: true,
  sanitize: false,
  highlight: function (code, lang) {
    return code;
  }
});

// Markdown渲染函数
const renderMarkdown = (content) => {
  try {
    return marked(content);
  } catch (error) {
    console.error('Markdown rendering error:', error);
    return content;
  }
};

// 调用AI对话接口
const callAiApi = async (input) => {
  try {
    const response = await request({
      url: '/workflow/run',
      method: 'post',
      data: {
        userInput: input,
        videoUrl: ''
      }
    });
    
    if (response) {
      return response;
    } else {
      throw new Error(response.message || '请求失败');
    }
  } catch (error) {
    console.error('AI API Error:', error);
    throw error;
  }
};

// 滚动到最新消息
const scrollToBottom = async () => {
  await nextTick();
  if (chatMessagesRef.value) {
    chatMessagesRef.value.scrollTop = chatMessagesRef.value.scrollHeight;
  }
};

// 发送消息
const sendMessage = async () => {
  if (!userInput.value.trim() || loading.value) return;
  
  const message = userInput.value;
  userInput.value = '';
  
  // 添加用户消息
  chatMessages.value.push({
    type: 'user',
    content: message
  });
  
  loading.value = true;
  try {
    // 调用AI接口
    const response = await callAiApi(message);
    
    // 添加AI回复
    chatMessages.value.push({
      type: 'ai',
      content: response
    });
    
    await scrollToBottom();
  } catch (error) {
    ElMessage.error('获取回复失败，请重试');
  } finally {
    loading.value = false;
  }
};

// 切换AI聊天窗口
const toggleAiChat = async (event) => {
  event.stopPropagation();
  if (!chatVisible.value) {
    chatVisible.value = true;
    chatMessages.value = [];
  }
};

// 拖拽调整大小相关方法
const startResize = (direction, e) => {
  isResizing.value = true;
  resizeDirection.value = direction;
  startPos.value = {
    x: e.clientX,
    y: e.clientY
  };
  startSize.value = {
    width: parseInt(chatContainerStyle.value.width),
    height: parseInt(chatContainerStyle.value.height)
  };

  document.addEventListener('mousemove', handleResize);
  document.addEventListener('mouseup', stopResize);
};

const handleResize = (e) => {
  if (!isResizing.value) return;

  const deltaX = e.clientX - startPos.value.x;
  const deltaY = e.clientY - startPos.value.y;
  
  let newWidth = startSize.value.width;
  let newHeight = startSize.value.height;

  switch (resizeDirection.value) {
    case 'e':
      newWidth += deltaX;
      break;
    case 'w':
      newWidth -= deltaX;
      break;
    case 'n':
      newHeight -= deltaY;
      break;
    case 's':
      newHeight += deltaY;
      break;
    case 'ne':
      newWidth += deltaX;
      newHeight -= deltaY;
      break;
    case 'nw':
      newWidth -= deltaX;
      newHeight -= deltaY;
      break;
    case 'se':
      newWidth += deltaX;
      newHeight += deltaY;
      break;
    case 'sw':
      newWidth -= deltaX;
      newHeight += deltaY;
      break;
  }

  // 限制最小和最大尺寸
  newWidth = Math.max(300, Math.min(800, newWidth));
  newHeight = Math.max(400, Math.min(800, newHeight));

  chatContainerStyle.value = {
    width: `${newWidth}px`,
    height: `${newHeight}px`
  };
};

const stopResize = () => {
  isResizing.value = false;
  document.removeEventListener('mousemove', handleResize);
  document.removeEventListener('mouseup', stopResize);
};
</script>

<style scoped>
.ai-assistant {
  position: relative;
}

.ai-fab {
  position: fixed;
  right: 40px;
  bottom: 40px;
  z-index: 1000;
}

.ai-fab-icon {
  position: relative;
  width: 100%;
  height: 100%;
  background: #2D64B3;
  border-radius: 12px;
  box-shadow: 0 4px 8px rgba(0,0,0,0.2);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.ai-fab-corner {
  position: absolute;
  top: 0;
  right: 0;
  width: 0;
  height: 0;
  border-style: solid;
  border-width: 0 22px 22px 0;
  border-color: transparent #FFAD44 transparent transparent;
}

.ai-fab-eyes {
  display: flex;
  justify-content: space-between;
  width: 30px;
  margin-top: 5px;
  z-index: 1;
}

.ai-fab-eye {
  width: 12px;
  height: 12px;
  background: white;
  border-radius: 50%;
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
}

.ai-fab-eye::after {
  content: '';
  width: 6px;
  height: 6px;
  background: black;
  border-radius: 50%;
}

.ai-fab-dots {
  display: flex;
  justify-content: space-between;
  width: 18px;
  margin-top: 6px;
  z-index: 1;
}

.ai-fab-dot {
  width: 4px;
  height: 4px;
  background: rgba(255, 255, 255, 0.6);
  border-radius: 50%;
}

.ai-chat-container {
  position: absolute;
  bottom: 80px;
  right: 0;
  background: white;
  border-radius: 16px;
  box-shadow: 0 8px 24px rgba(0,0,0,0.15);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.chat-header {
  padding: 16px 20px;
  background: #2D64B3;
  color: white;
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 16px;
  font-weight: 500;
  border-bottom: 1px solid rgba(255,255,255,0.1);
}

.close-icon {
  cursor: pointer;
  font-size: 18px;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  background: #F8FAFC;
  user-select: text;
}

.message {
  margin-bottom: 20px;
  max-width: 85%;
  word-break: break-word;
  display: flex;
  position: relative;
  background: transparent;
}

.user-message {
  margin-left: auto;
  flex-direction: row-reverse;
  background: transparent;
}

.ai-message {
  margin-right: auto;
  flex-direction: row;
  background: transparent;
}

.message > div {
  padding: 12px 16px;
  border-radius: 12px;
  font-size: 14px;
  line-height: 1.6;
  max-width: 100%;
}

.user-message > div {
  background: linear-gradient(135deg, #3B82F6 0%, #2563EB 100%);
  color: white;
  border-radius: 12px 12px 2px 12px;
  box-shadow: 0 2px 8px rgba(37, 99, 235, 0.2);
  display: inline-block;
}

.ai-message > div {
  background: white;
  color: #1F2937;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
  border-radius: 12px 12px 12px 2px;
  border: 1px solid #E5E7EB;
  display: inline-block;
}

.chat-input {
  padding: 16px;
  background: white;
  border-top: 1px solid #E5E7EB;
}

.chat-input :deep(.el-input-group__append) {
  background: #2563EB;
  border-color: #2563EB;
  color: white;
  padding: 0 16px;
}

.chat-input :deep(.el-input__wrapper) {
  box-shadow: 0 0 0 1px #E5E7EB inset;
}

.chat-input :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #2563EB inset;
}

.chat-input :deep(.el-input__inner) {
  height: 40px;
  line-height: 40px;
  font-size: 14px;
}

.chat-messages::-webkit-scrollbar {
  width: 6px;
}

.chat-messages::-webkit-scrollbar-track {
  background: transparent;
}

.chat-messages::-webkit-scrollbar-thumb {
  background: #CBD5E1;
  border-radius: 3px;
}

.chat-messages::-webkit-scrollbar-thumb:hover {
  background: #94A3B8;
}

.markdown-body {
  font-size: 14px;
  line-height: 1.6;
}

.markdown-body :deep(pre) {
  background: #F1F5F9;
  border-radius: 8px;
  padding: 12px 16px;
  margin: 8px 0;
}

.markdown-body :deep(code) {
  background: #F1F5F9;
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 13px;
  color: #2563EB;
}

.markdown-body :deep(pre code) {
  color: #1F2937;
  padding: 0;
  background: transparent;
}

.markdown-body :deep(p) {
  margin: 8px 0;
}

.markdown-body :deep(ul), 
.markdown-body :deep(ol) {
  padding-left: 1.5em;
  margin: 8px 0;
}

.markdown-body :deep(blockquote) {
  border-left: 4px solid #E5E7EB;
  padding-left: 12px;
  color: #64748B;
  margin: 8px 0;
}

.message {
  animation: message-fade-in 0.3s ease-out;
}

@keyframes message-fade-in {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.resize-handle {
  position: absolute;
  z-index: 2;
  background: transparent;
}

.resize-handle-n {
  top: 0;
  left: 6px;
  right: 6px;
  height: 6px;
  cursor: n-resize;
}

.resize-handle-e {
  top: 6px;
  right: 0;
  bottom: 6px;
  width: 6px;
  cursor: e-resize;
}

.resize-handle-s {
  bottom: 0;
  left: 6px;
  right: 6px;
  height: 6px;
  cursor: s-resize;
}

.resize-handle-w {
  top: 6px;
  left: 0;
  bottom: 6px;
  width: 6px;
  cursor: w-resize;
}

.resize-handle-ne {
  top: 0;
  right: 0;
  width: 10px;
  height: 10px;
  cursor: ne-resize;
}

.resize-handle-nw {
  top: 0;
  left: 0;
  width: 10px;
  height: 10px;
  cursor: nw-resize;
}

.resize-handle-se {
  bottom: 0;
  right: 0;
  width: 10px;
  height: 10px;
  cursor: se-resize;
}

.resize-handle-sw {
  bottom: 0;
  left: 0;
  width: 10px;
  height: 10px;
  cursor: sw-resize;
}

.resize-handle::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(45, 100, 179, 0.1);
  opacity: 0;
  transition: opacity 0.2s;
}

.resize-handle:hover::after {
  opacity: 1;
}

.chat-messages {
  margin: 6px;
}

.chat-header {
  padding: 12px 16px;
  margin-top: 6px;
}

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

.ai-message {
  margin-right: auto;
  padding: 12px 16px;
}

.user-message {
  color: white;
  margin-left: auto;
  padding: 8px 12px;
}

/* 思考中动画样式 */
.thinking {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 12px 16px;
  background: white;
  border-radius: 12px 12px 12px 2px;
  border: 1px solid #E5E7EB;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
}

.thinking .dot {
  width: 8px;
  height: 8px;
  background: #2D64B3;
  border-radius: 50%;
  animation: thinking 1.4s infinite ease-in-out;
}

.thinking .dot:nth-child(1) {
  animation-delay: -0.32s;
}

.thinking .dot:nth-child(2) {
  animation-delay: -0.16s;
}

@keyframes thinking {
  0%, 80%, 100% { 
    transform: scale(0);
    opacity: 0.3;
  }
  40% { 
    transform: scale(1);
    opacity: 1;
  }
}
</style> 