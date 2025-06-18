import { ref, onUnmounted } from 'vue';
import { Client } from '@stomp/stompjs';
// import SockJS from 'sockjs-client'; // 不再需要 SockJS
import { getChatHistory } from '@/api/chat';
import { useUserStore } from '@/stores/user';

export function useChat(courseId) {
    const messages = ref([]);
    const isConnected = ref(false);
    const userStore = useUserStore();

    let stompClient = null;

    const connect = () => {
        if (isConnected.value || !courseId) return;

        // 1. 获取历史消息
        getChatHistory(courseId).then(history => {
            messages.value = history;
        });
        
        // 2. 创建 STOMP 客户端，使用原生 WebSocket
        // 构造代理后的 WebSocket URL
        const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
        const host = window.location.host; // e.g., "localhost:5173"
        const brokerURL = `${protocol}//${host}/ws`;

        stompClient = new Client({
            brokerURL: brokerURL,
            reconnectDelay: 5000,
            debug: (str) => {
                console.log('STOMP: ' + str);
            },
            onConnect: () => {
                isConnected.value = true;
                // 订阅特定课程的聊天频道
                stompClient.subscribe(`/topic/course/${courseId}`, (message) => {
                    const receivedMessage = JSON.parse(message.body);
                    messages.value.push(receivedMessage);
                });
            },
            onDisconnect: () => {
                isConnected.value = false;
            },
            onStompError: (frame) => {
                console.error('Broker reported error: ' + frame.headers['message']);
                console.error('Additional details: ' + frame.body);
            },
        });

        // 3. 激活连接
        stompClient.activate();
    };

    const disconnect = () => {
        if (stompClient) {
            stompClient.deactivate();
        }
        isConnected.value = false;
    };

    const sendMessage = (content) => {
        if (stompClient && isConnected.value && content.trim() !== '') {
            const chatMessage = {
                courseId: courseId,
                content: content,
                senderId: userStore.userInfo.id, // DTO需要，但后端会重新设置
            };
            stompClient.publish({
                destination: '/app/chat.sendMessage',
                body: JSON.stringify(chatMessage),
            });
        }
    };

    // 在组件卸载时自动断开连接
    onUnmounted(() => {
        disconnect();
    });

    return {
        messages,
        isConnected,
        connect,
        disconnect,
        sendMessage
    };
} 