<template>
    <div class="chat-box-container">
        <div class="chat-header">
            <h3>课程讨论</h3>
            <span class="status" :class="isConnected ? 'connected' : 'disconnected'">
                {{ isConnected ? '已连接' : '未连接' }}
            </span>
        </div>
        <div class="message-list" ref="messageListRef">
            <div v-for="msg in messages" :key="msg.id" class="message-item" :class="{ 'my-message': isMyMessage(msg.senderId) }">
                <el-avatar :src="msg.senderAvatar" class="avatar"></el-avatar>
                <div class="message-content">
                    <div class="sender-name">{{ msg.senderName }}</div>
                    <div class="bubble">{{ msg.content }}</div>
                </div>
            </div>
             <el-empty v-if="messages.length === 0" description="暂无消息，快来发起讨论吧！" :image-size="80"></el-empty>
        </div>
        <div class="chat-input">
            <el-input
                v-model="newMessage"
                placeholder="输入消息..."
                @keyup.enter="handleSend"
                :disabled="!isConnected"
            ></el-input>
            <el-button type="primary" @click="handleSend" :disabled="!isConnected">发送</el-button>
        </div>
    </div>
</template>

<script setup>
import { ref, watch, nextTick } from 'vue';
import { useUserStore } from '@/stores/user';

const props = defineProps({
    messages: {
        type: Array,
        required: true
    },
    isConnected: {
        type: Boolean,
        required: true
    }
});

const emit = defineEmits(['sendMessage']);

const userStore = useUserStore();
const newMessage = ref('');
const messageListRef = ref(null);

const isMyMessage = (senderId) => {
    return userStore.userInfo.id === senderId;
};

const handleSend = () => {
    if (newMessage.value.trim()) {
        emit('sendMessage', newMessage.value);
        newMessage.value = '';
    }
};

// 监听消息列表变化，自动滚动到底部
watch(() => props.messages, () => {
    nextTick(() => {
        const container = messageListRef.value;
        if (container) {
            container.scrollTop = container.scrollHeight;
        }
    });
}, { deep: true });

</script>

<style scoped>
.chat-box-container {
    display: flex;
    flex-direction: column;
    height: 100%;
    border: 1px solid #e0e0e0;
    border-radius: 8px;
    overflow: hidden;
}

.chat-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 10px 15px;
    background-color: #f5f7fa;
    border-bottom: 1px solid #e0e0e0;
}

.chat-header h3 {
    margin: 0;
    font-size: 16px;
}

.status {
    font-size: 12px;
}
.status.connected {
    color: #67c23a;
}
.status.disconnected {
    color: #f56c6c;
}

.message-list {
    flex-grow: 1;
    padding: 15px;
    overflow-y: auto;
    background-color: #fff;
}

.message-item {
    display: flex;
    margin-bottom: 15px;
}

.message-item .avatar {
    margin-right: 10px;
}

.message-content {
    max-width: 70%;
}

.sender-name {
    font-size: 12px;
    color: #909399;
    margin-bottom: 5px;
}

.bubble {
    padding: 10px 15px;
    background-color: #f0f2f5;
    border-radius: 18px;
    word-wrap: break-word;
}


/* 我发送的消息 */
.my-message {
    flex-direction: row-reverse;
}

.my-message .avatar {
    margin-left: 10px;
    margin-right: 0;
}
.my-message .message-content {
    display: flex;
    flex-direction: column;
    align-items: flex-end;
}

.my-message .bubble {
    background-color: #409eff;
    color: #fff;
}

.chat-input {
    display: flex;
    padding: 10px;
    border-top: 1px solid #e0e0e0;
    background-color: #f5f7fa;
}
</style> 