<template>
  <div class="course-manage-container">
    <el-page-header @back="goBack" content="课程内容管理" class="page-header"></el-page-header>
    
    <el-tabs v-model="activeTab">
      <!-- 章节内容管理 -->
      <el-tab-pane label="章节内容" name="chapters">
        <el-row :gutter="20">
          <!-- 左侧：课程目录树 -->
          <el-col :span="8">
            <el-card class="box-card">
              <template #header>
                <div class="card-header">
                  <span>课程目录</span>
                  <el-button type="primary" :icon="Plus" @click="addChapter">添加章</el-button>
                </div>
              </template>
              <el-tree
                :data="chapterTree"
                :props="treeProps"
                node-key="id"
                :expand-on-click-node="false"
                :indent="24"
                draggable
                :allow-drop="allowDrop"
                @node-drop="handleNodeDrop"
              >
                <template #default="{ node, data }">
                  <span class="custom-tree-node">
                    <span>{{ getNumberedLabel(node) }}</span>
                    <span>
                      <el-button
                        type="text"
                        size="small"
                        v-if="data.type === 1"
                        @click="() => addSection(data)">
                        添加节
                      </el-button>
                      <el-button
                        type="text"
                        size="small"
                        @click="() => editNode(node, data)">
                        编辑
                      </el-button>
                      <el-button
                        type="text"
                        size="small"
                        style="color: #F56C6C;"
                        @click="() => removeNode(node, data)">
                        删除
                      </el-button>
                    </span>
                  </span>
                </template>
              </el-tree>
            </el-card>
          </el-col>
          
          <!-- 右侧：内容编辑区 -->
          <el-col :span="16">
            <el-card class="box-card">
              <template #header>
                <div class="card-header">
                  <span>{{ Object.keys(currentNode).length > 0 ? (currentNode.id ? '编辑内容' : '添加内容') : '请从左侧选择节点' }}</span>
                </div>
              </template>
              <div v-if="Object.keys(currentNode).length > 0">
                <el-form :model="currentNode" label-width="80px">
                    <el-form-item label="标题">
                        <el-input v-model="currentNode.title" />
                    </el-form-item>
                    <!-- 如果是视频节，显示上传组件 -->
                    <el-form-item v-if="currentNode.type === 2" label="视频">
                        <el-upload
                          class="video-uploader"
                          :action="qiniuData.uploadUrl"
                          :data="qiniuData"
                          :on-success="handleVideoSuccess"
                          :on-error="handleVideoError"
                          :on-progress="handleVideoProgress"
                          :before-upload="beforeVideoUpload"
                          :show-file-list="false"
                          :disabled="!currentNode.id"
                        >
                          <el-button size="small" type="primary" :disabled="!currentNode.id">点击上传</el-button>
                          <template #tip>
                            <div class="el-upload__tip" v-if="!currentNode.id">
                              请先保存当前课时，然后再上传视频。
                            </div>
                            <div v-if="isUploading" class="upload-progress">
                              <el-progress 
                                :percentage="uploadProgress"
                                :format="() => `${formatFileSize(uploadedSize)}/${formatFileSize(totalSize)} (${uploadProgress}%)`"
                                status="success"
                              />
                            </div>
                          </template>
                        </el-upload>
                        <br/>
                        <div v-if="currentNode.url" class="video-preview">
                            <div class="video-url">
                                视频链接: <el-link :href="currentNode.url" type="primary" target="_blank">{{ currentNode.url }}</el-link>
                            </div>
                            <div class="video-container">
                                <video 
                                    :src="currentNode.url" 
                                    controls
                                    class="video-player"
                                ></video>
                            </div>
                        </div>
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" @click="saveNode">保存</el-button>
                        <el-button @click="currentNode = {}">取消</el-button>
                    </el-form-item>
                </el-form>
              </div>
              <el-empty v-else description="请选择一个节点进行操作"></el-empty>
            </el-card>
          </el-col>
        </el-row>
      </el-tab-pane>

      <!-- 作业管理 -->
      <el-tab-pane label="作业管理" name="homework">
        <div class="tab-header">
            <h3>作业列表</h3>
            <el-button type="primary" :icon="Plus" @click="handleCreateHomework">发布新作业</el-button>
        </div>
        <el-table :data="homeworkList" style="width: 100%" v-loading="homeworkLoading">
            <el-table-column prop="title" label="作业标题" />
            <el-table-column prop="startTime" label="开始时间" />
            <el-table-column prop="endTime" label="结束时间" />
            <el-table-column label="操作" width="150">
                <template #default="scope">
                    <el-button size="small" type="primary" @click="handleEditHomework(scope.row)">编辑</el-button>
                    <el-button size="small" type="danger" @click="handleDeleteHomework(scope.row)">删除</el-button>
                </template>
            </el-table-column>
        </el-table>
        <el-empty v-if="homeworkList.length === 0" description="暂无作业，快去新建一个吧！"></el-empty>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Plus } from '@element-plus/icons-vue';
import { useUserStore } from '../../stores/user';
import { getCourseChapterTree, saveOrUpdateChapter, deleteChapter, getUploadToken, reorderChapters } from '@/api/courseChapter';
import { getHomeworkList, saveHomework, deleteHomework } from '@/api/homework';

const userStore = useUserStore()
// 假定API文件已更新
// import { saveHomework as apiSaveHomework } from '@/api/homework'; // 暂时注释掉

const getNumberedLabel = (node) => {
  // 层级为1，是章
  if (node.level === 1) {
    const chapterIndex = node.parent.childNodes.indexOf(node) + 1;
    return `${chapterIndex} ${node.data.title}`;
  }
  // 层级为2，可能是节或作业
  if (node.level === 2) {
    // 通过ID在原始数据中查找父节点（章）的索引，这比通过对象引用查找更可靠
    const chapterIndex = chapterTree.value.findIndex(c => c.id === node.parent.data.id) + 1;
    const sectionIndex = node.parent.childNodes.indexOf(node) + 1;
    
    const prefix = `${chapterIndex}.${sectionIndex}`;
    const typeLabel = node.data.type === 3 ? '作业' : ''; // 添加作业标识
    
    return `${prefix} ${typeLabel} ${node.data.title}`;
  }
  return node.data.title;
};

const props = defineProps({
  courseId: {
    type: [String, Number],
    required: true
  }
});

// --- 新增：七牛云域名配置 ---
// TODO: 请将此处替换为你自己在 application.properties 中配置的七牛云域名
const QINIU_DOMAIN = 'http://sxh8oib6z.hb-bkt.clouddn.com';

const router = useRouter();
const chapterTree = ref([]);
const currentNode = ref({}); // 当前正在编辑的节点
const treeProps = {
  children: 'children',
  label: 'title',
};

const activeTab = ref('chapters');
const homeworkList = ref([]);
const homeworkLoading = ref(false);

// --- 七牛云上传相关 ---
const qiniuData = ref({
  token: '',
  key: '',
  uploadUrl: 'http://upload-z1.qiniup.com/' // 默认为华南z2区，请根据你的存储区域修改
});

// 添加用于格式化文件大小的函数
const formatFileSize = (bytes) => {
  if (bytes === 0) return '0 B';
  const k = 1024;
  const sizes = ['B', 'KB', 'MB', 'GB'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
};

// 添加上传进度相关的响应式变量
const uploadProgress = ref(0);
const isUploading = ref(false);
const uploadedSize = ref(0);
const totalSize = ref(0);

onMounted(async () => {
  await fetchChapterTree();
  await fetchHomeworkList(); // 组件挂载时也获取一次作业列表
  await fetchUploadToken();
});

// 监听标签页切换
watch(activeTab, (newTab) => {
    if (newTab === 'homework') {
        fetchHomeworkList();
    }
});

const fetchHomeworkList = async () => {
    try {
        const res = await getHomeworkList(props.courseId);
        homeworkList.value = res;
    } catch (error) {
        ElMessage.error('获取作业列表失败');
        console.error(error);
    }
};

const fetchChapterTree = async () => {
  try {
    const res = await getCourseChapterTree(props.courseId);
    // 后端已经返回树形结构，直接使用即可
    chapterTree.value = res;
  } catch (error) {
    ElMessage.error('获取课程目录失败');
  }
};

const fetchUploadToken = async () => {
    try {
        if(userStore.isTeacher) {
        const res = await getUploadToken();
        const r = await getUploadToken();
        qiniuData.value.token = r;
      }
    } catch(e) {
      console.error(e);
      ElMessage.error("获取上传凭证失败，请检查后端服务和七牛云配置");
    }
};

const goBack = () => router.back();

const addChapter = () => {
  currentNode.value = {
    courseId: props.courseId,
    parentId: 0,
    type: 1, // 1表示章
    title: '',
    orderNum: chapterTree.value.length + 1 // 默认排序号为最后
  };
  ElMessage.info('请在右侧表单中填写新章节的信息');
};

const addSection = (data) => {
    currentNode.value = {
        courseId: props.courseId,
        parentId: data.id,
        type: 2, // 2表示节
        title: '',
        url: '',
        orderNum: data.children ? data.children.length + 1 : 1
    };
    ElMessage.info('请在右侧表单中填写新课时的信息');
};

const editNode = (node, data) => {
    currentNode.value = { ...data }; // 浅拷贝一份数据进行编辑
};

const removeNode = async (node, data) => {
    let confirmMessage = '';
    if (data.type === 1) {
        confirmMessage = '此操作将永久删除该章节及其下所有课时和作业, 是否继续?';
    } else if (data.type === 2) {
        confirmMessage = '此操作将永久删除该课时, 是否继续?';
    } else {
        confirmMessage = '此操作将永久删除该作业, 是否继续?';
    }
        
    try {
        await ElMessageBox.confirm(confirmMessage, '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
        });
        
        await deleteChapter(data.id);
        ElMessage.success('删除成功!');
        
        // If the deleted node is the one being edited, clear the form
        if (currentNode.value.id === data.id) {
            currentNode.value = {};
        }
        await fetchChapterTree(); // Refresh tree
    } catch (error) {
        if (error === 'cancel') {
            ElMessage.info('已取消删除');
        } else {
            console.error(error);
            ElMessage.error('删除失败');
        }
    }
};

const saveNode = async () => {
    if (!currentNode.value.title || !currentNode.value.title.trim()) {
        ElMessage.warning('请输入标题');
        return;
    }
    try {
        const savedData = await saveOrUpdateChapter(currentNode.value);
        ElMessage.success('保存成功!');
        await fetchChapterTree(); // 刷新树
        // 更新当前节点为保存后的数据（特别是为了获取新ID）
        currentNode.value = { ...savedData };
    } catch (error) {
        ElMessage.error('保存失败');
        console.error(error);
    }
};

/**
 * 拖拽时判断目标节点是否可以放置
 * @param {object} draggingNode - 被拖拽的节点
 * @param {object} dropNode - 目标节点
 * @param {string} type - 放置类型 ('prev', 'inner', 'next')
 */
const allowDrop = (draggingNode, dropNode, type) => {
    // 不允许将"章"拖拽到"节"的内部
    if (dropNode.data.type === 2 && type === 'inner') {
        return false;
    }
    // 不允许跨级拖拽（例如，把一个"节"拖到根级别）
    if (draggingNode.parent.id !== dropNode.parent.id) {
        return false;
    }
    return true;
};

/**
 * 拖拽成功完成时触发
 */
const handleNodeDrop = async (draggingNode, dropNode, dropType, ev) => {
    try {
        ElMessage.info('正在保存新顺序...');
        const parentNode = dropNode.parent;
        const children = parentNode.childNodes;

        const reorderList = children.map((node, index) => ({
            id: node.data.id,
            orderNum: index + 1
        }));
        
        await reorderChapters(reorderList);
        ElMessage.success('顺序更新成功!');
        await fetchChapterTree(); // 刷新以确认顺序
    } catch (error) {
        ElMessage.error('顺序更新失败');
        // 可以在此处重新获取数据以恢复到之前的状态
        await fetchChapterTree();
    }
};

// --- 视频上传钩子 ---
const beforeVideoUpload = async (file) => {
    const isLt500M = file.size / 1024 / 1024 < 500;

    if (!isLt500M) {
        ElMessage.error('上传视频大小不能超过 500MB!');
        return false;
    }

    if (!currentNode.value.id) {
        ElMessage.error('请先保存课时信息，然后再上传视频！');
        return false;
    }
    if (!qiniuData.value.token) {
        ElMessage.error('未能获取上传凭证，无法上传！');
        await fetchUploadToken(); // 尝试重新获取
        if(!qiniuData.value.token) return false; // 还是失败则阻止上传
    }
    // 从原始文件名中提取后缀
    const fileExt = file.name.split('.').pop();
    // 重新构造文件名，避免中文或特殊字符带来的URL编码问题
    qiniuData.value.key = `videos/${props.courseId}/${currentNode.value.id}_${Date.now()}${fileExt ? '.' + fileExt : ''}`;
    return true;
};

const handleVideoProgress = (event, file) => {
  uploadProgress.value = Math.round(event.percent);
  isUploading.value = true;
  uploadedSize.value = event.loaded;
  totalSize.value = event.total;
};

const handleVideoSuccess = (response, file) => {
  isUploading.value = false;
  uploadProgress.value = 100;
  // 构建完整的文件URL
  const fileUrl = `${QINIU_DOMAIN}/${response.key}`;
  currentNode.value.url = fileUrl;
  ElMessage.success('视频上传成功');
  
  // 3秒后重置进度条
  setTimeout(() => {
    uploadProgress.value = 0;
  }, 3000);
};

const handleVideoError = (err) => {
  isUploading.value = false;
  uploadProgress.value = 0;
  ElMessage.error('视频上传失败');
};

const handleCreateHomework = async () => {
    const newHomework = {
        courseId: props.courseId,
        title: `新作业 - ${new Date().toLocaleString()}`,
        description: '请填写作业描述...',
        questions: [],
        startTime: new Date(),
        endTime: new Date(new Date().getTime() + 7 * 24 * 60 * 60 * 1000)
    };

    try {
        const savedHomework = await saveHomework(newHomework);
        ElMessage.success('作业创建成功，正在跳转到编辑器...');
        router.push({ 
            name: 'HomeworkEditor', 
            params: { courseId: props.courseId, homeworkId: savedHomework.id }
        });
    } catch (error) {
        ElMessage.error('创建作业失败');
        console.error(error);
    }
};

const handleEditHomework = (homework) => {
    router.push(`/course/${props.courseId}/homework/edit/${homework.id}`);
};

const handleDeleteHomework = (row) => {
    ElMessageBox.confirm(
        `确定要删除作业《${row.title}》吗？这将同时删除所有学生的提交记录，此操作不可恢复。`,
        '危险操作确认',
        {
            confirmButtonText: '确认删除',
            cancelButtonText: '取消',
            type: 'warning',
        }
    ).then(async () => {
        try {
            await deleteHomework(row.id);
            ElMessage.success('作业删除成功');
            fetchHomeworkList(); // Refresh the list
        } catch (error) {
            ElMessage.error('删除失败，请稍后重试');
            console.error(error);
        }
    }).catch(() => {
        // User canceled
    });
};

</script>

<style scoped>
.el-tree {
  --el-tree-node-indent: 24px;
}
.course-manage-container {
  padding: 20px;
}
.page-header {
    margin-bottom: 20px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.custom-tree-node {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 14px;
  padding-right: 8px;
}
.video-preview {
    margin-top: 20px;
}

.video-url {
    margin-bottom: 15px;
}

.video-container {
    position: relative;
    width: 100%;
    max-width: 800px;
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

.upload-progress {
  margin-top: 10px;
  width: 100%;
  max-width: 400px; /* 设置最大宽度为400px */
}

.video-uploader {
  width: 100%;
  max-width: 400px; /* 让上传组件和进度条宽度一致 */
}

.el-upload__tip {
  width: 100%;
  max-width: 400px; /* 提示文本也保持一致的宽度 */
}
</style> 