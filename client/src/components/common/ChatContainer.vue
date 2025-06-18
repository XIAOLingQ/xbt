<template>
  <div>
    <!-- Chat Window -->
    <div v-if="isChatOpen" class="chat-window">
      <div class="chat-header">
        <el-icon class="close-icon" @click="toggleChat"><Close /></el-icon>
      </div>
      <div class="chat-body-container">
        <ChatBox
          :messages="chat.messages.value"
          :is-connected="chat.isConnected.value"
          @send-message="chat.sendMessage"
        />
      </div>
    </div>

    <!-- FAB to toggle chat -->
    <el-button
      class="chat-fab"
      type="primary"
      :icon="ChatDotRound"
      circle
      @click="toggleChat"
    ></el-button>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue';
import { useChat } from '@/composables/useChat';
import { ChatDotRound, Close } from '@element-plus/icons-vue';
import ChatBox from '@/components/common/ChatBox.vue';

const props = defineProps({
  courseId: {
    type: [String, Number],
    required: true
  }
});

const isChatOpen = ref(false);
const chat = useChat(props.courseId);

const toggleChat = () => {
  isChatOpen.value = !isChatOpen.value;
};

// Watch for chat window state to connect/disconnect
watch(isChatOpen, (isOpen) => {
  if (isOpen) {
    if (props.courseId) {
      chat.connect();
    }
  } else {
    chat.disconnect();
  }
});
</script>

<style scoped>
.chat-window {
  position: fixed;
  bottom: 110px;
  right: 40px;
  width: 400px;
  height: 60vh;
  max-height: 600px;
  background-color: #fff;
  border-radius: 12px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.15);
  display: flex;
  flex-direction: column;
  overflow: hidden;
  z-index: 1000;
  border: 1px solid #ebeef5;
}

.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  height: 50px;
  background-color: #409eff;
  color: #fff;
  font-size: 16px;
  font-weight: 600;
  flex-shrink: 0;
}

.close-icon {
  cursor: pointer;
  font-size: 20px;
}

.chat-body-container {
  flex-grow: 1;
  overflow: hidden;
  position: relative;
}

/* Make ChatBox fill the container */
.chat-body-container :deep(.chat-box-component) {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.chat-fab {
  position: fixed;
  right: 40px;
  bottom: 40px;
  width: 56px;
  height: 56px;
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.15);
  z-index: 1000;
}
</style> 