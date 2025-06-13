import { ref, computed } from 'vue';
import { ElMessage } from 'element-plus';
import { getLearnPageData } from '@/api/course';
import { updateVideoProgress } from '@/api/progress';

export function useCourseLearn(courseId, videoPlayer) {
    const courseInfo = ref({});
    const treeData = ref([]);
    const selectedNode = ref(null);

    const treeProps = {
        children: 'children',
        label: 'label',
    };

    const fetchLearnData = async () => {
        try {
            const data = await getLearnPageData(courseId.value);
            courseInfo.value = { id: data.id, title: data.title, description: data.description };
            
            treeData.value = (data.chapters || []).map(chapter => ({
                id: `chapter-${chapter.id}`,
                label: chapter.title,
                type: 'chapter',
                children: (chapter.sections || []).map(section => ({
                    id: section.id,
                    label: section.title,
                    type: 'section',
                    url: section.url,
                    duration: section.duration, // 假设API会返回视频总时长
                    isCompleted: section.isCompleted,
                }))
            }));

        } catch (error) {
            ElMessage.error('加载课程数据失败');
            console.error(error);
        }
    };
    
    // 提交进度的通用函数
    const submitProgress = (videoId, duration) => {
        const watchTime = Math.round(duration);
        // 只有在 watchTime 是一个有效的、非负的数字时才提交
        if (!videoId || isNaN(watchTime) || watchTime < 0) {
            return;
        }

        updateVideoProgress({
            videoId: videoId,
            watchDuration: watchTime
        }).catch(err => {
            console.error('更新学习进度失败(from useCourseLearn):', err);
        });
    }

    const handleNodeClick = (data) => {
        // 如果上一个节点是视频，提交它的进度
        if (selectedNode.value && selectedNode.value.type === 'section' && videoPlayer.value) {
            submitProgress(selectedNode.value.id, videoPlayer.value.currentTime);
        }

        if (data.type === 'section') {
            selectedNode.value = data;
        } else {
            selectedNode.value = null; // 或者处理作业等其他类型
        }
    };

    const handleVideoEnded = async () => {
        if (selectedNode.value && selectedNode.value.type === 'section') {
            // 提交最终进度 (视频总时长)
            submitProgress(selectedNode.value.id, selectedNode.value.duration);

            // 标记为已完成
            if (!selectedNode.value.isCompleted) {
                 selectedNode.value.isCompleted = true; // 立即在UI上更新
                 // 也可以在这里加一个调用后端标记完成的API，但为了简化，我们依赖进度提交
                 ElMessage.success(`《${selectedNode.value.label}》已完成`);
            }
        }
    };

    return {
        courseInfo,
        treeData,
        selectedNode,
        treeProps,
        fetchLearnData,
        handleNodeClick,
        handleVideoEnded
    };
} 