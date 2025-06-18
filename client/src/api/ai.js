import request from '../utils/request';

export function getTopicHistory() {
    return request.get('/ai/history/topics');
}

export function getQuestionsByBatchId(batchId) {
    return request.get(`/ai/history/questions`, {
        params: {
            batchId: batchId
        }
    });
}

export function generateAiQuestion(userInput) {
    return request.post('/ai/generate-question', { userInput });
}

export function submitAiAnswers(batchId, answers) {
    return request.post('/ai/submit', { batchId, answers });
}

const api = {
    getTopicHistory,
    getQuestionsByBatchId,
    generateAiQuestion,
    submitAiAnswers
};

export default api; 